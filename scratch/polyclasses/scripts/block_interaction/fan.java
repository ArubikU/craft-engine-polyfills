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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class Fan {
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
        Object object2;
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("power", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        double d = 15.0;
        double d2 = 15.0 == 0.0 ? 0.0 : scriptValue2.asNum() / d;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("strength", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "inversed";
            String string2 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object2 = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("inversed", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d3 = 5.0;
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object4);
                object = polyClassMachine3.tm$94_nearby_entities(d3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d3));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("entities", scriptValue7);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue7, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object5;
                Object object6;
                ScriptValue scriptValue8;
                PolyClassMachine polyClassMachine4;
                PolyClassMachine polyClassMachine5;
                PolyClassMachine polyClassMachine6;
                PolyClassMachine polyClassMachine7;
                PolyClassMachine polyClassMachine8;
                PolyClassMachine polyClassMachine9;
                ScriptValue scriptValue9;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("entity");
                if (!(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                double d4 = (scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine9 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine9.pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum();
                ScriptValue scriptValue12 = ScriptValue.of((double)d4);
                builder.val("dx", scriptValue12);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                double d5 = (scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue14 != ScriptValue.NULL ? ((polyClassMachine8 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine8.pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum();
                ScriptValue scriptValue15 = ScriptValue.of((double)d5);
                builder.val("dy", scriptValue15);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                double d6 = (scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue17 != ScriptValue.NULL ? ((polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17)) != null ? polyClassMachine7.pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum();
                ScriptValue scriptValue18 = ScriptValue.of((double)d6);
                builder.val("dz", scriptValue18);
                double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                ScriptValue scriptValue19 = ScriptValue.of((double)d7);
                builder.val("dist_sq", scriptValue19);
                if (!(d7 > 0.1)) continue;
                double d8 = Math.sqrt(d7);
                ScriptValue scriptValue20 = ScriptValue.of((double)d8);
                builder.val("dist", scriptValue20);
                double d9 = d8;
                double d10 = d9 == 0.0 ? 0.0 : d2 / d9;
                ScriptValue scriptValue21 = ScriptValue.of((double)d10);
                builder.val("force", scriptValue21);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue22 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22)) != null ? polyClassMachine6.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue22, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue23 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue23)) != null ? polyClassMachine5.pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue23, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue24 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue24)) != null ? polyClassMachine4.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue24, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"vec", arrayList, (ScriptContext)scriptContext);
                builder.val("dir", scriptValue25);
                if (scriptValue5.asNum() > 0.0) {
                    Object object7;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("dir");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(ScriptValue.of((double)(-1.0)));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue26, arrayList2, (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue27 = object7;
                    builder.val("dir", scriptValue27);
                }
                if ((scriptValue8 = scriptContext.getClassOrVar("dir")) != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((double)d10));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue28 = object6;
                builder.val("impulse", scriptValue28);
                ScriptValue scriptValue29 = scriptContext.getClassOrVar("entity");
                if (scriptValue29 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue28);
                    object5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue29, arrayList4, (ScriptContext)scriptContext);
                    continue;
                }
                object5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
