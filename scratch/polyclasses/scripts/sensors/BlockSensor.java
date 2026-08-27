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
package dev.arubik.craftengine.script.gen.sensors;

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

public final class BlockSensor {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object2);
                object = polyClassMachine3.tm$34_get_typed(string, string2);
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
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
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
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptContext.getClassOrVar("scan_range"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                Object object3;
                builder.val("i", scriptValue9);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue10);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                if (scriptValue11 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object4;
                    PolyClassMachine polyClassMachine4;
                    PolyClassMachine polyClassMachine5;
                    PolyClassMachine polyClassMachine6;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                    double d2 = -(scriptValue12 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine6.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue12, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue10.asNum();
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                    double d3 = -(scriptValue13 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine5.tg$130_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue10.asNum();
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                    double d4 = -(scriptValue14 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine4.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue10.asNum();
                    if (scriptValue11 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine7 = new PolyClassMachine(object4);
                        object3 = polyClassMachine7.tm$68_block_at(d2, d3, d4);
                    } else {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add(ScriptValue.of((double)d2));
                        arrayList4.add(ScriptValue.of((double)d3));
                        arrayList4.add(ScriptValue.of((double)d4));
                        object3 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext);
                    }
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = object3;
                builder.val("b", scriptValue15);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("b");
                if (!((scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("b");
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                    builder.val("matched", scriptValue18);
                    continue;
                }
                ScriptValue scriptValue19 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("matched", scriptValue19);
            }
        }
        double d5 = 0.0;
        ScriptValue scriptValue20 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue20);
        if (scriptContext.getNum("matched") > 0.0) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            double d6 = scriptContext.getNum("scan_range");
            arrayList5.add(ScriptValue.of((double)Math.floor((d6 == 0.0 ? 0.0 : scriptContext.getNum("matched") / d6) * 15.0)));
            arrayList5.add(ScriptValue.of((double)1.0));
            arrayList5.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue21 = ScriptFormula.callBuiltin((String)"clamp", arrayList5, (ScriptContext)scriptContext);
            builder.val("power", scriptValue21);
        }
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("power"));
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
        PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22)) != null ? polyClassMachine.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue22, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList6, (ScriptContext)scriptContext);
        FILE_SCOPE = builder.build();
    }
}
