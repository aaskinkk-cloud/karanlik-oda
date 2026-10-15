# Karanlık Oda

Telefonda çalışan, internet gerektirmeyen fotoğraf düzenleyici (PWA). Tek dosya: `index.html`; çevrimdışı önbellek `sw.js`.

**Adres:** https://aaskinkk-cloud.github.io/karanlik-oda/  
**Güncel sürüm:** 6.6 · 15.10.2026

## Bölümler
- **Otomatik · Işık · Renk · HSL · Eğriler · Detay · HDR · Efektler** – temel ve bölgesel düzenleme.
- **Filtreler** – hazır görünümler; *Sonbahar koleksiyonu* ve *Çiçek · gül · meyve* şeritleri. Referans fotoğraftan stil kopyalama.
- **Yazı · İmza** – çok fontlu kombin yazılar, imza, mockup (yüzeye kaynaşan yazı, otomatik uyarla).
- **Selfi** – kişi ve arka plan ayrı ayrı; arka planı değiştir (renk, geçiş, fotoğraf) ya da bulanıklaştır; güzellik (pürüzsüz cilt, cilt tonu, göz, diş); saç rengi ve parlaklığı; geniş açı kamera ve kadrajı genişletme (uzaklaştırma).
- **Kırp · Katman · Toplu işlem.**
- **Basit mod** – üst çubuktaki *Basit* düğmesi (ya da ana sayfadaki seçim) yalnız temel sekmeleri gösterir.
- **Kabataş Haber** – "Sıradan değil, sıladan haberler": haber kartları, TV alt bantları, müzik/efekt, seslendirme, katmanlı zaman çizelgesi, video (MP4) çıktısı, kanal logoları, örnek bülten.

## Sürüm notları
- **6.6** (15.10.2026)
  - **Basit mod** (yeniden): Önceki sürümlerde Karanlık Oda'da basit mod yoktu (Kareden Videoya ve Tını'da vardı). Artık üst çubukta **Basit** düğmesi ve ana sayfada *Basit mod: Açık / Kapalı* seçimi var; açıkken yalnız Otomatik · Filtreler · Selfi · Işık · Renk · Efektler · Kırp · Yazı · Kaydet sekmeleri görünür. Seçim hatırlanır, işlevler silinmez.
  - **Selfi sekmesi:** Kişiyi otomatik seç (yüz ipucuyla) + fırçayla düzelt; kişi ve arka plan için ayrı parlaklık/sıcaklık/doygunluk; arka planı *bulanık · düz renk · renk geçişi · fotoğraf* ile değiştir; güzellik: cilt pürüzsüzleştirme, cilt aydınlığı ve tonu, göz parlaklığı/netliği, diş beyazlatma, *Hızlı güzellik*; saç: 9 hazır renk + renk seçici, yoğunluk, parlaklık, saç alanını fırçayla ekle/çıkar. **Geniş açı kamera** (kamera izin verirse en geniş yakınlığı seçer, ör. 0,6×) ve **Kadrajı genişlet** (fotoğrafı uzaklaştırıp çevresini fotoğrafın devamı gibi / bulanık / düz renkle doldurur; yeni kopya olarak açılır).
  - **Akıllı bulanıklaştırma:** *Bulanıklaştırma fırçası* (yalnız boyanan yer bulanıklaşır; boyut, **sertlik**, silgi). *Çizgiye göre* bulanıklık artık **eğilebilir**: çizginin iki ucundaki tutamaçlardan biri sabit kalırken diğeri yukarı-aşağı oynar; eğim kaydırıcısı, "altını bulanıklaştır" seçeneği.
  - **Alan derinliği:** *Dokunarak odak seç* — net kalacak nesneye dokun, nesne otomatik bulunur, arka plan odaktan uzaklaştıkça kademeli bulanıklaşır (kapatılabilir).
