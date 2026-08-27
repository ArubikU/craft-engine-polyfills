/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class BlockRotater {
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
        builder.val("block", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
        if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            Object object2;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("block");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"facing"));
                object2 = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            if (object2.asBool()) {
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("block");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"facing"));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                } else {
                    v1 = ScriptValue.NULL;
                }
            } else {
                Object object3;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("block");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"horizontal_facing"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                if (object3.asBool()) {
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("block");
                    if (scriptValue7 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)"horizontal_facing"));
                        v3 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
