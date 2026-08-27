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
import java.util.ArrayList;

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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue8));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string3));
                arrayList2.add(scriptValue8);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                object = polyClassMachine.tm$34_get_typed(string, string4);
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                arrayList3.add(ScriptValue.of((String)string4));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue12));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string5));
                arrayList4.add(scriptValue12);
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string3));
                arrayList2.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
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
                ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue8));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    arrayList3.add(ScriptValue.of((String)string4));
                    arrayList3.add(scriptValue8);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList3, (ScriptContext)scriptContext);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                    object5 = polyClassMachine.tm$34_get_typed(string, string5);
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(ScriptValue.of((String)string));
                    arrayList4.add(ScriptValue.of((String)string5));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, arrayList4, (ScriptContext)scriptContext);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                    v5 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue13));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    arrayList5.add(ScriptValue.of((String)string6));
                    arrayList5.add(scriptValue13);
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue7));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string3));
                arrayList2.add(scriptValue7);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string3));
                arrayList2.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$129_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("tanks", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("tanks");
        Object object4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object4.asNum() <= 0.0) {
            return ScriptValue.of((String)"false");
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tanks");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("tank", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("tank");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"is_empty"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool()) {
            return ScriptValue.of((String)"false");
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object5);
                object = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("t_rpm", scriptValue8);
        if (scriptValue8.asNum() <= 0.0) {
            return ScriptValue.of((String)"false");
        }
        return ScriptValue.of((String)"true");
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        PolyClassMachine polyClassMachine3;
        ScriptValue scriptValue;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        PolyClassMachine polyClassMachine4;
        Object object7;
        Object object8;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"polyfills:steam"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((String)"rpm"));
        arrayList2.add(ScriptValue.of((double)32.0));
        arrayList2.add(ScriptValue.of((String)"su"));
        arrayList2.add(ScriptValue.of((double)64.0));
        arrayList2.add(ScriptValue.of((String)"per_tick"));
        arrayList2.add(ScriptValue.of((double)10.0));
        arrayList.add(ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext));
        arrayList.add(ScriptValue.of((String)"polyfills:heavy_steam"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"rpm"));
        arrayList3.add(ScriptValue.of((double)64.0));
        arrayList3.add(ScriptValue.of((String)"su"));
        arrayList3.add(ScriptValue.of((double)128.0));
        arrayList3.add(ScriptValue.of((String)"per_tick"));
        arrayList3.add(ScriptValue.of((double)15.0));
        arrayList.add(ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("GAS_SPECS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            String string = "target_rpm";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine5 = new PolyClassMachine(object9);
                object8 = polyClassMachine5.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string2));
                object8 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            object8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object8;
        builder.val("target_rpm", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            String string = "target_su";
            String string3 = "int";
            if (scriptValue5 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine6 = new PolyClassMachine(object10);
                object7 = polyClassMachine6.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((String)string));
                arrayList5.add(ScriptValue.of((String)string3));
                object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object7;
        builder.val("target_su", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine4.pg$129_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("gas_tanks", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("gas_tanks");
        Object object11 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object11.asNum() <= 0.0) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d = 0.0;
                if (scriptValue10 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object12);
                    v3 = ScriptValue.of((boolean)polyClassMachine7.tm$106_set_rpm_output(d));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((double)d));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                double d = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine8 = new PolyClassMachine(object13);
                    v4 = ScriptValue.of((boolean)polyClassMachine8.tm$56_report_su(d));
                } else {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((double)d));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("gas_tanks");
        if (scriptValue12 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add(ScriptValue.of((double)0.0));
            object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList8, (ScriptContext)scriptContext);
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object6;
        builder.val("tank", scriptValue13);
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("tank");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
            arrayList9.add(ScriptValue.of((String)"level"));
            object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList9, (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object5;
        builder.val("gas_level", scriptValue15);
        ScriptValue scriptValue16 = ScriptValue.of((String)"");
        builder.val("gas_id", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("null");
        builder.val("spec", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("tank");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
            arrayList10.add(ScriptValue.of((String)"is_empty"));
            object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList10, (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        if (object4.asBool() ^ true) {
            Object object14;
            Object object15;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("tank");
            if (scriptValue19 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(ScriptValue.of((String)"contents_key"));
                object15 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, arrayList11, (ScriptContext)scriptContext);
            } else {
                object15 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = object15;
            builder.val("gas_id", scriptValue20);
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("GAS_SPECS");
            if (scriptValue21 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                arrayList12.add(scriptValue20);
                object14 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, arrayList12, (ScriptContext)scriptContext);
            } else {
                object14 = ScriptValue.NULL;
            }
            ScriptValue scriptValue22 = object14;
            builder.val("spec", scriptValue22);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("spec"), (ScriptValue)scriptContext.getClassOrVar("null")) || scriptValue15.asNum() <= 0.0) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                double d = 0.0;
                if (scriptValue23 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine9 = new PolyClassMachine(object16);
                    v10 = ScriptValue.of((boolean)polyClassMachine9.tm$106_set_rpm_output(d));
                } else {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add(ScriptValue.of((double)d));
                    v10 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue23, arrayList13, (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object17;
                double d = 0.0;
                if (scriptValue24 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine10 = new PolyClassMachine(object17);
                    v11 = ScriptValue.of((boolean)polyClassMachine10.tm$56_report_su(d));
                } else {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(ScriptValue.of((double)d));
                    v11 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, arrayList14, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("spec");
        if (scriptValue25 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
            arrayList15.add(ScriptValue.of((String)"rpm"));
            object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue25, arrayList15, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = object3;
        builder.val("base_rpm", scriptValue26);
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("spec");
        if (scriptValue27 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
            arrayList16.add(ScriptValue.of((String)"su"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, arrayList16, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = object2;
        builder.val("base_su", scriptValue28);
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("spec");
        if (scriptValue29 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
            arrayList17.add(ScriptValue.of((String)"per_tick"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, arrayList17, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue30 = object;
        builder.val("base_per_tick", scriptValue30);
        if (scriptValue4.asNum() <= 0.0) {
            ScriptValue scriptValue31 = scriptValue26;
            builder.val("target_rpm", scriptValue31);
            ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
            if (scriptValue32 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object18;
                String string = "target_rpm";
                String string4 = "int";
                ScriptValue scriptValue33 = scriptValue31;
                if (scriptValue32 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine11 = new PolyClassMachine(object18);
                    v15 = ScriptValue.of((boolean)polyClassMachine11.tm$82_set_typed(string, string4, scriptValue33));
                } else {
                    ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                    arrayList18.add(ScriptValue.of((String)string));
                    arrayList18.add(ScriptValue.of((String)string4));
                    arrayList18.add(scriptValue33);
                    v15 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue32, arrayList18, (ScriptContext)scriptContext);
                }
            } else {
                v15 = ScriptValue.NULL;
            }
        }
        if (scriptValue6.asNum() <= 0.0) {
            ScriptValue scriptValue34 = scriptValue28;
            builder.val("target_su", scriptValue34);
            ScriptValue scriptValue35 = scriptContext.getClassOrVar("Machine");
            if (scriptValue35 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object19;
                String string = "target_su";
                String string5 = "int";
                ScriptValue scriptValue36 = scriptValue34;
                if (scriptValue35 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine12 = new PolyClassMachine(object19);
                    v16 = ScriptValue.of((boolean)polyClassMachine12.tm$82_set_typed(string, string5, scriptValue36));
                } else {
                    ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                    arrayList19.add(ScriptValue.of((String)string));
                    arrayList19.add(ScriptValue.of((String)string5));
                    arrayList19.add(scriptValue36);
                    v16 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue35, arrayList19, (ScriptContext)scriptContext);
                }
            } else {
                v16 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue37 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)1.0), (ScriptValue)((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine3.pg$149_overclock() : PolyDispatch.bootstrapGet("memberGet", "overclock", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
        double d5 = Math.floor(scriptValue30.asNum() * d4 * scriptValue37.asNum() * (1.0 - (scriptValue41 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue41)) != null ? polyClassMachine2.pg$136_efficiency() : PolyDispatch.bootstrapGet("memberGet", "efficiency", (ScriptValue)scriptValue41, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum()));
        ScriptValue scriptValue42 = ScriptValue.of((double)d5);
        builder.val("consume_per_tick", scriptValue42);
        double d6 = Math.max(1.0, scriptContext.getNum("consume_per_tick"));
        ScriptValue scriptValue43 = ScriptValue.of((double)d6);
        builder.val("consume_per_tick", scriptValue43);
        ScriptValue scriptValue44 = scriptContext.getClassOrVar("Machine");
        Object object20 = scriptValue44 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue44)) != null ? polyClassMachine.pg$165_is_overstressed() : PolyDispatch.bootstrapGet("memberGet", "is_overstressed", (ScriptValue)scriptValue44, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object20.asBool()) {
            ScriptValue scriptValue45 = scriptContext.getClassOrVar("Machine");
            if (scriptValue45 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object21;
                double d7 = 0.0;
                if (scriptValue45 instanceof ScriptValue.Obj && (object21 = (obj = (ScriptValue.Obj)scriptValue45).instance()) != null && !(object21 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine13 = new PolyClassMachine(object21);
                    v18 = ScriptValue.of((boolean)polyClassMachine13.tm$106_set_rpm_output(d7));
                } else {
                    ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                    arrayList20.add(ScriptValue.of((double)d7));
                    v18 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue45, arrayList20, (ScriptContext)scriptContext);
                }
            } else {
                v18 = ScriptValue.NULL;
            }
            ScriptValue scriptValue46 = scriptContext.getClassOrVar("Machine");
            if (scriptValue46 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object22;
                double d8 = -d2;
                if (scriptValue46 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue46).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine14 = new PolyClassMachine(object22);
                    v19 = ScriptValue.of((boolean)polyClassMachine14.tm$56_report_su(d8));
                } else {
                    ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
                    arrayList21.add(ScriptValue.of((double)d8));
                    v19 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue46, arrayList21, (ScriptContext)scriptContext);
                }
            } else {
                v19 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue47 = scriptContext.getClassOrVar("Machine");
            if (scriptValue47 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object23;
                double d9 = d;
                if (scriptValue47 instanceof ScriptValue.Obj && (object23 = (obj = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object23 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine15 = new PolyClassMachine(object23);
                    v20 = ScriptValue.of((boolean)polyClassMachine15.tm$106_set_rpm_output(d9));
                } else {
                    ArrayList<ScriptValue> arrayList22 = new ArrayList<ScriptValue>();
                    arrayList22.add(ScriptValue.of((double)d9));
                    v20 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue47, arrayList22, (ScriptContext)scriptContext);
                }
            } else {
                v20 = ScriptValue.NULL;
            }
            ScriptValue scriptValue48 = scriptContext.getClassOrVar("Machine");
            if (scriptValue48 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object24;
                double d10 = -d2;
                if (scriptValue48 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue48).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine16 = new PolyClassMachine(object24);
                    v21 = ScriptValue.of((boolean)polyClassMachine16.tm$56_report_su(d10));
                } else {
                    ArrayList<ScriptValue> arrayList23 = new ArrayList<ScriptValue>();
                    arrayList23.add(ScriptValue.of((double)d10));
                    v21 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue48, arrayList23, (ScriptContext)scriptContext);
                }
            } else {
                v21 = ScriptValue.NULL;
            }
            ScriptValue scriptValue49 = scriptContext.getClassOrVar("Machine");
            if (scriptValue49 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object25;
                String string = "vapor";
                double d11 = d6;
                if (scriptValue49 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine17 = new PolyClassMachine(object25);
                    v22 = ScriptValue.of((boolean)polyClassMachine17.tm$88_consume_gas(string, d11));
                } else {
                    ArrayList<ScriptValue> arrayList24 = new ArrayList<ScriptValue>();
                    arrayList24.add(ScriptValue.of((String)string));
                    arrayList24.add(ScriptValue.of((double)d11));
                    v22 = PolyDispatch.bootstrapCall("memberCall", "consume_gas", (ScriptValue)scriptValue49, arrayList24, (ScriptContext)scriptContext);
                }
            } else {
                v22 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
