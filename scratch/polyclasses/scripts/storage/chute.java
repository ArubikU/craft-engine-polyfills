/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.ChuteUtils
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.ChuteUtils;
import java.util.ArrayList;

public final class Chute {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue onRightClick(ScriptContext.Builder var0) {
        block3: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Player");
            v0 = var2_2 != ScriptValue.NULL ? ((var3_3 = PolyClassPlayer.ofGuarded((ScriptValue)var2_2)) != null ? var3_3.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var2_2, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
            if (v0) {
                var4_4 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
                ChuteUtils._chuteCycleFacing((ScriptContext.Builder)var4_4);
                return ScriptValue.NULL;
            }
            var5_5 = var1_1.getClassOrVar("Player");
            var7_7 = var5_5 != ScriptValue.NULL ? ((var6_6 = PolyClassPlayer.ofGuarded((ScriptValue)var5_5)) != null ? var6_6.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var5_5, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("held", var7_7);
            var8_8 = new ArrayList<ScriptValue>();
            var8_8.add(var7_7);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", var8_8, (ScriptContext)var1_1).asBool() ^ true)) ** GOTO lbl-1000
            var9_9 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
            var10_10 = var1_1.getClassOrVar("held");
            var9_9.val("id", (ScriptValue)(var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (ChuteUtils._isGlassItem((ScriptContext.Builder)var9_9).asBool()) {
                v1 = true;
            } else lbl-1000:
            // 2 sources

            {
                v1 = false;
            }
            if (!v1) break block3;
            var11_11 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
            ChuteUtils._chuteToggleWindow((ScriptContext.Builder)var11_11);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
        ChuteUtils._chuteDropHeld((ScriptContext.Builder)builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_chute_tick");
        arrayList.add("_chute_toggle_window");
        arrayList.add("_chute_cycle_facing");
        arrayList.add("_is_glass_item");
        arrayList.add("_chute_drop_held");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"storage/chute_utils.pf", null, arrayList);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
        builder2.val("smart", ScriptValue.of((boolean)false));
        ChuteUtils._chuteTick((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
