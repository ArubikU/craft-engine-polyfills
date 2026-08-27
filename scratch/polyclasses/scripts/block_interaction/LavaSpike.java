/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

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
public final class LavaSpike {
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
            ScriptValue scriptValue4 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$210_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("owner_id", scriptValue4);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.8;
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$94_nearby_entities(d) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = object2;
            builder.val("entities", scriptValue5);
            double d = 0.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)0.0);
            builder.val("hit_count", scriptValue6);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue5);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("is_owner");
            ScriptValue scriptValue8 = ScriptValue.of((double)d);
            if (list != null) {
                for (ScriptValue scriptValue9 : list) {
                    builder.val("entity", scriptValue9);
                    if (!((scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", LavaSpike.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                    boolean bl = ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue4);
                    ScriptValue scriptValue10 = ScriptValue.of((boolean)bl);
                    builder.val("is_owner", scriptValue10);
                    scriptValue7 = scriptValue10;
                    if (!(scriptValue7.asBool() ^ true)) continue;
                    Object object3 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "fire", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 80.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    Object object4 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 1.0)));
                    builder.val("hit_count", scriptValue11);
                    scriptValue8 = scriptValue11;
                }
            }
            if (scriptValue8.asNum() > 0.0) {
                if (scriptValue != ScriptValue.NULL) {
                    String string = "atk_cd";
                    String string4 = "int";
                    ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 15.0);
                    PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                    v5 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$82_set_typed(string, string4, scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
