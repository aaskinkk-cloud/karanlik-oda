/* Karanlık Oda Android (GeckoView) köprüsü: sayfanın tema rengini uygulamaya bildirir (kamera bölgesi ve kenarlar aynı renge boyanır). */
(function(){
  var port = null;
  try { port = browser.runtime.connectNative("browser"); } catch (e) { return; }
  var son = "";
  function gonder(){
    try {
      var m = document.querySelector('meta[name="theme-color"]');
      var c = m && m.content ? m.content.trim() : "";
      if (!c && document.body) c = getComputedStyle(document.body).backgroundColor;
      if (c && c !== son) { son = c; port.postMessage({ renk: c }); }
    } catch (e) {}
  }
  function izle(){
    gonder();
    try { new MutationObserver(gonder).observe(document.documentElement, { subtree: true, childList: true, attributes: true, attributeFilter: ["content", "class", "data-theme", "data-tema"] }); } catch (e) {}
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", izle); else izle();
})();
