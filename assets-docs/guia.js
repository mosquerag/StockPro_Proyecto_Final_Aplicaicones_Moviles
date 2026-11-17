/* ==========================================================================
   StockPro · interacciones específicas de la guía de instalación
   - Smoke test con checkboxes persistentes (localStorage)
   - Barra de progreso y reinicio
   - Resaltado del índice lateral
   ========================================================================== */

(function () {
  'use strict';

  /* ------------------------------------------------------------------
     1. Smoke test persistente
     ------------------------------------------------------------------ */
  var STORAGE_KEY = 'stockpro-smoke-test-v1';
  var steps = Array.prototype.slice.call(document.querySelectorAll('[data-smoke-step]'));

  if (steps.length) {
    var state = {};

    try {
      state = JSON.parse(window.localStorage.getItem(STORAGE_KEY) || '{}') || {};
    } catch (error) {
      state = {};
    }

    var counter = document.querySelector('[data-smoke-progress]');
    var progressBar = document.querySelector('[data-smoke-bar]');
    var resetButtons = document.querySelectorAll('[data-smoke-reset]');
    var mobileCounter = document.querySelector('[data-smoke-mobile-progress]');

    function persist() {
      try {
        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
      } catch (error) {
        // El almacenamiento puede estar deshabilitado; la interacción sigue funcionando.
      }
    }

    function update() {
      var done = 0;

      steps.forEach(function (checkbox) {
        var step = checkbox.closest('.smoke-step');
        if (step) step.classList.toggle('is-done', checkbox.checked);
        if (checkbox.checked) done += 1;
      });

      var percent = Math.round((done / steps.length) * 100);
      var label = done + ' / ' + steps.length;

      if (counter) counter.textContent = label;
      if (mobileCounter) mobileCounter.textContent = label;
      if (progressBar) {
        progressBar.style.width = percent + '%';
        progressBar.setAttribute('aria-valuenow', String(percent));
        progressBar.textContent = percent + '%';
      }
    }

    steps.forEach(function (checkbox) {
      var id = checkbox.getAttribute('data-smoke-step');
      checkbox.checked = Boolean(state[id]);

      checkbox.addEventListener('change', function () {
        state[id] = checkbox.checked;
        persist();
        update();
      });
    });

    resetButtons.forEach(function (button) {
      button.addEventListener('click', function () {
        state = {};
        steps.forEach(function (checkbox) { checkbox.checked = false; });
        try {
          window.localStorage.removeItem(STORAGE_KEY);
        } catch (error) {
          // Sin almacenamiento: solo se limpia el estado en memoria.
        }
        update();
      });
    });

    update();
  }

  /* ------------------------------------------------------------------
     2. Índice lateral activo
     ------------------------------------------------------------------ */
  var tocLinks = Array.prototype.slice.call(document.querySelectorAll('.guide-toc a[href^="#"]'));

  if (tocLinks.length && 'IntersectionObserver' in window) {
    var sections = tocLinks
      .map(function (link) {
        return document.getElementById(link.getAttribute('href').replace('#', ''));
      })
      .filter(Boolean);

    var tocObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        tocLinks.forEach(function (link) {
          var isCurrent = link.getAttribute('href') === '#' + entry.target.id;
          link.classList.toggle('active', isCurrent);
          if (isCurrent) link.setAttribute('aria-current', 'true');
          else link.removeAttribute('aria-current');
        });
      });
    }, { rootMargin: '-25% 0px -60% 0px', threshold: 0 });

    sections.forEach(function (section) { tocObserver.observe(section); });
  }
})();
