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
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public final class XpCollector {
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
            ScriptValue.Obj obj;
            Object object2;
            double d = 6.0;
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$94_nearby_entities(d);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("entities", scriptValue2);
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("total_xp", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue2);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("entity", scriptValue5);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:experience_orb")) continue;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "xp_value", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder.val("total_xp", scriptValue6);
                scriptValue4 = scriptValue6;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        if (scriptValue4.asNum() > 0.0) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "experience";
                ScriptValue scriptValue9 = scriptValue4;
                if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$98_fill_fluid(string, scriptValue9.asNum()));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "fill_fluid", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
