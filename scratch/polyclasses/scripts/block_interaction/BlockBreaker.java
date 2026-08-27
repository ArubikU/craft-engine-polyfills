/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class BlockBreaker {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:bedrock"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:end_portal"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:barrier"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("UNBREAKABLE", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v2.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("block", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("block");
        if ((scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ((scriptValue2 = scriptContext.getClassOrVar("UNBREAKABLE")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)scriptValue2, (ScriptValue)((scriptValue = scriptContext.getClassOrVar("block")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            Object object;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object2);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v22.tm$102_hold_contraption());
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue9 = scriptValue5;
                double d = 10.0;
                if (scriptValue8 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object3);
                    object = polyClassMachine_v23.tm$2_tick_break(scriptValue9, d);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object;
            builder.val("drops", scriptValue10);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue11;
                List list = ScriptProgram.elementsOf((ScriptValue)scriptValue10);
                if (list != null) {
                    for (ScriptValue scriptValue12 : list) {
                        CallSite callSite;
                        ScriptValue.Obj obj;
                        Object object4;
                        PolyClassMachine_v2 polyClassMachine_v24;
                        builder.val("drop", scriptValue12);
                        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                        ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? ((polyClassMachine_v24 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine_v24.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        ScriptValue scriptValue15 = scriptContext.getClassOrVar("drop");
                        if (scriptValue14 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Container")) {
                            PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                            callSite = polyClassContainer.tm$12_push(scriptValue15);
                            continue;
                        }
                        callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                    }
                }
                if ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object5);
                        v3 = ScriptValue.of((boolean)polyClassMachine_v25.tm$32_release_contraption());
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
            }
        } else {
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                if (scriptValue16 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v26.tm$32_release_contraption());
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
