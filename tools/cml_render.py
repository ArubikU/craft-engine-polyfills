#!/usr/bin/env python3
"""
cml_render.py — bake an iso PNG for EVERY model in the CraftEngine `modern` pack.

Variant of tools/blueprint_gen.py. Walks both namespaces (minecraft + cml), and for
each model JSON:
  * has `elements`            -> iso bake (real geometry, partial shapes correct)
  * parent = cube_*/cross/... -> synthesize a unit cube from the texture map, iso bake
  * parent = generated/handheld (flat item sprite) -> hi-res sprite composite
Outputs PNGs to  <out>/<ns>__<flattened model path>.png  (default 128px).

Run:  python tools/cml_render.py
"""
import json, math, os, sys

from PIL import Image

ASSETS = os.environ.get(
    "CML_ASSETS",
    r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\resourcepack\assets",
)
OUT = os.environ.get("CML_OUT", r"D:\Piero\Downloads\cmlfactory\assets\renders")
RES = int(os.environ.get("CML_RES", "128"))

NS_ROOTS = {ns: os.path.join(ASSETS, ns) for ns in ("minecraft", "cml")}

C30, S30 = math.cos(math.radians(30)), math.sin(math.radians(30))


def split_ns(ref, default_ns="minecraft"):
    if ref is None:
        return None, None
    ns, _, path = ref.partition(":") if ":" in ref else (default_ns, "", ref)
    return ns, path


def tex_path(ref):
    ns, path = split_ns(ref)
    root = NS_ROOTS.get(ns)
    if not root:
        return None
    p = os.path.join(root, "textures", *path.split("/")) + ".png"
    return p if os.path.exists(p) else None


def load_tex(ref):
    p = tex_path(ref)
    if not p:
        return None
    img = Image.open(p).convert("RGBA")
    if img.height > img.width and img.height % img.width == 0:  # animation strip
        img = img.crop((0, 0, img.width, img.width))
    if img.size != (16, 16):
        img = img.resize((16, 16), Image.NEAREST)
    return img


def model_path(ref):
    ns, path = split_ns(ref)
    root = NS_ROOTS.get(ns)
    if not root:
        return None
    p = os.path.join(root, "models", *path.split("/")) + ".json"
    return p if os.path.exists(p) else None


def resolve_textures(name, textures):
    seen = 0
    while name and name.startswith("#") and seen < 12:
        name = textures.get(name[1:])
        seen += 1
    return name


def merged_model(start_path):
    """Follow parent chain, merge textures (child wins), return (textures, elements, parent_name)."""
    textures, elements, parent_chain = {}, None, []
    cur = start_path
    seen = 0
    while cur and seen < 12:
        seen += 1
        try:
            with open(cur, encoding="utf-8") as fh:
                m = json.load(fh)
        except Exception:
            break
        for k, v in m.get("textures", {}).items():
            textures.setdefault(k, v)
        if elements is None and m.get("elements"):
            elements = m["elements"]
        par = m.get("parent")
        if par:
            parent_chain.append(par)
        if elements is not None:
            break
        np = model_path(par) if par else None
        if np is None:
            break
        cur = np
    return textures, elements, parent_chain


# ---- iso bake (from blueprint_gen.py, + element rotation) ----

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
            piv = m[i][i] or 1e-9
            m[i] = [v / piv for v in m[i]]
            for k in range(3):
                if k != i:
                    f = m[k][i]
                    m[k] = [a - f * b for a, b in zip(m[k], m[i])]
        return m[0][3], m[1][3], m[2][3]
    a, b, c = solve(u0, u1, u2)
    d, e, f = solve(v0, v1, v2)
    return (a, b, c, d, e, f)


def _faces(x0, y0, z0, x1, y1, z1):
    return {
        "up":    (1.00, (x0, y1, z0), (x1, y1, z0), (x0, y1, z1)),
        "south": (0.80, (x0, y1, z1), (x1, y1, z1), (x0, y0, z1)),
        "east":  (0.62, (x1, y1, z1), (x1, y1, z0), (x1, y0, z1)),
    }


