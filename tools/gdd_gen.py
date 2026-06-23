#!/usr/bin/env python3
"""Generate the CML SMP Game Bible (GDD) docs with beautiful_table bricks."""
import io, os

WS = r"D:\Piero\Downloads\cmlfactory\0 GDD"


def q(s):
    return '"' + str(s).replace("\\", "\\\\").replace('"', '\\"') + '"'


def bt(title, cols, rows):
    """cols=[(id,name,type)]  rows=[{cid:val}]  -> beautiful_table content string."""
    colstr = ", ".join("(id = %s, name = %s, type = %s)" % (q(c[0]), q(c[1]), c[2]) for c in cols)
    rstr = []
    for i, r in enumerate(rows):
        cells = ", ".join("%s = %s" % (c[0], (q(r[c[0]]) if not (c[2] == "number" and str(r.get(c[0], "")).replace('.', '', 1).isdigit()) else r[c[0]])) for c in cols if c[0] in r)
        rstr.append("(id = %s, cells = (%s))" % (q("r%d" % i), cells))
    return "title = %s, columns = [%s], rows = [%s]" % (q(title), colstr, ", ".join(rstr))


def B(bid, kind, pos, content):
    return "[[bricks]]\nid = %s\nkind = %s\nposition = %d\ncontent = (%s)\n" % (q(bid), kind, pos, content)


def text(bid, pos, style, md):
    return B(bid, "text", pos, "displayStyle = %s, markdown = %s" % (style, q(md)))


def code(bid, pos, md):
    return B(bid, "text", pos, "displayStyle = code, markdown = %s" % q(md))


def callout(bid, pos, icon, md):
    return B(bid, "callout", pos, "icon = %s, markdown = %s" % (q(icon), q(md)))


def beauty(bid, pos, title, cols, rows):
    return B(bid, "beautiful_table", pos, bt(title, cols, rows))


def diagram(bid, pos, md):
    # text brick (paragraph) whose markdown holds a live mermaid/chart fence
    return B(bid, "text", pos, "displayStyle = paragraph, markdown = %s" % q(md))


def widget(bid, pos, html):
    return B(bid, "widget", pos, "widgetLang = html, widgetArgs = (), code = %s" % q(html))


RECIPE_WIDGET = """<style>
body{margin:0;font-family:monospace;background:#262626;color:#eee;padding:6px}
h4{color:#ffcc33;margin:10px 0 2px;font-size:13px}
.tt{font-size:10px;color:#7fd4ff;margin:2px 0 5px}
.rc{display:inline-flex;align-items:center;margin:6px 10px 6px 0;padding:8px;background:#343434;border:2px solid #555;border-radius:6px;vertical-align:top}
.grid{display:grid;grid-gap:3px}
.slot,.res{background:#8b8b8b;border:2px solid #373737;display:flex;align-items:center;justify-content:center;font-size:8px;text-align:center;color:#1c1c1c;padding:1px;box-sizing:border-box;line-height:1.05}
.slot{width:44px;height:44px}
.res{width:54px;height:54px;font-size:9px;background:#9b9b6b}
.empty{background:#6f6f6f;border-color:#5a5a5a}
.arrow{font-size:22px;margin:0 12px;color:#bbb}
</style>
<div id=cr></div>
<script>
const R=[
 {t:'Copper Furnace',tool:'vapor_furnace_mk1_blueprint',rows:['III','IGI'],keys:{I:'Iron',G:'Iron Gear'},out:'Copper Furnace'},
 {t:'Gas Motor Mk1',tool:'vapor_motor_blueprint',rows:['CIC','IGI'],keys:{C:'Copper',I:'Iron',G:'Iron Gear'},out:'Gas Motor'},
 {t:'Conveyor x4',tool:'conveyor_blueprint',rows:['KKK','ICI'],keys:{K:'Kelp',I:'Iron',C:'Copper'},out:'Conveyor x4'},
 {t:'Depot',tool:'depot_blueprint',rows:['III','CGC'],keys:{I:'Iron',C:'Copper',G:'Iron Gear'},out:'Depot'},
 {t:'Funnel',tool:'funnel_blueprint',rows:['CCC','CGC'],keys:{C:'Copper',G:'Iron Gear'},out:'Funnel'},
 {t:'Copper Wire x3 (+Brass 30%)',tool:'wire_blueprint',rows:['CCC',' G '],keys:{C:'Copper',G:'Copper Gear'},out:'Copper Wire'},
 {t:'Stone Gear',tool:'mesa vanilla',rows:[' S ','SNS',' S '],keys:{S:'Stone',N:'Nugget'},out:'Stone Gear'},
 {t:'Iron Gear',tool:'mesa vanilla',rows:[' I ','INI',' I '],keys:{I:'Iron',N:'Nugget'},out:'Iron Gear'},
 {t:'Aluminum Block',tool:'mesa vanilla',rows:['AAA','AAA','AAA'],keys:{A:'Al Ingot'},out:'Al Block'}
];
function cell(ch,keys){if(!ch||ch===' ')return '<div class=\\'slot empty\\'></div>';return '<div class=slot>'+(keys[ch]||ch)+'</div>';}
let h='';
for(const r of R){
 const cols=r.rows[0].length;
 let g='<div class=grid style=\\'grid-template-columns:repeat('+cols+',44px)\\'>';
 for(const row of r.rows)for(const ch of row)g+=cell(ch,r.keys);
 g+='</div>';
 h+='<h4>'+r.t+'</h4><div class=tt>'+(r.tool==='mesa vanilla'?'Mesa de crafteo':'Workbench + '+r.tool)+'</div>';
 h+='<div class=rc>'+g+'<div class=arrow>&#10148;</div><div class=res>'+r.out+'</div></div>';
}
document.getElementById('cr').innerHTML=h;
</script>"""


