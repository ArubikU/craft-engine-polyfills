/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

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
        PolyClassBlock_v2 polyClassBlock_v2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock_v2.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl ^ true) {
            Object object;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "facing";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
                    PolyClassBlock_v2 polyClassBlock_v22 = new PolyClassBlock_v2(object2);
                    object = ScriptValue.of((boolean)polyClassBlock_v22.tm$28_has_property(string));
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            if (object.asBool()) {
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("block");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object3;
                    String string = "facing";
                    if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Block")) {
                        PolyClassBlock_v2 polyClassBlock_v23 = new PolyClassBlock_v2(object3);
                        v2 = ScriptValue.of((boolean)polyClassBlock_v23.tm$0_cycle_prop(string));
                    } else {
                        v2 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                Object object4;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("block");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    String string = "horizontal_facing";
                    if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Block")) {
                        PolyClassBlock_v2 polyClassBlock_v24 = new PolyClassBlock_v2(object5);
                        object4 = ScriptValue.of((boolean)polyClassBlock_v24.tm$28_has_property(string));
                    } else {
                        object4 = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                if (object4.asBool()) {
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("block");
                    if (scriptValue6 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object6;
                        String string = "horizontal_facing";
                        if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Block")) {
                            PolyClassBlock_v2 polyClassBlock_v25 = new PolyClassBlock_v2(object6);
                            v4 = ScriptValue.of((boolean)polyClassBlock_v25.tm$0_cycle_prop(string));
                        } else {
                            v4 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
