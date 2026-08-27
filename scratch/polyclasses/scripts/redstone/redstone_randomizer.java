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
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RedstoneRandomizer {
    public static void run(ScriptContext.Builder builder) {
        block21: {
            double d;
            ScriptContext scriptContext;
            block20: {
                Object object;
                ScriptValue.Obj obj;
                Object object2;
                scriptContext = builder.peek();
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                Object object3 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                d = object3.asNum() > 0.0 ? 1.0 : 0.0;
                ScriptValue scriptValue2 = ScriptValue.of((double)d);
                builder.val("cur_power", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
                if (scriptValue3 != ScriptValue.NULL) {
                    ScriptValue.Obj obj2;
                    Object object4;
                    String string = "prev_power";
                    String string2 = "int";
                    if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                        object = polyClassMachine_v4.tm$34_get_typed(string, string2);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string2));
                        object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue4 = object;
                builder.val("prev_power", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object5;
                    String string = "prev_power";
                    String string3 = "int";
                    ScriptValue scriptValue6 = ScriptValue.of((double)d);
                    if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                        v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string3));
                        arrayList.add(scriptValue6);
                        v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
                if (!(d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)))) break block20;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)4.0));
                double d2 = Math.floor(ScriptFormula.callBuiltin((String)"random", arrayList, (ScriptContext)scriptContext).asNum());
                ScriptValue scriptValue7 = ScriptValue.of((double)d2);
                builder.val("dir", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    String string = "rand_dir";
                    String string4 = "int";
                    ScriptValue scriptValue9 = ScriptValue.of((double)d2);
                    if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                        v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string4, scriptValue9));
                    } else {
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(ScriptValue.of((String)string));
                        arrayList2.add(ScriptValue.of((String)string4));
                        arrayList2.add(scriptValue9);
                        v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList2, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object7;
                    double d3 = 15.0;
                    if (scriptValue10 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                        v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d3));
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(ScriptValue.of((double)d3));
                        v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue10, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
                break block21;
            }
            if (!(d == 0.0)) break block21;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                double d4 = 0.0;
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d4));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d4));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
    }
}
