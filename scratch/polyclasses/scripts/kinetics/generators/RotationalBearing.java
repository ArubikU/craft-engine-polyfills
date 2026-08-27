/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassMachine;
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
            String string = "head_last_tick";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
            Object object2;
            String string = "head_angle";
            String string3 = "int";
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string4 = "head_angle";
                String string5 = "int";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string4, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)object2, (ScriptValue)ScriptValue.of((double)(scriptValue5.asNum() * scriptContext.getNum("rpm_value") * 0.3)));
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
            v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            String string = "head_last_tick";
            String string6 = "int";
            ScriptValue scriptValue10 = scriptValue3;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
            v3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _stop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine != null ? polyClassMachine.pg$129_facing_dy() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "y");
        }
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine2 != null ? polyClassMachine2.pg$133_facing_dx() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "x");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        PolyClassContraption polyClassContraption;
        ScriptValue scriptValue2;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asNum() <= 0.0) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine != null ? polyClassMachine.pg$191_contraption() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                String string = "assembled";
                String string3 = "int";
                ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0);
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                v1 = polyClassMachine2 != null ? ScriptValue.of((boolean)polyClassMachine2.tm$82_set_typed(string, string3, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string = "contraption_uuid";
                String string4 = "string";
                ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearing.class, "");
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                v2 = polyClassMachine3 != null ? ScriptValue.of((boolean)polyClassMachine3.tm$82_set_typed(string, string4, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0)))) {
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue9 = RotationalBearing._spinAxis(builder4);
                double d = 0.0;
                PolyClassContraption polyClassContraption2 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue4);
                v3 = polyClassContraption2 != null ? ScriptValue.of((boolean)polyClassContraption2.tm$16_set_spin(scriptValue9.asStr(), d)) : PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue4, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            RotationalBearing._stop(builder5);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        double d = 5.0;
        double d2 = 5.0 == 0.0 ? 0.0 : (scriptValue4 != ScriptValue.NULL ? ((polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassContraption.tg$61_weight() : PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) / d;
        double d3 = Math.floor(d2);
        ScriptValue scriptValue10 = ScriptValue.of((double)d3);
        builder.val("su", scriptValue10);
        PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (polyClassMachine4 != null ? polyClassMachine4.tg$199_is_overstressed() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue11 = RotationalBearing._spinAxis(builder6);
                double d4 = 0.0;
                PolyClassContraption polyClassContraption3 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue4);
                v5 = polyClassContraption3 != null ? ScriptValue.of((boolean)polyClassContraption3.tm$16_set_spin(scriptValue11.asStr(), d4)) : PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue4, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                double d5 = d3;
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                v6 = polyClassMachine5 != null ? ScriptValue.of((boolean)polyClassMachine5.tm$56_report_su(d5)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("rpm_value",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearing.class, 0.0));
            RotationalBearing._advanceHeadAngle(builder7);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue13 = RotationalBearing._spinAxis(builder8);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("rpm");
            PolyClassContraption polyClassContraption4 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue4);
            v7 = polyClassContraption4 != null ? ScriptValue.of((boolean)polyClassContraption4.tm$16_set_spin(scriptValue13.asStr(), scriptValue14.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue4, (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
        if (scriptValue15 != ScriptValue.NULL) {
            double d6 = d3;
            PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
            v8 = polyClassMachine6 != null ? ScriptValue.of((boolean)polyClassMachine6.tm$56_report_su(d6)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("rpm_value", scriptContext.getClassOrVar("rpm"));
        RotationalBearing._advanceHeadAngle(builder9);
        FILE_SCOPE = builder.build();
    }
}
