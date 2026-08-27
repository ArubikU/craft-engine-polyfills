/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("block", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
        if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            boolean bl = false;
            ScriptValue scriptValue4 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue4);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockDispenser.class, 27.0));
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
            if (list != null) {
                for (ScriptValue scriptValue5 : list) {
                    ScriptValue.Obj obj;
                    Object object;
                    PolyClassMachine_v2 polyClassMachine_v22;
                    CallSite callSite;
                    ScriptValue.Obj obj2;
                    Object object2;
                    PolyClassMachine_v2 polyClassMachine_v23;
                    builder.val("i", scriptValue5);
                    if (!(scriptContext.getBool("placed") ^ true)) continue;
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v23.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("i");
                    if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                        callSite = polyClassContainer.tm$0_get_item(scriptValue8.asNum());
                    } else {
                        callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                    }
                    CallSite callSite2 = callSite;
                    builder.val("item", (ScriptValue)callSite2);
                    ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
                    arrayList2.add(callSite2);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("block");
                    ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue9, (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("ok", scriptValue10);
                    if (!scriptValue10.asBool()) continue;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v22.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("i");
                    double d = 1.0;
                    if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                        v1 = polyClassContainer.tm$6_remove_item(scriptValue13.asNum(), d);
                    } else {
                        v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    }
                    boolean bl2 = true;
                    ScriptValue scriptValue14 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue14);
                }
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("block");
            ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("drops", scriptValue16);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue16);
            if (list != null) {
                for (ScriptValue scriptValue17 : list) {
                    CallSite callSite;
                    ScriptValue.Obj obj;
                    Object object;
                    PolyClassMachine_v2 polyClassMachine_v24;
                    builder.val("drop", scriptValue17);
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v24 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v24.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("drop");
                    if (scriptValue19 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                        callSite = polyClassContainer.tm$12_push(scriptValue20);
                        continue;
                    }
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
