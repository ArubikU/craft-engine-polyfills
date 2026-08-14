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
      { label: ":renderer", href: "block-behaviors/renderer.html" },
      { label: ":data_machine", href: "block-behaviors/data-machine.html" },
      { label: ":data_multiblock", href: "block-behaviors/data-multiblock.html" },
      { label: ":data_motor", href: "block-behaviors/data-motor.html" },
      { label: ":gas_pump", href: "block-behaviors/gas-pump.html" },
      { label: ":machine_pump", href: "block-behaviors/machine-pump.html" },
      { label: ":pipe_block", href: "block-behaviors/pipe-block.html" },
      { label: ":valve_block", href: "block-behaviors/valve-block.html" },
      { label: ":fluid_tank_block", href: "block-behaviors/fluid-tank-block.html" },
      { label: ":gas_provider", href: "block-behaviors/gas-provider.html" },
      { label: ":fan_block", href: "block-behaviors/fan-block.html" },
      { label: ":spike_block", href: "block-behaviors/spike-block.html" },
      { label: ":magnet_block", href: "block-behaviors/magnet-block.html" },
      { label: ":spreading_block", href: "block-behaviors/spreading-block.html" },
      { label: ":bubble_block", href: "block-behaviors/bubble-block.html" },
      { label: ":bush_block", href: "block-behaviors/bush-block.html" },
      { label: ":vertical_crop", href: "block-behaviors/vertical-crop-block.html" },
      { label: ":tearing_crop", href: "block-behaviors/tearing-crop-block.html" },
      { label: ":change_over_time", href: "block-behaviors/change-over-time-block.html" },
      { label: ":storage_block", href: "block-behaviors/storage-block.html" },
      { label: ":custom_crafter", href: "block-behaviors/custom-crafter.html" },
      { label: ":redstone_operator", href: "block-behaviors/redstone-operator.html" },
      { label: ":redstone_controller", href: "block-behaviors/redstone-controller.html" },
      { label: ":chainery_block", href: "block-behaviors/chainery-block.html" },
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

  // Determine base path: from /docs/ pages prefix='', from /docs/block-behaviors/ prefix='../'
  // For links going to /api/ (start with ../), add extra ../ when in subdir
  const path = window.location.pathname;
  const inSubdir = path.includes('/block-behaviors/');
  const prefix = inSubdir ? '../' : '';  // for docs-internal links
  const rootPrefix = inSubdir ? '../../' : '../';  // for links going to /api/ or /index.html

  const nav = document.querySelector('.sidebar');
  if (!nav) return;

  let html = '';
  sections.forEach(s => {
    html += `<div class="nav-section">`;
    html += `<div class="nav-section-header"><i data-lucide="${s.icon}"></i> ${s.title} <span class="caret">&#9660;</span></div>`;
    html += `<ul class="nav-items">`;
    s.items.forEach(item => {
      let href;
      if (item.href.startsWith('../')) {
        // Link goes outside /docs/ (e.g. ../api/index.html) — use rootPrefix
        href = rootPrefix + item.href.substring(3); // strip ../ and prepend correct root
      } else {
        href = prefix + item.href;
      }
      const active = path.endsWith(item.href.replace(prefix, '')) ? ' class="active"' : '';
      html += `<li><a href="${href}"${active}>${item.label}</a></li>`;
    });
    html += `</ul></div>`;
  });

  nav.innerHTML = html;

  // Re-run Lucide after injecting icons
  if (window.lucide) lucide.createIcons();
})();
