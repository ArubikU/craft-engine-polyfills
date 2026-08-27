/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
            ScriptValue scriptValue;
            ScriptContext scriptContext;
            block9: {
                ScriptValue scriptValue2;
                ScriptValue scriptValue3;
                scriptContext = builder.peek();
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
                if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
                    ChuteUtils._chuteOpenAmountDialog((ScriptContext.Builder)builder2);
                    return ScriptValue.NULL;
                }
                PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
                scriptValue = polyClassPlayer_v22 != null ? polyClassPlayer_v22.pg$48_main_hand() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("held", scriptValue);
                if (!ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) break block9;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object;
                    String string = "_chute_filter";
                    String string2 = "str";
                    ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SmartChute.class, "");
                    if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                        v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue5));
                    } else {
                        v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                    }
                } else {
                    v0 = ScriptValue.NULL;
                }
                break block10;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(ChuteUtils.fileScope()).copyFrom(scriptContext);
            builder3.val("id", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
            if (!(ChuteUtils._isGlassItem((ScriptContext.Builder)builder3).asBool() ^ true)) break block10;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue7;
                String string = "_chute_filter";
                String string3 = "str";
                Object object2 = scriptValue7 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue7));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
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
        builder2.val("smart",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", SmartChute.class, 1));
        ChuteUtils._chuteTick((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
