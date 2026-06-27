# Report: `minecraft:tool` component crashes the client on CraftEngine custom items

## Summary
Adding a vanilla `minecraft:tool` data component to a CraftEngine custom item (the hammers) via
`data: components: minecraft:tool: {...}` makes the **client disconnect** the moment the server sends
that item inside a packet — `container_set_content` (inventory) or `recipe_book_add` (recipe book).
Removing the `minecraft:tool` component fixes it. Mining is instead provided by the base item
(`material: <vanilla_pickaxe>`).

## Symptom (client side)
```
io.netty.handler.codec.DecoderException: Failed to decode packet 'clientbound/minecraft:recipe_book_add'
  ...
Caused by: java.util.NoSuchElementException: No value present
  at java.base/java.util.Optional.orElseThrow(Optional.java:377)
  at net.minecraft.class_9135$25.method_64551(class_9135.java:665)   # ByteBufCodecs holder codec
  at net.minecraft.class_9135$25.decode(class_9135.java:655)
  at net.minecraft.class_9139$19.decode(...)
  at net.minecraft.class_9326$3.decode(class_9326.java:84)           # DataComponentPatch decode
  at net.minecraft.class_1799$1.decode(class_1799.java:157)          # ItemStack decode
  ... (also happened for clientbound/minecraft:container_set_content)
```
- `class_9326` = `DataComponentPatch`; `class_1799` = `ItemStack`; `class_9135` = `ByteBufCodecs`.
- `Optional.orElseThrow → No value present` = a **registry Holder lookup returned empty** while decoding
  one of the item's components.

## Server side (the same item failing to build during recipe visual generation)
When the SAME component is written as the wrong shape, CraftEngine itself throws while building the
recipe ingredient look (this is how we know CE routes it through its generic SNBT component path):
```
Cannot parse component minecraft:attribute_modifiers
  at ...ComponentItemWrapper.setComponentInternal(ComponentItemWrapper.java:164/168)
  at ...ComponentItemFactory1_20_5.setSparrowTagComponent(...)
  at ...item.processor.ComponentsProcessor.apply(...)
  at ...BukkitItemDefinition.buildItem(...)
  at ...BukkitRecipeManager.getIngredientLooks(...)
Caused by: IllegalArgumentException: DataResult.Error['Not a list: {...}']
```

## Root cause
CraftEngine builds custom-item data components through a **generic SNBT path**
(`ComponentItemWrapper.setComponentInternal` → `ComponentItemFactory1_20_5.setSparrowTagComponent`),
NOT through the vanilla codecs.

For `minecraft:tool`, each rule's `blocks` field is a **`HolderSet<Block>`** (a block tag, e.g.
`#minecraft:mineable/pickaxe`). The vanilla `/give` command builds this HolderSet with the native
codec, which serializes a *named tag* correctly for the network. CraftEngine's generic SNBT builder
instead produces a HolderSet/Holder that is **not network-resolvable**: on the wire the client's
`ByteBufCodecs` holder codec reads a reference, looks it up in the registry, gets `Optional.empty()`,
and `orElseThrow()`s → `No value present` → the whole packet fails to decode → disconnect.

In short: **CE's component parser mis-serializes the tool component's block-tag HolderSet.** Vanilla
items with the identical component work because vanilla never goes through CE's SNBT path.

## Why `attribute_modifiers` works but `tool` does not
- `attribute_modifiers` has a **native CraftEngine helper** (`data: attribute_modifiers: [ ... ]`) that
  builds the component with the proper builder → correct, network-safe component. (Using the *raw*
  `minecraft:attribute_modifiers` component instead went through the generic path and ALSO failed with
  `Not a list` / decode errors.)
- `minecraft:tool` has **no CE helper**, so the only way to set it is the raw component → generic SNBT
  path → broken HolderSet → crash.

## Trigger / reproduction
1. Define a CE item with `data: components: minecraft:tool: { rules: [ { blocks: "#minecraft:mineable/pickaxe", speed: 6, correct_for_drops: true } ] }`.
2. Make it a recipe result/ingredient (so it enters the recipe book) or put it in any container.
3. Join the server → client throws `DecoderException` on `recipe_book_add` / `container_set_content`.

Environment: MC/Paper 1.21.11, CraftEngine 26.x, client on Fabric + Polymer 0.15.2 (Polymer's
`TransformingPacketCodec` is in the stack but is not the cause — it just wraps the vanilla item codec).

## Workaround (applied)
- **Do not** set `minecraft:tool` via components on CE items.
- Set `material:` to the homolog **vanilla pickaxe** (`wooden_pickaxe`, `iron_pickaxe`,
  `diamond_pickaxe`, ...). The base item carries a correctly-serialized native tool component, so the
  hammer mines at that pickaxe's tier + speed with no crash.
- Cost: cannot customize the tool rules (no extra axe-block mining, no custom 75/85% speed, no custom
  tier-gate). Mining = exactly the base pickaxe's.

## Recommendation
- Report to CraftEngine: the generic component parser should serialize `minecraft:tool` (and any
  component containing a `HolderSet`/tag) using the vanilla codec so it is network-safe — or add a
  native `tool:` helper like the existing `attribute_modifiers:` helper.
- Until then, rely on `material:` for tool behavior.
