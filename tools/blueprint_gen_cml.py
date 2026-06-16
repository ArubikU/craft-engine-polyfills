#!/usr/bin/env python3
"""
Blueprint lore generator for craft-engine-polyfills.

Spec-driven: a 6-slot input grid + up to 2 outputs (each cell an item icon + count/chance).
Icons come from either a flat texture reference (vanilla items resolve client-side) or a
BAKED isometric render of a real block MODEL JSON (parses elements + per-face textures/uv,
so partial shapes like the thin conveyor slab render correctly — not as a full cube).

Outputs:
  * baked icon PNGs under textures/font/image/baked/  (2x supersampled for crispness)
  * demo/configuration/images.yml      (CraftEngine images for every referenced icon)
  * demo/configuration/items/<id>.yml  (name + grid lore w/ output on the RIGHT + recipe)

Run:  python tools/blueprint_gen.py   then sync demo/ -> testserver and `craftengine reload all`.
"""
import json
import math
import os
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RP = os.path.join(ROOT, "testserver", "plugins", "CraftEngine", "resources", "modern", "resourcepack", "assets", "minecraft")
TEX = os.path.join(RP, "textures")
MODELS = os.path.join(RP, "models")
BAKE_DIR = os.path.join(TEX, "font", "image", "baked")
ITEMS_DIR = os.path.join(ROOT, "testserver", "plugins", "CraftEngine", "resources", "modern", "configuration", "item", "polyfills")
IMAGES_YML = os.path.join(ROOT, "testserver", "plugins", "CraftEngine", "resources", "modern", "configuration", "polyfills_images.yml")

ICON_HEIGHT = 18      # input icon display size (px) in lore
ICON_ASCENT = 13
OUTPUT_HEIGHT = 32    # output icon ~2x the inputs (emphasise the result)
OUTPUT_ASCENT = 22
BAKE_RES = 48         # baked PNG resolution (supersampled; ~2.5x of 18 for quality)
GRID_SHIFT = 6        # left indent of the grid (px)
OUT_SHIFT = 74        # indent of the output line so it sits to the RIGHT of the grid
TITLE_SHIFT = 10

# ---------------- icon library ----------------
# kind "tex"   -> reference a texture path (vanilla resolves client-side)
# kind "model" -> bake an iso render of a full block model JSON (path under models/)
ICONS = {
    "iron":   {"image": "i_iron",   "kind": "tex", "tex": "minecraft:item/iron_ingot"},
    "copper": {"image": "i_copper", "kind": "tex", "tex": "minecraft:item/copper_ingot"},
    "kelp":   {"image": "i_kelp",   "kind": "tex", "tex": "minecraft:item/dried_kelp"},
    "stone_gear":  {"image": "i_gear_s", "kind": "tex", "tex": "minecraft:item/custom/stone_gear"},
    "copper_gear": {"image": "i_gear_c", "kind": "tex", "tex": "minecraft:item/custom/copper_gear"},
    "iron_gear":   {"image": "i_gear_i", "kind": "tex", "tex": "minecraft:item/custom/iron_gear"},
    "conveyor": {"image": "i_conveyor", "kind": "model",
                 "model": "block/custom/conveyor/conveyor_flat_middle"},
    "depot":    {"image": "i_depot", "kind": "model", "model": "block/custom/conveyor_io/depot"},
    "motor":    {"image": "i_motor", "kind": "model", "model": "block/custom/gas_motor_mk1/gas_motor_mk1"},
    "furnace":  {"image": "i_furnace", "kind": "model", "model": "block/custom/vapor_furnace_mk1/vapor_furnace_mk1"},
    "copper_wire":    {"image": "i_wire",     "kind": "tex", "tex": "minecraft:item/custom/copper_wire"},
    "brass_shavings": {"image": "i_shavings", "kind": "tex", "tex": "minecraft:item/custom/brass_shavings"},
    "merger":   {"image": "i_merger",   "kind": "model", "model": "block/custom/conveyor_io/merger"},
    "splitter": {"image": "i_splitter", "kind": "model", "model": "block/custom/conveyor_io/splitter"},
    "funnel":   {"image": "i_funnel",   "kind": "model", "model": "block/custom/funnel/funnel_out"},
}

