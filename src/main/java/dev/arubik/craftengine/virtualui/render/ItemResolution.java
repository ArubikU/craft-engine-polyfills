package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.DynamicFieldResolver;
import dev.arubik.craftengine.virtualui.model.ItemSource;
import dev.arubik.craftengine.virtualui.model.WidgetRenderContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;

/**
 * Resolves an item id string to an NMS {@link ItemStack} — tries CraftEngine's own item registry
 * FIRST (so a widget can show a CraftEngine custom item, e.g. {@code "cml:crate_acacia"}, not just
 * vanilla materials), falling back to vanilla's {@code BuiltInRegistries.ITEM}. Same resolution
 * order and pack-agnostic path fallback as {@code ItemType.create(id, count)} — see that method's
 * own comment for why the path fallback exists (an item from CraftEngine's OTHER bundled packs,
 * e.g. {@code default_assets}, doesn't resolve via {@code CraftEngineItems.byId} alone).
 *
 * <p>{@link #resolve(ItemSource, Player, WidgetRenderContext, String)} is the full entry point
 * used by every item-bearing widget (see {@code Widget.ItemSource}): a script can hand a widget
 * an already-built {@code ScriptValue.Item} directly (full fidelity — custom NBT, a specific
 * player's held item, whatever), the same convention {@code Menu.set_item}/{@code
 * Dialog.body_item} already use, instead of being limited to a bare id string.
 */
public final class ItemResolution {

    private ItemResolution() {}

    public static ItemStack resolve(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            net.momirealms.craftengine.core.util.Key key = net.momirealms.craftengine.core.util.Key.of(id);
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(key);
            if (def == null) {
                var byPath = net.momirealms.craftengine.bukkit.item.BukkitItemManager.instance()
                        .getItemDefinitionByPath(key.value());
                if (byPath.isPresent() && byPath.get() instanceof net.momirealms.craftengine.bukkit.item.BukkitItemDefinition bukkitDef) {
                    def = bukkitDef;
                }
            }
            if (def != null) {
                org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                return org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
            }
        } catch (Throwable ignored) {}
        try {
            Identifier loc = Identifier.parse(id.contains(":") ? id : "minecraft:" + id);
            Item item = BuiltInRegistries.ITEM.getValue(loc);
            return item != null ? new ItemStack(item) : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** Full {@link ItemSource} resolution: a literal already-built stack wins outright; a
     *  {@code .pf:} whole-field script ref is evaluated and read back as EITHER an id or a built
     *  item; a {@code ${expr}}-only dynamic string resolves through {@link DynamicFieldResolver}'s
     *  text path (always a plain id, same as any other inline substitution) and then through this
     *  class's normal id lookup; a literal id goes through the same CE-then-vanilla lookup
     *  {@link #resolve(String)} does. */
    public static ItemStack resolve(ItemSource source, Player player, WidgetRenderContext ctx, String contextTypeName) {
        if (source == null || source.isEmpty()) return null;
        if (source.literalStack() instanceof ItemStack stack) return stack.copy();
        if (source.scriptRef() != null) {
            String ref = source.scriptRef();
            if (ref.contains(".pf:")) {
                dev.arubik.craftengine.script.ScriptValue result =
                        DynamicFieldResolver.resolveValue(ref, player, ctx, contextTypeName);
                if (result instanceof dev.arubik.craftengine.script.ScriptValue.Item item && item.stack() != null) {
                    return item.stack().copy();
                }
                if (result != null && result != dev.arubik.craftengine.script.ScriptValue.NULL) {
                    return resolve(result.asStr());
                }
                return null;
            }
            return resolve(DynamicFieldResolver.resolveText(ref, player, ctx, contextTypeName));
        }
        return resolve(source.literalId());
    }
}
