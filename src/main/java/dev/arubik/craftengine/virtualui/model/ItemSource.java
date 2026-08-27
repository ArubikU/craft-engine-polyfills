package dev.arubik.craftengine.virtualui.model;

/**
 * Where a widget's item comes from — mirrors {@code Menu.set_item}/{@code Dialog.body_item}'s own
 * convention of taking a real, already-built {@code ScriptValue.Item} (an NMS {@code ItemStack},
 * kept as {@code Object} here the same way {@code CameraSession} keeps its NMS entity, so this
 * model package stays NMS-import-free) rather than forcing everything through a bare id string.
 * Exactly one of the three is set (or none — {@link #EMPTY}):
 * <ul>
 *   <li>{@code literalId} — a plain item id, e.g. {@code "cml:crate_acacia"} or
 *       {@code "minecraft:compass"}, resolved via {@code ItemResolution} (CraftEngine registry
 *       first, vanilla fallback).
 *   <li>{@code literalStack} — an actual NMS {@code ItemStack} a script already built (via
 *       {@code Item.create(...)}, pulled off a player's inventory, whatever) and handed straight
 *       to the widget — full fidelity (custom NBT, lore, enchantments, a specific player's held
 *       item) with no re-resolution.
 *   <li>{@code scriptRef} — a {@code "file.pf:func"} ref (the same convention every other dynamic
 *       VirtualUI field uses), evaluated fresh each render tick with {@code Button} bound; its
 *       result is read back as EITHER kind (a returned {@code ScriptValue.Item} is used directly,
 *       a returned string is resolved as an id) — see {@code ItemResolution#resolve}.
 * </ul>
 */
public record ItemSource(String literalId, Object literalStack, String scriptRef) {

    public static final ItemSource EMPTY = new ItemSource(null, null, null);

    public static ItemSource ofId(String id) { return id == null || id.isBlank() ? EMPTY : new ItemSource(id, null, null); }
    public static ItemSource ofStack(Object nmsStack) { return nmsStack == null ? EMPTY : new ItemSource(null, nmsStack, null); }
    public static ItemSource ofScript(String ref) { return ref == null || ref.isBlank() ? EMPTY : new ItemSource(null, null, ref); }

    public boolean isEmpty() { return literalId == null && literalStack == null && scriptRef == null; }
}
