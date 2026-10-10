/* Shows a full-screen "No internet" notice whenever the device is offline. */
(function () {
  var loadedOffline = !navigator.onLine;
  var box;

  function build() {
    if (box) return box;
    box = document.createElement('div');
    box.id = 'noInternet';
    box.setAttribute('role', 'alert');
    box.style.cssText = 'display:none;position:fixed;inset:0;z-index:2147483647;align-items:center;justify-content:center;flex-direction:column;gap:14px;padding:24px;text-align:center;background:#fff7ec;color:#3b2a1a;font-family:system-ui,-apple-system,Segoe UI,Roboto,sans-serif;';
    box.innerHTML =
      '<div style="font-size:56px;line-height:1">&#128225;</div>' +
      '<h2 style="margin:0;font-size:22px;font-weight:800;color:#ec6713">No internet....</h2>' +
      '<p style="margin:0;font-size:16px;opacity:.85">Please try again.</p>' +
      '<button id="noInternetRetry" type="button" style="margin-top:6px;padding:12px 28px;border:0;border-radius:999px;background:#ec6713;color:#fff;font-size:16px;font-weight:700;cursor:pointer">Try again</button>';
    document.documentElement.appendChild(box);
    box.querySelector('#noInternetRetry').addEventListener('click', function () {
      if (navigator.onLine) { location.reload(); }
      else { box.style.opacity = '.5'; setTimeout(function () { box.style.opacity = '1'; }, 200); }
    });
    return box;
  }

  function show() { build().style.display = 'flex'; }
  function hide() { if (box) box.style.display = 'none'; }

  function update() {
    if (!navigator.onLine) { show(); return; }
    // Back online: if the page loaded without network, its CDN assets are missing, so reload.
    if (loadedOffline) { location.reload(); } else { hide(); }
  }

  window.addEventListener('offline', update);
  window.addEventListener('online', update);
  if (loadedOffline) {
    if (document.documentElement) show();
  }
})();
