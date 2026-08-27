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
public final class Pulser {
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
        ScriptValue scriptValue2;
        Object object2;
        ScriptValue scriptValue3;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "duration";
            String string2 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("duration", scriptValue5);
        if (scriptValue5.asNum() <= 0.0) {
            double d = 10.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)10.0);
            builder.val("duration", scriptValue6);
        }
        if ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "prev_power";
            String string3 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string3);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("prev", scriptValue7);
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        Object object6 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = object6.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue8);
        if (d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Pulser.class, 0.0)))) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "countdown";
                String string4 = "int";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("duration");
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object7);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v22.tm$82_set_typed(string, string4, scriptValue10));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            String string = "countdown";
            String string5 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object8);
                object = polyClassMachine_v23.tm$34_get_typed(string, string5);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = object;
        builder.val("t", scriptValue11);
        if (scriptValue11.asNum() > 0.0) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                double d2 = 15.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v24 = new PolyClassMachine_v2(object9);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v24.tm$108_emit_redstone(d2));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "countdown";
                String string6 = "int";
                ScriptValue scriptValue14 = ScriptValue.of((double)(scriptValue11.asNum() - 1.0));
                if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object10);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v25.tm$82_set_typed(string, string6, scriptValue14));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                double d3 = 0.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object11);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v26.tm$108_emit_redstone(d3));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue17 = ScriptValue.of((double)d);
            if (scriptValue16 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v27 = new PolyClassMachine_v2(object12);
                v8 = ScriptValue.of((boolean)polyClassMachine_v27.tm$82_set_typed(string, string7, scriptValue17));
            } else {
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
