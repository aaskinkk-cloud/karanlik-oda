package app.karanlikoda.android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONObject;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Karanlık Oda — Android uygulaması 3.0: GeckoView (Firefox motoru) uygulamanın içinde.
 * Neden: WebView'de grafik birimi çöküyordu; aynı sayfa Firefox'ta çökmeden çalıştı (27.11.2026 testi).
 * Chrome'a da Firefox'a da bağlı değil: motor APK'nın içinde. Veriler uygulamanın kendi profilinde.
 * Ekran: kalıcı tam ekran (immersive); kamera bölgesi sayfanın tema rengine boyanır, sayfa kameranın altından başlar.
 */
public class MainActivity extends Activity {
    private static final String ADRES = "https://aaskinkk-cloud.github.io/karanlik-oda/";
    private static final String YEDEK = "https://ahmet-uygulamalar.pages.dev/karanlik-oda/";
    private static final int DOSYA_SEC = 41;

    private static GeckoRuntime motor;          // süreç başına tek motor
    private GeckoSession oturum;
    private GeckoView gv;
    private FrameLayout kap;
    private boolean geriVar = false, yedekte = false;
    private int hata = 0;
    private int renk = 0xFF0F1213;
    private String sonAdres = null;
    private WebExtension.Port port;

    private GeckoResult<GeckoSession.PromptDelegate.PromptResponse> dosyaSonuc;
    private GeckoSession.PromptDelegate.FilePrompt dosyaIstek;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = w.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            w.setAttributes(lp);
        }
        kap = new FrameLayout(this);
        kap.setBackgroundColor(renk);
        gv = new GeckoView(this);
        kap.addView(gv, new FrameLayout.LayoutParams(-1, -1));
        setContentView(kap);
        /* kamera bölgesi: sayfa onun altından başlar, bölgenin kendisi kabın (tema) rengindedir */
        kap.setOnApplyWindowInsetsListener((v, ins) -> {
            int ust = 0, sol = 0, sag = 0;
            if (Build.VERSION.SDK_INT >= 28 && ins.getDisplayCutout() != null) {
                ust = ins.getDisplayCutout().getSafeInsetTop();
                sol = ins.getDisplayCutout().getSafeInsetLeft();
                sag = ins.getDisplayCutout().getSafeInsetRight();
            }
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) gv.getLayoutParams();
            if (p.topMargin != ust || p.leftMargin != sol || p.rightMargin != sag) {
                p.topMargin = ust; p.leftMargin = sol; p.rightMargin = sag; gv.setLayoutParams(p);
            }
            return ins;
        });

        if (motor == null) {
            GeckoRuntimeSettings.Builder ayar = new GeckoRuntimeSettings.Builder()
                    .consoleOutput(true)
                    .aboutConfigEnabled(false);
            motor = GeckoRuntime.create(getApplicationContext(), ayar.build());
        }
        oturumKur();
        /* sayfa köprüsü (tema rengi); kurulamazsa sayfa yine açılır */
        motor.getWebExtensionController()
                .ensureBuiltIn("resource://android/assets/kopru/", "kopru@karanlikoda")
                .accept(ext -> { kopruBagla(ext); ac(); }, e -> ac());
        tamEkran();
    }

    private WebExtension kopruEk;
    private void kopruBagla(WebExtension ext) {
        kopruEk = ext;
        if (ext == null || oturum == null) return;
        oturum.getWebExtensionController().setMessageDelegate(ext, new WebExtension.MessageDelegate() {
            @Override public void onConnect(WebExtension.Port p) {
                port = p;
                p.setDelegate(new WebExtension.PortDelegate() {
                    @Override public void onPortMessage(Object m, WebExtension.Port pp) {
                        if (m instanceof JSONObject) {
                            String c = ((JSONObject) m).optString("renk", "");
                            if (!c.isEmpty()) runOnUiThread(() -> renkUygula(c));
                        }
                    }
                    @Override public void onDisconnect(WebExtension.Port pp) { if (port == pp) port = null; }
                });
            }
        }, "browser");
    }

    private boolean acildi = false;
    private void ac() {
        if (acildi) return; acildi = true;
        oturum.loadUri(sonAdres != null ? sonAdres : ADRES);
    }

    private void oturumKur() {
        oturum = new GeckoSession(new GeckoSessionSettings.Builder()
                .usePrivateMode(false)
                .viewportMode(GeckoSessionSettings.VIEWPORT_MODE_MOBILE)
                .userAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
                .build());

        oturum.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
            @Override public void onCanGoBack(GeckoSession s, boolean c) { geriVar = c; }
            /* uygulama adresleri içeride açılır; diğer bağlantılar (YouTube, blog vb.) telefonun kendi uygulamasında */
            @Override public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession s, LoadRequest r) {
                String u = r.uri == null ? "" : r.uri;
                if (bizim(u) || !u.startsWith("http")) return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                disAc(u);
                return GeckoResult.fromValue(AllowOrDeny.DENY);
            }
            @Override public GeckoResult<GeckoSession> onNewSession(GeckoSession s, String uri) {
                if (uri != null && !bizim(uri)) disAc(uri); else if (uri != null) s.loadUri(uri);
                return null;
            }
            /* GitHub açılmazsa bir kez daha denenir, sonra Cloudflare yedeği */
            @Override public GeckoResult<String> onLoadError(GeckoSession s, String uri, org.mozilla.geckoview.WebRequestError e) {
                if (uri == null || !uri.startsWith(ADRES)) return null;
                if (hata == 0) { hata = 1; new Handler(Looper.getMainLooper()).postDelayed(() -> s.loadUri(ADRES), 1500); return null; }
                if (!yedekte) {
                    yedekte = true;
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "GitHub açılmadı — yedek adresten (Cloudflare) açılıyor", Toast.LENGTH_LONG).show());
                    new Handler(Looper.getMainLooper()).post(() -> s.loadUri(YEDEK));
                }
                return null;
            }
        });

        oturum.setProgressDelegate(new GeckoSession.ProgressDelegate() {
            @Override public void onPageStart(GeckoSession s, String url) { if (url != null && url.startsWith("http")) sonAdres = url; }
        });

        oturum.setContentDelegate(new GeckoSession.ContentDelegate() {
            /* indirmeler (blob/data dahil) İndirilenler klasörüne */
            @Override public void onExternalResponse(GeckoSession s, WebResponse r) { indir(r); }
            /* sayfa işlemi çökerse ya da sistem kapatırsa: oturum yeniden kurulur, son adres açılır */
            @Override public void onCrash(GeckoSession s) { yenidenAc("Sayfa çöktü, yeniden açılıyor"); }
            @Override public void onKill(GeckoSession s) { yenidenAc("Sayfa kapatıldı, yeniden açılıyor"); }
            @Override public void onFullScreen(GeckoSession s, boolean tam) { tamEkran(); }
        });

        oturum.setPromptDelegate(new GeckoSession.PromptDelegate() {
            @Override public GeckoResult<PromptResponse> onFilePrompt(GeckoSession s, FilePrompt p) {
                if (dosyaSonuc != null && dosyaIstek != null) { dosyaSonuc.complete(dosyaIstek.dismiss()); }
                dosyaIstek = p; dosyaSonuc = new GeckoResult<>();
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, p.type == FilePrompt.Type.MULTIPLE);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try { startActivityForResult(i, DOSYA_SEC); }
                catch (Exception e) { GeckoResult<PromptResponse> r = dosyaSonuc; dosyaSonuc = null; r.complete(p.dismiss()); return r; }
                return dosyaSonuc;
            }
        });

        oturum.setPermissionDelegate(new GeckoSession.PermissionDelegate() {
            @Override public GeckoResult<Integer> onContentPermissionRequest(GeckoSession s, ContentPermission perm) {
                int t = perm.permission;
                boolean izin = t == PERMISSION_AUTOPLAY_AUDIBLE || t == PERMISSION_AUTOPLAY_INAUDIBLE
                        || t == PERMISSION_PERSISTENT_STORAGE || t == PERMISSION_STORAGE_ACCESS;
                return GeckoResult.fromValue(izin ? ContentPermission.VALUE_ALLOW : ContentPermission.VALUE_DENY);
            }
        });

        oturum.open(motor);
        gv.setSession(oturum);
        if (kopruEk != null) kopruBagla(kopruEk);
    }

    private boolean bizim(String u) { return u.startsWith(ADRES) || u.startsWith(YEDEK) || u.startsWith("https://aaskinkk-cloud.github.io/karanlik-oda") || u.startsWith("https://ahmet-uygulamalar.pages.dev/karanlik-oda"); }

    private void yenidenAc(String m) {
        runOnUiThread(() -> {
            Toast.makeText(this, m, Toast.LENGTH_LONG).show();
            try { gv.releaseSession(); } catch (Exception e) { }
            try { oturum.close(); } catch (Exception e) { }
            oturumKur();
            oturum.loadUri(sonAdres != null ? sonAdres : ADRES);
        });
    }

    private void disAc(String u) {
        runOnUiThread(() -> {
            try { Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(u)); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); }
            catch (Exception e) { Toast.makeText(this, "Bağlantı açılamadı", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void renkUygula(String c) {
        int r;
        try {
            c = c.trim();
            Matcher m = Pattern.compile("rgba?\\(\\s*(\\d+)[ ,]+(\\d+)[ ,]+(\\d+)").matcher(c);
            if (m.find()) r = Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
            else r = Color.parseColor(c.length() == 4 && c.startsWith("#") ? "#" + c.charAt(1) + c.charAt(1) + c.charAt(2) + c.charAt(2) + c.charAt(3) + c.charAt(3) : c);
        } catch (Exception e) { return; }
        renk = r;
        kap.setBackgroundColor(r);
        getWindow().getDecorView().setBackgroundColor(r);
    }

    /** İndirilen dosya (sayfadan "Kaydet", yedek, video) İndirilenler'e yazılır. */
    private void indir(WebResponse r) {
        new Thread(() -> {
            String ad = dosyaAdi(r), tur = baslik(r, "content-type");
            if (tur == null || tur.isEmpty()) tur = "application/octet-stream";
            int sc = tur.indexOf(';'); if (sc > 0) tur = tur.substring(0, sc).trim();
            try (InputStream in = r.body) {
                if (in == null) throw new Exception("boş yanıt");
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues cv = new ContentValues();
                    cv.put(MediaStore.Downloads.DISPLAY_NAME, ad);
                    cv.put(MediaStore.Downloads.MIME_TYPE, tur);
                    Uri u = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
                    try (OutputStream o = getContentResolver().openOutputStream(u)) { kopyala(in, o); }
                } else {
                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ad);
                    try (FileOutputStream o = new FileOutputStream(f)) { kopyala(in, o); }
                }
                final String m = ad;
                runOnUiThread(() -> Toast.makeText(this, "İndirilenler'e kaydedildi: " + m, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                final String m = e.getMessage();
                runOnUiThread(() -> Toast.makeText(this, "Kaydedilemedi: " + m, Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private static void kopyala(InputStream in, OutputStream o) throws java.io.IOException {
        byte[] b = new byte[65536]; int n; while ((n = in.read(b)) > 0) o.write(b, 0, n);
    }
    private static String baslik(WebResponse r, String ad) {
        if (r.headers == null) return null;
        for (java.util.Map.Entry<String, String> e : r.headers.entrySet()) if (e.getKey() != null && e.getKey().equalsIgnoreCase(ad)) return e.getValue();
        return null;
    }
    private static String dosyaAdi(WebResponse r) {
        String cd = baslik(r, "content-disposition"), ad = null;
        if (cd != null) {
            Matcher m = Pattern.compile("filename\\*=(?:UTF-8'')?([^;]+)", Pattern.CASE_INSENSITIVE).matcher(cd);
            if (m.find()) { try { ad = URLDecoder.decode(m.group(1).trim().replace("\"", ""), "UTF-8"); } catch (Exception e) { } }
            if (ad == null) { m = Pattern.compile("filename=\"?([^\";]+)\"?", Pattern.CASE_INSENSITIVE).matcher(cd); if (m.find()) ad = m.group(1).trim(); }
        }
        if (ad == null && r.uri != null && r.uri.startsWith("http")) { String s = Uri.parse(r.uri).getLastPathSegment(); if (s != null && !s.isEmpty()) ad = s; }
        if (ad == null || ad.isEmpty()) ad = "karanlik-oda-" + System.currentTimeMillis();
        return ad.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    @Override protected void onActivityResult(int req, int res, Intent d) {
        if (req == DOSYA_SEC && dosyaSonuc != null && dosyaIstek != null) {
            GeckoResult<GeckoSession.PromptDelegate.PromptResponse> sonuc = dosyaSonuc; GeckoSession.PromptDelegate.FilePrompt p = dosyaIstek;
            dosyaSonuc = null; dosyaIstek = null;
            Uri[] u = null;
            if (res == RESULT_OK && d != null) {
                if (d.getClipData() != null) {
                    int n = d.getClipData().getItemCount(); u = new Uri[n];
                    for (int i = 0; i < n; i++) u[i] = d.getClipData().getItemAt(i).getUri();
                } else if (d.getData() != null) u = new Uri[]{ d.getData() };
            }
            if (u == null || u.length == 0) sonuc.complete(p.dismiss());
            else if (u.length == 1) sonuc.complete(p.confirm(this, u[0]));
            else sonuc.complete(p.confirm(this, u));
            return;
        }
        super.onActivityResult(req, res, d);
    }

    /** Kalıcı tam ekran: sistem çubukları gizli; kaydırınca geçici görünür ve sayfanın üstüne biner. */
    private void tamEkran() {
        final Window w = getWindow();
        final View d = w.getDecorView();
        w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= 30) {
            Runnable r = () -> {
                WindowInsetsController c = d.getWindowInsetsController();
                if (c == null) c = w.getInsetsController();
                if (c != null) {
                    c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                }
            };
            r.run(); d.post(r); d.postDelayed(r, 300);
        }
    }

    @Override public void onWindowFocusChanged(boolean f) { super.onWindowFocusChanged(f); if (f) tamEkran(); }
    @Override protected void onResume() { super.onResume(); tamEkran(); if (oturum != null) oturum.setActive(true); }
    @Override protected void onPause() { if (oturum != null) oturum.setActive(false); super.onPause(); }
    @Override protected void onDestroy() {
        try { gv.releaseSession(); } catch (Exception e) { }
        try { if (oturum != null) oturum.close(); } catch (Exception e) { }
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (oturum != null && geriVar) oturum.goBack(); else super.onBackPressed();
    }
}
