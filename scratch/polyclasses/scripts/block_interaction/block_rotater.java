/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class BlockRotater {
    public static void run(ScriptContext.Builder builder) {
        block8: {
            Object object;
            ScriptContext scriptContext;
            block9: {
                Object object2;
                ScriptValue.Obj obj;
                Object object3;
                scriptContext = builder.peek();
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object3).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("block", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
                if (!((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) break block8;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("block");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"facing"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                if (!object2.asBool()) break block9;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("block");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"facing"));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                } else {
                    v1 = ScriptValue.NULL;
                }
                break block8;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("block");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"horizontal_facing"));
                object = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            if (!object.asBool()) break block8;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("block");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"horizontal_facing"));
                v3 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
    }
}
