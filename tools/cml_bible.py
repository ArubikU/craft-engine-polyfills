#!/usr/bin/env python3
"""Reorganize the Killio cmlfactory workspace into a 'game bible' + build a recipe index.
Inspired by mcrecipe (per-item: made-by / used-in). Generates KAML .kd docs directly."""
import os, re, json, glob, shutil, io, datetime

WS = r"D:\Piero\Downloads\cmlfactory"
SRC = r"D:\Github\craft-engine-polyfills\src\main\resources"
CFG = r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\configuration"

import yaml


def q(s):
    s = str(s)
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"') + '"'


def brick(bid, kind, pos, content):
    return "[[bricks]]\nid = %s\nkind = %s\nposition = %d\ncontent = (%s)\n" % (q(bid), kind, pos, content)


def doc(path, title, bricks):
    head = "#killio kd 2026-v1\nid = %s\ntitle = %s\n\n" % (q(path.replace(WS + os.sep, "").replace("\\", "/")), q(title))
    io.open(os.path.join(WS, path), "w", encoding="utf-8").write(head + "\n".join(bricks))


def tbl(title, header, rows, maxr=80):
    """return list of content-strings (chunked tables)."""
    out = []
    for i in range(0, len(rows), maxr):
        chunk = rows[i:i + maxr]
        rr = "[" + ", ".join(["[" + ", ".join(q(c) for c in header) + "]"] +
                             ["[" + ", ".join(q(c) for c in r) + "]" for r in chunk]) + "]"
        t = title if len(rows) <= maxr else "%s (%d-%d)" % (title, i + 1, i + len(chunk))
        out.append("title = %s, rows = %s" % (q(t), rr))
    return out


# ---------------- 1. backup ----------------
def backup():
    dst = os.path.join(WS, "backups", "pre-bible")
    if os.path.exists(dst):
        shutil.rmtree(dst)
    n = 0
    for dp, _, files in os.walk(WS):
        if any(x in dp for x in (os.sep + "backups", os.sep + ".killio", os.sep + "assets", os.sep + ".git", os.sep + ".cursor", os.sep + ".github")):
            continue
        for f in files:
            if f.endswith((".kd", ".kb", ".kf")):
                rel = os.path.relpath(os.path.join(dp, f), WS)
                tp = os.path.join(dst, rel)
                os.makedirs(os.path.dirname(tp), exist_ok=True)
                shutil.copy2(os.path.join(dp, f), tp)
                n += 1
    print("backup:", n, "files")


# ---------------- 2. parse recipes ----------------
def short(idstr):
    return str(idstr).split(":")[-1] if idstr else "?"


def parse_recipes():
    made = {}   # item -> list of (kind, desc)
    used = {}   # item -> list of (kind, desc)

    def add(d, k, v):
        d.setdefault(k, []).append(v)

    # machine recipes (JSON)
    for f in glob.glob(os.path.join(SRC, "recipes", "*.json")):
        if "test_m" in f:
            continue
        try:
            r = json.load(io.open(f, encoding="utf-8"))
        except Exception:
            continue
        typ = r.get("type", "?")
        ins = []
        for i in r.get("inputs", []):
            nm = i.get("id") or i.get("tag") or "?"
            ins.append("%sx %s" % (i.get("amount", 1), short(nm)))
        outs = r.get("outputs", [])
        odesc = ", ".join("%sx %s%s" % (o.get("amount", 1), short(o.get("id", "?")),
                          (" @%d%%" % int(o["chance"] * 100)) if o.get("chance", 1) < 1 else "") for o in outs)
        t = r.get("time", "?")
        line = "%s | in: %s | %st" % (typ, ", ".join(ins), t)
        for o in outs:
            add(made, short(o.get("id", "?")), (typ, "in: %s -> %sx (%st)" % (", ".join(ins), o.get("amount", 1), t)))
        for i in r.get("inputs", []):
            add(used, short(i.get("id") or i.get("tag")), (typ, "%s -> %s" % (typ, odesc)))

    # workbench recipes (JSON)
    for f in glob.glob(os.path.join(SRC, "workbench_recipes", "*.json")):
        try:
            r = json.load(io.open(f, encoding="utf-8"))
        except Exception:
            continue
        pat = " / ".join(r.get("pattern", []))
        keys = ", ".join("%s=%s" % (k, short(v)) for k, v in r.get("keys", {}).items())
        tool = short(r.get("tool", ""))
        for o in r.get("outputs", []):
            add(made, short(o.get("id", "?")), ("workbench", "tool %s | %s | %s" % (tool, pat, keys)))
        for v in r.get("keys", {}).values():
            for o in r.get("outputs", []):
                add(used, short(v), ("workbench", "-> %s" % short(o.get("id", "?"))))

    # yml crafting recipes
    for f in glob.glob(os.path.join(CFG, "**", "*.yml"), recursive=True):
        try:
            data = yaml.safe_load(io.open(f, encoding="utf-8"))
        except Exception:
            continue
        if not isinstance(data, dict) or "recipes" not in data or not isinstance(data["recipes"], dict):
            continue
        for rid, r in data["recipes"].items():
            if not isinstance(r, dict):
                continue
            typ = r.get("type", "shaped")
            res = r.get("result", {})
            rid_out = short(res.get("id") if isinstance(res, dict) else res)
            cnt = res.get("count", 1) if isinstance(res, dict) else 1
            ings = []
            if "ingredients" in r:
                ig = r["ingredients"]
                if isinstance(ig, dict):
                    ings = [short(v) for v in ig.values()]
                elif isinstance(ig, list):
                    ings = [short(v) for v in ig]
            elif "ingredient" in r:
                ings = [short(r["ingredient"])]
            desc = "%s | %s -> %sx" % (typ, "+".join(dict.fromkeys(ings)), cnt)
            add(made, rid_out, (typ, desc))
            for ig in set(ings):
                add(used, ig, (typ, "-> %s" % rid_out))
    return made, used


