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
package dev.arubik.craftengine.script.gen.farming;

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
public final class Freezer {
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
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.callBuiltin2((String)"make_map", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:water")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:ice")), (ScriptContext)scriptContext);
        builder.val("WATER_TO_ICE", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            double d = 4.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$86_blocks_in_range(d) : PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("blocks", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("ice_id");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("block", scriptValue5);
                ScriptValue scriptValue6 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue, (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("null"), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ice_id", scriptValue6);
                scriptValue4 = scriptValue6;
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        }
        if (scriptValue2 != ScriptValue.NULL) {
            double d = 4.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$94_nearby_entities(d) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("entities", scriptValue7);
        List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue7);
        if (list2 != null) {
            for (ScriptValue scriptValue8 : list2) {
                builder.val("entity", scriptValue8);
                if (!((scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                Object object3 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "freeze", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Freezer.class, 120.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
