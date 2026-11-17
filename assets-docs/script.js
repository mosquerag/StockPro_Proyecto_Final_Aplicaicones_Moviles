/* ==========================================================================
   StockPro · interacciones de la landing y la guía
   - Navbar con sombra al hacer scroll
   - Animaciones de entrada y contadores
   - Copiar comandos con feedback
   - Pestañas Bootstrap + apertura por hash
   - Mermaid renderizado solo cuando la pestaña está visible
   ========================================================================== */

(function () {
  'use strict';

  document.documentElement.classList.add('js');

  var prefersReducedMotion = window.matchMedia
    ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
    : false;

  /* ------------------------------------------------------------------
     1. Navbar
     ------------------------------------------------------------------ */
  var navbar = document.querySelector('.sp-navbar');

  function updateNavbar() {
    if (!navbar) return;
    navbar.classList.toggle('is-scrolled', window.scrollY > 10);
  }

  updateNavbar();
  window.addEventListener('scroll', updateNavbar, { passive: true });

  var mainCollapse = document.getElementById('menuPrincipal');
  if (mainCollapse) {
    mainCollapse.addEventListener('click', function (event) {
      var link = event.target.closest('a[href^="#"]');
      if (!link || !window.bootstrap) return;
      if (!mainCollapse.classList.contains('show')) return;
      var instance = window.bootstrap.Collapse.getOrCreateInstance(mainCollapse, { toggle: false });
      instance.hide();
    });
  }

  /* ------------------------------------------------------------------
     2. Animaciones de entrada
     ------------------------------------------------------------------ */
  var revealItems = Array.prototype.slice.call(document.querySelectorAll('.reveal'));

  if (!prefersReducedMotion && 'IntersectionObserver' in window) {
    var revealObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        var el = entry.target;
        var delay = Number(el.getAttribute('data-delay') || 0);
        el.style.transitionDelay = Math.min(delay * 80, 320) + 'ms';
        el.classList.add('is-visible');
        revealObserver.unobserve(el);
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -36px 0px' });

    revealItems.forEach(function (el) { revealObserver.observe(el); });
  } else {
    revealItems.forEach(function (el) { el.classList.add('is-visible'); });
  }

  /* ------------------------------------------------------------------
     3. Contadores
     ------------------------------------------------------------------ */
  var counters = Array.prototype.slice.call(document.querySelectorAll('[data-count]'));

  function animateCounter(el) {
    var target = Number(el.getAttribute('data-count'));
    if (!Number.isFinite(target)) return;

    if (prefersReducedMotion) {
      el.textContent = String(target);
      return;
    }

    var duration = 900;
    var started = null;

    function tick(now) {
      if (started === null) started = now;
      var progress = Math.min((now - started) / duration, 1);
      var eased = 1 - Math.pow(1 - progress, 3);
      el.textContent = String(Math.round(target * eased));
      if (progress < 1) window.requestAnimationFrame(tick);
    }

    window.requestAnimationFrame(tick);
  }

  if (counters.length) {
    if ('IntersectionObserver' in window && !prefersReducedMotion) {
      var counterObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
          if (!entry.isIntersecting) return;
          animateCounter(entry.target);
          counterObserver.unobserve(entry.target);
        });
      }, { threshold: 0.5 });
      counters.forEach(function (el) { counterObserver.observe(el); });
    } else {
      counters.forEach(animateCounter);
    }
  }

  /* ------------------------------------------------------------------
     4. Copiar al portapapeles
     ------------------------------------------------------------------ */
  function legacyCopy(text) {
    return new Promise(function (resolve, reject) {
      var area = document.createElement('textarea');
      area.value = text;
      area.setAttribute('readonly', '');
      area.style.position = 'fixed';
      area.style.top = '-9999px';
      area.style.opacity = '0';
      document.body.appendChild(area);
      area.select();

      try {
        var ok = document.execCommand('copy');
        area.remove();
        ok ? resolve() : reject(new Error('copy-failed'));
      } catch (error) {
        area.remove();
        reject(error);
      }
    });
  }

  function copyText(text) {
    if (navigator.clipboard && window.isSecureContext) {
      // Si el permiso de portapapeles falla, se intenta el método clásico
      // para que el botón siga funcionando en navegadores restringidos.
      return navigator.clipboard.writeText(text).catch(function () {
        return legacyCopy(text);
      });
    }

    return legacyCopy(text);
  }

  function copySource(button) {
    var block = button.closest('.code-block, .credential-card, [data-copy-block]');
    var source = null;

    if (button.hasAttribute('data-copy-text')) {
      source = button.getAttribute('data-copy-text');
    } else if (block) {
      var pre = block.querySelector('pre');
      if (pre) source = pre.innerText;
    }

    if (!source) return Promise.reject(new Error('nothing-to-copy'));
    return copyText(source.replace(/\s+$/, ''));
  }

  function showCopyFeedback(button) {
    if (!button.dataset.originalHtml) {
      button.dataset.originalHtml = button.innerHTML;
    }
    button.innerHTML = '<i class="bi bi-check2" aria-hidden="true"></i> Copiado \u2713';
    button.classList.add('copied');

    window.clearTimeout(button._copyTimer);
    button._copyTimer = window.setTimeout(function () {
      button.innerHTML = button.dataset.originalHtml;
      button.classList.remove('copied');
    }, 2000);
  }

  document.addEventListener('click', function (event) {
    var button = event.target.closest('.copy-btn');
    if (!button) return;

    event.preventDefault();
    copySource(button)
      .then(function () { showCopyFeedback(button); })
      .catch(function () {
        button.innerHTML = '<i class="bi bi-x-circle" aria-hidden="true"></i> No se pudo';
        window.clearTimeout(button._copyTimer);
        button._copyTimer = window.setTimeout(function () {
          if (button.dataset.originalHtml) button.innerHTML = button.dataset.originalHtml;
        }, 2000);
      });
  });

  /* ------------------------------------------------------------------
     5. Pestañas Bootstrap y enlaces por hash
     ------------------------------------------------------------------ */
  var hashToTab = {
    diagramas: 'tab-diagramas',
    basedatos: 'tab-basedatos',
    adrs: 'tab-adrs',
    instalacion: 'tab-instalacion',
    documentacion: 'tab-documentacion'
  };

  function openTabFromHash(shouldScroll) {
    if (!window.bootstrap) return;
    var key = window.location.hash.replace('#', '').toLowerCase();
    var button = document.getElementById(hashToTab[key] || '');
    if (!button) return;

    var tab = window.bootstrap.Tab.getOrCreateInstance(button);
    tab.show();

    if (shouldScroll) {
      var target = document.getElementById('documentacion');
      if (target) {
        window.setTimeout(function () {
          target.scrollIntoView({ behavior: prefersReducedMotion ? 'auto' : 'smooth', block: 'start' });
        }, 80);
      }
    }
  }

  openTabFromHash(false);
  window.addEventListener('hashchange', function () { openTabFromHash(true); });

  /* ------------------------------------------------------------------
     6. Mermaid bajo demanda
     ------------------------------------------------------------------ */
  var diagramSources = new Map();
  var mermaidInitialized = false;

  function initMermaid() {
    if (mermaidInitialized || !window.mermaid) return;
    window.mermaid.initialize({
      startOnLoad: false,
      theme: 'base',
      securityLevel: 'loose',
      fontFamily: '"Plus Jakarta Sans", system-ui, sans-serif',
      // En Mermaid 11 la opción raíz tiene prioridad sobre flowchart.htmlLabels.
      // Ambas se declaran para que el SVG use <text> y no <foreignObject>.
      htmlLabels: false,
      flowchart: {
        htmlLabels: false,
        useMaxWidth: true,
        curve: 'basis'
      },
      sequence: {
        useMaxWidth: true
      },
      er: {
        useMaxWidth: true,
        layoutDirection: 'TB'
      },
      themeVariables: {
        background: '#FFFFFF',
        primaryColor: '#E6F7F0',
        primaryTextColor: '#19211E',
        primaryBorderColor: '#087F5B',
        lineColor: '#087F5B',
        secondaryColor: '#EFF4F1',
        tertiaryColor: '#FFFFFF',
        textColor: '#19211E',
        fontSize: '14px',
        fontFamily: '"Plus Jakarta Sans", system-ui, sans-serif',
        attributeBackgroundColorOdd: '#F7FAF8',
        attributeBackgroundColorEven: '#FFFFFF'
      }
    });
    mermaidInitialized = true;
  }

  // Inicializar de inmediato (antes de DOMContentLoaded) para que Mermaid no
  // auto-renderice los diagramas ocultos con la configuración por defecto.
  initMermaid();

  async function renderDiagrams(panel) {
    if (!panel || !window.mermaid) return;
    initMermaid();

    var nodes = Array.prototype.slice
      .call(panel.querySelectorAll('.mermaid'))
      .filter(function (node) {
        return node.getAttribute('data-rendered') !== 'ok' && node.offsetParent !== null;
      });

    for (var i = 0; i < nodes.length; i += 1) {
      var node = nodes[i];
      if (!diagramSources.has(node)) {
        diagramSources.set(node, node.textContent);
      }

      node.setAttribute('data-rendered', 'rendering');

      try {
        await window.mermaid.run({ nodes: [node] });
        node.setAttribute('data-rendered', 'ok');
      } catch (error) {
        node.textContent = diagramSources.get(node);
        node.setAttribute('data-rendered', 'error');
        console.error('No se pudo renderizar el diagrama Mermaid:', error);
      }
    }
  }

  document.addEventListener('shown.bs.tab', function (event) {
    var selector = event.target.getAttribute('data-bs-target') || event.target.getAttribute('href');
    if (!selector || selector.charAt(0) !== '#') return;
    var panel = document.querySelector(selector);
    renderDiagrams(panel);
  });

  // Si por hash o por estado inicial ya hay un panel visible con diagramas,
  // se renderiza después de que el navegador calcule el layout.
  window.addEventListener('load', function () {
    Array.prototype.slice
      .call(document.querySelectorAll('.tab-pane.active'))
      .forEach(function (panel) { renderDiagrams(panel); });
  });

  /* ------------------------------------------------------------------
     7. Año dinámico
     ------------------------------------------------------------------ */
  document.querySelectorAll('[data-year]').forEach(function (el) {
    el.textContent = String(new Date().getFullYear());
  });
})();
