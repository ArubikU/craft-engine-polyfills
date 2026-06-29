#!/usr/bin/env python3
"""Regenerate fluid_block_tank.yml (states subtree) + fbt_items.yml for the 15-mask set."""
BASE = r"D:\Github\craft-engine-polyfills\testserver\plugins\CraftEngine\resources\modern\configuration"
BLOCK = BASE + r"\block\polyfills\fluid_block_tank.yml"
ITEMS = BASE + r"\item\polyfills\fbt_items.yml"

POS = ["single", "bottom", "middle", "top"]
MASKS = ["none", "n", "e", "s", "w", "ne", "nw", "es", "sw",
         "nep", "nwp", "esp", "swp", "nesw", "solid"]

# bottom/top per position
BT = {"single": ("true", "true"), "bottom": ("true", "false"),
      "top": ("false", "true"), "middle": ("false", "false")}

def block_yml():
    head = """# Fluid Block Tank - entity-renderer (copper_tank pattern): ONE shared vanilla state + per-combo item model,
# so the (bottom x top x facing) visuals don't consume the blockstate-group pool. facing culls interior sides;
# corner masks split by footprint width: ne/nw/es/sw = windowed half-corner (w==2), *p = plain walls (w>=3).
blocks:
  cml:fluid_block_tank:
    settings:
      template:
      - default:hardness/stone
      - default:sound/metal
      overrides:
        item: cml:fluid_block_tank
        push-reaction: DESTROY
        can-occlude: false
        propagate-skylight: true
        is-redstone-conductor: false
    behavior:
      type: polyfills:fluid_block_tank
    loot:
      template: default:loot_table/basic
      arguments:
        item: cml:fluid_block_tank
    states:
      properties:
        bottom: { type: boolean, default: true }
        top: { type: boolean, default: true }
        facing: { type: polyfills:tank_facing, default: nesw }
      appearances:
"""
    app = []
    for pos in POS:
        for m in MASKS:
            name = f"{pos}_{m}"
            app.append(
                f"        {name}:\n"
                f"          state: copper_grate[waterlogged=false]\n"
                f"          model: {{ path: cml:block/fluid_block_tank/empty }}\n"
                f"          entity-renderer: {{ item: cml:fbt_{name} }}\n"
            )
    var = ["      variants:\n"]
    i = 0
    for pos in POS:
        b, t = BT[pos]
        for m in MASKS:
            var.append(f'        "bottom={b},top={t},facing={m}": {{ appearance: {pos}_{m}, id: {i} }}\n')
            i += 1
    tail = """
items:
  cml:fluid_block_tank:
    material: nether_brick
    data:
      item-name: <lang:item.cml.fluid_block_tank>
    model:
      type: minecraft:model
      path: cml:block/fluid_block_tank/block_single_face_nesw
    behavior:
      type: block_item
      block: cml:fluid_block_tank

recipes:
  cml:fluid_block_tank:
    type: shaped
    pattern: [ "PPP", "PGP", "PPP" ]
    ingredients:
      P: cml:copper_plate
      G: minecraft:glass
    result: { id: cml:fluid_block_tank, count: 1 }
"""
    return head + "".join(app) + "".join(var) + tail

def items_yml():
    out = ["# Entity-renderer item models for the fluid_block_tank (1 shared blockstate -> unlimited models).\n",
           "items:\n"]
    for pos in POS:
        for m in MASKS:
            name = f"{pos}_{m}"
            out.append(
                f"  cml:fbt_{name}:\n"
                f"    material: paper\n"
                f"    data: {{ item-name: ' ' }}\n"
                f"    model: {{ type: minecraft:model, path: cml:block/fluid_block_tank/block_{pos}_face_{m} }}\n"
            )
    return "".join(out)

open(BLOCK, "w", encoding="utf-8").write(block_yml())
open(ITEMS, "w", encoding="utf-8").write(items_yml())
print("wrote", BLOCK, "and", ITEMS, "-> 60 appearances/variants/items")
