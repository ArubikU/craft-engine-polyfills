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
import java.util.ArrayList;

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
        PolyClassPlayer polyClassPlayer;
        Object object;
        Object object2;
        ScriptValue scriptValue2;
        PolyClassPlayer polyClassPlayer2;
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
        Object object3 = scriptValue7 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassPlayer2.pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object3.asBool() ^ true && ((scriptValue2 = scriptContext.getClassOrVar("clicked")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("clicked");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue6);
            object2 = PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object2;
        builder.val("target", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
        if ((scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = ScriptValue.of((String)"cml:funnel");
        builder.val("block_id", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("null");
        builder.val("props", scriptValue12);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"up")) {
            ScriptValue scriptValue13 = ScriptValue.of((String)"cml:floor_funnel");
            builder.val("block_id", scriptValue13);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"down")) {
            ScriptValue scriptValue14 = ScriptValue.of((String)"cml:ceiling_funnel");
            builder.val("block_id", scriptValue14);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"facing"));
            arrayList.add(scriptValue6);
            ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
            builder.val("props", scriptValue15);
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("target");
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("block_id"));
            arrayList.add(scriptContext.getClassOrVar("props"));
            object = PolyDispatch.bootstrapCall("memberCall", "place_custom", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = object;
        builder.val("placed", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("event");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList arrayList = new ArrayList();
            v3 = PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        if (scriptValue17.asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
        Object object4 = scriptValue19 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue19)) != null ? polyClassPlayer.pg$49_is_creative() : PolyDispatch.bootstrapGet("memberGet", "is_creative", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object4.asBool() ^ true) {
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "main_hand";
                double d = 1.0;
                if (scriptValue20 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object5);
                    v5 = ScriptValue.of((boolean)polyClassPlayer3.tm$34_remove_item(string, d));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((double)d));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("target")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"minecraft:block.copper.place"));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)1.0));
            v6 = PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
