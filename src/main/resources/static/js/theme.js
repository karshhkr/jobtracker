(function () {
  var t = localStorage.getItem('jt_theme');
  if (t) document.documentElement.setAttribute('data-theme', t);
})();