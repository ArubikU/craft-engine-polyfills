#!/bin/bash
# generate_api_docs.sh — Generates /api/ JavaDoc-style HTML pages from Java source
# Reads src/main/java on the main branch, generates HTML to the current directory (gh-pages)
#
# Usage (on gh-pages branch):
#   bash generate_api_docs.sh
#
# Requires: bash, git (to read from migrate/craftengine-26 branch)

set -e

BRANCH="migrate/craftengine-26"
SRC_BASE="src/main/java/dev/arubik/craftengine"
API_DIR="./api"
CSS_REL_PREFIX=""  # computed per file depth

# Packages to document
PACKAGES=(
  "machine:MachineDefinition MachineDefinitionLoader"
  "machine/render:RendererSpec RendererManager ModelRendersDriven BetterModelMachineRenderer ModelEngineMachineRenderer ParticleUtils SpeedFormula"
  "machine/render/formula:PolyFormula PolyValue PolyContext PolyClass PolyScript PolyScriptRegistry BlockClass WorldClass PlayerClass EntityClass MachineClass MultiBlockClass ContraptionClass InventoryClass ItemClass UpgradesClass FluidTanksClass GasTanksClass TankAccessClass LocationClass WorkbenchClass"
  "machine/render/variable:MachineRenderContext VariableSpec"
  "machine/attribute:MachineAttributes"
  "machine/block/entity:DataMachineBlockEntity DataMultiBlockMachineBlockEntity"
  "contraption/core:ContraptionEntity ContraptionState ContraptionLevel"
  "contraption/element:ContraptionElement ElementTypes ContraptionMachineRendererElement"
  "contraption/config:ContraptionConfig"
  "fluid:FluidTank"
  "gas:GasTank"
  "crafting:WorkbenchDefinition WorkbenchLoader"
  "multiblock:MultiBlockDefinition MultiBlockSchema"
  "block/behavior:RendererBehavior"
  "contraption/api:ContraptionHitboxProvider ContraptionTickable ContraptionType ContraptionTypeRegistry PowerConsumer PowerSource"
  "contraption:ContraptionWorlds MovementBehavior MovementContext ContraptionInteractionListener"
)

