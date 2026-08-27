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
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class JumpPad {
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
        PolyClassMachine polyClassMachine;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "force";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object2 = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("force", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("force", scriptValue3);
        }
        double d = 1.0 * scriptContext.getNum("force");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("horiz_scale", scriptValue4);
        double d2 = 0.8 * scriptContext.getNum("force");
        ScriptValue scriptValue5 = ScriptValue.of((double)d2);
        builder.val("vert_power", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine.pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
        ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
        builder.val("is_vertical", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d3 = 0.7;
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object4);
                object = polyClassMachine3.tm$94_nearby_entities(d3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d3));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue9, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object5;
                Object object6;
                PolyClassMachine polyClassMachine4;
                PolyClassMachine polyClassMachine5;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("entity");
                if (!(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                if (bl) {
                    Object object7;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(ScriptValue.of((double)0.0));
                        arrayList2.add(ScriptValue.of((double)d2));
                        arrayList2.add(ScriptValue.of((double)0.0));
                        arrayList.add(ScriptFormula.callBuiltin((String)"vec", arrayList2, (ScriptContext)scriptContext));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object7 = ScriptValue.NULL;
                    continue;
                }
                ArrayList arrayList = new ArrayList();
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                arrayList3.add((ScriptValue)(scriptValue12 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine5.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                arrayList3.add(ScriptValue.of((double)0.0));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                arrayList3.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine4.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)ScriptFormula.callBuiltin((String)"vec", arrayList3, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
                builder.val("dir", (ScriptValue)callSite);
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((double)0.0));
                arrayList5.add(ScriptValue.of((double)(d2 * 0.6)));
                arrayList5.add(ScriptValue.of((double)0.0));
                arrayList4.add(ScriptFormula.callBuiltin((String)"vec", arrayList5, (ScriptContext)scriptContext));
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((double)d));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue14, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "add", (ScriptValue)object6, arrayList4, (ScriptContext)scriptContext);
                builder.val("launch", (ScriptValue)callSite2);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
                    arrayList7.add(callSite2);
                    object5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue15, arrayList7, (ScriptContext)scriptContext);
                    continue;
                }
                object5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
