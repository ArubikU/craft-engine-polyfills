/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class ExamplePvpDamageMultiplier {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onDamage(ScriptContext.Builder var0) {
        block4: {
            var1_1 = var0.peek();
            var2_2 = new ArrayList<ScriptValue>();
            var3_3 = var1_1.getClassOrVar("event");
            var2_2.add((ScriptValue)(var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "damager", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (!ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"type_of", var2_2, (ScriptContext)var1_1), (String)"Player")) ** GOTO lbl-1000
            var4_4 = new ArrayList<ScriptValue>();
            var5_5 = var1_1.getClassOrVar("event");
            var4_4.add((ScriptValue)(var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "entity", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"type_of", var4_4, (ScriptContext)var1_1), (String)"Player")) {
                v0 = true;
            } else lbl-1000:
            // 2 sources

            {
                v0 = false;
            }
            if (!v0) break block4;
            var6_6 = var1_1.getClassOrVar("event");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = new ArrayList<ScriptValue>();
                var8_8 = var1_1.getClassOrVar("event");
                var7_7.add(ScriptValue.of((double)((var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "damage", (ScriptValue)var8_8, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * 2.0)));
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_damage", (ScriptValue)var6_6, var7_7, (ScriptContext)var1_1);
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }
}
