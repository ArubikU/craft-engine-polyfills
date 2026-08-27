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
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptValue scriptValue4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("event");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "clicked_block", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("clicked", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("event");
        ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "clicked_face", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("face", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer != null ? polyClassPlayer.tg$49_is_sneaking() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true && ((scriptValue3 = scriptContext.getClassOrVar("clicked")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("clicked");
        ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue9, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue10);
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("target");
        if ((scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:funnel");
        builder.val("block_id", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("null");
        builder.val("props", scriptValue13);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"up")) {
            ScriptValue scriptValue14 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:floor_funnel");
            builder.val("block_id", scriptValue14);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"down")) {
            ScriptValue scriptValue15 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:ceiling_funnel");
            builder.val("block_id", scriptValue15);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "facing"));
            arrayList.add(scriptValue8);
            ScriptValue scriptValue16 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
            builder.val("props", scriptValue16);
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("target");
        ScriptValue scriptValue18 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "place_custom", (ScriptValue)scriptValue17, (ScriptValue)scriptContext.getClassOrVar("block_id"), (ScriptValue)scriptContext.getClassOrVar("props"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("placed", scriptValue18);
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("event");
        Object object = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue18.asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer2 != null ? polyClassPlayer2.tg$52_is_creative() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_creative", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true) {
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "main_hand";
                double d = 1.0;
                if (scriptValue20 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer3.tm$34_remove_item(string, d));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object3 = (scriptValue = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "minecraft:block.copper.place")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }
}
