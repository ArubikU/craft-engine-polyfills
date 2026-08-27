/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.ChuteUtils
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.ChuteUtils;
import java.util.ArrayList;

public final class Chute {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onRightClick(ScriptContext.Builder var0) {
        block3: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Player");
            v0 /* !! */  = var2_2 != ScriptValue.NULL ? (var2_2 instanceof ScriptValue.Obj && (var4_4 = (var3_3 = (ScriptValue.Obj)var2_2).instance()) != null && !(var4_4 instanceof PolyClass) && var3_3.typeName().equals("Player") ? new PolyClassPlayer(var4_4).pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var2_2, (ScriptContext)var1_1)) : ScriptValue.NULL;
            if (v0 /* !! */ .asBool()) {
                var5_5 = new ArrayList<E>();
                ScriptFormula.callBuiltin((String)"_chute_cycle_facing", var5_5, (ScriptContext)var1_1);
                return ScriptValue.NULL;
            }
            var6_6 = var1_1.getClassOrVar("Player");
            var9_9 = var6_6 != ScriptValue.NULL ? (var6_6 instanceof ScriptValue.Obj && (var8_8 = (var7_7 = (ScriptValue.Obj)var6_6).instance()) != null && !(var8_8 instanceof PolyClass) && var7_7.typeName().equals("Player") ? new PolyClassPlayer(var8_8).pg$47_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var6_6, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("held", var9_9);
            var10_10 = new ArrayList<ScriptValue>();
            var10_10.add(var9_9);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", var10_10, (ScriptContext)var1_1).asBool() ^ true)) ** GOTO lbl-1000
            var11_11 = ScriptContext.builder().copyFrom(var1_1);
            var12_12 = var1_1.getClassOrVar("held");
            var11_11.val("id", (ScriptValue)(var12_12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var12_12, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (ChuteUtils._isGlassItem((ScriptContext.Builder)var11_11).asBool()) {
                v1 = true;
            } else lbl-1000:
            // 2 sources

            {
                v1 = false;
            }
            if (!v1) break block3;
            var13_13 = ScriptContext.builder().copyFrom(var1_1);
            ChuteUtils._chuteToggleWindow((ScriptContext.Builder)var13_13);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((boolean)false));
        ScriptFormula.callBuiltin((String)"_chute_tick", arrayList2, (ScriptContext)scriptContext);
    }
}
