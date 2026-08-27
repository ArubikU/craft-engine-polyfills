/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassContraption;
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
public final class Windmill {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _sailCount(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("n", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("contraption");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "blocks", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue3 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                builder.val("block", scriptValue4);
                if (!ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "sail")), (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 1.0)));
                builder.val("n", scriptValue5);
                scriptValue3 = scriptValue5;
            }
        }
        return scriptValue3;
    }

    public static ScriptValue _advanceHeadAngle(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "head_last_tick";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)(scriptValue3.asNum() - scriptContext.getNum("last"))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 100.0)), (ScriptContext)scriptContext);
        builder.val("elapsed", scriptValue5);
        if (scriptValue != ScriptValue.NULL) {
            Object object2;
            String string = "head_angle";
            String string3 = "int";
            if (scriptValue != ScriptValue.NULL) {
                String string4 = "head_angle";
                String string5 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string4, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)object2, (ScriptValue)ScriptValue.of((double)(scriptValue5.asNum() * scriptContext.getNum("rpm_value") * 0.3)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "head_last_tick";
            String string6 = "int";
            ScriptValue scriptValue7 = scriptValue3;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
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
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("rpm_value",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0));
        Windmill._advanceHeadAngle(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue _spinAxis(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v3 != null ? polyClassMachine_v3.pg$129_facing_dy() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "y");
        }
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v32 != null ? polyClassMachine_v32.pg$133_facing_dx() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0))) ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "x");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0))) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "false"));
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        double d;
        Object object;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        double d2 = 8.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)8.0);
        builder.val("SAILS_PER_RPM", scriptValue2);
        double d3 = 16.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)16.0);
        builder.val("MAX_RPM", scriptValue3);
        double d4 = 8.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue4);
        double d5 = 8.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)8.0);
        builder.val("CAPACITY_PER_RPM", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue6);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asNum() <= 0.0) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue7 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue7);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "assembled";
                String string3 = "int";
                ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0);
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                v1 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$82_set_typed(string, string3, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "contraption_uuid";
                String string4 = "string";
                ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Windmill.class, "");
                PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                v2 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$82_set_typed(string, string4, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("contraption", scriptValue7);
        ScriptValue scriptValue11 = Windmill._sailCount(builder4);
        builder.val("sails", scriptValue11);
        if (scriptValue11.asNum() < d4) {
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue12 = Windmill._spinAxis(builder5);
                double d6 = 0.0;
                PolyClassContraption polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue7);
                v3 = polyClassContraption != null ? ScriptValue.of((boolean)polyClassContraption.tm$16_set_spin(scriptValue12.asStr(), d6)) : PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue7, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder6);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
        if (scriptValue13 != ScriptValue.NULL) {
            String string = "windmill_dir";
            String string5 = "int";
            PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue13);
            object = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object;
        builder.val("dir", scriptValue14);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 0.0)))) {
            double d7 = 1.0;
            ScriptValue scriptValue15 = ScriptValue.of((double)1.0);
            builder.val("dir", scriptValue15);
        }
        ScriptValue scriptValue16 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((d = d2) == 0.0 ? 0.0 : scriptValue11.asNum() / d)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Windmill.class, 1.0)), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        builder.val("speed", scriptValue16);
        double d8 = scriptValue16.asNum() * scriptContext.getNum("dir");
        ScriptValue scriptValue17 = ScriptValue.of((double)d8);
        builder.val("rpm_out", scriptValue17);
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue18 = Windmill._spinAxis(builder7);
            double d9 = d8;
            PolyClassContraption polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue7);
            v5 = polyClassContraption != null ? ScriptValue.of((boolean)polyClassContraption.tm$16_set_spin(scriptValue18.asStr(), d9)) : PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue7, (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        if (scriptValue13 != ScriptValue.NULL) {
            double d10 = d8;
            PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue13);
            v6 = polyClassMachine_v35 != null ? ScriptValue.of((boolean)polyClassMachine_v35.tm$106_set_rpm_output(d10)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("rpm_value", ScriptValue.of((double)d8));
        Windmill._advanceHeadAngle(builder8);
        double d11 = scriptValue16.asNum() * d5;
        ScriptValue scriptValue19 = ScriptValue.of((double)d11);
        builder.val("su", scriptValue19);
        if (scriptValue13 != ScriptValue.NULL) {
            double d12 = -d11;
            PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue13);
            v7 = polyClassMachine_v36 != null ? ScriptValue.of((boolean)polyClassMachine_v36.tm$56_report_su(d12)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d12), (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
