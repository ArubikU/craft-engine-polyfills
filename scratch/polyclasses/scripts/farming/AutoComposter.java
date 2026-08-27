/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "compost_level";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        Object object3 = scriptContext.getClassOrVar("item");
        ScriptValue scriptValue6 = ScriptValue.of((boolean)bl);
        ScriptValue scriptValue7 = scriptValue3;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue9;
                CallSite callSite;
                ScriptValue.Obj obj2;
                Object object5;
                ScriptValue scriptValue10;
                builder.val("i", scriptValue8);
                if (!(scriptValue6.asBool() ^ true)) continue;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue11 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue12 = scriptValue8;
                if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                    callSite = polyClassContainer.tm$0_get_item(scriptValue12.asNum());
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("item", (ScriptValue)callSite2);
                object3 = callSite2;
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)object3, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("COMPOST_VALUES");
                ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue13, (ScriptValue)(object3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)object3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("compost_value", scriptValue14);
                scriptValue5 = scriptValue14;
                if (!(scriptValue5.asNum() > 0.0)) continue;
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue15 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue16 = scriptValue8;
                double d = 1.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v2 = polyClassContainer.tm$6_remove_item(scriptValue16.asNum(), d);
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)scriptValue5);
                builder.val("level", scriptValue17);
                scriptValue7 = scriptValue17;
                boolean bl2 = true;
                ScriptValue scriptValue18 = ScriptValue.of((boolean)true);
                builder.val("found", scriptValue18);
                scriptValue6 = scriptValue18;
            }
        }
        if (scriptValue6.asBool()) {
            ScriptValue scriptValue19;
            if (scriptValue7.asNum() >= 8.0) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue20;
                ScriptValue scriptValue21 = ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", AutoComposter.class, "minecraft:bone_meal")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", AutoComposter.class, 1.0)), (ScriptContext)scriptContext);
                builder.val("bonemeal", scriptValue21);
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue22 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue20 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue23 = scriptValue21;
                if (scriptValue22 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object6);
                    v3 = polyClassContainer.tm$12_push(scriptValue23);
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                }
                double d = 0.0;
                ScriptValue scriptValue24 = ScriptValue.of((double)0.0);
                builder.val("level", scriptValue24);
            }
            if ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "compost_level";
                String string3 = "int";
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("level");
                if (scriptValue19 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue25));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
