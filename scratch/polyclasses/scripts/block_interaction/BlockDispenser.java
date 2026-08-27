/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class BlockDispenser {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("block", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
        if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            boolean bl = false;
            ScriptValue scriptValue4 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue4);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)27.0));
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
            if (list != null) {
                for (ScriptValue scriptValue5 : list) {
                    ScriptValue.Obj obj;
                    Object object;
                    PolyClassMachine_v4 polyClassMachine_v42;
                    Object object2;
                    CallSite callSite;
                    ScriptValue.Obj obj2;
                    Object object3;
                    PolyClassMachine_v4 polyClassMachine_v43;
                    builder.val("i", scriptValue5);
                    if (!(scriptContext.getBool("placed") ^ true)) continue;
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v43.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("i");
                    if (scriptValue7 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                        callSite = polyClassContainer.tm$0_get_item(scriptValue8.asNum());
                    } else {
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(scriptValue8);
                        callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
                    }
                    CallSite callSite2 = callSite;
                    builder.val("item", (ScriptValue)callSite2);
                    ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                    arrayList3.add(callSite2);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("block");
                    if (scriptValue9 != ScriptValue.NULL) {
                        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                        arrayList4.add(callSite2);
                        object2 = PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue9, arrayList4, (ScriptContext)scriptContext);
                    } else {
                        object2 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue10 = object2;
                    builder.val("ok", scriptValue10);
                    if (!scriptValue10.asBool()) continue;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("i");
                    double d = 1.0;
                    if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                        v2 = polyClassContainer.tm$6_remove_item(scriptValue13.asNum(), d);
                    } else {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue13);
                        arrayList5.add(ScriptValue.of((double)d));
                        v2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue12, arrayList5, (ScriptContext)scriptContext);
                    }
                    boolean bl2 = true;
                    ScriptValue scriptValue14 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue14);
                }
            }
        } else {
            Object object;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("block");
            if (scriptValue15 != ScriptValue.NULL) {
                ArrayList arrayList = new ArrayList();
                object = PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = object;
            builder.val("drops", scriptValue16);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue16);
            if (list != null) {
                for (ScriptValue scriptValue17 : list) {
                    CallSite callSite;
                    ScriptValue.Obj obj;
                    Object object4;
                    PolyClassMachine_v4 polyClassMachine_v44;
                    builder.val("drop", scriptValue17);
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v44 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v44.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("drop");
                    if (scriptValue19 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                        callSite = polyClassContainer.tm$12_push(scriptValue20);
                        continue;
                    }
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue20);
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
