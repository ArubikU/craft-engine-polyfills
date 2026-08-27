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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$34_get_typed(string, string2);
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
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("cur"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext)).asNum(), 512.0);
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue7 = ScriptValue.of((double)d);
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue7));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "target_su";
            String string4 = "int";
            if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                object = polyClassMachine_v4.tm$34_get_typed(string, string4);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("cur_su", scriptValue9);
        if (scriptValue9.asNum() <= 0.0) {
            double d2 = 64.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)64.0);
            builder.val("cur_su", scriptValue10);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "target_su";
            String string5 = "int";
            double d3 = scriptContext.getNum("cur");
            ScriptValue scriptValue11 = ScriptValue.of((double)Math.floor(d3 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d / d3));
            if (scriptValue instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string5, scriptValue11));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_rpm";
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
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 32.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)32.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.max(scriptContext.getNum("cur") - ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        if (d == 0.0) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "target_su";
                String string4 = "int";
                ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0);
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string4, scriptValue8));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                    object5 = polyClassMachine_v4.tm$34_get_typed(string, string5);
                } else {
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object5;
            builder.val("cur_su", scriptValue11);
            if (scriptValue11.asNum() <= 0.0) {
                double d2 = 64.0;
                ScriptValue scriptValue12 = ScriptValue.of((double)64.0);
                builder.val("cur_su", scriptValue12);
            }
            if ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "target_su";
                String string6 = "int";
                double d3 = scriptContext.getNum("cur");
                ScriptValue scriptValue13 = ScriptValue.of((double)Math.floor(d3 == 0.0 ? 0.0 : scriptContext.getNum("cur_su") * d / d3));
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string6, scriptValue13));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_su";
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
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 64.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)64.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("cur"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext)).asNum(), 1024.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "target_su";
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
        builder.val("cur", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 64.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)64.0);
            builder.val("cur", scriptValue3);
        }
        double d = Math.max(scriptContext.getNum("cur") - ScriptFormula.callBuiltin1((String)"num", (ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptContext)scriptContext).asNum(), 0.0);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("new_val", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_su";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$138_gas_tanks() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("tanks", scriptValue2);
        Object object2 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object2.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("tanks");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("tank");
        if ((scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "false");
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                object = polyClassMachine_v42.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("t_rpm", scriptValue7);
        if (scriptValue7.asNum() <= 0.0) {
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$106_set_rpm_output(d));
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$56_report_su(d));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptValue scriptValue4;
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
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("GAS_SPECS", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v42.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("target_rpm", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "target_su";
            String string3 = "int";
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v43.tm$34_get_typed(string, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("target_su", scriptValue9);
        PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue10 = polyClassMachine_v44 != null ? polyClassMachine_v44.pg$138_gas_tanks() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("gas_tanks", scriptValue10);
        Object object5 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object5.asNum() <= 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v45.tm$106_set_rpm_output(d));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d = 0.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v46 = new PolyClassMachine_v4(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v46.tm$56_report_su(d));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("gas_tanks");
        ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue14);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("tank");
        ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "level")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("gas_level", scriptValue16);
        ScriptValue scriptValue17 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "");
        builder.val("gas_id", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("null");
        builder.val("spec", scriptValue18);
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("tank");
        if ((scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("tank");
            ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "contents_key")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("gas_id", scriptValue21);
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("GAS_SPECS");
            ScriptValue scriptValue23 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)scriptValue21, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("spec", scriptValue23);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("spec"), (ScriptValue)scriptContext.getClassOrVar("null")) || scriptValue16.asNum() <= 0.0) {
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                double d = 0.0;
                if (scriptValue24 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v47.tm$106_set_rpm_output(d));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
            if (scriptValue25 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                double d = 0.0;
                if (scriptValue25 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v48 = new PolyClassMachine_v4(object9);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v48.tm$56_report_su(d));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue27 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_rpm", scriptValue27);
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue29 = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_su", scriptValue29);
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue31 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue30, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_per_tick", scriptValue31);
        if (scriptValue7.asNum() <= 0.0) {
            ScriptValue scriptValue32 = scriptValue27;
            builder.val("target_rpm", scriptValue32);
            ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
            if (scriptValue33 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "target_rpm";
                String string4 = "int";
                ScriptValue scriptValue34 = scriptValue32;
                if (scriptValue33 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue33).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v49 = new PolyClassMachine_v4(object10);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v49.tm$82_set_typed(string, string4, scriptValue34));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue33, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        if (scriptValue9.asNum() <= 0.0) {
            ScriptValue scriptValue35 = scriptValue29;
            builder.val("target_su", scriptValue35);
            ScriptValue scriptValue36 = scriptContext.getClassOrVar("Machine");
            if (scriptValue36 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                String string = "target_su";
                String string5 = "int";
                ScriptValue scriptValue37 = scriptValue35;
                if (scriptValue36 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v410 = new PolyClassMachine_v4(object11);
                    v8 = ScriptValue.of((boolean)polyClassMachine_v410.tm$82_set_typed(string, string5, scriptValue37));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue36, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue37, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue38 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 1.0)), (ScriptValue)((polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v4.pg$172_overclock() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "overclock", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder.val("oc_factor", scriptValue38);
        double d = scriptContext.getNum("target_rpm") * scriptValue38.asNum();
        ScriptValue scriptValue39 = ScriptValue.of((double)d);
        builder.val("eff_rpm", scriptValue39);
        double d2 = scriptContext.getNum("target_su") * scriptValue38.asNum();
        ScriptValue scriptValue40 = ScriptValue.of((double)d2);
        builder.val("eff_su", scriptValue40);
        double d3 = scriptValue27.asNum();
        double d4 = d3 == 0.0 ? 0.0 : scriptContext.getNum("target_rpm") / d3;
        ScriptValue scriptValue41 = ScriptValue.of((double)d4);
        builder.val("rpm_ratio", scriptValue41);
        PolyClassMachine_v4 polyClassMachine_v411 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        double d5 = Math.floor(scriptValue31.asNum() * d4 * scriptValue38.asNum() * (1.0 - (polyClassMachine_v411 != null ? polyClassMachine_v411.tg$151_efficiency() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "efficiency", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()))));
        ScriptValue scriptValue42 = ScriptValue.of((double)d5);
        builder.val("consume_per_tick", scriptValue42);
        double d6 = Math.max(1.0, scriptContext.getNum("consume_per_tick"));
        ScriptValue scriptValue43 = ScriptValue.of((double)d6);
        builder.val("consume_per_tick", scriptValue43);
        PolyClassMachine_v4 polyClassMachine_v412 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (polyClassMachine_v412 != null ? polyClassMachine_v412.tg$199_is_overstressed() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            ScriptValue scriptValue44 = scriptContext.getClassOrVar("Machine");
            if (scriptValue44 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d7 = 0.0;
                if (scriptValue44 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue44).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v413 = new PolyClassMachine_v4(object12);
                    v9 = ScriptValue.of((boolean)polyClassMachine_v413.tm$106_set_rpm_output(d7));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue44, (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue45 = scriptContext.getClassOrVar("Machine");
            if (scriptValue45 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                double d8 = -d2;
                if (scriptValue45 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue45).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v414 = new PolyClassMachine_v4(object13);
                    v10 = ScriptValue.of((boolean)polyClassMachine_v414.tm$56_report_su(d8));
                } else {
                    v10 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue45, (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue46 = scriptContext.getClassOrVar("Machine");
            if (scriptValue46 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                double d9 = d;
                if (scriptValue46 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue46).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v415 = new PolyClassMachine_v4(object14);
                    v11 = ScriptValue.of((boolean)polyClassMachine_v415.tm$106_set_rpm_output(d9));
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue46, (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue47 = scriptContext.getClassOrVar("Machine");
            if (scriptValue47 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                double d10 = -d2;
                if (scriptValue47 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v416 = new PolyClassMachine_v4(object15);
                    v12 = ScriptValue.of((boolean)polyClassMachine_v416.tm$56_report_su(d10));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue47, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue48 = scriptContext.getClassOrVar("Machine");
            if (scriptValue48 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "vapor";
                double d11 = d6;
                if (scriptValue48 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue48).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v417 = new PolyClassMachine_v4(object16);
                    v13 = ScriptValue.of((boolean)polyClassMachine_v417.tm$88_consume_gas(string, d11));
                } else {
                    v13 = PolyDispatch.bootstrapCall("memberCall", "consume_gas", (ScriptValue)scriptValue48, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
