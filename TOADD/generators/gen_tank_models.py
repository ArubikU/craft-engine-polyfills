#!/usr/bin/env python3
"""Generate cml fluid_block_tank face models from Create originals.

CULLING = DELETE WHOLE CUBES (never faces, never UVs). A side cube (wall or window) is
KEPT only if it physically TOUCHES one of the cell's EXTERIOR boundary planes, computed
from the cube's own from/to. Cubes that touch no exterior side are deleted, so the
interior of a multiblock is truly hollow. Lid cubes (those with up/down faces) are kept
as-is per source position (block_single=both lids, bottom/top=one, middle=none).

Belongs test: a cube belongs to side D iff it is a THIN slab (extent perpendicular to D
<= 4px) flush against D's plane. (Full-length walls touch the perpendicular sides at
their ends, so the thin-slab gate is what stops a corner cube keeping all four walls.)
  north: min_z < 1  & (max_z-min_z)<=4     south: max_z > 15 & (max_z-min_z)<=4
  west:  min_x < 1  & (max_x-min_x)<=4     east:  max_x > 15 & (max_x-min_x)<=4

Per facing mask:
  none            base/middle,          exterior={}            -> lids only (interior cell)
  n/e/s/w         window,               exterior={that side}   -> centered porthole on 1 side (w>=3 edge-mid)
  ne/nw/es/sw     create half-corner,   exterior={2 adj sides} -> merged half-window corner (w==2)
  nep/nwp/esp/swp base,                 exterior={2 adj sides} -> plain walls, no window (w>=3 corner)
  nesw            window,               exterior=all           -> isolated 1x1 column, full window
  solid           base,                 exterior=all (keep-all)-> hammer window-off, opaque walls
"""
import json, os, copy

SRC = r"C:\Users\ejane\AppData\Local\Temp\createmodels"
OUT = r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\resourcepack\assets\cml\models\block\fluid_block_tank"

LETTER = {"n": "north", "e": "east", "s": "south", "w": "west"}
HORIZ = ("north", "east", "south", "west")
HALF_SRC = {"ne": "window_ne", "nw": "window_nw", "es": "window_se", "sw": "window_sw"}
POSITIONS = ["single", "bottom", "middle", "top"]

def texmap(pos, src_tex):
    # Texture PATHS only (never UVs): swap create:->cml: / fluid_tank->fluid_block_tank,
    # then override #1 (walls) with the per-position vertical-CTM connection texture.
    out = {k: v.replace("create:block/fluid_tank", "cml:block/fluid_block_tank") for k, v in src_tex.items()}
    out["1"] = "cml:block/fluid_block_tank_conn_" + pos
    return out

def load(pos, suffix):
    return json.load(open(os.path.join(SRC, f"block_{pos}{suffix}.json")))

def touched_sides(el):
    fx, fy, fz = el["from"]
    tx, ty, tz = el["to"]
    minx, maxx = min(fx, tx), max(fx, tx)
    minz, maxz = min(fz, tz), max(fz, tz)
    thin_x = (maxx - minx) <= 4
    thin_z = (maxz - minz) <= 4
    s = set()
    if minz < 1 and thin_z:  s.add("north")
    if maxz > 15 and thin_z: s.add("south")
    if minx < 1 and thin_x:  s.add("west")
    if maxx > 15 and thin_x: s.add("east")
    return s

def is_lid(el):
    return "up" in el["faces"] or "down" in el["faces"]

def emit(pos, mask, exterior, source_suffix, keep_all=False):
    d = load(pos, source_suffix)
    out = {"credit": "Made with Blockbench", "parent": "block/block",
           "textures": texmap(pos, d["textures"]), "elements": []}
    for el in d["elements"]:
        if is_lid(el) or keep_all or (touched_sides(el) & exterior):
            out["elements"].append(copy.deepcopy(el))
    path = os.path.join(OUT, f"block_{pos}_face_{mask}.json")
    json.dump(out, open(path, "w"), separators=(",", ":"))
    return path

def main():
    os.makedirs(OUT, exist_ok=True)
    count = 0
    for pos in POSITIONS:
        emit(pos, "none", set(), ""); count += 1
        for ltr, wd in LETTER.items():
            emit(pos, ltr, {wd}, "_window"); count += 1
        for mask, src in HALF_SRC.items():
            emit(pos, mask, {LETTER[c] for c in mask}, "_" + src); count += 1
        for mask in ("ne", "nw", "es", "sw"):
            emit(pos, mask + "p", {LETTER[c] for c in mask}, ""); count += 1
        emit(pos, "nesw", set(HORIZ), "_window"); count += 1
        emit(pos, "solid", set(HORIZ), "", keep_all=True); count += 1
    print(f"emitted {count} models to {OUT}")

if __name__ == "__main__":
    main()
