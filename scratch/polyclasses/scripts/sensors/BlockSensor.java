/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassRedstone
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassRedstone;
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
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptValue scriptValue;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object2;
        PolyClassMachine_v4 polyClassMachine_v42;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object4;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object4);
                object3 = polyClassMachine_v43.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("scan_range", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 8.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)8.0);
            builder.val("scan_range", scriptValue4);
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("null");
        builder.val("filter_id", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 4.0;
        if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)d));
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("filter_item", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("filter_item");
            ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue9);
        }
        double d2 = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue10);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("scan_range"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object5;
                builder.val("i", scriptValue11);
                ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue12);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    PolyClassMachine_v4 polyClassMachine_v44;
                    PolyClassMachine_v4 polyClassMachine_v45;
                    PolyClassMachine_v4 polyClassMachine_v46;
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                    double d3 = -(scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v46 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v46.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue12.asNum();
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                    double d4 = -(scriptValue15 != ScriptValue.NULL ? ((polyClassMachine_v45 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine_v45.tg$130_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue15, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue12.asNum();
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                    double d5 = -(scriptValue16 != ScriptValue.NULL ? ((polyClassMachine_v44 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine_v44.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue12.asNum();
                    if (scriptValue13 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object6);
                        object5 = polyClassMachine_v47.tm$68_block_at(d3, d4, d5);
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(ScriptValue.of((double)d3));
                        arrayList3.add(ScriptValue.of((double)d4));
                        arrayList3.add(ScriptValue.of((double)d5));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue13, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue17 = object5;
                builder.val("b", scriptValue17);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("b");
                if (!((scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("b");
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                    builder.val("matched", scriptValue20);
                    continue;
                }
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("matched", scriptValue21);
            }
        }
        double d6 = 0.0;
        ScriptValue scriptValue22 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue22);
        if (scriptContext.getNum("matched") > 0.0) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            double d7 = scriptContext.getNum("scan_range");
            arrayList4.add(ScriptValue.of((double)Math.floor((d7 == 0.0 ? 0.0 : scriptContext.getNum("matched") / d7) * 15.0)));
            arrayList4.add(ScriptValue.of((double)1.0));
            arrayList4.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue23 = ScriptFormula.callBuiltin((String)"clamp", arrayList4, (ScriptContext)scriptContext);
            builder.val("power", scriptValue23);
        }
        ScriptValue scriptValue24 = (scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("power");
        if (scriptValue24 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Redstone")) {
            PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object);
            v3 = ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue25.asNum()));
        } else {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptValue25);
            v3 = PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue24, arrayList5, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
