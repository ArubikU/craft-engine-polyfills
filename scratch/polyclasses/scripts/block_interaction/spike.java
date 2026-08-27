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
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Spike {
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
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "atk_cd";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cooldown", scriptValue2);
        if (scriptValue2.asNum() > 0.0) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "atk_cd";
                String string3 = "int";
                ScriptValue scriptValue3 = ScriptValue.of((double)(scriptValue2.asNum() - 1.0));
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue3)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            Object object2;
            PolyClassMachine_v3 polyClassMachine_v3;
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptValue scriptValue4 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d = 0.0;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue4);
            ScriptValue scriptValue5 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            builder.val("weapon", scriptValue5);
            ScriptValue scriptValue6 = ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool() ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 2.0)) : (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("base_dmg", scriptValue6);
            ScriptValue scriptValue7 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$210_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("owner_id", scriptValue7);
            if (scriptValue != ScriptValue.NULL) {
                double d2 = 0.8;
                PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$94_nearby_entities(d2) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object2;
            builder.val("entities", scriptValue8);
            double d3 = 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
            builder.val("hit_count", scriptValue9);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue8);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("is_owner");
            ScriptValue scriptValue11 = ScriptValue.of((double)d3);
            if (list != null) {
                for (ScriptValue scriptValue12 : list) {
                    builder.val("entity", scriptValue12);
                    if (!((scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Spike.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                    boolean bl = ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue7);
                    ScriptValue scriptValue13 = ScriptValue.of((boolean)bl);
                    builder.val("is_owner", scriptValue13);
                    scriptValue10 = scriptValue13;
                    if (!(scriptValue10.asBool() ^ true)) continue;
                    Object object3 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)scriptValue12, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 1.0)));
                    builder.val("hit_count", scriptValue14);
                    scriptValue11 = scriptValue14;
                }
            }
            if (scriptValue11.asNum() > 0.0) {
                if (scriptValue != ScriptValue.NULL) {
                    String string = "atk_cd";
                    String string4 = "int";
                    ScriptValue scriptValue15 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 10.0);
                    PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                    v4 = polyClassMachine_v34 != null ? ScriptValue.of((boolean)polyClassMachine_v34.tm$82_set_typed(string, string4, scriptValue15)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
