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
public final class PulseTrigger {
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
        PolyClassMachine_v2 polyClassMachine_v2;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "edge_mode";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v22.tm$34_get_typed(string, string2);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("mode", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "prev_power";
            String string3 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object5);
                object2 = polyClassMachine_v23.tm$34_get_typed(string, string3);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("prev", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        Object object6 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v2.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object6.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue7);
        double d2 = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("fire", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 0.0))) && d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 0.0)))) {
            double d3 = 1.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 1.0))) && d == 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 1.0)))) {
            double d4 = 1.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 2.0))) && ScriptFormula.valuesEqual((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue5) ^ true) {
            double d5 = 1.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("fire"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 1.0)))) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "pulse_t";
                String string4 = "int";
                ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulseTrigger.class, 2.0);
                if (scriptValue12 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v24 = new PolyClassMachine_v2(object7);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v24.tm$82_set_typed(string, string4, scriptValue13));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            String string = "pulse_t";
            String string5 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object8);
                object = polyClassMachine_v25.tm$34_get_typed(string, string5);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object;
        builder.val("t", scriptValue14);
        if (scriptValue14.asNum() > 0.0) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                double d6 = 15.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object9);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v26.tm$108_emit_redstone(d6));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "pulse_t";
                String string6 = "int";
                ScriptValue scriptValue17 = ScriptValue.of((double)(scriptValue14.asNum() - 1.0));
                if (scriptValue16 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v27 = new PolyClassMachine_v2(object10);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v27.tm$82_set_typed(string, string6, scriptValue17));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            if (scriptValue18 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                double d7 = 0.0;
                if (scriptValue18 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v28 = new PolyClassMachine_v2(object11);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v28.tm$108_emit_redstone(d7));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
        if (scriptValue19 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue20 = ScriptValue.of((double)d);
            if (scriptValue19 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v29 = new PolyClassMachine_v2(object12);
                v8 = ScriptValue.of((boolean)polyClassMachine_v29.tm$82_set_typed(string, string7, scriptValue20));
            } else {
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