def write(path, title, bricks):
    head = "#killio kd 2026-v1\nid = %s\ntitle = %s\n\n" % (q("0 GDD/" + path), q(title))
    io.open(os.path.join(WS, path), "w", encoding="utf-8").write(head + "\n".join(bricks))
    print("wrote", path)


# ============ 1 VISION ============
v_loop = """```mermaid
flowchart TD
  A[1 Recolectar cobre hierro madera agua] --> B[2 Energia: Iron Pump a Copper Furnace a Steam]
  B --> C[3 Potencia: Steam a Gas Motor a RPM y SU]
  C --> D[4 Procesar: Crusher Smeltery Fan Refinery]
  D --> E[5 Automatizar: Conveyor Pipe Funnel Depot]
  E --> F[6 Avanzar edad: Copper a Steel a Aluminum]
  F --> G[Endgame: fabrica auto-suficiente en bucle cerrado]
```"""

pillars = [
    {"n": "1", "p": "Industria tangible", "d": "Cada maquina es un bloque visible que trabaja: ves el vapor salir, la cinta moverse, el crusher girar. Cero menus abstractos infinitos."},
    {"n": "2", "p": "Energia en capas fisicas", "d": "Tres redes reales conviven: Vapor->RPM/SU (mecanica), Gas a presion (steam/nitrogeno) y Fluidos con presion literal (suben por bombeo). No es energia universal."},
    {"n": "3", "p": "Progresion por materiales", "d": "3 edades: Copper -> Steel -> Aluminum. Cada salto da EFICIENCIA (menos consumo, mas velocidad/alcance), no poder bruto."},
    {"n": "4", "p": "Mundo cozy con identidad", "d": "Maderas custom, industria de sal, mobs variantes, economia (Cemelita) y cosmeticos rodean la fabrica para que el servidor se sienta vivo."},
]
write("1 Vision.kd", "1 · Vision General", [
    text("v-h", 0, "heading", "# 1 · Vision General — The Elevator Pitch"),
    callout("v-pitch", 1, "factory", "**CML SMP** es un servidor survival de *industria victoriana a vapor*. El jugador construye fabricas fisicas y visibles —tuberias, tanques, vapor, engranajes— donde cada maquina es un bloque que trabaja a la vista. La fabrica **existe en el mundo**, no en menus."),
    text("v-loop-h", 2, "heading", "## Core Loop"),
    diagram("v-loop", 3, v_loop),
    text("v-end", 4, "paragraph", "**Endgame:** no es un jefe ni un item final. Es una **planta industrial victoriana funcionando sola**. El estatus se mide por la escala y elegancia de tu fabrica, no por stats de combate."),
    text("v-pill-h", 5, "heading", "## Pilares de diseno"),
    beauty("v-pill", 6, "4 Pilares de diseno",
           [("n", "#", "text"), ("p", "Pilar", "text"), ("d", "Significado para el jugador", "text")],
           pillars),
    callout("v-diff", 7, "sparkles", "**Diferenciador:** se siente como industria *victoriana fisica* (vapor, presion, tuberias, materiales reales) — NO como Create/Mekanism modernos. El foco es la arquitectura visible de la fabrica."),
])

