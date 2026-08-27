/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;

public final class Utils$2 {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue isRestingItem(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("entity");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item") ^ true) {
            return ScriptValue.of((boolean)false);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("entity");
        CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("ey", (ScriptValue)callSite);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine.pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("by", scriptValue4);
        return ScriptValue.of((callSite.asNum() >= scriptValue4.asNum() - 0.2 && callSite.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.2)).asNum() ? 1 : 0) != 0);
    }

    public static ScriptValue oppositeDir(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("d").equals("north")) {
            return ScriptValue.of((String)"south");
        }
        if (scriptContext.getStr("d").equals("south")) {
            return ScriptValue.of((String)"north");
        }
        if (scriptContext.getStr("d").equals("east")) {
            return ScriptValue.of((String)"west");
        }
        if (scriptContext.getStr("d").equals("west")) {
            return ScriptValue.of((String)"east");
        }
        return scriptContext.getClassOrVar("d");
    }
}
