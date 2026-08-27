/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.ChuteUtils;
import java.util.ArrayList;

public final class SmartChute {
    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        block10: {
            ScriptContext scriptContext;
            block9: {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue.Obj obj2;
                Object object2;
                scriptContext = builder.peek();
                ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
                Object object3 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Player") ? new PolyClassPlayer(object2).pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (object3.asBool()) {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                    ChuteUtils._chuteOpenAmountDialog((ScriptContext.Builder)builder2);
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
                ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player") ? new PolyClassPlayer(object).pg$47_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("held", scriptValue3);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                if (!ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) break block9;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object4;
                    String string = "_chute_filter";
                    String string2 = "str";
                    ScriptValue scriptValue5 = ScriptValue.of((String)"");
                    if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                        v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue5));
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
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
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
                Object object5 = scriptValue7 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue7));
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
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((boolean)true));
        ScriptFormula.callBuiltin((String)"_chute_tick", arrayList2, (ScriptContext)scriptContext);
    }
}
