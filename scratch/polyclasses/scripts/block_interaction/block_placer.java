/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class BlockPlacer {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("facing", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("facing");
        if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            boolean bl = false;
            ScriptValue scriptValue4 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue4);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)9.0));
            List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext), (int)1);
            if (list != null) {
                for (ScriptValue[] scriptValueArray : list) {
                    ScriptValue.Obj obj2;
                    Object object2;
                    Object object3;
                    ScriptValue.Obj obj3;
                    Object object4;
                    builder.val("i", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                    if (!(scriptContext.getBool("placed") ^ true)) continue;
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptContext.getClassOrVar("i"));
                    ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                    CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object4).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
                    builder.val("item", (ScriptValue)callSite);
                    ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                    arrayList3.add(callSite);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("facing");
                    if (scriptValue6 != ScriptValue.NULL) {
                        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                        arrayList4.add(callSite);
                        object3 = PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
                    } else {
                        object3 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue7 = object3;
                    builder.val("ok", scriptValue7);
                    if (!scriptValue7.asBool()) continue;
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptContext.getClassOrVar("i"));
                    arrayList5.add(ScriptValue.of((double)1.0));
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? (scriptValue8 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList5, (ScriptContext)scriptContext);
                    boolean bl2 = true;
                    ScriptValue scriptValue9 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue9);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