# ============ 2 ITEM CATALOG ============
RC = ("r", "Rareza", "text")
machines = [
    {"i": "Copper Furnace", "r": "Common", "rol": "Generador de vapor", "e": "Agua->Steam (top). Tambien cuece y produce Steel con lava"},
    {"i": "Gas Motor Mk1", "r": "Common", "rol": "Generador de RPM", "e": "Gas -> RPM por la cara frontal (steam 32rpm/64su)"},
    {"i": "Crusher MK1", "r": "Common", "rol": "Procesador (RPM)", "e": "Tritura mena -> raw x2-3 + outputs por probabilidad"},
    {"i": "Copper Fan", "r": "Common", "rol": "Procesador (gas)", "e": "Sopla columna; el bloque-proceso elige familia (smelt/blast/wash/freeze)"},
    {"i": "Iron Pump", "r": "Common", "rol": "Bomba de fluidos", "e": "Sube agua/lava con presion 8 (8 bloques de altura)"},
    {"i": "Gas Pump", "r": "Uncommon", "rol": "Extractor de gas", "e": "Extrae nitrogeno de un vein de Nitrogenated Cal"},
    {"i": "Refinery (+Mixer)", "r": "Rare", "rol": "Multibloque washer", "e": "Refina bauxita con agua + steam"},
    {"i": "Smeltery", "r": "Uncommon", "rol": "Fundidor (RPM)", "e": "Polvo purificado -> lingote"},
    {"i": "Pressurizer Well", "r": "Rare", "rol": "Multibloque 3x3x6", "e": "Sube limite de bombas de un vein de 1 a 5"},
]
logistics = [
    {"i": "Conveyor Belt", "r": "Common", "rol": "Transporte items", "e": "64 rpm base, 4 items/segmento. Varita POINTED/MAGIC"},
    {"i": "Conveyor Splitter / Merger", "r": "Common", "rol": "Ruteo items", "e": "Divide / combina lineas"},
    {"i": "Copper Pipe", "r": "Common", "rol": "Transporte fluido", "e": "1000 mB cap, 100 mB/t. Varita auto-ruteo 3D"},
    {"i": "Steel Pipe", "r": "Uncommon", "rol": "Transporte gas", "e": "Igual que copper pero para gases"},
    {"i": "Copper Tank", "r": "Common", "rol": "Almacen fluido", "e": "Niveles visibles 0-4 (agua/lava)"},
    {"i": "Funnel / Floor / Ceiling", "r": "Common", "rol": "I/O contenedor", "e": "Puente cinta<->cofre. Click der. alterna in/out"},
    {"i": "Depot", "r": "Common", "rol": "Buffer", "e": "27-54 slots, senal de comparador, sin GUI"},
    {"i": "Engineer's Workbench", "r": "Common", "rol": "Estacion de crafteo", "e": "Doble bloque; arma maquinas con blueprints"},
]
components = [
    {"i": "Stone / Copper / Iron Gear", "r": "Common", "rol": "Steam-tech component", "e": "Componente base de casi toda maquina"},
    {"i": "Copper Wire", "r": "Common", "rol": "Steam-tech component", "e": "Output del Wire Blueprint"},
    {"i": "Brass Shavings", "r": "Common", "rol": "Machining byproduct", "e": "Subproducto (30% del Wire Blueprint)"},
    {"i": "Blueprints (x8)", "r": "Uncommon", "rol": "Herramienta reusable", "e": "Define la receta de maquina en el Workbench"},
    {"i": "Hammers (wood->netherite)", "r": "Common-Rare", "rol": "Ensamblador", "e": "Arman multibloques (Refinery, Pressurizer)"},
    {"i": "Upgrades (copper/iron/gold/diamond)", "r": "Uncommon-Rare", "rol": "Modulo de mejora", "e": "+slots / +generacion; diamante desbloquea Overclock"},
    {"i": "Scrapped Upgrade", "r": "Epic", "rol": "Modulo defectuoso", "e": "Buffa una stat, dana otra"},
    {"i": "Casts (iron/gold/diamond)", "r": "Uncommon-Rare", "rol": "Molde", "e": "Amplian capacidad de upgrades de una maquina"},
]
materials = [
    {"i": "Steel Ingot / Block", "r": "Uncommon", "age": "Steel", "src": "Copper Furnace: lava + raw_iron + coal"},
    {"i": "Bauxite Ore / Stone", "r": "Uncommon", "age": "Aluminum", "src": "Worldgen (ore overworld raro; stone en badlands)"},
    {"i": "Bauxite Scraping", "r": "Uncommon", "age": "Aluminum", "src": "Crusher de ore/stone"},
    {"i": "Purified Aluminum Dust", "r": "Rare", "age": "Aluminum", "src": "Refinery (scraping+quartz+agua+steam)"},
    {"i": "Aluminum Ingot / Block", "r": "Rare", "age": "Aluminum", "src": "Smeltery"},
    {"i": "Smooth Bauxite / Bricks", "r": "Common", "age": "Aluminum", "src": "Stonecutter / crafteo (decorativo)"},
    {"i": "Salt / Pink Salt", "r": "Common", "age": "World", "src": "Menas dripstone/stone/deepslate + ferns"},
    {"i": "Nitrogen (gas)", "r": "Uncommon", "age": "Steel+", "src": "Gas Pump sobre Nitrogenated Cal"},
]
gases = [
    {"i": "Steam", "r": "Common", "src": "Copper Furnace", "u": "Gas Motor, Refinery, Fan (smelt)"},
    {"i": "Heavy Steam", "r": "Uncommon", "src": "Tier superior", "u": "Gas Motor (+rpm/su), Fan (blast)"},
    {"i": "Nitrogen", "r": "Uncommon", "src": "Gas Pump / Cal", "u": "Fan (freeze), Gas Motor"},
]
world = [
    {"cat": "Maderas", "n": "4 sets +frozen", "ex": "Maple (+syrup), Ash, Palm, Frozen Ash", "ref": "World/Woods"},
    {"cat": "Carved / Mosaic / Crates", "n": "16/15/16", "ex": "por cada madera vanilla", "ref": "decorativos"},
    {"cat": "Terracotta Tiles", "n": "16", "ex": "todos los tintes", "ref": "building set"},
    {"cat": "Sal & Farming", "n": "~42", "ex": "bricks/tiles/crystals/ores, sea asparagus", "ref": "World/Salt"},
    {"cat": "Mobs (variantes)", "n": "32", "ex": "chicken/cow/pig skins", "ref": "Reference/Mobs"},
    {"cat": "Foreign (cosmetico/urbano)", "n": "~87", "ex": "HMC cosmetics, plushes, street furniture, Cemelita", "ref": "Foreign"},
]
write("2 Item Catalog.kd", "2 · Catalogo de Items", [
    text("c-h", 0, "heading", "# 2 · Catalogo de Items Custom y Sistemas"),
    callout("c-note", 1, "info", "Namespace: `cml:`. Rareza = **clasificacion de diseno** (por edad/rol), no atributo nativo. Lista exhaustiva de 390 ids en @[doc:5 Reference/Catalog - Industrial.kd:Reference / Catalogo]."),
    text("c-mach-h", 2, "heading", "## A · Maquinas (procesamiento y energia)"),
    beauty("c-mach", 3, "A · Maquinas", [("i", "Item", "text"), RC, ("rol", "Rol", "text"), ("e", "Efecto especial", "text")], machines),
    text("c-log-h", 4, "heading", "## B · Logistica"),
    beauty("c-log", 5, "B · Logistica", [("i", "Item", "text"), RC, ("rol", "Rol", "text"), ("e", "Efecto", "text")], logistics),
    text("c-comp-h", 6, "heading", "## C · Componentes de crafteo"),
    beauty("c-comp", 7, "C · Componentes", [("i", "Item", "text"), RC, ("rol", "Lore / rol", "text"), ("e", "Efecto", "text")], components),
    text("c-mat-h", 8, "heading", "## D · Materiales y recursos"),
    beauty("c-mat", 9, "D · Materiales", [("i", "Item", "text"), RC, ("age", "Edad", "text"), ("src", "Origen", "text")], materials),
    text("c-gas-h", 10, "heading", "## E · Energia / Fluidos (gases)"),
    beauty("c-gas", 11, "E · Gases", [("i", "Gas", "text"), RC, ("src", "Fuente", "text"), ("u", "Uso", "text")], gases),
    text("c-world-h", 12, "heading", "## F · Mundo & Foreign (resumen)"),
    beauty("c-world", 13, "F · Mundo & Foreign", [("cat", "Categoria", "text"), ("n", "Conteo", "text"), ("ex", "Ejemplos", "text"), ("ref", "Detalle", "text")], world),
])

