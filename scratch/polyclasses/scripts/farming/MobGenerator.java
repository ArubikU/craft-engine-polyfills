/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class MobGenerator {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "mob_index";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v42.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("mob_idx", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)1.0);
            builder.val("mob_idx", scriptValue4);
        }
        ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:zombie");
        builder.val("mob_type", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 2.0)))) {
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:skeleton");
            builder.val("mob_type", scriptValue6);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 3.0)))) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:spider");
            builder.val("mob_type", scriptValue7);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 4.0)))) {
            ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:wither_skeleton");
            builder.val("mob_type", scriptValue8);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 5.0)))) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:creeper");
            builder.val("mob_type", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 6.0)))) {
            ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:piglin");
            builder.val("mob_type", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 7.0)))) {
            ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:enderman");
            builder.val("mob_type", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 8.0)))) {
            ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:iron_golem");
            builder.val("mob_type", scriptValue12);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 9.0)))) {
            ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:slime");
            builder.val("mob_type", scriptValue13);
        }
        ScriptValue scriptValue14 = (scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue14 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)d));
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("fuel", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue.Obj obj3;
            Object object4;
            PolyClassMachine_v4 polyClassMachine_v43;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("World");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object5;
                PolyClassMachine_v4 polyClassMachine_v44;
                PolyClassMachine_v4 polyClassMachine_v45;
                PolyClassMachine_v4 polyClassMachine_v46;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("mob_type");
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue17 != ScriptValue.NULL ? ((polyClassMachine_v46 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue17)) != null ? polyClassMachine_v46.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue19 != ScriptValue.NULL ? ((polyClassMachine_v45 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue19)) != null ? polyClassMachine_v45.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 1.0)));
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v44 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v44.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                if (scriptValue15 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object5);
                    v2 = polyClassWorld.tm$8_spawn_entity(scriptValue16.asStr(), scriptValue18.asNum(), scriptValue20.asNum(), scriptValue22.asNum());
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue16);
                    arrayList2.add(scriptValue18);
                    arrayList2.add(scriptValue20);
                    arrayList2.add(scriptValue22);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "spawn_entity", (ScriptValue)scriptValue15, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue23)) != null ? polyClassMachine_v43.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue23, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d2 = 0.0;
            double d3 = 1.0;
            if (scriptValue24 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                v3 = polyClassContainer.tm$6_remove_item(d2, d3);
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((double)d2));
                arrayList3.add(ScriptValue.of((double)d3));
                v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue24, arrayList3, (ScriptContext)scriptContext);
            }
        }
        FILE_SCOPE = builder.build();
    }
}
