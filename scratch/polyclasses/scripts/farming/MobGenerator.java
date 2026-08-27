/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
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
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "mob_index";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mob_idx", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("mob_idx", scriptValue3);
        }
        ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:zombie");
        builder.val("mob_type", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 2.0)))) {
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:skeleton");
            builder.val("mob_type", scriptValue5);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 3.0)))) {
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:spider");
            builder.val("mob_type", scriptValue6);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 4.0)))) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:wither_skeleton");
            builder.val("mob_type", scriptValue7);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 5.0)))) {
            ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:creeper");
            builder.val("mob_type", scriptValue8);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 6.0)))) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:piglin");
            builder.val("mob_type", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 7.0)))) {
            ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:enderman");
            builder.val("mob_type", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 8.0)))) {
            ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:iron_golem");
            builder.val("mob_type", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 9.0)))) {
            ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", MobGenerator.class, "minecraft:slime");
            builder.val("mob_type", scriptValue12);
        }
        ScriptValue scriptValue13 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue13);
        ScriptValue scriptValue14 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("fuel", scriptValue14);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool() ^ true) {
            PolyClassMachine_v3 polyClassMachine_v33;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("World");
            if (scriptValue15 != ScriptValue.NULL) {
                PolyClassMachine_v3 polyClassMachine_v34;
                PolyClassMachine_v3 polyClassMachine_v35;
                PolyClassMachine_v3 polyClassMachine_v36;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("mob_type");
                ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v36.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v35.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 1.0)));
                ScriptValue scriptValue19 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v34.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", MobGenerator.class, 0.5)));
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue15);
                v1 = polyClassWorld != null ? polyClassWorld.tm$8_spawn_entity(scriptValue16.asStr(), scriptValue17.asNum(), scriptValue18.asNum(), scriptValue19.asNum()) : PolyDispatch.bootstrapCall("memberCall", "spawn_entity", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v33.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d2 = 0.0;
            double d3 = 1.0;
            PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue20);
            Object object2 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(d2, d3) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