# ---------------- 3. reorg ----------------
ROUTES = {
    "1 Game Loop": ("#c9a227", "gamepad-2"),
    "2 Industry": ("#b87333", "cog"),
    "3 World": ("#5a9c5a", "trees"),
    "4 Foreign": ("#7a6ad8", "globe"),
    "5 Reference": ("#888888", "list"),
}


def kf(folder, color, icon, name):
    os.makedirs(os.path.join(WS, folder), exist_ok=True)
    io.open(os.path.join(WS, folder, "folder.kf"), "w", encoding="utf-8").write(
        '#killio kf 2026-v1\nname = %s\ncolor = %s\nicon = %s\n' % (q(name), q(color), q(icon)))


def move(src, dst):
    s = os.path.join(WS, src)
    if os.path.exists(s):
        os.makedirs(os.path.dirname(os.path.join(WS, dst)), exist_ok=True)
        shutil.move(s, os.path.join(WS, dst))


def main():
    backup()
    made, used = parse_recipes()
    print("recipes: made=%d items, used=%d items" % (len(made), len(used)))

    for f, (c, i) in ROUTES.items():
        kf(f, c, i, f.split(" ", 1)[1])

    # move existing detailed docs into routes
    moves = [
        ("Overview.kd", "1 Game Loop/Overview.kd"),
        ("Copper Age/Copper Age.kd", "2 Industry/Copper Age.kd"),
        ("Copper Age/Blueprints & Workbench.kd", "2 Industry/Blueprints & Workbench.kd"),
        ("Steel Age/Steel Age.kd", "2 Industry/Steel Age.kd"),
        ("Aluminum Age/Aluminum Age.kd", "2 Industry/Aluminum Age.kd"),
        ("World Content/Woods.kd", "3 World/Woods.kd"),
        ("World Content/Salt & Farming.kd", "3 World/Salt & Farming.kd"),
        ("World Content/Decorations & Misc.kd", "4 Foreign/Decorations & Misc.kd"),
        ("Catalog/Industrial.kd", "5 Reference/Catalog - Industrial.kd"),
        ("Catalog/Woods.kd", "5 Reference/Catalog - Woods.kd"),
        ("Catalog/Salt & Farming.kd", "5 Reference/Catalog - Salt & Farming.kd"),
        ("Catalog/Building & Terracotta.kd", "5 Reference/Catalog - Building & Terracotta.kd"),
        ("Catalog/Urban & Street.kd", "5 Reference/Catalog - Urban & Street.kd"),
        ("Catalog/Cosmetics & Deco.kd", "5 Reference/Catalog - Cosmetics & Deco.kd"),
        ("Catalog/Economy.kd", "5 Reference/Catalog - Economy.kd"),
        ("Catalog/Mobs.kd", "5 Reference/Catalog - Mobs.kd"),
        ("Catalog/Rails.kd", "5 Reference/Catalog - Rails.kd"),
    ]
    for s, d in moves:
        move(s, d)
    # drop emptied folders
    for old in ("Copper Age", "Steel Age", "Aluminum Age", "World Content", "Catalog"):
        p = os.path.join(WS, old)
        if os.path.isdir(p):
            shutil.rmtree(p)

    # fix the broken doc ids + cross-refs inside moved docs (path-based id -> new path)
    pathmap = {s: d for s, d in moves}
    for d in pathmap.values():
        fp = os.path.join(WS, d)
        if not os.path.exists(fp):
            continue
        txt = io.open(fp, encoding="utf-8").read()
        # rewrite id line to new path
        txt = re.sub(r'^id = "[^"]*"', 'id = %s' % q(d), txt, count=1, flags=re.M)
        # remap any @[doc:OLD:...] refs
        for s2, d2 in pathmap.items():
            txt = txt.replace("@[doc:%s:" % s2, "@[doc:%s:" % d2)
        io.open(fp, "w", encoding="utf-8").write(txt)

    # ---- Game Loop index ----
    gl = [
        brick("gl-h", "text", 0, 'displayStyle = "heading", markdown = "# Game Loop"'),
        brick("gl-i", "text", 1, 'displayStyle = "paragraph", markdown = "Como se juega cmlfactory de principio a fin. Pack steampunk cozy-tecnico (CraftEngine, namespace cml:). 3 edades: Copper -> Steel -> Aluminum."'),
        brick("gl-loop", "text", 2, 'displayStyle = "code", markdown = "Agua (Iron Pump) -> Copper Furnace + Charcoal -> Steam\\nSteam -> Gas Motor -> RPM -> Crusher / Smeltery\\nBauxite -> Crusher -> Refinery (agua+steam) -> Smeltery -> Aluminum\\nNitrogenated Cal -> Gas Pump -> Nitrogen -> Fan (freezing)"'),
        brick("gl-ages", "table", 3, tbl("Edades", ["Edad", "Material", "Hito"], [
            ["Copper", "Copper/Iron", "Vapor, motor, crusher, logistica"],
            ["Steel", "Steel Ingot", "Lava+raw_iron+coal en el Copper Furnace; Refinery"],
            ["Aluminum", "Aluminum Ingot", "Cadena bauxita completa"]])[0]),
        brick("gl-routes", "text", 4, 'displayStyle = "paragraph", markdown = "**Rutas:** @[doc:2 Industry/Copper Age.kd:Industry] (maquinas, recetas, sistemas) · @[doc:3 World/Woods.kd:World] (mundo, arboles, sal, mobs) · @[doc:4 Foreign/Decorations & Misc.kd:Foreign] (cosmeticos, economia, urbano) · @[doc:5 Reference/Catalog - Industrial.kd:Reference] (catalogo + recetas)."'),
    ]
    doc("1 Game Loop/Game Loop.kd", "Game Loop", gl)

    # ---- Route index docs ----
    doc("2 Industry/_Industry.kd", "Industry — index", [
        brick("in-h", "text", 0, 'displayStyle = "heading", markdown = "# Industry"'),
        brick("in-i", "text", 1, 'displayStyle = "paragraph", markdown = "El motor steampunk (polyfills). Detalle por edad: @[doc:2 Industry/Copper Age.kd:Copper Age] · @[doc:2 Industry/Steel Age.kd:Steel Age] · @[doc:2 Industry/Aluminum Age.kd:Aluminum Age] · @[doc:2 Industry/Blueprints & Workbench.kd:Blueprints]. Recetas completas: @[doc:5 Reference/Recipes - Machines.kd:Recipe Index]."'),
    ])
    doc("3 World/_World.kd", "World — index", [
        brick("wo-h", "text", 0, 'displayStyle = "heading", markdown = "# World"'),
        brick("wo-i", "text", 1, 'displayStyle = "paragraph", markdown = "Contenido de mundo: @[doc:3 World/Woods.kd:Woods & Trees] · @[doc:3 World/Salt & Farming.kd:Salt & Farming]. Mobs/building en @[doc:5 Reference/Catalog - Mobs.kd:Reference]."'),
    ])
    doc("4 Foreign/_Foreign.kd", "Foreign — index", [
        brick("fo-h", "text", 0, 'displayStyle = "heading", markdown = "# Foreign"'),
        brick("fo-i", "text", 1, 'displayStyle = "paragraph", markdown = "Contenido importado / no-nativo: cosmeticos (HMC), economia, mobiliario urbano, plushes, graffiti, pack ejemplo topaz. Ver @[doc:4 Foreign/Decorations & Misc.kd:Decorations] y catalogos @[doc:5 Reference/Catalog - Cosmetics & Deco.kd:Cosmetics] · @[doc:5 Reference/Catalog - Economy.kd:Economy] · @[doc:5 Reference/Catalog - Urban & Street.kd:Urban]."'),
    ])

    # ---- Recipe Index docs (made-by / used-in) ----
    def recipe_rows(d):
        rows = []
        for item in sorted(d):
            for kind, desc in d[item]:
                rows.append([item, kind, desc])
        return rows

    mb = recipe_rows(made)
    ui = recipe_rows(used)
    bricks_m = [brick("rm-h", "text", 0, 'displayStyle = "heading", markdown = "# Recipe Index — Made By (%d outputs)"' % len(made))]
    for i, c in enumerate(tbl("Producido por", ["item", "metodo", "receta"], mb)):
        bricks_m.append(brick("rm-t%d" % i, "table", i + 1, c))
    doc("5 Reference/Recipes - Machines.kd", "Recipes — Made By", bricks_m)

    bricks_u = [brick("ru-h", "text", 0, 'displayStyle = "heading", markdown = "# Recipe Index — Used In (%d inputs)"' % len(used))]
    for i, c in enumerate(tbl("Usado en", ["item", "metodo", "produce"], ui)):
        bricks_u.append(brick("ru-t%d" % i, "table", i + 1, c))
    doc("5 Reference/Recipes - Used In.kd", "Recipes — Used In", bricks_u)

    print("done. made rows=%d used rows=%d" % (len(mb), len(ui)))


if __name__ == "__main__":
    main()
