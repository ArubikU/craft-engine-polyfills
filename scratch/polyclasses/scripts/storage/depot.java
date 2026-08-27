/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
public final class Depot {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        double d = 9.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)9.0);
        builder.val("SIZE", scriptValue3);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue4 = polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue4);
        PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer2 != null ? polyClassPlayer2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) || ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) {
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext));
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("ri");
            Object object = scriptContext.getClassOrVar("slot_item");
            if (list != null) {
                for (ScriptValue scriptValue6 : list) {
                    ScriptValue.Obj obj;
                    Object object2;
                    ScriptValue scriptValue7;
                    CallSite callSite;
                    ScriptValue.Obj obj2;
                    Object object3;
                    ScriptValue scriptValue8;
                    builder.val("i", scriptValue6);
                    double d2 = d - 1.0 - scriptValue6.asNum();
                    ScriptValue scriptValue9 = ScriptValue.of((double)d2);
                    builder.val("ri", scriptValue9);
                    scriptValue5 = scriptValue9;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue10 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue11 = scriptValue5;
                    if (scriptValue10 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                        callSite = polyClassContainer.tm$0_get_item(scriptValue11.asNum());
                    } else {
                        callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    }
                    CallSite callSite2 = callSite;
                    builder.val("slot_item", (ScriptValue)callSite2);
                    object = callSite2;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
                    if (scriptValue12 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object4;
                        Object object5 = object;
                        if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object4);
                            v1 = ScriptValue.of((boolean)polyClassPlayer3.tm$14_give_item(object5));
                        } else {
                            v1 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue12, (ScriptValue)object5, (ScriptContext)scriptContext);
                        }
                    } else {
                        v1 = ScriptValue.NULL;
                    }
                    PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue13 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue14 = scriptValue5;
                    ScriptValue scriptValue15 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)object, (ScriptContext)scriptContext);
                    if (scriptValue13 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                        v2 = polyClassContainer.tm$6_remove_item(scriptValue14.asNum(), scriptValue15.asNum());
                    } else {
                        v2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                    }
                    break;
                }
            }
        } else {
            CallSite callSite;
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue16;
            ScriptValue scriptValue17 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            builder.val("held_count", scriptValue17);
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue18 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL);
            ScriptValue scriptValue19 = scriptValue4;
            if (scriptValue18 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                callSite = polyClassContainer.tm$12_push(scriptValue19);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
            }
            CallSite callSite3 = callSite;
            builder.val("leftover", (ScriptValue)callSite3);
            double d3 = scriptValue17.asNum() - (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite3, (ScriptContext)scriptContext).asBool() ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Depot.class, 0.0)) : ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)callSite3, (ScriptContext)scriptContext)).asNum();
            ScriptValue scriptValue20 = ScriptValue.of((double)d3);
            builder.val("placed", scriptValue20);
            if (d3 > 0.0) {
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Player");
                if (scriptValue21 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    String string = "main_hand";
                    double d4 = d3;
                    if (scriptValue21 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object6);
                        v4 = ScriptValue.of((boolean)polyClassPlayer4.tm$34_remove_item(string, d4));
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
