/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.ChuteUtils;
import java.util.ArrayList;

public final class SmartChute {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        block10: {
            ScriptContext scriptContext;
            block9: {
                PolyClassPlayer polyClassPlayer;
                PolyClassPlayer polyClassPlayer2;
                scriptContext = builder.peek();
                ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
                boolean bl = scriptValue != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer2.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
                if (bl) {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
                    ChuteUtils._chuteOpenAmountDialog((ScriptContext.Builder)builder2);
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
                ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("held", scriptValue3);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                if (!ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) break block9;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object;
                    String string = "_chute_filter";
                    String string2 = "str";
                    ScriptValue scriptValue5 = ScriptValue.of((String)"");
                    if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                        v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue5));
                    } else {
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(ScriptValue.of((String)string));
                        arrayList2.add(ScriptValue.of((String)string2));
                        arrayList2.add(scriptValue5);
                        v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList2, (ScriptContext)scriptContext);
                    }
                } else {
                    v1 = ScriptValue.NULL;
                }
                break block10;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
            ScriptValue scriptValue = scriptContext.getClassOrVar("held");
            builder3.val("id", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
            if (!(ChuteUtils._isGlassItem((ScriptContext.Builder)builder3).asBool() ^ true)) break block10;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue7;
                String string = "_chute_filter";
                String string3 = "str";
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("held");
                Object object2 = scriptValue7 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v3 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue7));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue7);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
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
        arrayList.add("_is_glass_item");
        arrayList.add("_chute_drop_held");
        arrayList.add("_chute_open_amount_dialog");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"storage/chute_utils.pf", null, arrayList);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
        builder2.val("smart", ScriptValue.of((boolean)true));
        ChuteUtils._chuteTick((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
