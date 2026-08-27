/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassUpgrades
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassUpgrades;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class Crusher {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        Object object2;
        double d;
        double d2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        double d3 = Math.max(0.05, scriptContext.getNum("overclock"));
        ScriptValue scriptValue2 = ScriptValue.of((double)d3);
        builder.val("speed_mult", scriptValue2);
        boolean bl = scriptContext.getBool("processing") && scriptContext.getNum("rpm") > 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((boolean)bl);
        builder.val("running", scriptValue3);
        if (bl) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            double d4 = 10.0;
            arrayList.add(ScriptValue.of((double)Math.floor(10.0 == 0.0 ? 0.0 : scriptContext.getNum("rpm") / d4)));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)6.0));
            scriptValue = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
        } else {
            scriptValue = ScriptValue.of((double)0.0);
        }
        ScriptValue scriptValue4 = scriptValue;
        builder.val("smoke_count", scriptValue4);
        double d5 = scriptContext.getNum("max_progress") > 0.0 ? Math.sin(((d2 = scriptContext.getNum("max_progress")) == 0.0 ? 0.0 : scriptContext.getNum("progress") / d2) * (Math.PI * 2)) * 0.1 : 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)d5);
        builder.val("pulse_y", scriptValue5);
        boolean bl2 = scriptContext.getNum("overclock") > 1.5;
        ScriptValue scriptValue6 = ScriptValue.of((boolean)bl2);
        builder.val("glow", scriptValue6);
        boolean bl3 = scriptContext.getNum("overclock") > 2.5;
        ScriptValue scriptValue7 = ScriptValue.of((boolean)bl3);
        builder.val("turbo", scriptValue7);
        double d6 = 10.0;
        if (10.0 == 0.0) {
            d = 0.0;
        } else {
            ArrayList arrayList = new ArrayList();
            d = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext).asNum() % d6;
        }
        boolean bl4 = d < 2.0;
        ScriptValue scriptValue8 = ScriptValue.of((boolean)bl4);
        builder.val("flash", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Upgrades");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "cml:upgrade_diamond";
            if (scriptValue9 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Upgrades")) {
                PolyClassUpgrades polyClassUpgrades = new PolyClassUpgrades(object3);
                object2 = ScriptValue.of((double)polyClassUpgrades.tm$2_count(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                object2 = PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        boolean bl5 = object2.asNum() > 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((boolean)bl5);
        builder.val("has_diamond", scriptValue10);
        double d7 = 0.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)0.0);
        builder.val("upgrade_bonus", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Upgrades");
        List list = ScriptProgram.rowsOf((ScriptValue)(scriptValue12 != ScriptValue.NULL ? (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Upgrades") ? new PolyClassUpgrades(object).pg$9_inventory() : PolyDispatch.bootstrapGet("memberGet", "inventory", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("item", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("upgrade_bonus");
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue13, (ScriptValue)ScriptFormula.callBuiltin((String)"item_count", arrayList, (ScriptContext)scriptContext));
                builder.val("upgrade_bonus", scriptValue14);
            }
        }
    }
}
