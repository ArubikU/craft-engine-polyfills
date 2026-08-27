/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
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
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cooldown", scriptValue2);
        if (scriptValue2.asNum() > 0.0) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "atk_cd";
                String string3 = "int";
                ScriptValue scriptValue4 = ScriptValue.of((double)(scriptValue2.asNum() - 1.0));
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
                v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue4)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            Object object2;
            ScriptValue scriptValue5;
            ScriptValue scriptValue6;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue7 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue7);
            ScriptValue scriptValue8 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            builder.val("weapon", scriptValue8);
            ScriptValue scriptValue9 = ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool() ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 2.0)) : (scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("base_dmg", scriptValue9);
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue10 = polyClassMachine2 != null ? polyClassMachine2.pg$210_owner_uuid() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("owner_id", scriptValue10);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                double d2 = 0.8;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                object2 = polyClassMachine3 != null ? polyClassMachine3.tm$94_nearby_entities(d2) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object2;
            builder.val("entities", scriptValue12);
            double d3 = 0.0;
            ScriptValue scriptValue13 = ScriptValue.of((double)0.0);
            builder.val("hit_count", scriptValue13);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue12);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("is_owner");
            ScriptValue scriptValue15 = ScriptValue.of((double)d3);
            if (list != null) {
                for (ScriptValue scriptValue16 : list) {
                    builder.val("entity", scriptValue16);
                    if (!((scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Spike.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                    boolean bl = ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue10);
                    ScriptValue scriptValue17 = ScriptValue.of((boolean)bl);
                    builder.val("is_owner", scriptValue17);
                    scriptValue14 = scriptValue17;
                    if (!(scriptValue14.asBool() ^ true)) continue;
                    Object object3 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)scriptValue16, (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 1.0)));
                    builder.val("hit_count", scriptValue18);
                    scriptValue15 = scriptValue18;
                }
            }
            if (scriptValue15.asNum() > 0.0) {
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                if (scriptValue19 != ScriptValue.NULL) {
                    String string = "atk_cd";
                    String string4 = "int";
                    ScriptValue scriptValue20 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 10.0);
                    PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue19);
                    v4 = polyClassMachine4 != null ? ScriptValue.of((boolean)polyClassMachine4.tm$82_set_typed(string, string4, scriptValue20)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
