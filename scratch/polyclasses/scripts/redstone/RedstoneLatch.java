/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RedstoneLatch {
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
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object2.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("cur_power", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "prev_power";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object3);
                object = polyClassMachine_v22.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("prev_power", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "prev_power";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine_v23.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        if (d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneLatch.class, 0.0)))) {
            Object object5;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "latched";
                String string4 = "int";
                if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v24 = new PolyClassMachine_v2(object6);
                    object5 = polyClassMachine_v24.tm$34_get_typed(string, string4);
                } else {
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object5;
            builder.val("latched", scriptValue8);
            double d2 = scriptValue8.asNum() > 0.0 ? 0.0 : 1.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)d2);
            builder.val("new_state", scriptValue9);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "latched";
                String string5 = "int";
                ScriptValue scriptValue11 = ScriptValue.of((double)d2);
                if (scriptValue10 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v25.tm$82_set_typed(string, string5, scriptValue11));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                double d3;
                double d4 = d3 = d2 > 0.0 ? 15.0 : 0.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object8);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v26.tm$108_emit_redstone(d3));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
