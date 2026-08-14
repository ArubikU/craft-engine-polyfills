/* ============================================================
   CraftEngine Polyfills — Documentation JS
   ============================================================ */

(function () {
  'use strict';

  // ---- Copy buttons --------------------------------------------------
  function initCopyButtons() {
    document.querySelectorAll('pre').forEach(function (pre) {
      var btn = document.createElement('button');
      btn.className = 'copy-btn';
      btn.textContent = 'Copy';
      btn.addEventListener('click', function () {
        var text = pre.querySelector('code') ? pre.querySelector('code').textContent : pre.textContent;
        navigator.clipboard.writeText(text).then(function () {
          btn.textContent = 'Copied!';
          btn.classList.add('copied');
          setTimeout(function () {
            btn.textContent = 'Copy';
            btn.classList.remove('copied');
          }, 1800);
        }).catch(function () {
          // fallback
          var ta = document.createElement('textarea');
          ta.value = text;
          document.body.appendChild(ta);
          ta.select();
          document.execCommand('copy');
          document.body.removeChild(ta);
          btn.textContent = 'Copied!';
          btn.classList.add('copied');
          setTimeout(function () {
            btn.textContent = 'Copy';
            btn.classList.remove('copied');
          }, 1800);
        });
      });
      pre.appendChild(btn);
    });
  }

  // ---- Sidebar collapsible sections ----------------------------------
  function initSidebarCollapse() {
    document.querySelectorAll('.nav-section-header').forEach(function (header) {
      header.addEventListener('click', function () {
        header.parentElement.classList.toggle('collapsed');
      });
    });
  }

  // ---- Sidebar active link -------------------------------------------
  function setActiveLink() {
    var path = window.location.pathname;
    document.querySelectorAll('.sidebar .nav-items a').forEach(function (a) {
      var href = a.getAttribute('href');
      if (!href) return;
      // Compare basename
      var aBase = href.split('/').pop().split('#')[0];
      var pBase = path.split('/').pop();
      if (aBase && pBase && aBase === pBase) {
        a.classList.add('active');
        // Expand parent section
        var section = a.closest('.nav-section');
        if (section) section.classList.remove('collapsed');
      }
    });
  }

  // ---- In-page search ------------------------------------------------
  // Build a searchable index from nav links + headings on the current page
  var searchIndex = [];

  function buildSearchIndex() {
    // Nav links
    document.querySelectorAll('.sidebar .nav-items a').forEach(function (a) {
      var title = a.textContent.trim();
      var href  = a.getAttribute('href') || '#';
      if (title) searchIndex.push({ title: title, href: href, type: 'page' });
    });

    // On-page headings
    document.querySelectorAll('h2, h3').forEach(function (h) {
      if (!h.id) {
        h.id = h.textContent.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
      }
      searchIndex.push({
        title: h.textContent.trim(),
        href: '#' + h.id,
        type: 'section'
      });
    });
  }

  function initSearch() {
    var input   = document.getElementById('search');
    var results = document.getElementById('search-results');
    if (!input || !results) return;

    input.addEventListener('input', function () {
      var q = input.value.trim().toLowerCase();
      if (q.length < 2) { results.classList.remove('visible'); results.innerHTML = ''; return; }

      var hits = searchIndex.filter(function (item) {
        return item.title.toLowerCase().indexOf(q) >= 0;
      }).slice(0, 12);

      if (hits.length === 0) {
        results.innerHTML = '<a style="color:var(--text-muted)">No results</a>';
      } else {
        results.innerHTML = hits.map(function (item) {
          var hl = item.title.replace(new RegExp('(' + q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi'), '<em>$1</em>');
          var icon = item.type === 'page' ? '&#128196; ' : '&#128279; ';
          return '<a href="' + item.href + '">' + icon + hl + '</a>';
        }).join('');
      }
      results.classList.add('visible');
    });

    document.addEventListener('click', function (e) {
      if (!input.contains(e.target) && !results.contains(e.target)) {
        results.classList.remove('visible');
      }
    });

    input.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') { results.classList.remove('visible'); input.blur(); }
    });
  }

  // ---- Mobile hamburger ---------------------------------------------
  function initHamburger() {
    var btn     = document.getElementById('hamburger');
    var sidebar = document.querySelector('.sidebar');
    if (!btn || !sidebar) return;
    btn.addEventListener('click', function () {
      sidebar.classList.toggle('open');
    });
    document.addEventListener('click', function (e) {
      if (sidebar.classList.contains('open') && !sidebar.contains(e.target) && e.target !== btn) {
        sidebar.classList.remove('open');
      }
    });
  }

  // ---- Smooth scroll offset for fixed topbar ------------------------
  function initAnchorOffset() {
    document.querySelectorAll('a[href^="#"]').forEach(function (a) {
      a.addEventListener('click', function (e) {
        var target = document.querySelector(a.getAttribute('href'));
        if (!target) return;
        e.preventDefault();
        var offset = 56 + 16;
        var top = target.getBoundingClientRect().top + window.pageYOffset - offset;
        window.scrollTo({ top: top, behavior: 'smooth' });
      });
    });
  }

  // ---- Init all ------------------------------------------------------
  document.addEventListener('DOMContentLoaded', function () {
    initCopyButtons();
    initSidebarCollapse();
    buildSearchIndex();
    initSearch();
    setActiveLink();
    initHamburger();
    initAnchorOffset();
  });
})();
