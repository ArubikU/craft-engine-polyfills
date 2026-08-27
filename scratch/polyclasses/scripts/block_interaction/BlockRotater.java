/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        PolyClassBlock_v4 polyClassBlock_v4;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock_v4 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock_v4.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl ^ true) {
            Object object;
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "facing";
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
                    PolyClassBlock_v4 polyClassBlock_v42 = new PolyClassBlock_v4(object2);
                    object = ScriptValue.of((boolean)polyClassBlock_v42.tm$28_has_property(string));
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            if (object.asBool()) {
                if (scriptValue2 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object3;
                    String string = "facing";
                    if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Block")) {
                        PolyClassBlock_v4 polyClassBlock_v43 = new PolyClassBlock_v4(object3);
                        v2 = ScriptValue.of((boolean)polyClassBlock_v43.tm$0_cycle_prop(string));
                    } else {
                        v2 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                Object object4;
                if (scriptValue2 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    String string = "horizontal_facing";
                    if (scriptValue2 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Block")) {
                        PolyClassBlock_v4 polyClassBlock_v44 = new PolyClassBlock_v4(object5);
                        object4 = ScriptValue.of((boolean)polyClassBlock_v44.tm$28_has_property(string));
                    } else {
                        object4 = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                if (object4.asBool()) {
                    if (scriptValue2 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object6;
                        String string = "horizontal_facing";
                        if (scriptValue2 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Block")) {
                            PolyClassBlock_v4 polyClassBlock_v45 = new PolyClassBlock_v4(object6);
                            v4 = ScriptValue.of((boolean)polyClassBlock_v45.tm$0_cycle_prop(string));
                        } else {
                            v4 = PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
