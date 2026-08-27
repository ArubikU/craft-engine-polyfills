/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                object2 = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue7));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                object = polyClassMachine.tm$34_get_typed(string, string4);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                v3 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue11));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue8));
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                    object5 = polyClassMachine.tm$34_get_typed(string, string5);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue13));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$138_gas_tanks() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
            ScriptValue.Obj obj;
            Object object3;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
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
        PolyClassMachine polyClassMachine;
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
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object2 = polyClassMachine2.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object4);
                object = polyClassMachine3.tm$34_get_typed(string, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("target_su", scriptValue9);
        PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue10 = polyClassMachine4 != null ? polyClassMachine4.pg$138_gas_tanks() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("gas_tanks", scriptValue10);
        Object object5 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object5.asNum() <= 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine5 = new PolyClassMachine(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine5.tm$106_set_rpm_output(d));
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
                    PolyClassMachine polyClassMachine6 = new PolyClassMachine(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine6.tm$56_report_su(d));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue13 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("tank", scriptValue13);
        ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "level")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("gas_level", scriptValue14);
        ScriptValue scriptValue15 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "");
        builder.val("gas_id", scriptValue15);
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("null");
        builder.val("spec", scriptValue16);
        if ((scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "is_empty")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue17 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "contents_key")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("gas_id", scriptValue17);
            ScriptValue scriptValue18 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("spec", scriptValue18);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("spec"), (ScriptValue)scriptContext.getClassOrVar("null")) || scriptValue14.asNum() <= 0.0) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
            if (scriptValue19 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                double d = 0.0;
                if (scriptValue19 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine7.tm$106_set_rpm_output(d));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                double d = 0.0;
                if (scriptValue20 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine8 = new PolyClassMachine(object9);
                    v6 = ScriptValue.of((boolean)polyClassMachine8.tm$56_report_su(d));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "rpm")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_rpm", scriptValue22);
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "su")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_su", scriptValue24);
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("spec");
        ScriptValue scriptValue26 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue25, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasMotor.class, "per_tick")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("base_per_tick", scriptValue26);
        if (scriptValue7.asNum() <= 0.0) {
            ScriptValue scriptValue27 = scriptValue22;
            builder.val("target_rpm", scriptValue27);
            ScriptValue scriptValue28 = scriptContext.getClassOrVar("Machine");
            if (scriptValue28 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "target_rpm";
                String string4 = "int";
                ScriptValue scriptValue29 = scriptValue27;
                if (scriptValue28 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine9 = new PolyClassMachine(object10);
                    v7 = ScriptValue.of((boolean)polyClassMachine9.tm$82_set_typed(string, string4, scriptValue29));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue28, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        if (scriptValue9.asNum() <= 0.0) {
            ScriptValue scriptValue30 = scriptValue24;
            builder.val("target_su", scriptValue30);
            ScriptValue scriptValue31 = scriptContext.getClassOrVar("Machine");
            if (scriptValue31 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                String string = "target_su";
                String string5 = "int";
                ScriptValue scriptValue32 = scriptValue30;
                if (scriptValue31 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine10 = new PolyClassMachine(object11);
                    v8 = ScriptValue.of((boolean)polyClassMachine10.tm$82_set_typed(string, string5, scriptValue32));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue32, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue33 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasMotor.class, 1.0)), (ScriptValue)((polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$172_overclock() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "overclock", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder.val("oc_factor", scriptValue33);
        double d = scriptContext.getNum("target_rpm") * scriptValue33.asNum();
        ScriptValue scriptValue34 = ScriptValue.of((double)d);
        builder.val("eff_rpm", scriptValue34);
        double d2 = scriptContext.getNum("target_su") * scriptValue33.asNum();
        ScriptValue scriptValue35 = ScriptValue.of((double)d2);
        builder.val("eff_su", scriptValue35);
        double d3 = scriptValue22.asNum();
        double d4 = d3 == 0.0 ? 0.0 : scriptContext.getNum("target_rpm") / d3;
        ScriptValue scriptValue36 = ScriptValue.of((double)d4);
        builder.val("rpm_ratio", scriptValue36);
        PolyClassMachine polyClassMachine11 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        double d5 = Math.floor(scriptValue26.asNum() * d4 * scriptValue33.asNum() * (1.0 - (polyClassMachine11 != null ? polyClassMachine11.tg$151_efficiency() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "efficiency", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()))));
        ScriptValue scriptValue37 = ScriptValue.of((double)d5);
        builder.val("consume_per_tick", scriptValue37);
        double d6 = Math.max(1.0, scriptContext.getNum("consume_per_tick"));
        ScriptValue scriptValue38 = ScriptValue.of((double)d6);
        builder.val("consume_per_tick", scriptValue38);
        PolyClassMachine polyClassMachine12 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (polyClassMachine12 != null ? polyClassMachine12.tg$199_is_overstressed() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            ScriptValue scriptValue39 = scriptContext.getClassOrVar("Machine");
            if (scriptValue39 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d7 = 0.0;
                if (scriptValue39 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue39).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine13 = new PolyClassMachine(object12);
                    v9 = ScriptValue.of((boolean)polyClassMachine13.tm$106_set_rpm_output(d7));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue40 = scriptContext.getClassOrVar("Machine");
            if (scriptValue40 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                double d8 = -d2;
                if (scriptValue40 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine14 = new PolyClassMachine(object13);
                    v10 = ScriptValue.of((boolean)polyClassMachine14.tm$56_report_su(d8));
                } else {
                    v10 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue40, (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue41 = scriptContext.getClassOrVar("Machine");
            if (scriptValue41 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                double d9 = d;
                if (scriptValue41 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine15 = new PolyClassMachine(object14);
                    v11 = ScriptValue.of((boolean)polyClassMachine15.tm$106_set_rpm_output(d9));
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue41, (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue42 = scriptContext.getClassOrVar("Machine");
            if (scriptValue42 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                double d10 = -d2;
                if (scriptValue42 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue42).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine16 = new PolyClassMachine(object15);
                    v12 = ScriptValue.of((boolean)polyClassMachine16.tm$56_report_su(d10));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue42, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue43 = scriptContext.getClassOrVar("Machine");
            if (scriptValue43 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "vapor";
                double d11 = d6;
                if (scriptValue43 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue43).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine17 = new PolyClassMachine(object16);
                    v13 = ScriptValue.of((boolean)polyClassMachine17.tm$88_consume_gas(string, d11));
                } else {
                    v13 = PolyDispatch.bootstrapCall("memberCall", "consume_gas", (ScriptValue)scriptValue43, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