def _rot(p, rotation):
    if not rotation:
        return p
    ang = math.radians(rotation.get("angle", 0))
    if ang == 0:
        return p
    ox, oy, oz = rotation.get("origin", [8, 8, 8])
    axis = rotation.get("axis", "y")
    x, y, z = p[0] - ox, p[1] - oy, p[2] - oz
    c, s = math.cos(ang), math.sin(ang)
    if axis == "x":
        y, z = y * c - z * s, y * s + z * c
    elif axis == "y":
        x, z = x * c + z * s, -x * s + z * c
    else:
        x, y = x * c - y * s, x * s + y * c
    return (x + ox, y + oy, z + oz)


def bake(textures, elements):
    def proj(p):
        x, y, z = p
        return ((x - z) * C30, (x + z) * S30 - y)

    pts = []
    for el in elements:
        fx, fy, fz = el["from"]; tx, ty, tz = el["to"]
        rot = el.get("rotation")
        for X in (fx, tx):
            for Y in (fy, ty):
                for Z in (fz, tz):
                    pts.append(proj(_rot((X, Y, Z), rot)))
    if not pts:
        return None
    minx = min(p[0] for p in pts); maxx = max(p[0] for p in pts)
    miny = min(p[1] for p in pts); maxy = max(p[1] for p in pts)
    span = max(maxx - minx, maxy - miny) or 1.0
    margin = RES * 0.07
    scale = (RES - 2 * margin) / span
    offx = margin - minx * scale + (RES - 2 * margin - (maxx - minx) * scale) / 2
    offy = margin - miny * scale + (RES - 2 * margin - (maxy - miny) * scale) / 2

    def to2d(p):
        px, py = proj(p)
        return (px * scale + offx, py * scale + offy)

    draws = []
    for el in elements:
        fx, fy, fz = el["from"]; tx, ty, tz = el["to"]
        rot = el.get("rotation")
        faces = el.get("faces", {})
        fdef = _faces(fx, fy, fz, tx, ty, tz)
        fallback = next((faces[fn] for fn in ("north", "south", "east", "west", "up", "down") if fn in faces), None)
        for fname, (shade, o, pu, pv) in fdef.items():
            fc = faces.get(fname, fallback)
            if not fc:
                continue
            texname = resolve_textures(fc.get("texture"), textures)
            tex = load_tex(texname) if texname else None
            if tex is None:
                continue
            uv = fc.get("uv", [0, 0, 16, 16])
            crop = tex.crop((min(uv[0], uv[2]), min(uv[1], uv[3]), max(uv[0], uv[2]), max(uv[1], uv[3]))).convert("RGBA")
            if crop.size[0] == 0 or crop.size[1] == 0:
                crop = tex
            o, pu, pv = _rot(o, rot), _rot(pu, rot), _rot(pv, rot)
            o2, u2, v2 = to2d(o), to2d(pu), to2d(pv)
            area = abs((u2[0] - o2[0]) * (v2[1] - o2[1]) - (u2[1] - o2[1]) * (v2[0] - o2[0]))
            if area < 0.5:
                continue
            depth = sum(o) + sum(pu) + sum(pv)
            draws.append((depth, shade, crop, o2, u2, v2))
    if not draws:
        return None
    draws.sort(key=lambda d: d[0])
    out = Image.new("RGBA", (RES, RES), (0, 0, 0, 0))
    for _, shade, crop, o2, u2, v2 in draws:
        w, h = crop.size
        coeffs = _affine([o2, u2, v2], [(0, 0), (w, 0), (0, h)])
        layer = crop.transform((RES, RES), Image.AFFINE, coeffs, resample=Image.NEAREST, fillcolor=(0, 0, 0, 0))
        if shade != 1.0:
            px = layer.load()
            for yy in range(RES):
                for xx in range(RES):
                    r, g, b, a = px[xx, yy]
                    if a:
                        px[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
        out.alpha_composite(layer)
    return out


def pick(textures, keys):
    for k in keys:
        if k in textures:
            return "#" + k
    return ("#" + next(iter(textures))) if textures else None


def synth_cube(parent, textures):
    p = parent.split(":")[-1].replace("block/", "")
    if p in ("cross", "tinted_cross", "plant", "tinted_double_plant"):
        t = pick(textures, ["cross", "0", "plant", "1", "particle"])
        faces = {f: {"texture": t} for f in ("north", "south", "east", "west", "up", "down")}
    elif p in ("cube_all",):
        t = pick(textures, ["all", "0", "particle"]); faces = {"north": {"texture": t}, "up": {"texture": t}, "east": {"texture": t}}
    elif p in ("cube_column", "cube_column_horizontal"):
        side = pick(textures, ["side", "0"]); end = pick(textures, ["end", "top", "1"]) or side
        faces = {"north": {"texture": side}, "east": {"texture": side}, "up": {"texture": end}}
    elif p in ("cube_bottom_top", "cube_top"):
        side = pick(textures, ["side", "0"]); top = pick(textures, ["top", "1"]) or side
        faces = {"north": {"texture": side}, "east": {"texture": side}, "up": {"texture": top}}
    else:  # cube / orientable / generic
        side = pick(textures, ["side", "north", "0", "all", "texture", "particle"])
        top = pick(textures, ["top", "up", "end"]) or side
        front = pick(textures, ["front", "north"]) or side
        faces = {"north": {"texture": front}, "east": {"texture": side}, "up": {"texture": top}}
    if not any(f.get("texture") for f in faces.values()):
        return None
    return [{"from": [0, 0, 0], "to": [16, 16, 16], "faces": faces}]


def flat_sprite(textures):
    layers = []
    i = 0
    while ("layer%d" % i) in textures:
        t = load_tex(textures["layer%d" % i]); i += 1
        if t:
            layers.append(t)
    if not layers:
        t = load_tex(pick(textures, ["0", "particle"]).lstrip("#")) if textures else None
        if t:
            layers.append(t)
    if not layers:
        return None
    base = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for l in layers:
        base.alpha_composite(l)
    return base.resize((RES, RES), Image.NEAREST)


def out_name(ns, rel):
    return "%s__%s" % (ns, rel.replace("/", "_").replace("\\", "_"))


def main():
    os.makedirs(OUT, exist_ok=True)
    ok = skip = err = 0
    for ns, root in NS_ROOTS.items():
        mroot = os.path.join(root, "models")
        if not os.path.isdir(mroot):
            continue
        for dirpath, _, files in os.walk(mroot):
            for fn in files:
                if not fn.endswith(".json"):
                    continue
                full = os.path.join(dirpath, fn)
                rel = os.path.relpath(full, mroot)[:-5].replace("\\", "/")
                try:
                    textures, elements, parents = merged_model(full)
                    img = None
                    if elements:
                        img = bake(textures, elements)
                    if img is None:
                        par = parents[0] if parents else ""
                        pl = par.split(":")[-1]
                        if pl.endswith(("generated", "handheld")) or any(x in pl for x in ("generated", "handheld")):
                            img = flat_sprite(textures)
                        else:
                            cube = synth_cube(par or "cube_all", textures)
                            img = bake(textures, cube) if cube else None
                    if img is None:
                        img = flat_sprite(textures)
                    if img is None:
                        skip += 1
                        continue
                    img.save(os.path.join(OUT, out_name(ns, rel) + ".png"))
                    ok += 1
                except Exception as e:
                    err += 1
                    if err <= 15:
                        print("ERR", ns, rel, repr(e))
    print("baked=%d skipped=%d errors=%d -> %s" % (ok, skip, err, OUT))


if __name__ == "__main__":
    main()
