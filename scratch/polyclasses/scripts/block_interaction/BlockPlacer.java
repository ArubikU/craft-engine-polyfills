/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
public final class BlockPlacer {
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("facing", scriptValue2);
        if ((scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            boolean bl = false;
            ScriptValue scriptValue3 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue3);
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockPlacer.class, 9.0)), (ScriptContext)scriptContext));
            Object object = scriptContext.getClassOrVar("item");
            ScriptValue scriptValue4 = ScriptValue.of((boolean)bl);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("ok");
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
                    if (!(scriptValue4.asBool() ^ true)) continue;
                    PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue9 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue10 = scriptValue6;
                    if (scriptValue9 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                        callSite = polyClassContainer.tm$0_get_item(scriptValue10.asNum());
                    } else {
                        callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                    }
                    CallSite callSite2 = callSite;
                    builder.val("item", (ScriptValue)callSite2);
                    object = callSite2;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("facing");
                    ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue11, (ScriptValue)object, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("ok", scriptValue12);
                    scriptValue5 = scriptValue12;
                    if (!scriptValue5.asBool()) continue;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue13 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue14 = scriptValue6;
                    double d = 1.0;
                    if (scriptValue13 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                        v1 = polyClassContainer.tm$6_remove_item(scriptValue14.asNum(), d);
                    } else {
                        v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    }
                    boolean bl2 = true;
                    ScriptValue scriptValue15 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue15);
                    scriptValue4 = scriptValue15;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
