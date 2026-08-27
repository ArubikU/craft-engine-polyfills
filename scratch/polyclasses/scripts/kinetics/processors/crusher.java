/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassUpgrades
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClassUpgrades;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Crusher {
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
        Object object;
        double d;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        double d2 = Math.max(0.05, scriptContext.getNum("overclock"));
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("speed_mult", scriptValue3);
        boolean bl = scriptContext.getBool("processing") && scriptContext.getNum("rpm") > 0.0;
        ScriptValue scriptValue4 = ScriptValue.of((boolean)bl);
        builder.val("running", scriptValue4);
        if (bl) {
            double d3 = 10.0;
            scriptValue2 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor(10.0 == 0.0 ? 0.0 : scriptContext.getNum("rpm") / d3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Crusher.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Crusher.class, 6.0)), (ScriptContext)scriptContext);
        } else {
            scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Crusher.class, 0.0);
        }
        ScriptValue scriptValue5 = scriptValue2;
        builder.val("smoke_count", scriptValue5);
        double d4 = scriptContext.getNum("max_progress") > 0.0 ? Math.sin(((d = scriptContext.getNum("max_progress")) == 0.0 ? 0.0 : scriptContext.getNum("progress") / d) * (Math.PI * 2)) * 0.1 : 0.0;
        ScriptValue scriptValue6 = ScriptValue.of((double)d4);
        builder.val("pulse_y", scriptValue6);
        boolean bl2 = scriptContext.getNum("overclock") > 1.5;
        ScriptValue scriptValue7 = ScriptValue.of((boolean)bl2);
        builder.val("glow", scriptValue7);
        boolean bl3 = scriptContext.getNum("overclock") > 2.5;
        ScriptValue scriptValue8 = ScriptValue.of((boolean)bl3);
        builder.val("turbo", scriptValue8);
        double d5 = 10.0;
        double d6 = 10.0 == 0.0 ? 0.0 : ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext).asNum() % d5;
        boolean bl4 = d6 < 2.0;
        ScriptValue scriptValue9 = ScriptValue.of((boolean)bl4);
        builder.val("flash", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Upgrades");
        if (scriptValue10 != ScriptValue.NULL) {
            String string = "cml:upgrade_diamond";
            PolyClassUpgrades polyClassUpgrades = PolyClassUpgrades.ofGuarded((ScriptValue)scriptValue10);
            object = polyClassUpgrades != null ? ScriptValue.of((double)polyClassUpgrades.tm$2_count(string)) : PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        boolean bl5 = object.asNum() > 0.0;
        ScriptValue scriptValue11 = ScriptValue.of((boolean)bl5);
        builder.val("has_diamond", scriptValue11);
        double d7 = 0.0;
        ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
        builder.val("upgrade_bonus", scriptValue12);
        PolyClassUpgrades polyClassUpgrades = PolyClassUpgrades.ofVar((ScriptContext)scriptContext, (String)"Upgrades");
        List list = ScriptProgram.elementsOf((ScriptValue)(polyClassUpgrades != null ? polyClassUpgrades.pg$10_inventory() : ((scriptValue = scriptContext.getClassOrVar("Upgrades")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inventory", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        ScriptValue scriptValue13 = ScriptValue.of((double)d7);
        if (list != null) {
            for (ScriptValue scriptValue14 : list) {
                builder.val("item", scriptValue14);
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue13, (ScriptValue)ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue14, (ScriptContext)scriptContext));
                builder.val("upgrade_bonus", scriptValue15);
                scriptValue13 = scriptValue15;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
