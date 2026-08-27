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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class BlockSensor {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object4;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("scan_range", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 8.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)8.0);
            builder.val("scan_range", scriptValue3);
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("null");
        builder.val("filter_id", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)4.0));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("filter_item", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("filter_item");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue7);
        }
        double d = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue8);
        List list = ScriptProgram.resolveForRows((String)"range(scan_range)", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object5;
                builder.val("i", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue9);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    ScriptValue.Obj obj5;
                    Object object7;
                    ScriptValue.Obj obj6;
                    Object object8;
                    ScriptValue.Obj obj7;
                    Object object9;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    double d2 = -(scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine") ? new PolyClassMachine_v2(object9).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * scriptValue9.asNum();
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                    double d3 = -(scriptValue12 != ScriptValue.NULL ? (scriptValue12 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Machine") ? new PolyClassMachine_v2(object8).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * scriptValue9.asNum();
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                    double d4 = -(scriptValue13 != ScriptValue.NULL ? (scriptValue13 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine") ? new PolyClassMachine_v2(object7).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * scriptValue9.asNum();
                    if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                        object5 = polyClassMachine_v2.tm$68_block_at(d2, d3, d4);
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(ScriptValue.of((double)d2));
                        arrayList3.add(ScriptValue.of((double)d3));
                        arrayList3.add(ScriptValue.of((double)d4));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue10, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = object5;
                builder.val("b", scriptValue14);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("b");
                if (!((scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("b");
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                    builder.val("matched", scriptValue17);
                    continue;
                }
                ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("matched", scriptValue18);
            }
        }
        double d5 = 0.0;
        ScriptValue scriptValue19 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue19);
        if (scriptContext.getNum("matched") > 0.0) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            double d6 = scriptContext.getNum("scan_range");
            arrayList4.add(ScriptValue.of((double)Math.floor((d6 == 0.0 ? 0.0 : scriptContext.getNum("matched") / d6) * 15.0)));
            arrayList4.add(ScriptValue.of((double)1.0));
            arrayList4.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue20 = ScriptFormula.callBuiltin((String)"clamp", arrayList4, (ScriptContext)scriptContext);
            builder.val("power", scriptValue20);
        }
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(scriptContext.getClassOrVar("power"));
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
        PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)(scriptValue21 != ScriptValue.NULL ? (scriptValue21 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList5, (ScriptContext)scriptContext);
    }
}
