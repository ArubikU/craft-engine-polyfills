/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;

public final class Utils$2 {
    public static ScriptValue isRestingItem(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("entity");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item") ^ true) {
            return ScriptValue.of((boolean)false);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("entity");
        CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("ey", (ScriptValue)callSite);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("by", scriptValue4);
        return ScriptValue.of((callSite.asNum() >= scriptValue4.asNum() - 0.2 && callSite.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.2)).asNum() ? 1 : 0) != 0);
    }

    public static ScriptValue oppositeDir(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("d"), (String)"north")) {
            return ScriptValue.of((String)"south");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("d"), (String)"south")) {
            return ScriptValue.of((String)"north");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("d"), (String)"east")) {
            return ScriptValue.of((String)"west");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("d"), (String)"west")) {
            return ScriptValue.of((String)"east");
        }
        return scriptContext.getClassOrVar("d");
    }
}
