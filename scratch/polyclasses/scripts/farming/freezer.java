/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin2((String)"make_map", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:water")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:ice")), (ScriptContext)scriptContext);
        builder.val("WATER_TO_ICE", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            double d = 4.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$86_blocks_in_range(d);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("blocks", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ice_id");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("block", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("WATER_TO_ICE");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue7, (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("null"), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ice_id", scriptValue8);
                scriptValue5 = scriptValue8;
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d = 4.0;
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v4.tm$94_nearby_entities(d);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        if (list2 != null) {
            for (ScriptValue scriptValue10 : list2) {
                builder.val("entity", scriptValue10);
                if (!((scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "freeze", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Freezer.class, 120.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