# HTML header template
header_template() {
  local title="$1"
  local pkg="$2"
  local class="$3"
  local depth="$4"
  local css_path=""
  for ((i=0; i<depth; i++)); do css_path="../$css_path"; done
  css_path="${css_path}docs/assets/style.css"

  cat <<HEADER
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>${class} — CEP API</title>
<link rel="stylesheet" href="${css_path}">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github-dark.min.css">
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<style>
.api-content .method-entry { border-left: 3px solid #30363d; padding: 0.75rem 1rem; margin: 0.75rem 0; background: #161b22; border-radius: 0 6px 6px 0; }
.api-content .method-sig { font-family: 'SFMono-Regular', monospace; font-size: 0.85rem; color: #79c0ff; margin-bottom: 0.4rem; }
.api-content .method-desc { color: #8b949e; font-size: 0.85rem; }
.api-content .class-header { margin-bottom: 2rem; }
.api-content .class-header .mod { color: #79c0ff; font-size: 0.85rem; }
.api-content .class-header .kind { color: #ff7b72; font-weight: 600; }
.api-content .class-header .name { color: #f0f6fc; font-size: 1.8rem; font-weight: 700; }
.api-content .pkg-path { color: #8b949e; font-size: 0.8rem; margin-bottom: 0.5rem; }
</style>
</head>
<body>
<div class="topbar" role="banner">
  <a href="${css_path%docs/assets/style.css}index.html" class="logo"><i data-lucide="code-2"></i> CraftEngine Polyfills</a>
  <div class="spacer"></div>
  <a href="${css_path%docs/assets/style.css}api/index.html" style="color:var(--accent)">API</a>
  <a href="https://github.com/Arubik/craft-engine-polyfills" class="gh-link"><i data-lucide="github"></i></a>
</div>
<div class="layout">
<nav class="sidebar">
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="package"></i> formula</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/formula/PolyFormula.html">PolyFormula</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/formula/PolyValue.html">PolyValue</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/formula/PolyContext.html">PolyContext</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/formula/PolyScript.html">PolyScript</a></li>
    </ul>
  </div>
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="package"></i> render</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/RendererSpec.html">RendererSpec</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/render/RendererManager.html">RendererManager</a></li>
    </ul>
  </div>
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="package"></i> machine</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/machine/MachineDefinition.html">MachineDefinition</a></li>
    </ul>
  </div>
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="package"></i> contraption</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/contraption/core/ContraptionEntity.html">ContraptionEntity</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/contraption/core/ContraptionState.html">ContraptionState</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/contraption/api/ContraptionType.html">ContraptionType</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/contraption/api/PowerSource.html">PowerSource</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/contraption/api/PowerConsumer.html">PowerConsumer</a></li>
    </ul>
  </div>
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="package"></i> fluid / gas</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/fluid/FluidTank.html">FluidTank</a></li>
      <li><a href="${css_path%docs/assets/style.css}api/dev/arubik/craftengine/gas/GasTank.html">GasTank</a></li>
    </ul>
  </div>
  <div class="nav-section">
    <div class="nav-section-header"><i data-lucide="arrow-left"></i> Navigation</div>
    <ul class="nav-items">
      <li><a href="${css_path%docs/assets/style.css}api/index.html">API Index</a></li>
      <li><a href="${css_path%docs/assets/style.css}docs/getting-started.html">Docs</a></li>
      <li><a href="${css_path%docs/assets/style.css}index.html">Home</a></li>
    </ul>
  </div>
</nav>
<main class="content api-content" style="padding-top:2rem">
  <div class="breadcrumbs">
    <a href="${css_path%docs/assets/style.css}api/index.html">API</a><span class="sep">/</span>
    <span>${pkg}</span><span class="sep">/</span>
    <span>${class}</span>
  </div>
  <div class="pkg-path">dev.arubik.craftengine.${pkg//\//.}</div>
HEADER
}

# HTML footer template
footer_template() {
  cat <<FOOTER
</main>
</div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script>hljs.highlightAll(); if(window.lucide) lucide.createIcons();</script>
</body>
</html>
FOOTER
}

echo "=== Generating API docs from branch: $BRANCH ==="

for entry in "${PACKAGES[@]}"; do
  IFS=':' read -r pkg classes <<< "$entry"

  for class in $classes; do
    src_path="$SRC_BASE/$pkg/$class.java"
    out_dir="$API_DIR/dev/arubik/craftengine/$pkg"
    out_file="$out_dir/$class.html"

    mkdir -p "$out_dir"

    # Read source from git
    source_content=$(git show "$BRANCH:$src_path" 2>/dev/null || echo "")
    if [ -z "$source_content" ]; then
      echo "  SKIP: $src_path (not found)"
      continue
    fi

    # Extract class declaration line
    class_decl=$(echo "$source_content" | grep -E "^public (final |sealed |abstract )?(class|interface|record|enum) " | head -1)

    # Extract public methods (match any indentation level)
    public_methods=$(echo "$source_content" | grep -E "^\s*public\s+" | grep -v "^public\s+(final|sealed|abstract)?\s*(class|interface|record|enum)" | grep -v "import" | head -50)

    # Extract javadoc (first /** ... */ block)
    class_javadoc=$(echo "$source_content" | sed -n '/\/\*\*/,/\*\//p' | head -20 | sed 's/^ \* //' | grep -v '^\/' | grep -v '^\*')

    # Compute depth for CSS path (api/dev/arubik/craftengine/pkg = 4 + pkg_depth)
    pkg_depth=$(echo "$pkg" | tr -cd '/' | wc -c)
    depth=$((5 + pkg_depth))

    echo "  GEN: $out_file"

    {
      header_template "$class" "$pkg" "$class" "$depth"

      echo "  <div class=\"class-header\">"
      if [ -n "$class_decl" ]; then
        echo "    <div><span class=\"mod\">$(echo "$class_decl" | sed 's/public //' | sed "s/ $class.*//")</span> <span class=\"kind\">$(echo "$class_decl" | grep -oP '(class|interface|record|enum)')</span> <span class=\"name\">$class</span></div>"
      else
        echo "    <div><span class=\"name\">$class</span></div>"
      fi
      if [ -n "$class_javadoc" ]; then
        echo "    <p style=\"color:var(--text-muted);margin-top:0.5rem\">$(echo "$class_javadoc" | head -3 | tr '\n' ' ')</p>"
      fi
      echo "  </div>"

      # Source overview
      echo "  <h2>Source</h2>"
      echo "  <pre><code class=\"language-java\">$(echo "$class_decl")</code></pre>"

      # Methods
      if [ -n "$public_methods" ]; then
        echo "  <h2>Public API</h2>"
        echo "$public_methods" | while IFS= read -r line; do
          clean=$(echo "$line" | sed 's/^\s*//' | sed 's/{$//')
          if [ -n "$clean" ]; then
            echo "  <div class=\"method-entry\">"
            echo "    <div class=\"method-sig\">$clean</div>"
            echo "  </div>"
          fi
        done
      fi

      footer_template
    } > "$out_file"
  done
done

echo "=== Done. Files generated in $API_DIR/ ==="
echo "Commit with: git add api/ && git commit -m 'docs: generated API reference'"
