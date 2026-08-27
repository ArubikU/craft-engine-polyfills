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
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true && (scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue4, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue7);
        if ((scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:funnel");
        builder.val("block_id", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("null");
        builder.val("props", scriptValue9);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"up")) {
            ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:floor_funnel");
            builder.val("block_id", scriptValue10);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"down")) {
            ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "cml:ceiling_funnel");
            builder.val("block_id", scriptValue11);
        } else {
            ScriptValue scriptValue12 = ScriptFormula.callBuiltin2((String)"make_map", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "facing")), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            builder.val("props", scriptValue12);
        }
        ScriptValue scriptValue13 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "place_custom", (ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("block_id"), (ScriptValue)scriptContext.getClassOrVar("props"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("placed", scriptValue13);
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("event");
        Object object = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue13.asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer_v22 != null ? polyClassPlayer_v22.tg$52_is_creative() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_creative", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "main_hand";
                double d = 1.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v23 = new PolyClassPlayer_v2(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer_v23.tm$34_remove_item(string, d));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelPlace.class, "minecraft:block.copper.place")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelPlace.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }
}
