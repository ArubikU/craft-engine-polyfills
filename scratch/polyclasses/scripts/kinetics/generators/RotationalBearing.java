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
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RotationalBearing {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _advanceHeadAngle(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "head_last_tick";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("last", scriptValue2);
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
        builder.val("now", scriptValue3);
        if (scriptValue2.asNum() <= 0.0) {
            ScriptValue scriptValue4 = scriptValue3;
            builder.val("last", scriptValue4);
        }
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)(scriptValue3.asNum() - scriptContext.getNum("last"))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 100.0)), (ScriptContext)scriptContext);
        builder.val("elapsed", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            Object object4;
            String string = "head_angle";
            String string3 = "int";
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object5;
                String string4 = "head_angle";
                String string5 = "int";
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                    object4 = polyClassMachine_v4.tm$34_get_typed(string4, string5);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)object4, (ScriptValue)ScriptValue.of((double)(scriptValue5.asNum() * scriptContext.getNum("rpm_value") * 0.3)));
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue8));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "head_last_tick";
            String string6 = "int";
            ScriptValue scriptValue10 = scriptValue3;
            if (scriptValue9 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string6, scriptValue10));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _stop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$56_report_su(d));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("rpm_value",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0));
        RotationalBearing._advanceHeadAngle(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue _spinAxis(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v4 != null ? polyClassMachine_v4.pg$129_facing_dy() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "y");
        }
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v42 != null ? polyClassMachine_v42.pg$133_facing_dx() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "x");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object.asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0))) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "false"));
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        RotationalBearing._stop(builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asNum() <= 0.0) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$191_contraption() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "assembled";
                String string3 = "int";
                ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0);
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v42.tm$82_set_typed(string, string3, scriptValue6));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "contraption_uuid";
                String string4 = "string";
                ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "");
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v43.tm$82_set_typed(string, string4, scriptValue8));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0)))) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("contraption");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue9, (ScriptValue)RotationalBearing._spinAxis(builder4), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0)), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder5);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        double d = 5.0;
        double d2 = Math.floor(5.0 == 0.0 ? 0.0 : (scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() / d);
        ScriptValue scriptValue10 = ScriptValue.of((double)d2);
        builder.val("su", scriptValue10);
        PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (polyClassMachine_v44 != null ? polyClassMachine_v44.tg$199_is_overstressed() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("contraption");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue11, (ScriptValue)RotationalBearing._spinAxis(builder6), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0)), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                double d3 = d2;
                if (scriptValue12 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object5);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v45.tm$56_report_su(d3));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("rpm_value",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0));
            RotationalBearing._advanceHeadAngle(builder7);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("contraption");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            v6 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue13, (ScriptValue)RotationalBearing._spinAxis(builder8), (ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            double d4 = d2;
            if (scriptValue14 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v46 = new PolyClassMachine_v4(object6);
                v7 = ScriptValue.of((boolean)polyClassMachine_v46.tm$56_report_su(d4));
            } else {
                v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
            }
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("rpm_value", scriptContext.getClassOrVar("rpm"));
        RotationalBearing._advanceHeadAngle(builder9);
        FILE_SCOPE = builder.build();
    }
}