BLUEPRINTS = {
    "conveyor_blueprint": {
        "name": "Conveyor Blueprint", "grad": ("#7db4ff", "#3a6fd6"),
        "model": "minecraft:item/custom/conveyor_blueprint",
        "grid": [["kelp", "kelp", "kelp"], ["iron", "copper", "iron"]],
        "outputs": [{"item": "conveyor", "count": 4}],
        "desc_color": "dark_aqua",
        "desc": ["Stamped slats and rollers — the", "backbone of any factory floor."],
        "craft": {"pattern": ["PPP", "PCP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "C": "minecraft:copper_ingot"}},
    },
    "depot_blueprint": {
        "name": "Depot Blueprint", "grad": ("#e0a878", "#b06a3c"),
        "model": "minecraft:item/custom/depot_blueprint",
        "grid": [["iron", "iron", "iron"], ["copper", "iron_gear", "copper"]],
        "outputs": [{"item": "depot", "count": 1}],
        "desc_color": "gold",
        "desc": ["A reinforced bin that buffers a belt's", "output — hopper-fed, comparator-read."],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
    "vapor_motor_blueprint": {
        "name": "Vapor Motor Blueprint", "grad": ("#7fe0ee", "#2a8fa6"),
        "model": "minecraft:item/custom/vapor_motor_blueprint",
        "grid": [["copper", "iron", "copper"], ["iron", "iron_gear", "iron"]],
        "outputs": [{"item": "motor", "count": 1}],
        "desc_color": "dark_aqua",
        "desc": ["Boils vapor into rotation — the prime", "mover that drives your conveyors."],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
    "vapor_furnace_mk1_blueprint": {
        "name": "Vapor Furnace Mk1 Blueprint", "grad": ("#c0c0c0", "#808080"),
        "model": "minecraft:item/custom/vapor_motor_blueprint",
        "grid": [["iron", "iron", "iron"], ["iron", "iron_gear", "iron"]],
        "outputs": [{"item": "furnace", "count": 1}],
        "desc_color": "gray",
        "desc": ["Burns fuel to boil water into vapor", "and smelt ores — results drop below."],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
    "wire_blueprint": {
        "name": "Wire Blueprint", "grad": ("#f0d27a", "#b88a2c"),
        "model": "minecraft:item/custom/wire_blueprint",
        "grid": [["copper", "copper", "copper"], [None, "copper_gear", None]],
        "outputs": [{"item": "copper_wire", "count": 3},
                    {"item": "brass_shavings", "chance": 30}],
        "desc_color": "gold",
        "desc": [],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
    "funnel_blueprint": {
        "name": "Funnel Blueprint", "grad": ("#e0a878", "#b06a3c"),
        "model": "minecraft:item/custom/depot_blueprint",
        "grid": [["copper", "copper", "copper"], ["copper", "iron_gear", "copper"]],
        "outputs": [{"item": "funnel", "count": 1}],
        "desc_color": "gold",
        "desc": ["Pulls items off a belt into a chest", "(or feeds them on) - one per side."],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
    # MULTI-RECIPE blueprint: one plan, two recipes (right-click cycles between them).
    "logistics_blueprint": {
        "name": "Logistics Blueprint",
        "model": "minecraft:item/custom/logistics_blueprint",
        "recipes": [
            {"grid": [["conveyor", "iron", "conveyor"], [None, "stone_gear", None]],
             "outputs": [{"item": "merger", "count": 1}]},
            {"grid": [[None, "stone_gear", None], ["conveyor", "iron", "conveyor"]],
             "outputs": [{"item": "splitter", "count": 1}]},
        ],
        "craft": {"pattern": ["PPP", "PGP", "PPP"],
                  "ingredients": {"P": "minecraft:paper", "G": "cml:iron_gear"}},
    },
}

# ---------------- model iso renderer ----------------
C30 = math.cos(math.radians(30))
S30 = math.sin(math.radians(30))


def _tex_image(path):
    p = os.path.join(TEX, path + ".png")
    img = Image.open(p).convert("RGBA")
    if img.height > img.width:        # animated strip -> first frame
        img = img.crop((0, 0, img.width, img.width))
    if img.size != (16, 16):
        img = img.resize((16, 16), Image.NEAREST)
    return img


def _resolve_tex(name, textures):
    seen = 0
    while name and name.startswith("#") and seen < 8:
        name = textures.get(name[1:])
        seen += 1
    if name and ":" in name:
        name = name.split(":", 1)[1]
    return name


def _affine(dst, src):
    (x0, y0), (x1, y1), (x2, y2) = dst
    (u0, v0), (u1, v1), (u2, v2) = src

    def solve(r0, r1, r2):
        m = [[x0, y0, 1.0, r0], [x1, y1, 1.0, r1], [x2, y2, 1.0, r2]]
        for i in range(3):
            if abs(m[i][i]) < 1e-9:
                for k in range(i + 1, 3):
                    if abs(m[k][i]) > 1e-9:
                        m[i], m[k] = m[k], m[i]
                        break
            piv = m[i][i]
            m[i] = [v / piv for v in m[i]]
            for k in range(3):
                if k != i:
                    f = m[k][i]
                    m[k] = [a - f * b for a, b in zip(m[k], m[i])]
        return m[0][3], m[1][3], m[2][3]
    a, b, c = solve(u0, u1, u2)
    d, e, f = solve(v0, v1, v2)
    return (a, b, c, d, e, f)


# visible faces for a +x/+y/+z corner camera, with shading + corner order (origin,u,v)
def _faces(x0, y0, z0, x1, y1, z1):
    return {
        "up":    (1.00, (x0, y1, z0), (x1, y1, z0), (x0, y1, z1)),
        "south": (0.80, (x0, y1, z1), (x1, y1, z1), (x0, y0, z1)),
        "east":  (0.62, (x1, y1, z1), (x1, y1, z0), (x1, y0, z1)),
    }


def bake_model(model_path):
    with open(os.path.join(MODELS, model_path + ".json"), encoding="utf-8") as fh:
        model = json.load(fh)
    textures = model.get("textures", {})
    elements = model.get("elements", [])
    R = BAKE_RES

    def proj(p):
        x, y, z = p
        return ((x - z) * C30, (x + z) * S30 - y)

    # bounds over all element corners
    pts = []
    for el in elements:
        fx, fy, fz = el["from"]
        tx, ty, tz = el["to"]
        for X in (fx, tx):
            for Y in (fy, ty):
                for Z in (fz, tz):
                    pts.append(proj((X, Y, Z)))
    if not pts:
        return Image.new("RGBA", (R, R), (0, 0, 0, 0))
    minx = min(p[0] for p in pts); maxx = max(p[0] for p in pts)
    miny = min(p[1] for p in pts); maxy = max(p[1] for p in pts)
    span = max(maxx - minx, maxy - miny) or 1.0
    margin = R * 0.06
    scale = (R - 2 * margin) / span
    offx = margin - minx * scale + (R - 2 * margin - (maxx - minx) * scale) / 2
    offy = margin - miny * scale + (R - 2 * margin - (maxy - miny) * scale) / 2

    def to2d(p):
        px, py = proj(p)
        return (px * scale + offx, py * scale + offy)

    # collect drawable faces with depth
    draws = []
    for el in elements:
        fx, fy, fz = el["from"]
        tx, ty, tz = el["to"]
        faces = el.get("faces", {})
        fdef = _faces(fx, fy, fz, tx, ty, tz)
        # fallback texture for an undefined-but-visible face
        fallback = None
        for fn in ("north", "south", "east", "west", "up", "down"):
            if fn in faces:
                fallback = faces[fn]
                break
        for fname, (shade, o, pu, pv) in fdef.items():
            fc = faces.get(fname, fallback)
            if fc is None:
                continue
            texname = _resolve_tex(fc.get("texture"), textures)
            if not texname:
                continue
            try:
                tex = _tex_image(texname)
            except Exception:
                continue
            uv = fc.get("uv", [0, 0, 16, 16])
            crop = tex.crop((min(uv[0], uv[2]), min(uv[1], uv[3]),
                             max(uv[0], uv[2]), max(uv[1], uv[3]))).convert("RGBA")
            if crop.size[0] == 0 or crop.size[1] == 0:
                crop = tex
            o2, u2, v2 = to2d(o), to2d(pu), to2d(pv)
            # Skip faces that project to a zero-area quad (edge-on / flat elements),
            # which would make the affine solve singular.
            area = abs((u2[0] - o2[0]) * (v2[1] - o2[1]) - (u2[1] - o2[1]) * (v2[0] - o2[0]))
            if area < 0.5:
                continue
            depth = (o[0] + o[1] + o[2]) + (pu[0] + pu[1] + pu[2]) + (pv[0] + pv[1] + pv[2])
            draws.append((depth, shade, crop, o2, u2, v2))

    draws.sort(key=lambda d: d[0])  # far -> near
    out = Image.new("RGBA", (R, R), (0, 0, 0, 0))
    for _, shade, crop, o2, u2, v2 in draws:
        w, h = crop.size
        coeffs = _affine([o2, u2, v2], [(0, 0), (w, 0), (0, h)])
        layer = crop.transform((R, R), Image.AFFINE, coeffs, resample=Image.NEAREST,
                               fillcolor=(0, 0, 0, 0))
        if shade != 1.0:
            px = layer.load()
            for yy in range(R):
                for xx in range(R):
                    r, g, b, a = px[xx, yy]
                    if a:
                        px[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
        out.alpha_composite(layer)
    return out


# ---------------- emit ----------------
def recipes_of(bp):
    if "recipes" in bp:
        return bp["recipes"]
    return [{"grid": bp["grid"], "outputs": bp["outputs"]}]


def usage():
    inp, outp = set(), set()
    for bp in BLUEPRINTS.values():
        for rec in recipes_of(bp):
            for row in rec["grid"]:
                inp.update(c for c in row if c)
            outp.update(o["item"] for o in rec["outputs"])
    return inp, outp


def write_images():
    os.makedirs(BAKE_DIR, exist_ok=True)
    inp, outp = usage()
    out = ["# AUTO-GENERATED by tools/blueprint_gen.py",
           "# Each icon: <img> at input size, <img>_o at output size (~2x).", "images:"]
    for key in sorted(inp | outp):
        ic = ICONS[key]
        if ic["kind"] == "model":
            bake_model(ic["model"]).save(os.path.join(BAKE_DIR, ic["image"] + ".png"))
            file_ref = "minecraft:font/image/baked/%s.png" % ic["image"]
        else:
            file_ref = ic["tex"]
        if key in inp:
            out += ["  cml:%s:" % ic["image"], "    height: %d" % ICON_HEIGHT,
                    "    ascent: %d" % ICON_ASCENT, "    font: minecraft:default",
                    "    file: %s" % file_ref, "    grid_size: 1,1"]
        if key in outp:
            out += ["  cml:%s_o:" % ic["image"], "    height: %d" % OUTPUT_HEIGHT,
                    "    ascent: %d" % OUTPUT_ASCENT, "    font: minecraft:default",
                    "    file: %s" % file_ref, "    grid_size: 1,1"]
    open(IMAGES_YML, "w", encoding="utf-8").write("\n".join(out) + "\n")
    print("wrote", IMAGES_YML)


def tag_in(key):
    return "<image:cml:%s:0:0>" % ICONS[key]["image"]


def tag_out(key):
    return "<image:cml:%s_o:0:0>" % ICONS[key]["image"]


def cell(c):
    return tag_in(c) if c else "<shift:%d>" % ICON_HEIGHT


def out_label(o):
    return ("%d%%" % o["chance"]) if "chance" in o else ("x%d" % o.get("count", 1))


NAME_COLORS = {"conveyor_blueprint": "aqua", "depot_blueprint": "gold",
               "vapor_motor_blueprint": "aqua", "wire_blueprint": "yellow",
               "logistics_blueprint": "light_purple", "funnel_blueprint": "gold"}


def write_blueprint(bp_id, bp):
    recs = recipes_of(bp)
    # No italic (<!i> on every line), no gradient, i18n sections (vanilla-style).
    L = ['      lore:', '        - "<!i> "']
    for ri, rec in enumerate(recs):
        rows = rec["grid"]
        outs = "  ".join("<white>%s <white>%s" % (tag_out(o["item"]), out_label(o)) for o in rec["outputs"])
        L += ['        - "<!i><shift:%d><white>%s"' % (GRID_SHIFT, "".join(cell(c) for c in rows[0])),
              '        - "<!i><shift:%d><gray>➜  %s"' % (OUT_SHIFT, outs),
              '        - "<!i><shift:%d><white>%s"' % (GRID_SHIFT, "".join(cell(c) for c in rows[1])),
              '        - "<!i> "']
        if len(recs) > 1 and ri < len(recs) - 1:
            L += ['        - "<!i><shift:%d><dark_gray>─ ─ ─ ─ ─" ' % GRID_SHIFT, '        - "<!i> "']
    if len(recs) > 1:
        L.append('        - "<!i><light_purple><lang:cml.bp.multi>"')
    L += ['        - "<!i><gray><lang:cml.bp.craftable_on>"',
          '        - "<!i>  <blue><lang:cml.bp.workbench>"',
          '        - "<!i><gray><lang:cml.bp.usage>"',
          '        - "<!i>  <blue><lang:cml.bp.placeable>"',
          '        - "<!i> "',
          '        - "<!i><dark_gray><lang:cml.bp.reusable>"',
          '        - "<!i><dark_gray><lang:cml.bp.autofill>"']

    name = '<!i><%s><lang:item.cml.%s>' % (NAME_COLORS.get(bp_id, "white"), bp_id)
    cr = bp["craft"]
    doc = ["# AUTO-GENERATED by tools/blueprint_gen.py — do not edit by hand.",
           "items:", "  cml:%s:" % bp_id, "    data:",
           '      item_name: "%s"' % name]
    doc += L
    doc += ["    model: %s" % bp["model"], "",
            "recipes:", "  cml:%s:" % bp_id, "    type: shaped", "    pattern:"]
    doc += ['      - "%s"' % r for r in cr["pattern"]]
    doc.append("    ingredients:")
    doc += ['      %s: %s' % (k, v) for k, v in cr["ingredients"].items()]
    doc += ["    result:", "      id: cml:%s" % bp_id, "      count: 1"]
    path = os.path.join(ITEMS_DIR, bp_id + ".yml")
    open(path, "w", encoding="utf-8").write("\n".join(doc) + "\n")
    print("wrote", path)


def main():
    write_images()
    for bp_id, bp in BLUEPRINTS.items():
        write_blueprint(bp_id, bp)


if __name__ == "__main__":
    main()