# ============ 3 RECIPES ============
craft_matrix = """BLUEPRINTS (herramienta reusable)        ENGRANAJES
+---+---+---+                            +---+---+---+
| P | P | P |  P = Paper                 |   | S |   |  S=Stone N=Iron Nugget
| P |C/G| P |  C = Copper  G = Iron Gear | S | N | S |  Stone Gear
| P | P | P |  -> 1 Blueprint            |   | S |   |  (Copper: C+N, Iron: I+N)
+---+---+---+                            +---+---+---+

ALUMINUM BLOCK       STEEL PIPE (x4)        BAUXITE BRICKS (x4)
| A | A | A |        1 Steel Ingot ->      | B | B |  B=Smooth Bauxite
| A | A | A |  A=Ingot   4 Steel Pipe      | B | B |  -> 4 (o stonecut)
| A | A | A |  -> 1     (o Stonecutter)    +---+---+"""
wb_matrix = """Las MAQUINAS se arman en el Engineer's Workbench con un Blueprint
como herramienta (reusable). Grid 3 ancho x 2 alto:

COPPER FURNACE        GAS MOTOR MK1        CONVEYOR (x4)
| I | I | I |         | C | I | C |        | K | K | K |  K=Dried Kelp
| I | G | I |         | I | G | I |        | I | C | I |  I=Iron C=Copper G=Iron Gear

DEPOT                 FUNNEL               COPPER WIRE (x3 +Brass 30%)
| I | I | I |         | C | C | C |        | C | C | C |
| C | G | C |         | C | G | C |        |   | G |   |  G=Copper Gear"""
prog = """```mermaid
flowchart LR
  Water[Agua] --> Pump[Iron Pump] --> Furnace[Copper Furnace] --> Steam
  Charcoal --> Furnace
  Steam --> Motor[Gas Motor] --> RPM
  RPM --> Crusher
  RPM --> Smeltery
  Steam --> Refinery
  Lava --> Furnace2[Copper Furnace] --> Steel[Steel Ingot]
  Bauxite[Bauxite Ore] --> Crusher --> Scraping --> Refinery --> Dust[Purified Dust] --> Smeltery --> Aluminum[Aluminum Ingot]
  Cal[Nitrogenated Cal] --> GasPump[Gas Pump] --> Nitrogen --> Fan
```"""
mach_rec = [
    {"m": "Copper Furnace", "in": "500 mB agua + fuel", "out": "500 mB steam", "t": "120t", "p": "combustible"},
    {"m": "Copper Furnace", "in": "1 raw_iron + fuel", "out": "1 iron_ingot", "t": "200t", "p": "combustible"},
    {"m": "Copper Furnace (STEEL)", "in": "1000mB lava + 4 raw_iron + 4 coal", "out": "1 steel_ingot", "t": "150t", "p": "lava=calor"},
    {"m": "Crusher", "in": "1 mena (cu/fe/au)", "out": "2-3 raw + bonus 25%", "t": "100t", "p": "16 rpm / 8 su"},
    {"m": "Crusher", "in": "1 bauxite_ore", "out": "2 scraping (+1 @25%)", "t": "100t", "p": "16 rpm / 8 su"},
    {"m": "Refinery", "in": "3 scraping + quartz + 1000mB agua + 100mB steam", "out": "3 purified dust", "t": "200t", "p": "steam + agua"},
    {"m": "Smeltery", "in": "1 purified dust", "out": "1 aluminum_ingot", "t": "150t", "p": "16 rpm / 8 su"},
    {"m": "Fan", "in": "cobblestone / clay / gravel / ice", "out": "stone / brick / flint / packed_ice", "t": "60-120t", "p": "gas x bloque"},
]
gating = [
    {"g": "Steam", "n": "Iron Pump (agua) + Copper Furnace + combustible"},
    {"g": "RPM (Crusher/Smeltery)", "n": "Steam + Gas Motor"},
    {"g": "Steel", "n": "Copper Furnace + lava + coal"},
    {"g": "Aluminum", "n": "Crusher + Refinery (multibloque, hammer) + Smeltery + nitrogeno/steam"},
    {"g": "Overclock", "n": "Upgrade de Diamante instalado"},
    {"g": "Vein nitrogeno x5 bombas", "n": "Pressurizer Well (3x3x6)"},
]
write("3 Recipes.kd", "3 · Recetas y Procesamiento", [
    text("r-h", 0, "heading", "# 3 · Sistema de Recetas y Procesamiento"),
    callout("r-note", 1, "list", "Indice completo (171 outputs, made-by / used-in) en @[doc:5 Reference/Recipes - Machines.kd:Reference / Recetas]. Abajo: recetas clave en formato matriz."),
    text("r-craft-h", 2, "heading", "## 3.1 · Matriz de Crafteo (mesa vanilla 3x3)"),
    code("r-craft", 3, craft_matrix),
    text("r-wb-h", 4, "heading", "## 3.2 · Ensamblaje de Maquinas (Workbench 3x2 + Blueprint)"),
    code("r-wb", 5, wb_matrix),
    text("r-mach-h", 6, "heading", "## 3.3 · Procesamiento de Maquina (requisitos)"),
    beauty("r-mach", 7, "Recetas de maquina", [("m", "Maquina", "text"), ("in", "Entrada", "text"), ("out", "Salida", "text"), ("t", "Tiempo", "text"), ("p", "Energia", "text")], mach_rec),
    text("r-prog-h", 8, "heading", "## 3.4 · Progresion de materiales"),
    diagram("r-prog", 9, prog),
    text("r-gate-h", 10, "heading", "## 3.5 · Gating"),
    beauty("r-gate", 11, "Que desbloquea que", [("g", "Para tener...", "text"), ("n", "Necesitas antes...", "text")], gating),
    text("r-ui-h", 12, "heading", "## 3.6 · Recetas visuales (UI de crafteo)"),
    widget("r-ui", 13, RECIPE_WIDGET),
])

