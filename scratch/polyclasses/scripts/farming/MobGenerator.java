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
        ScriptValue scriptValue;
        PolyClassMachine_v4 polyClassMachine_v4;
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
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue14 = (polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        if (scriptValue14 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("fuel", (ScriptValue)callSite2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue.Obj obj3;
            Object object4;
            ScriptValue scriptValue15;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("World");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object5;
                ScriptValue scriptValue17;
                ScriptValue scriptValue18;
                ScriptValue scriptValue19;
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("mob_type");
                PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v43 != null ? polyClassMachine_v43.pg$200_x() : ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v44 != null ? polyClassMachine_v44.pg$202_y() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 1.0)));
                PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue23 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v45 != null ? polyClassMachine_v45.pg$206_z() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                if (scriptValue16 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object5);
                    v2 = polyClassWorld.tm$8_spawn_entity(scriptValue20.asStr(), scriptValue21.asNum(), scriptValue22.asNum(), scriptValue23.asNum());
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "spawn_entity", (ScriptValue)scriptValue16, (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue24 = polyClassMachine_v46 != null ? polyClassMachine_v46.pg$120_container() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d2 = 0.0;
            double d3 = 1.0;
            if (scriptValue24 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                v3 = polyClassContainer.tm$6_remove_item(d2, d3);
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            }
        }
        FILE_SCOPE = builder.build();
    }
}
