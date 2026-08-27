/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
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
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
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
        PolyClassBlock_v2 polyClassBlock_v2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock_v2.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl) {
            boolean bl2 = false;
            ScriptValue scriptValue3 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue3);
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockDispenser.class, 27.0)), (ScriptContext)scriptContext));
            Object object = scriptContext.getClassOrVar("item");
            ScriptValue scriptValue4 = ScriptValue.of((boolean)bl2);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("ok");
            if (list != null) {
                for (ScriptValue scriptValue6 : list) {
                    ScriptValue.Obj obj;
                    Object object2;
                    ScriptValue scriptValue7;
                    Object object3;
                    CallSite callSite;
                    ScriptValue.Obj obj2;
                    Object object4;
                    ScriptValue scriptValue8;
                    builder.val("i", scriptValue6);
                    if (!(scriptValue4.asBool() ^ true)) continue;
                    PolyClassMachine_v2 polyClassMachine_v22 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue9 = polyClassMachine_v22 != null ? polyClassMachine_v22.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue10 = scriptValue6;
                    if (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                        callSite = polyClassContainer.tm$0_get_item(scriptValue10.asNum());
                    } else {
                        callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                    }
                    CallSite callSite2 = callSite;
                    builder.val("item", (ScriptValue)callSite2);
                    object = callSite2;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("block");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object5;
                        Object object6 = object;
                        if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Block")) {
                            PolyClassBlock_v2 polyClassBlock_v22 = new PolyClassBlock_v2(object5);
                            object3 = ScriptValue.of((boolean)polyClassBlock_v22.tm$30_place_from_item(object6));
                        } else {
                            object3 = PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue11, (ScriptValue)object6, (ScriptContext)scriptContext);
                        }
                    } else {
                        object3 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue12 = object3;
                    builder.val("ok", scriptValue12);
                    scriptValue5 = scriptValue12;
                    if (!scriptValue5.asBool()) continue;
                    PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue13 = polyClassMachine_v23 != null ? polyClassMachine_v23.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue14 = scriptValue6;
                    double d = 1.0;
                    if (scriptValue13 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                        v3 = polyClassContainer.tm$6_remove_item(scriptValue14.asNum(), d);
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    }
                    boolean bl3 = true;
                    ScriptValue scriptValue15 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue15);
                    scriptValue4 = scriptValue15;
                }
            }
        } else {
            Object object;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("block");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                if (scriptValue16 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Block")) {
                    PolyClassBlock_v2 polyClassBlock_v23 = new PolyClassBlock_v2(object7);
                    object = polyClassBlock_v23.tm$20_break_and_drop();
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue17 = object;
            builder.val("drops", scriptValue17);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue17);
            if (list != null) {
                for (ScriptValue scriptValue18 : list) {
                    CallSite callSite;
                    ScriptValue.Obj obj;
                    Object object8;
                    ScriptValue scriptValue19;
                    builder.val("drop", scriptValue18);
                    PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue20 = polyClassMachine_v24 != null ? polyClassMachine_v24.pg$120_container() : ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue21 = scriptValue18;
                    if (scriptValue20 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                        callSite = polyClassContainer.tm$12_push(scriptValue21);
                        continue;
                    }
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