# ============ 4 MECHANICS ============
pc_gas = """# GAS PUMP — extraccion de nitrogeno
ON tick(gas_pump):
  IF block_below IS nitrogenated_cal AND has_fuel:
     vein   = flood_fill(cal_blocks)          # full=4, mid=2, empty=1 puntos
     points = min(8, sum(vein.points))        # tope 8 por bomba
     IF pump_is_owner(vein):                  # 1 bomba/vein (5 con Pressurizer)
        buffer += points * 10 mB  every 20t   # buffer interno 4000 mB
        push nitrogen out TOP face"""
pc_fan = """# COPPER FAN — el bloque-proceso decide el resultado
ON gas_available(fan):
  blow_air_column(facing, reach_by_gas)       # steam=5, heavy=7, nitrogen=8
  block = first_process_block_in_column()
  family = SWITCH block:
     fire/campfire -> SMELTING
     lava          -> BLASTING
     water         -> WASHING
     (nitrogen)    -> FREEZING
  FOR item IN airflow:
     IF recipe(item, family) EXISTS: convert(item)"""
pc_pump = """# IRON PUMP / PRESION DE FLUIDOS
ON tick(pump):
  pull fluid FROM below                        # lava: area 32; agua: 1 fuente
  stamp pressure = 8
  push UP: pressure -= 1 per block             # sube ~8 bloques
  push DOWN / horizontal: pressure unchanged   # viaja sin perder fuerza"""
