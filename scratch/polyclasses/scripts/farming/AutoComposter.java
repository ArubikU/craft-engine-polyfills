/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class AutoComposter {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:wheat"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:wheat_seeds"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:beetroot_seeds"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:melon_seeds"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:pumpkin_seeds"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:carrot"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:potato"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:beetroot"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:apple"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:cactus"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:vine"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:seagrass"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:tall_seagrass"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:kelp"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:dried_kelp"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:lily_pad"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:dandelion"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:grass"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:tall_grass"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:fern"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:large_fern"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:dead_bush"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:mushroom_stew"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 3.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:red_mushroom"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:brown_mushroom"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:bread"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 3.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:cookie"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:pumpkin"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 4.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:melon"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 4.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:melon_slice"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:hay_block"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 5.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:nether_wart"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:cocoa_beans"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:sweet_berries"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:glow_berries"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 2.0));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("COMPOST_VALUES", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "compost_level";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("level", scriptValue3);
        boolean bl = false;
        ScriptValue scriptValue4 = ScriptValue.of((boolean)false);
        builder.val("found", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 18.0)), (ScriptContext)scriptContext));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("compost_value");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("item");
        ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
        ScriptValue scriptValue8 = scriptValue3;
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                PolyClassMachine_v3 polyClassMachine_v3;
                PolyClassMachine_v3 polyClassMachine_v32;
                builder.val("i", scriptValue9);
                if (!(scriptValue7.asBool() ^ true)) continue;
                ScriptValue scriptValue10 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue11 = scriptValue9;
                PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue10);
                Object object2 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(scriptValue11.asNum()) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                builder.val("item", object2);
                scriptValue6 = object2;
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue12 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue, (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("compost_value", scriptValue12);
                scriptValue5 = scriptValue12;
                if (!(scriptValue5.asNum() > 0.0)) continue;
                ScriptValue scriptValue13 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue14 = scriptValue9;
                double d = 1.0;
                PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue13);
                Object object3 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(scriptValue14.asNum(), d) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)scriptValue5);
                builder.val("level", scriptValue15);
                scriptValue8 = scriptValue15;
                boolean bl2 = true;
                ScriptValue scriptValue16 = ScriptValue.of((boolean)true);
                builder.val("found", scriptValue16);
                scriptValue7 = scriptValue16;
            }
        }
        if (scriptValue7.asBool()) {
            if (scriptValue8.asNum() >= 8.0) {
                PolyClassMachine_v3 polyClassMachine_v3;
                ScriptValue scriptValue17 = ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:bone_meal")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0)), (ScriptContext)scriptContext);
                builder.val("bonemeal", scriptValue17);
                ScriptValue scriptValue18 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue19 = scriptValue17;
                PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue18);
                Object object4 = polyClassContainer != null ? polyClassContainer.tm$12_push(scriptValue19) : PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                double d = 0.0;
                ScriptValue scriptValue20 = ScriptValue.of((double)0.0);
                builder.val("level", scriptValue20);
            }
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "compost_level";
                String string3 = "int";
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("level");
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue21)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
