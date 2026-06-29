#!/usr/bin/env python3
"""Apply CTM tiles to the EXISTING (hand-finetuned) fluid_block_tank wall faces.

Edits in place: only swaps the #1 (wall) texture reference of each WALL element to the
matching ctm tile cml:block/fbt_ctm_r{row}_c{col}. Geometry, UVs, lids, windows (#3/#5),
inner (#4) and top (#0) are untouched.

Tile = sheet[row][col]:
  row  <- vertical position of the cell   (top / middle / bottom / single)
  col  <- horizontal state of THAT wall   (left-end / middle / right-end / single-width)

Models are rendered 180deg-flipped about Y, so a wall cube physically at local side D
shows at world side OPP[D]; the column is computed from the world side + the mask.
"""
import json, os, glob, re

OUT = r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\resourcepack\assets\cml\models\block\fluid_block_tank"

OPP = {"north": "south", "south": "north", "east": "west", "west": "east"}

# --- EDITABLE MAPPING ---------------------------------------------------------
ROW = {"top": 0, "middle": 1, "bottom": 3, "single": 1}      # vertical -> sheet row
COL = {"L": 0, "M": 1, "R": 3, "single": 1}                  # horizontal -> sheet col
# -----------------------------------------------------------------------------

def belongs(el):
    fx, fy, fz = el["from"]; tx, ty, tz = el["to"]
    mnx, mxx = min(fx, tx), max(fx, tx); mnz, mxz = min(fz, tz), max(fz, tz)
    tx4 = (mxx - mnx) <= 4; tz4 = (mxz - mnz) <= 4
    s = set()
    if mnz < 1 and tz4:  s.add("north")
    if mxz > 15 and tz4: s.add("south")
    if mnx < 1 and tx4:  s.add("west")
    if mxx > 15 and tx4: s.add("east")
    return s

def mask_ext(mask):
    if mask == "none":          return set()
    if mask in ("solid", "nesw"): return {"north", "east", "south", "west"}
    L = {"n": "north", "e": "east", "s": "south", "w": "west"}
    return {L[c] for c in mask if c in "nesw"}

def col_for(world_side, ext):
    perp = ("west", "east") if world_side in ("north", "south") else ("north", "south")
    left = perp[0] in ext; right = perp[1] in ext
    if left and right: return "single"
    if left:           return "L"
    if right:          return "R"
    return "M"

def main():
    files = glob.glob(os.path.join(OUT, "block_*_face_*.json"))
    changed = 0
    for fp in files:
        m = re.match(r"block_(single|bottom|middle|top)_face_(.+)\.json", os.path.basename(fp))
        if not m:
            continue
        pos, mask = m.group(1), m.group(2)
        ext = mask_ext(mask)
        d = json.load(open(fp))
        tex = d.setdefault("textures", {})
        used = {}
        for el in d["elements"]:
            faces = el["faces"]
            if "up" in faces or "down" in faces:
                continue  # lid
            # only WALL elements reference #1; windows use #3/#5
            wall_faces = [f for f in faces.values() if f.get("texture") == "#1"]
            if not wall_faces:
                continue
            local = belongs(el)
            if not local:
                continue
            world_side = OPP[next(iter(local))]
            row = ROW[pos]; col = COL[col_for(world_side, ext)]
            key = f"c{row}{col}"
            tex[key] = f"cml:block/fbt_ctm_r{row}_c{col}"
            used[key] = True
            for f in wall_faces:
                f["texture"] = "#" + key
        if used:
            json.dump(d, open(fp, "w"), separators=(",", ":"))
            changed += 1
    print(f"updated {changed} models with CTM wall tiles")

if __name__ == "__main__":
    main()
