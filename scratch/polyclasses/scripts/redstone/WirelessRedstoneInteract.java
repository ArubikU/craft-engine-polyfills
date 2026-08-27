/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class WirelessRedstoneInteract {
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
        ScriptContext scriptContext = builder.peek();
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer != null ? polyClassPlayer.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            Object object;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "_wr_mode";
                String string2 = "int";
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                    object = polyClassMachine.tm$34_get_typed(string, string2);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("mode", scriptValue3);
            double d = ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstoneInteract.class, 0.0))) ? 1.0 : 0.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)d);
            builder.val("new_mode", scriptValue4);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "_wr_mode";
                String string3 = "int";
                ScriptValue scriptValue6 = ScriptValue.of((double)d);
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            if (d == 0.0) {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object4;
                    String string = "<green>Wireless Redstone: TRANSMITTER";
                    if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object4);
                        v2 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                    } else {
                        v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    String string = "<aqua>Wireless Redstone: RECEIVER";
                    if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object5);
                        v3 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
            }
        } else {
            Object object;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "_wr_ch";
                String string4 = "int";
                if (scriptValue9 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                    object = polyClassMachine.tm$34_get_typed(string, string4);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object;
            builder.val("ch", scriptValue10);
            double d = 16.0;
            double d2 = 16.0 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstoneInteract.class, 1.0))).asNum() % d;
            ScriptValue scriptValue11 = ScriptValue.of((double)d2);
            builder.val("new_ch", scriptValue11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "_wr_ch";
                String string5 = "int";
                ScriptValue scriptValue13 = ScriptValue.of((double)d2);
                if (scriptValue12 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                    v5 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue13));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                ScriptValue scriptValue15 = ScriptValue.of((String)("<yellow>Channel: " + ScriptFormula.numToStr((double)d2)));
                if (scriptValue14 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object8);
                    v6 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(scriptValue15.asStr()));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
