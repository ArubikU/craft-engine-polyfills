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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
            var2_2 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
            if (var2_2 != null ? var2_2.tg$49_is_sneaking() : ((var3_3 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var3_3, (ScriptContext)var1_1).asBool() : ScriptValue.NULL.asBool())) {
                var4_4 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
                ChuteUtils._chuteCycleFacing((ScriptContext.Builder)var4_4);
                return ScriptValue.NULL;
            }
            var5_5 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
            var7_7 = var5_5 != null ? var5_5.pg$48_main_hand() : ((var6_6 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var6_6, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("held", var7_7);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var7_7, (ScriptContext)var1_1).asBool() ^ true)) ** GOTO lbl-1000
            var8_8 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
            var8_8.val("id", (ScriptValue)(var7_7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var7_7, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (ChuteUtils._isGlassItem((ScriptContext.Builder)var8_8).asBool()) {
                v0 = true;
            } else lbl-1000:
            // 2 sources

            {
                v0 = false;
            }
            if (!v0) break block3;
            var9_9 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(var1_1);
            ChuteUtils._chuteToggleWindow((ScriptContext.Builder)var9_9);
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
        builder2.val("smart",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Chute.class, 0));
        ChuteUtils._chuteTick((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
