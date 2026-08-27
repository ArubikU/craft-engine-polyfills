/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class FunnelPlace {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer_v2 polyClassPlayer_v2;
        ScriptValue scriptValue2;
        PolyClassPlayer_v2 polyClassPlayer_v22;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("event");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "clicked_block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("clicked", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("event");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "clicked_face", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("face", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        boolean bl = scriptValue7 != ScriptValue.NULL ? ((polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassPlayer_v22.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue7, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl ^ true && ((scriptValue2 = scriptContext.getClassOrVar("clicked")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("clicked");
        ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue8, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
        if ((scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:funnel");
        builder.val("block_id", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("null");
        builder.val("props", scriptValue12);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"up")) {
            ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:floor_funnel");
            builder.val("block_id", scriptValue13);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"down")) {
            ScriptValue scriptValue14 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:ceiling_funnel");
            builder.val("block_id", scriptValue14);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "facing"));
            arrayList.add(scriptValue6);
            ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
            builder.val("props", scriptValue15);
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("target");
        ScriptValue scriptValue17 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "place_custom", (ScriptValue)scriptValue16, (ScriptValue)scriptContext.getClassOrVar("block_id"), (ScriptValue)scriptContext.getClassOrVar("props"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("placed", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("event");
        Object object = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue17.asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
        boolean bl2 = scriptValue19 != ScriptValue.NULL ? ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19)) != null ? polyClassPlayer_v2.tg$52_is_creative() : PolyDispatch.bootstrapGet("memberGet", "is_creative", (ScriptValue)scriptValue19, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl2 ^ true) {
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "main_hand";
                double d = 1.0;
                if (scriptValue20 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v23 = new PolyClassPlayer_v2(object2);
                    v3 = ScriptValue.of((boolean)polyClassPlayer_v23.tm$34_remove_item(string, d));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        Object object3 = (scriptValue = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "minecraft:block.copper.place")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }
}