pc_steel = """# COPPER FURNACE — vapor + acero
ON tick(furnace) WHILE has_fuel:
  IF tank == water: water 500mB -> steam 500mB (out TOP)   # 120t
  IF tank == lava AND inputs == [4 raw_iron, 4 coal]:
     -> 1 steel_ingot (out BOTTOM)                          # lava ES el calor"""
pc_multi = """# MULTIBLOQUE (Refinery / Pressurizer Well)
ON right_click(structure_block) WITH hammer:
  IF shape_matches(schema):
     core.role = CORE ; otros.role = PART
     machine.start()
ON break(any_block): machine.disassemble()"""
pc_power = """# POTENCIA MECANICA (RPM / SU, estilo Create)
ON tick(gas_motor) WHILE gas_available:
  emit RPM out facing                          # steam 32rpm, heavy 48rpm
FOR machine IN rpm_network:                    # crusher, smeltery, cintas
  total_su += machine.stress
  IF total_su > motor.su_cap: network.stall()  # se detiene si excede"""
systems = [
    {"s": "Edades", "d": "Copper -> Steel -> Aluminum. Gating por material; cada edad = eficiencia, no poder."},
    {"s": "Upgrades / Overclock", "d": "Modulos copper/iron/gold/diamond (+slots/+generacion). Solo diamante desbloquea Overclock (mas velocidad)."},
    {"s": "Stress (RPM/SU)", "d": "Cada maquina/cinta impone SU. Si el SU total supera el cap del motor, la linea se detiene. Ratio base: 1 horno -> 1 motor -> 1 crusher."},
    {"s": "Presion de fluidos", "d": "Los fluidos solo SUBEN si tienen presion (bomba estampa 8; -1 por bloque). Mecanica fisica, no instantanea."},
    {"s": "Veins de gas", "d": "Nitrogenated Cal NO se agota (estilo Satisfactory). Riqueza por bloque. 1 bomba/vein; Pressurizer Well la sube a 5."},
    {"s": "Economia", "d": "Moneda Cemelita (bunch/pack/sack) + tickets (cosmetic/pet, common/silver/golden). Capa social/comercial."},
    {"s": "Cosmeticos (HMC)", "d": "Backpacks, jetpacks, wings, hats, balloons, kites — capa de expresion del jugador, sin impacto en balance."},
]
write("4 Mechanics.kd", "4 · Mecanicas de Juego", [
    text("m-h", 0, "heading", "# 4 · Mecanicas de Juego (pseudo-codigo)"),
    callout("m-note", 1, "code", "Logica resumida como pseudo-codigo digerible. Constantes reales del codigo (`fluid/`, `gas/`, `rotation/`, `conveyor/`)."),
    text("m-ev-h", 2, "heading", "## 4.1 · Eventos clave"),
    code("m-gas", 3, pc_gas),
    code("m-fan", 4, pc_fan),
    code("m-pump", 5, pc_pump),
    code("m-steel", 6, pc_steel),
    code("m-multi", 7, pc_multi),
    code("m-power", 8, pc_power),
    text("m-sys-h", 9, "heading", "## 4.2 · Sistemas de juego"),
    beauty("m-sys", 10, "Sistemas", [("s", "Sistema", "text"), ("d", "Descripcion", "text")], systems),
])
# ============ 5 AGES ============
ages_flow = """```mermaid
flowchart LR
  C[Copper Age - vapor RPM logistica] --> S[Steel Age - multibloques acero]
  S --> A[Aluminum Age - eficiencia bucle cerrado]
```"""
copper_tbl = [
    {"i": "Copper Furnace", "k": "Maquina", "n": "Genera vapor; cuece; base de Steel"},
    {"i": "Gas Motor Mk1", "k": "Maquina", "n": "Gas a RPM"},
    {"i": "Crusher MK1 / Copper Fan", "k": "Maquina", "n": "Procesado RPM / gas"},
    {"i": "Iron Pump / Gas Pump", "k": "Maquina", "n": "Fluidos / nitrogeno"},
    {"i": "Conveyor / Pipe / Funnel / Depot / Tank", "k": "Logistica", "n": "Transporte y buffers"},
    {"i": "Engineer's Workbench", "k": "Estacion", "n": "Arma maquinas con blueprints"},
    {"i": "Gears / Wire / Casts / Upgrades / Hammers", "k": "Componente", "n": "Crafting steam-tech"},
    {"i": "Copper / Iron / Charcoal / Steam / Nitrogen", "k": "Material", "n": "Recursos base"},
]
steel_tbl = [
    {"i": "Steel Ingot / Steel Block", "k": "Material", "n": "Copper Furnace: lava + raw_iron + coal"},
    {"i": "Refinery (+ Mixer)", "k": "Multibloque", "n": "Washer/refinador de bauxita"},
    {"i": "Smeltery", "k": "Maquina", "n": "Polvo a lingote (RPM)"},
    {"i": "Pressurizer Well", "k": "Multibloque 3x3x6", "n": "Sube limite de bombas del vein 1 a 5"},
    {"i": "Steel Pipe", "k": "Logistica", "n": "Transporte de gas"},
    {"i": "Steel Hammer", "k": "Componente", "n": "Ensambla multibloques"},
]
alu_tbl = [
    {"i": "Bauxite Ore / Stone", "k": "Material", "n": "Worldgen (overworld / badlands)"},
    {"i": "Bauxite Scraping", "k": "Material", "n": "Crusher"},
    {"i": "Purified Aluminum Dust", "k": "Material", "n": "Refinery"},
    {"i": "Aluminum Ingot / Block", "k": "Material", "n": "Smeltery"},
    {"i": "Smooth Bauxite / Bauxite Bricks", "k": "Building", "n": "Stonecutter / crafteo"},
    {"i": "Aluminum Hammer", "k": "Componente", "n": "Ensambla multibloques"},
]
write("5 Ages.kd", "5 · Las 3 Edades", [
    text("a-h", 0, "heading", "# 5 · Las 3 Edades"),
    callout("a-i", 1, "milestone", "La progresion NO es por poder bruto sino por **eficiencia**. Cada edad reusa las mecanicas de la anterior y desbloquea materiales + multibloques nuevos."),
    diagram("a-flow", 2, ages_flow),
    text("a-c-h", 3, "heading", "## Copper Age — Supervivencia industrial"),
    callout("a-c-d", 4, "flame", "**Tema:** arrancar de cero. Introduce las 3 redes (vapor->RPM, gas, fluidos con presion). **Idea de diseno:** que el jugador VEA su primera fabrica trabajar; deco pendiente: Steam Vent (particulas), Pressure Gauge (aguja LOW/MED/HIGH), Copper Grate/Catwalk/Beam."),
    beauty("a-c-t", 5, "Copper Age — contenido", [("i", "Item", "text"), ("k", "Tipo", "text"), ("n", "Nota", "text")], copper_tbl),
    text("a-s-h", 6, "heading", "## Steel Age — Industria pesada"),
    callout("a-s-d", 7, "factory", "**Tema:** multibloques y gas a presion. El Steel se hace en el MISMO Copper Furnace (lava=calor), recompensando al jugador por dominar fluidos. **Ideas:** Riveted Steel, Steel Support Beam, Steel Catwalk, Industrial Window, Factory Floor; futuro Steel Furnace MK2 con Salt Bricks."),
    beauty("a-s-t", 8, "Steel Age — contenido", [("i", "Item", "text"), ("k", "Tipo", "text"), ("n", "Nota", "text")], steel_tbl),
    text("a-a-h", 9, "heading", "## Aluminum Age — Optimizacion"),
    callout("a-a-d", 10, "zap", "**Tema:** eficiencia, NO mas poder. Cierra el bucle: la cadena de aluminio usa Crusher + Refinery + Smeltery + agua + steam EN SERIE. **Ideas:** variantes de aluminio (Fan/Pump/Crusher mas eficientes), Aluminum Panel/Vent, White Factory Tiles, Industrial Light Housing."),
    beauty("a-a-t", 11, "Aluminum Age — contenido", [("i", "Item", "text"), ("k", "Tipo", "text"), ("n", "Nota", "text")], alu_tbl),
])

