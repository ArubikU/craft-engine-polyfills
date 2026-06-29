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

# --- MAPPING (calibrated from user's sheet table) -----------------------------
# rows (vertical):   y0=top  y1=middle  y2=bottom  y3=single
# cols (horizontal): x0=1-wide/both-ends(nesw)  x1=west-end(sw)  x2=middle(s)  x3=east-end(es)
ROW = {"top": 0, "middle": 1, "bottom": 2, "single": 3}
COL = {"single": 0, "L": 1, "M": 2, "R": 3}
# -----------------------------------------------------------------------------

def principal_side(el):
    """The wall's actual facing = the side it is THINNEST against (its normal). A corner
    segment is thin on two axes; the thinner one (the 1px wall plane) is the real wall."""
    fx, fy, fz = el["from"]; tx, ty, tz = el["to"]
    mnx, mxx = min(fx, tx), max(fx, tx); mnz, mxz = min(fz, tz), max(fz, tz)
    thz = mxz - mnz; thx = mxx - mnx
    cands = []
    if mnz < 1 and thz <= 4:  cands.append((thz, "north"))
    if mxz > 15 and thz <= 4: cands.append((thz, "south"))
    if mnx < 1 and thx <= 4:  cands.append((thx, "west"))
    if mxx > 15 and thx <= 4: cands.append((thx, "east"))
    if not cands:
        return None
    cands.sort()  # thinnest first; deterministic tie-break by side name
    return cands[0][1]

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
            local = principal_side(el)
            if not local:
                continue
            world_side = OPP[local]
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
