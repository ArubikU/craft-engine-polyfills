/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class GasMotor {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue increaseRpm(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_rpm";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 32.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)32.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("cur"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext)).asNum(), 512.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string4 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("cur_su", scriptValue6);
        if (scriptValue6.asNum() <= 0.0) {
            double d2 = 64.0;
            ScriptValue scriptValue7 = ScriptValue.of((double)64.0);
            builder.val("cur_su", scriptValue7);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string5 = "int";
            double d3 = scriptContext.getNum("cur");
            ScriptValue scriptValue8 = ScriptValue.of((double)Math.floor(d3 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d / d3));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue decreaseRpm(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_rpm";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 32.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)32.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.max(scriptContext.getNum("cur") - ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        if (d == 0.0) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "target_su";
                String string4 = "int";
                ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0);
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            Object object2;
            if (scriptValue != ScriptValue.NULL) {
                String string = "target_su";
                String string5 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object2;
            builder.val("cur_su", scriptValue7);
            if (scriptValue7.asNum() <= 0.0) {
                double d2 = 64.0;
                ScriptValue scriptValue8 = ScriptValue.of((double)64.0);
                builder.val("cur_su", scriptValue8);
            }
            if (scriptValue != ScriptValue.NULL) {
                String string = "target_su";
                String string6 = "int";
                double d3 = scriptContext.getNum("cur");
                ScriptValue scriptValue9 = ScriptValue.of((double)Math.floor(d3 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d / d3));
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue increaseSu(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 64.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)64.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("cur"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext)).asNum(), 1024.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue decreaseSu(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 64.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)64.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.max(scriptContext.getNum("cur") - ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$138_gas_tanks() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("tanks", scriptValue2);
        Object object2 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object2.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue3);
        if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "target_rpm";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("t_rpm", scriptValue5);
        if (scriptValue5.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "true");
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
        PolyClassMachine_v3 polyClassMachine_v3;
        PolyClassMachine_v3 polyClassMachine_v32;
        PolyClassMachine_v3 polyClassMachine_v33;
        PolyClassMachine_v3 polyClassMachine_v34;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "polyfills:steam"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 32.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 64.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 10.0));
        arrayList.add(ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "polyfills:heavy_steam"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 64.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 128.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 15.0));
        arrayList.add(ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("GAS_SPECS", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "target_rpm";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("target_rpm", scriptValue3);
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "target_su";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v36 != null ? polyClassMachine_v36.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("target_su", scriptValue4);
        ScriptValue scriptValue5 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v34.pg$138_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("gas_tanks", scriptValue5);
        Object object3 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object3.asNum() <= 0.0) {
            if (scriptValue2 != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v3 = polyClassMachine_v37 != null ? ScriptValue.of((boolean)polyClassMachine_v37.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            if (scriptValue2 != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v4 = polyClassMachine_v38 != null ? ScriptValue.of((boolean)polyClassMachine_v38.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue6);
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "level")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("gas_level", scriptValue7);
        ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "");
        builder.val("gas_id", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("null");
        builder.val("spec", scriptValue9);
        if ((scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue10 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "contents_key")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("gas_id", scriptValue10);
            ScriptValue scriptValue11 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("spec", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("spec"), (ScriptValue)scriptContext.getClassOrVar("null")) || scriptValue7.asNum() <= 0.0) {
            if (scriptValue2 != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine_v3 polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v5 = polyClassMachine_v39 != null ? ScriptValue.of((boolean)polyClassMachine_v39.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            if (scriptValue2 != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine_v3 polyClassMachine_v310 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v6 = polyClassMachine_v310 != null ? ScriptValue.of((boolean)polyClassMachine_v310.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_rpm", scriptValue13);
        ScriptValue scriptValue14 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_su", scriptValue14);
        ScriptValue scriptValue15 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_per_tick", scriptValue15);
        if (scriptValue3.asNum() <= 0.0) {
            ScriptValue scriptValue16 = scriptValue13;
            builder.val("target_rpm", scriptValue16);
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "target_rpm";
                String string4 = "int";
                ScriptValue scriptValue17 = scriptValue16;
                PolyClassMachine_v3 polyClassMachine_v311 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v7 = polyClassMachine_v311 != null ? ScriptValue.of((boolean)polyClassMachine_v311.tm$82_set_typed(string, string4, scriptValue17)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        if (scriptValue4.asNum() <= 0.0) {
            ScriptValue scriptValue18 = scriptValue14;
            builder.val("target_su", scriptValue18);
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "target_su";
                String string5 = "int";
                ScriptValue scriptValue19 = scriptValue18;
                PolyClassMachine_v3 polyClassMachine_v312 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v8 = polyClassMachine_v312 != null ? ScriptValue.of((boolean)polyClassMachine_v312.tm$82_set_typed(string, string5, scriptValue19)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 1.0)), (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v33.pg$172_overclock() : PolyDispatch.bootstrapGet("memberGet", "overclock", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        builder.val("oc_factor", scriptValue20);
        double d = scriptContext.getNum("target_rpm") * scriptValue20.asNum();
        ScriptValue scriptValue21 = ScriptValue.of((double)d);
        builder.val("eff_rpm", scriptValue21);
        double d2 = scriptContext.getNum("target_su") * scriptValue20.asNum();
        ScriptValue scriptValue22 = ScriptValue.of((double)d2);
        builder.val("eff_su", scriptValue22);
        double d3 = scriptValue13.asNum();
        double d4 = d3 == 0.0 ? 0.0 : scriptContext.getNum("target_rpm") / d3;
        ScriptValue scriptValue23 = ScriptValue.of((double)d4);
        builder.val("rpm_ratio", scriptValue23);
        double d5 = Math.floor(scriptValue15.asNum() * d4 * scriptValue20.asNum() * (1.0 - (scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v32.tg$151_efficiency() : PolyDispatch.bootstrapGet("memberGet", "efficiency", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum())));
        ScriptValue scriptValue24 = ScriptValue.of((double)d5);
        builder.val("consume_per_tick", scriptValue24);
        double d6 = Math.max(1.0, scriptContext.getNum("consume_per_tick"));
        ScriptValue scriptValue25 = ScriptValue.of((double)d6);
        builder.val("consume_per_tick", scriptValue25);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v3.tg$199_is_overstressed() : PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl) {
            if (scriptValue2 != ScriptValue.NULL) {
                double d7 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v313 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v10 = polyClassMachine_v313 != null ? ScriptValue.of((boolean)polyClassMachine_v313.tm$106_set_rpm_output(d7)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            if (scriptValue2 != ScriptValue.NULL) {
                double d8 = -d2;
                PolyClassMachine_v3 polyClassMachine_v314 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v11 = polyClassMachine_v314 != null ? ScriptValue.of((boolean)polyClassMachine_v314.tm$56_report_su(d8)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
            } else {
                v11 = ScriptValue.NULL;
            }
        } else {
            if (scriptValue2 != ScriptValue.NULL) {
                double d9 = d;
                PolyClassMachine_v3 polyClassMachine_v315 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v12 = polyClassMachine_v315 != null ? ScriptValue.of((boolean)polyClassMachine_v315.tm$106_set_rpm_output(d9)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
            } else {
                v12 = ScriptValue.NULL;
            }
            if (scriptValue2 != ScriptValue.NULL) {
                double d10 = -d2;
                PolyClassMachine_v3 polyClassMachine_v316 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v13 = polyClassMachine_v316 != null ? ScriptValue.of((boolean)polyClassMachine_v316.tm$56_report_su(d10)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
            } else {
                v13 = ScriptValue.NULL;
            }
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "vapor";
                double d11 = d6;
                PolyClassMachine_v3 polyClassMachine_v317 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v14 = polyClassMachine_v317 != null ? ScriptValue.of((boolean)polyClassMachine_v317.tm$88_consume_gas(string, d11)) : PolyDispatch.bootstrapCall("memberCall", "consume_gas", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
            } else {
                v14 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
