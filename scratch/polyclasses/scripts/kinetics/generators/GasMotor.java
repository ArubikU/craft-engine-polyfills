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
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("cur", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 32.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)32.0);
            builder.val("cur", scriptValue4);
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("cur");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("amount"));
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList, (ScriptContext)scriptContext)).asNum(), 512.0);
        ScriptValue scriptValue6 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue8 = ScriptValue.of((double)d);
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue8));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "target_su";
            String string4 = "int";
            if (scriptValue9 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                object = polyClassMachine_v2.tm$34_get_typed(string, string4);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object;
        builder.val("cur_su", scriptValue10);
        if (scriptValue10.asNum() <= 0.0) {
            double d2 = 64.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)64.0);
            builder.val("cur_su", scriptValue11);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "target_su";
            String string5 = "int";
            double d3 = scriptContext.getNum("cur");
            ScriptValue scriptValue12 = ScriptValue.of((double)Math.floor(d3 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d / d3));
            if (scriptValue instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                v4 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string5, scriptValue12));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue decreaseRpm(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        double d = scriptContext.getNum("cur");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("amount"));
        double d2 = Math.max(d - ScriptFormula.callBuiltin((String)"num", arrayList, (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d2);
        builder.val("new_val", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d2);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        if (d2 == 0.0) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "target_su";
                String string4 = "int";
                ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0);
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string4, scriptValue8));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue9;
            Object object5;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "target_su";
                String string5 = "int";
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                    object5 = polyClassMachine_v2.tm$34_get_typed(string, string5);
                } else {
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object5;
            builder.val("cur_su", scriptValue11);
            if (scriptValue11.asNum() <= 0.0) {
                double d3 = 64.0;
                ScriptValue scriptValue12 = ScriptValue.of((double)64.0);
                builder.val("cur_su", scriptValue12);
            }
            if ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "target_su";
                String string6 = "int";
                double d4 = scriptContext.getNum("cur");
                ScriptValue scriptValue13 = ScriptValue.of((double)Math.floor(d4 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d2 / d4));
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string6, scriptValue13));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue increaseSu(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_su";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("cur");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("amount"));
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList, (ScriptContext)scriptContext)).asNum(), 1024.0);
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue7 = ScriptValue.of((double)d);
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue7));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue decreaseSu(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_su";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        double d = scriptContext.getNum("cur");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("amount"));
        double d2 = Math.max(d - ScriptFormula.callBuiltin((String)"num", arrayList, (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d2);
        builder.val("new_val", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d2);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$138_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("tanks", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("tanks");
        Object object2 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object2.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tanks");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("tank");
        if ((scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue7 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object3);
                object = polyClassMachine_v22.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("t_rpm", scriptValue8);
        if (scriptValue8.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "true");
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$106_set_rpm_output(d));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$56_report_su(d));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine_v2 polyClassMachine_v2;
        PolyClassMachine_v2 polyClassMachine_v22;
        PolyClassMachine_v2 polyClassMachine_v23;
        ScriptValue scriptValue;
        PolyClassMachine_v2 polyClassMachine_v24;
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
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("GAS_SPECS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object3);
                object2 = polyClassMachine_v25.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("target_rpm", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "target_su";
            String string3 = "int";
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object4);
                object = polyClassMachine_v26.tm$34_get_typed(string, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("target_su", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine_v24 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine_v24.pg$138_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("gas_tanks", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("gas_tanks");
        Object object5 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object5.asNum() <= 0.0) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d = 0.0;
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v27 = new PolyClassMachine_v2(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v27.tm$106_set_rpm_output(d));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v28 = new PolyClassMachine_v2(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v28.tm$56_report_su(d));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("gas_tanks");
        ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue13);
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("tank");
        ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "level")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("gas_level", scriptValue15);
        ScriptValue scriptValue16 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "");
        builder.val("gas_id", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("null");
        builder.val("spec", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("tank");
        if ((scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("tank");
            ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "contents_key")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("gas_id", scriptValue20);
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("GAS_SPECS");
            ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("spec", scriptValue22);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("spec"), (ScriptValue)scriptContext.getClassOrVar("null")) || scriptValue15.asNum() <= 0.0) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                double d = 0.0;
                if (scriptValue23 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v29 = new PolyClassMachine_v2(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v29.tm$106_set_rpm_output(d));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                double d = 0.0;
                if (scriptValue24 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v210 = new PolyClassMachine_v2(object9);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v210.tm$56_report_su(d));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue26 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue25, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_rpm", scriptValue26);
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue28 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_su", scriptValue28);
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue30 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_per_tick", scriptValue30);
        if (scriptValue4.asNum() <= 0.0) {
            ScriptValue scriptValue31 = scriptValue26;
            builder.val("target_rpm", scriptValue31);
            ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
            if (scriptValue32 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "target_rpm";
                String string4 = "int";
                ScriptValue scriptValue33 = scriptValue31;
                if (scriptValue32 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v211 = new PolyClassMachine_v2(object10);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v211.tm$82_set_typed(string, string4, scriptValue33));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        if (scriptValue6.asNum() <= 0.0) {
            ScriptValue scriptValue34 = scriptValue28;
            builder.val("target_su", scriptValue34);
            ScriptValue scriptValue35 = scriptContext.getClassOrVar("Machine");
            if (scriptValue35 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                String string = "target_su";
                String string5 = "int";
                ScriptValue scriptValue36 = scriptValue34;
                if (scriptValue35 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v212 = new PolyClassMachine_v2(object11);
                    v8 = ScriptValue.of((boolean)polyClassMachine_v212.tm$82_set_typed(string, string5, scriptValue36));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue35, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue37 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 1.0)), (ScriptValue)((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v23.pg$172_overclock() : PolyDispatch.bootstrapGet("memberGet", "overclock", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        builder.val("oc_factor", scriptValue37);
        double d = scriptContext.getNum("target_rpm") * scriptValue37.asNum();
        ScriptValue scriptValue38 = ScriptValue.of((double)d);
        builder.val("eff_rpm", scriptValue38);
        double d2 = scriptContext.getNum("target_su") * scriptValue37.asNum();
        ScriptValue scriptValue39 = ScriptValue.of((double)d2);
        builder.val("eff_su", scriptValue39);
        double d3 = scriptValue26.asNum();
        double d4 = d3 == 0.0 ? 0.0 : scriptContext.getNum("target_rpm") / d3;
        ScriptValue scriptValue40 = ScriptValue.of((double)d4);
        builder.val("rpm_ratio", scriptValue40);
        ScriptValue scriptValue41 = scriptContext.getClassOrVar("Machine");
        double d5 = Math.floor(scriptValue30.asNum() * d4 * scriptValue37.asNum() * (1.0 - (scriptValue41 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue41)) != null ? polyClassMachine_v22.tg$151_efficiency() : PolyDispatch.bootstrapGet("memberGet", "efficiency", (ScriptValue)scriptValue41, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum())));
        ScriptValue scriptValue42 = ScriptValue.of((double)d5);
        builder.val("consume_per_tick", scriptValue42);
        double d6 = Math.max(1.0, scriptContext.getNum("consume_per_tick"));
        ScriptValue scriptValue43 = ScriptValue.of((double)d6);
        builder.val("consume_per_tick", scriptValue43);
        ScriptValue scriptValue44 = scriptContext.getClassOrVar("Machine");
        boolean bl = scriptValue44 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue44)) != null ? polyClassMachine_v2.tg$199_is_overstressed() : PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue44, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl) {
            ScriptValue scriptValue45 = scriptContext.getClassOrVar("Machine");
            if (scriptValue45 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d7 = 0.0;
                if (scriptValue45 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue45).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v213 = new PolyClassMachine_v2(object12);
                    v10 = ScriptValue.of((boolean)polyClassMachine_v213.tm$106_set_rpm_output(d7));
                } else {
                    v10 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue45, (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue46 = scriptContext.getClassOrVar("Machine");
            if (scriptValue46 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                double d8 = -d2;
                if (scriptValue46 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue46).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v214 = new PolyClassMachine_v2(object13);
                    v11 = ScriptValue.of((boolean)polyClassMachine_v214.tm$56_report_su(d8));
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue46, (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue47 = scriptContext.getClassOrVar("Machine");
            if (scriptValue47 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                double d9 = d;
                if (scriptValue47 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v215 = new PolyClassMachine_v2(object14);
                    v12 = ScriptValue.of((boolean)polyClassMachine_v215.tm$106_set_rpm_output(d9));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue47, (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue48 = scriptContext.getClassOrVar("Machine");
            if (scriptValue48 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                double d10 = -d2;
                if (scriptValue48 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue48).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v216 = new PolyClassMachine_v2(object15);
                    v13 = ScriptValue.of((boolean)polyClassMachine_v216.tm$56_report_su(d10));
                } else {
                    v13 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue48, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue49 = scriptContext.getClassOrVar("Machine");
            if (scriptValue49 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "vapor";
                double d11 = d6;
                if (scriptValue49 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v217 = new PolyClassMachine_v2(object16);
                    v14 = ScriptValue.of((boolean)polyClassMachine_v217.tm$88_consume_gas(string, d11));
                } else {
                    v14 = PolyDispatch.bootstrapCall("memberCall", "consume_gas", (ScriptValue)scriptValue49, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                }
            } else {
                v14 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
