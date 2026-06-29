#!/usr/bin/env python3
"""Generate cml fluid_block_tank face models from Create originals.

Convention (item-display flips model 180deg about Y): a model face on local side L
displays at world side opposite(L). Keep a face's real texture iff opposite(L) is in
the cell's EXTERIOR set; otherwise null it (#6 = transparent) so it hides on the
interior. up/down faces always kept.

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

# world-exterior corner mask -> Create half-corner source (flip: source has geometry on OPPOSITE sides)
HALF_SRC = {"ne": "window_sw", "nw": "window_se", "es": "window_nw", "sw": "window_ne"}

POSITIONS = ["single", "bottom", "middle", "top"]

def texmap(pos):
    # #1 walls use the per-position vertical-CTM connection texture; rest map 1:1 with namespace swap.
    return {
        "0": "cml:block/fluid_block_tank_top",
        "1": "cml:block/fluid_block_tank_conn_" + pos,
        "4": "cml:block/fluid_block_tank_inner",
        "5": "cml:block/fluid_block_tank_window_single",
        "particle": "cml:block/fluid_block_tank",
        "6": "cml:block/null",
    }

def load(pos, suffix):
    p = os.path.join(SRC, f"block_{pos}{suffix}.json")
    return json.load(open(p))

def emit(pos, mask, exterior, source_suffix, keep_all=False):
    """exterior: set of world dirs ('north'...). keep_all: ignore exterior, keep every face real."""
    d = load(pos, source_suffix)
    out = {"credit": "Made with Blockbench", "parent": "block/block", "textures": texmap(pos), "elements": []}
    for el in d["elements"]:
        ne = copy.deepcopy(el)
        for side in list(ne["faces"].keys()):
            if side not in HORIZ:
                continue  # up/down always kept
            f = ne["faces"][side]
            disp = OPP[side]  # world side this local face shows on after the 180 flip
            keep = keep_all or (disp in exterior)
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
