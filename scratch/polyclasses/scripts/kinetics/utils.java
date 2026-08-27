/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Utils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _deposit(ScriptContext.Builder builder) {
        block8: {
            ScriptContext scriptContext;
            block7: {
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object;
                PolyClassContraption polyClassContraption;
                ScriptValue scriptValue;
                scriptContext = builder.peek();
                if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue2 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("contraption", scriptValue2);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block7;
                ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassContraption.pg$52_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("item");
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ContraptionContainer")) {
                    PolyClassContraptionContainer polyClassContraptionContainer = new PolyClassContraptionContainer(object);
                    callSite = polyClassContraptionContainer.tm$12_push(scriptValue4);
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("leftover", (ScriptValue)callSite2);
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true)) break block8;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj2;
                    Object object2;
                    ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
                    arrayList.add(callSite2);
                    v1 = scriptValue5 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                } else {
                    v1 = ScriptValue.NULL;
                }
                break block8;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                v2 = scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _linearBreakSpeed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 20.0;
        ScriptValue scriptValue = ScriptValue.of((double)20.0);
        builder.val("LINEAR_SPEED_SCALE", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("contraption");
        return ScriptValue.of((double)Math.max(1.0, Math.abs((scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()) * d));
    }

    public static ScriptValue _updateActivated(ScriptContext.Builder builder) {
        block12: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("act_key");
                String string = "int";
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                    object = polyClassMachine_v2.tm$34_get_typed(scriptValue2.asStr(), string);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("was", scriptValue3);
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("is_now"), (ScriptValue)scriptValue3) ^ true)) break block12;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue5;
                String string = "activated";
                ScriptValue scriptValue6 = scriptValue5 = scriptContext.getNum("is_now") > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Utils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Utils.class, "false"));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$16_set_property(string, scriptValue5.asStr()));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("act_key");
                String string = "int";
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("is_now");
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }
}
