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
package dev.arubik.craftengine.script.gen.farming;

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
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Fertilizer {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "level";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("level_flag", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)1.0);
            builder.val("level_flag", scriptValue4);
        }
        ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level_flag"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Fertilizer.class, 2.0)));
        builder.val("radius", scriptValue5);
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite, (ScriptContext)scriptContext).asBool() ^ true) {
            Object object4;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object5;
                ScriptValue scriptValue8 = scriptValue5;
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object5);
                    object4 = polyClassMachine_v22.tm$86_blocks_in_range(scriptValue8.asNum());
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object4;
            builder.val("blocks", scriptValue9);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("ok");
            if (list != null) {
                for (ScriptValue scriptValue11 : list) {
                    CallSite callSite2;
                    ScriptValue.Obj obj4;
                    Object object6;
                    ScriptValue scriptValue12;
                    ScriptValue scriptValue13;
                    builder.val("block", scriptValue11);
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("block");
                    if (!((scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "age")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() || ((scriptValue13 = scriptContext.getClassOrVar("block")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "growth")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("block");
                    ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("ok", scriptValue16);
                    scriptValue10 = scriptValue16;
                    if (!scriptValue10.asBool()) continue;
                    PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue17 = polyClassMachine_v23 != null ? polyClassMachine_v23.pg$120_container() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    double d2 = 0.0;
                    double d3 = 1.0;
                    if (scriptValue17 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object6);
                        callSite2 = polyClassContainer.tm$6_remove_item(d2, d3);
                        break;
                    }
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                    break;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