- **6.5** – Kabataş Haber: **foto katmanı araçları** – her fotoğrafın **süresi** (saniye) ayarlanır, büyüt, yatay/dikey konum, hareket (otomatik · sabit · yakınlaş · uzaklaş · sola/sağa kay), **tamamı görünsün (sığdır)**, çoğalt, sil, sırala, ayarları sıfırla. Katman başlığına dokununca **gizle/göster** (foto, manşet, ses, müzik, efekt, bant, ek yazı). **Renkli manşet** biçimi: her satır ayrı renkli kutu, satır başına renk ve boy, `*yıldız içi*` vurgulu; 8 **örnek manşet**. **Biçime göre TV görünümü**: şablon seçince müzik ve geçiş, konu (etiket) seçince bant rengi kendiliğinden değişir (kapatılabilir). **Konuya göre logo**: Röportaj → mikrofon, Hava → güneş, Spor → top, Duyuru → megafon… (logo ve TV bandında; kapatılabilir). Tüm biçimlerde (4:5, 1:1, 9:16, 16:9) denendi.
- **6.4** – Kabataş Haber: önizlemedeki yazıya **dokununca** ilgili düzenleme alanı açılır (manşet, spot, sahne manşeti, kayan bant, açılış/kapanış cümlesi, künye, etiket, ek yazı). **Ek (zamanlı) yazılar parmakla sürüklenip** istenen yere taşınır; yazıya çift dokunuş yerine döndürür. Klavye açılınca odaktaki yazı alanı panelin üstüne kayar, klavyenin altında kalmaz (katmana çift dokunuşla gidilen alanlar dahil). Panel kapalıyken Büyük/Küçük/Gizle'ye basmak paneli açar; önizleme gizliyken panel kapatılırsa önizleme büyük boyda geri gelir (boş ekran kalmaz).
- **6.3** – Düzenleyicide (telefon) stüdyo iyileştirmeleri: panelin üstündeki **tutamaç** sürüklenerek önizleme/panel payı ayarlanır (seçim hatırlanır; çift dokunuş varsayılana döner); tutamağa ya da seçili sekmeye tekrar dokununca panel kapanır, önizleme ekranı doldurur. Yazı alanına dokununca (klavye) önizleme küçülür, sekmeler gizlenir, yazılan alan görünür kalır. Yatay telefonda önizleme solda, panel sağda; sekmeler tek satırda kayar. Kabataş Haber: üst başlık bandı inceltildi (tek satır; dar ekranda slogan gizlenir), önizleme altındaki düğmeler taşmıyor, "Katmanlar" düğmesi alt çubukta olduğu için yukarıdan kaldırıldı.
- **6.2** – Kabataş Haber **stüdyo düzeni** (telefon): önizleme üstte, araç çubuğu altta (Haber · Foto · Katmanlar · Ses · Paylaş · Biçim · Sahneler · TV·Müzik · Logo·künye). Her araç kendi panelinde açılır; uzun sayfa kaydırma yok. "▾ Kapat" paneli kapatıp önizlemeyi tam boy gösterir. Klavye açılınca önizleme küçülür, araç çubuğu gizlenir, yazılan alan görünür kalır. Zaman çizelgesi Katmanlar aracında. Çift dokunma ilgili aracı açar. Yatay telefonda önizleme solda, panel sağda. Büyük · Küçük · Gizle panel açıkken önizleme payını belirler. Masaüstü düzeni değişmedi.
- **6.1** – İki ayrı sohbette yapılan 5.9 sürümleri birleştirildi (temel: telefondaki 5.9 "Büyük · Küçük · Gizle" sürümü). Üzerine eklenen: Kabataş Haber katmanlarına **çift dokunma** – ilgili düzenleme alanına kaydırır, vurgular, yazı alanına odaklanır (sahne/manşet → manşet veya sahne kartı, foto → Fotoğraf bölümü, ses → okunacak metin, ek yazı → yazı kutusu, müzik → seviye, bant → bant yazısı).
- **5.9** – Kabataş Haber: önizleme **Büyük · Küçük · Gizle** (seçim hatırlanır; yazı alanına dokununca önizleme kendiliğinden küçülür); üretilen video küçük gösterilir (⤢ Büyüt, ▴ Videoyu gizle); üst çubukta **＋ Yeni** (mevcut işi taslaklara kaydedip yeni haber başlatır); 16:9 kartta uzun manşetin logoya binmesi düzeltildi.
- **5.8** – Görsellerden ölçülmüş 4 sonbahar filtresi (Balkabağı baharatı, Ekim turuncusu, Sonbahar · canlı, Karanlık natürmort). Yeni hazır yazılar: Balkabağı baharatı (retro kalın), Ekim bandı (el yazısı + tam genişlik turuncu bant), Sonbahar başlık (gölgeli kalın). Yeni fontlar: Titan One, Style Script, Montserrat 800.
- **5.7** – 25 filtre: Sonbahar koleksiyonu; çiçek, gül, meyve.
- **5.6** – Mockup yazılarında "otomatik uyarla" açıkken yazının kaybolması düzeltildi.
- **5.5** – Haberlerde katmanlar/zaman çizelgesi, ek yazılar, kendi müziğin, örnek bülten.
- **5.4** – Müzik ritmine göre değişen fotoğraflar, geçişler, kanal logoları.
- **5.3** – TV alt bantları (kırmızı, beyaz, turuncu, koyu sarı), haber müziği, geçiş sesleri.
- **5.2** – Mockup yüzey kaynaştırma, Kabataş Haber köşesi.

## Güncelleme
Yeni sürümün tüm dosyalarını depo köküne yükle; uygulamayı bir kez kapatıp aç (önbellek `sw.js` sürümüyle yenilenir).
