// sidebar.js - Auto-builds the docs sidebar on every page from a single definition.
// Include via <script src="assets/sidebar.js"></script> or "../assets/sidebar.js" etc.
(function() {
  const sections = [
    { title: "Guide", icon: "book-open", items: [
      { label: "Getting Started", href: "getting-started.html" },
    ]},
    { title: "Machines", icon: "cpu", items: [
      { label: "Config Reference", href: "machines.html" },
      { label: "Upgrades", href: "upgrades.html" },
      { label: "Bars & Gauges", href: "bars.html" },
    ]},
    { title: "Scripting", icon: "terminal", items: [
      { label: "PolyFormula", href: "polyformula.html" },
      { label: "Renderers", href: "renderers.html" },
    ]},
    { title: "Block Behaviors", icon: "puzzle", items: [
      { label: "Overview", href: "block-behaviors.html" },
      { label: "polyfills:renderer", href: "block-behaviors/renderer.html" },
      { label: "polyfills:data_machine", href: "block-behaviors/data-machine.html" },
      { label: "polyfills:data_multiblock", href: "block-behaviors/data-multiblock.html" },
      { label: "polyfills:gas_pump", href: "block-behaviors/gas-pump.html" },
      { label: "polyfills:pipe_block", href: "block-behaviors/pipe-block.html" },
      { label: "polyfills:fan_block", href: "block-behaviors/fan-block.html" },
      { label: "polyfills:bubble_block", href: "block-behaviors/bubble-block.html" },
      { label: "polyfills:bush_block", href: "block-behaviors/bush-block.html" },
      { label: "polyfills:change_over_time", href: "block-behaviors/change-over-time-block.html" },
    ]},
    { title: "Contraptions", icon: "box", items: [
      { label: "System Overview", href: "contraptions.html" },
    ]},
    { title: "Fluids & Pipes", icon: "droplets", items: [
      { label: "Network Reference", href: "pipes-fluids.html" },
    ]},
    { title: "API", icon: "file-code", items: [
      { label: "Class Index", href: "../api/index.html" },
    ]},
  ];

  // Determine base path for links (handle being in /docs/ or /docs/block-behaviors/)
  const path = window.location.pathname;
  const inSubdir = path.includes('/block-behaviors/');
  const prefix = inSubdir ? '../' : '';

  const nav = document.querySelector('.sidebar');
  if (!nav) return;

  let html = '';
  sections.forEach(s => {
    html += `<div class="nav-section">`;
    html += `<div class="nav-section-header"><i data-lucide="${s.icon}"></i> ${s.title} <span class="caret">&#9660;</span></div>`;
    html += `<ul class="nav-items">`;
    s.items.forEach(item => {
      const href = item.href.startsWith('../') ? item.href : prefix + item.href;
      const active = path.endsWith(item.href.replace(prefix, '')) ? ' class="active"' : '';
      html += `<li><a href="${href}"${active}>${item.label}</a></li>`;
    });
    html += `</ul></div>`;
  });

  nav.innerHTML = html;

  // Re-run Lucide after injecting icons
  if (window.lucide) lucide.createIcons();
})();
