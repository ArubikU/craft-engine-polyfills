/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class XpInfo {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _tank(ScriptContext.Builder builder) {
        Object object;
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$152_fluid_tanks() : PolyDispatch.bootstrapGet("memberGet", "fluid_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("tanks", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("tanks");
        Object object3 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object3.asNum() <= 0.0) {
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tanks");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue item(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)"minecraft:experience_bottle");
    }

    public static ScriptValue name(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)"<green>XP Collector");
    }

    public static ScriptValue lore(ScriptContext.Builder builder) {
        double d;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = XpInfo._tank(builder2);
        builder.val("tank", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"<gray>No XP tank"));
            return new ScriptValue.Array(arrayList);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tank");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"level"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("level", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tank");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"capacity"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("cap", scriptValue5);
        double d2 = scriptValue5.asNum() > 0.0 ? Math.floor(((d = scriptValue5.asNum()) == 0.0 ? 0.0 : scriptValue3.asNum() / d) * 100.0) : 0.0;
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        builder.val("pct", scriptValue6);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"<gray>Absorbs nearby experience orbs."));
        arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>Stored: <white>"), (ScriptValue)ScriptValue.of((double)Math.floor(scriptValue3.asNum()))), (ScriptValue)ScriptValue.of((String)" <gray>/ <white>")), (ScriptValue)ScriptValue.of((double)Math.floor(scriptValue5.asNum()))), (ScriptValue)ScriptValue.of((String)" mB")));
        arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>Fill: <yellow>"), (ScriptValue)ScriptValue.of((double)d2)), (ScriptValue)ScriptValue.of((String)"%")));
        return new ScriptValue.Array(arrayList);
    }
}
