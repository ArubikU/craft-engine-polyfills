#!/usr/bin/env python3
"""Generate cml fluid_block_tank face models from Create originals.

Convention (DIRECT): keep a face's real texture iff its own side L is in the cell's
EXTERIOR set; otherwise null it (#6 = transparent) so interior-facing sides hide.
up/down faces always kept.

Per facing mask:
  none            base,                 exterior={}            -> lids only
  n/e/s/w         window,               exterior={that side}   -> centered porthole on 1 side (w>=3 edge-mid)
  ne/nw/es/sw     create half-corner,   exterior={2 adj sides} -> merged half-window corner (w==2)
  nep/nwp/esp/swp base,                 exterior={2 adj sides} -> plain walls, no window (w>=3 corner)
  nesw            window,               exterior=all           -> isolated 1x1 column, full window
  solid           base,                 exterior=all (keep-all)-> hammer window-off, opaque walls
"""
import json, os, copy

SRC = r"C:\Users\ejane\AppData\Local\Temp\createmodels"
OUT = r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\resourcepack\assets\cml\models\block\fluid_block_tank"

OPP = {"north": "south", "south": "north", "east": "west", "west": "east"}
LETTER = {"n": "north", "e": "east", "s": "south", "w": "west"}
HORIZ = ("north", "east", "south", "west")

# world-exterior corner mask -> Create half-corner source (DIRECT: geometry on the named exterior sides)
HALF_SRC = {"ne": "window_ne", "nw": "window_nw", "es": "window_se", "sw": "window_sw"}

POSITIONS = ["single", "bottom", "middle", "top"]

def texmap(pos, src_tex):
    # Preserve EACH source model's own texture keys (single uses #5 window_single; multi-height uses #3
    # fluid_tank_window with vertical-CTM UVs). Swap create:->cml: / fluid_tank->fluid_block_tank, then
    # override #1 (walls) with the per-position connection texture and add #6 (null) for culled faces.
    out = {}
    for k, v in src_tex.items():
        out[k] = v.replace("create:block/fluid_tank", "cml:block/fluid_block_tank")
    out["1"] = "cml:block/fluid_block_tank_conn_" + pos
    out["6"] = "cml:block/null"
    return out

def load(pos, suffix):
    p = os.path.join(SRC, f"block_{pos}{suffix}.json")
    return json.load(open(p))

def emit(pos, mask, exterior, source_suffix, keep_all=False):
    """exterior: set of world dirs ('north'...). keep_all: ignore exterior, keep every face real."""
    d = load(pos, source_suffix)
    out = {"credit": "Made with Blockbench", "parent": "block/block", "textures": texmap(pos, d["textures"]), "elements": []}
    for el in d["elements"]:
        ne = copy.deepcopy(el)
        for side in list(ne["faces"].keys()):
            if side not in HORIZ:
                continue  # up/down always kept
            f = ne["faces"][side]
            # DIRECT mapping: keep the face on the exterior side itself; null interior-facing sides.
            keep = keep_all or (side in exterior)
            if not keep:
                f["texture"] = "#6"
        out["elements"].append(ne)
    # flatten groups (not needed for rendering)
    path = os.path.join(OUT, f"block_{pos}_face_{mask}.json")
    json.dump(out, open(path, "w"), separators=(",", ":"))
    return path

def main():
    os.makedirs(OUT, exist_ok=True)
    count = 0
    for pos in POSITIONS:
        # none -> lids only
        emit(pos, "none", set(), ""); count += 1
        # edge-mids: window source, 1 exterior side
        for ltr, wd in LETTER.items():
            emit(pos, ltr, {wd}, "_window"); count += 1
        # windowed corners (w==2): create half-corner source, 2 exterior sides
        for mask, src in HALF_SRC.items():
            ext = {LETTER[c] for c in mask}
            emit(pos, mask, ext, "_" + src); count += 1
        # plain corners (w>=3): base source, 2 exterior sides, no window
        for mask in ("ne", "nw", "es", "sw"):
            ext = {LETTER[c] for c in mask}
            emit(pos, mask + "p", ext, ""); count += 1
        # nesw isolated column: full window
        emit(pos, "nesw", set(HORIZ), "_window"); count += 1
        # solid: base, keep all walls (opaque, no window)
        emit(pos, "solid", set(HORIZ), "", keep_all=True); count += 1
    print(f"emitted {count} models to {OUT}")

if __name__ == "__main__":
    main()