# ============ 6 WORLD ============
world_pie = """```mermaid
pie title Contenido custom por categoria (390 ids)
  "Woods" : 129
  "Industrial" : 76
  "Cosmetics y Deco" : 55
  "Salt y Farming" : 42
  "Mobs" : 32
  "Urban y Street" : 22
  "Building y Terracotta" : 19
  "Economy" : 10
  "Rails" : 5
```"""
woods = [
    {"w": "Maple (Arce)", "x": "Set completo + Maple Syrup + Maple Log goteante (savia) + Maple Leaf Litter"},
    {"w": "Ash (Fresno)", "x": "Set completo"},
    {"w": "Frozen Ash", "x": "Variante helada: set completo"},
    {"w": "Half Frozen Ash", "x": "Estado intermedio: set completo (transicion de hielo)"},
    {"w": "Palm (Palmera)", "x": "Set completo + Palm Crown Log / Crown Wood"},
]
pumpkin = [
    {"i": "Pale Pumpkin", "n": "Calabaza palida (bloque + carved)"},
    {"i": "Jack o Soul / Pale / Pale Soul Lantern", "n": "3 variantes talladas iluminadas"},
    {"i": "Pale Pumpkin Pie", "n": "Comida"},
    {"i": "Frozen Heart (awake/sleep)", "n": "Bloque especial helado (estructura/evento)"},
]
salt = [
    {"i": "Salt / Pink Salt", "n": "Item base (2 colores)"},
    {"i": "Salt Fern / Pink Salt Fern / Sea Asparagus", "n": "Plantas"},
    {"i": "Salt Block / Pink Salt Block (salt_sand)", "n": "Bloque base"},
    {"i": "Salt Brick + Double/Smooth/Tiled/Dirty Tiled (+ slabs)", "n": "Familia de ladrillos (x2 colores)"},
    {"i": "Salt Crystal Block + Slab (+ pink)", "n": "Cristales"},
    {"i": "Salt Ore: Dripstone / Stone / Deepslate (+ pink)", "n": "6 menas de worldgen"},
]
building = [
    {"i": "Terracotta Tiles", "n": "16 colores (todos los tintes)"},
    {"i": "Carved Wood", "n": "16 tipos (cada madera vanilla + ash/maple/palm)"},
    {"i": "Mosaic", "n": "15 tipos"},
    {"i": "Crates", "n": "16 tipos (cajas de almacen/deco)"},
    {"i": "Cobbled Tuff (+ Bricks + Tiles)", "n": "Set de toba"},
    {"i": "Smooth Bauxite / Bauxite Bricks", "n": "Industrial (Aluminum Age)"},
    {"i": "Powered Copper Rails", "n": "normal/exposed/weathered/oxidized + waxed"},
]
mobs = [
    {"m": "Chicken", "v": "rooster young/old, unfeathered, cold, amber, bronzed, cluckshroom, fancy, gold_crested, midnight, skewbald, stormy, temperate"},
    {"m": "Cow", "v": "albino, ashen, cookie, cream, dairy, moobloom, moolip, pinto, sunset, umbra, wooly"},
    {"m": "Pig", "v": "dried_muddy, mottled, muddy, pale, piebald, pink_footed, sooty, spotted"},
]
write("6 World.kd", "6 · World (Mundo)", [
    text("w-h", 0, "heading", "# 6 · World — Contenido de mundo"),
    callout("w-i", 1, "trees", "Capa survival/cozy alrededor de la fabrica. Lista exhaustiva: @[doc:5 Reference/Catalog - Woods.kd:Reference / Catalogo]."),
    diagram("w-pie", 2, world_pie),
    text("w-wood-h", 3, "heading", "## Maderas & Arboles"),
    beauty("w-wood", 4, "Maderas", [("w", "Madera", "text"), ("x", "Extras unicos", "text")], woods),
    callout("w-wood-d", 5, "lightbulb", "**Idea de diseno:** Tree Tap sobre Maple Log goteante -> Maple Sap -> Maple Syrup (cadena de agricultura industrial)."),
    text("w-pump-h", 6, "heading", "## Pale Pumpkin & especiales"),
    beauty("w-pump", 7, "Pale Pumpkin", [("i", "Item", "text"), ("n", "Nota", "text")], pumpkin),
    text("w-salt-h", 8, "heading", "## Industria de la Sal"),
    beauty("w-salt", 9, "Sal (x2 colores: Salt / Pink Salt)", [("i", "Item", "text"), ("n", "Nota", "text")], salt),
    callout("w-salt-d", 10, "lightbulb", "**Idea:** conectar Salt Bricks como insumo de un Steel Furnace MK2 (puente World -> Industry)."),
    text("w-build-h", 11, "heading", "## Bloques de construccion"),
    beauty("w-build", 12, "Building", [("i", "Set", "text"), ("n", "Detalle", "text")], building),
    text("w-mob-h", 13, "heading", "## Mobs (variantes)"),
    beauty("w-mob", 14, "Mobs", [("m", "Animal", "text"), ("v", "Variantes", "text")], mobs),
    callout("w-gen", 15, "globe", "**Worldgen:** Bauxite Ore (overworld raro) + Bauxite Stone (badlands), 6 menas de sal, veins de Nitrogenated Cal (no se agotan), arboles Maple/Ash/Palm."),
])

print("GDD done.")
