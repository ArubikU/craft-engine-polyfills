/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine != null ? polyClassMachine.pg$137_facing_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue4);
        if ((scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ((scriptValue = scriptContext.getClassOrVar("UNBREAKABLE")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)scriptValue, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            Object object;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                if (scriptValue5 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                    v0 = ScriptValue.of((boolean)polyClassMachine2.tm$102_hold_contraption());
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = scriptValue4;
                double d = 10.0;
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object3);
                    object = polyClassMachine3.tm$2_tick_break(scriptValue7, d);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object;
            builder.val("drops", scriptValue8);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue9;
                List list = ScriptProgram.elementsOf((ScriptValue)scriptValue8);
                if (list != null) {
                    for (ScriptValue scriptValue10 : list) {
                        CallSite callSite;
                        ScriptValue.Obj obj;
                        Object object4;
                        ScriptValue scriptValue11;
                        builder.val("drop", scriptValue10);
                        PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        ScriptValue scriptValue12 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL);
                        ScriptValue scriptValue13 = scriptValue10;
                        if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Container")) {
                            PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                            callSite = polyClassContainer.tm$12_push(scriptValue13);
                            continue;
                        }
                        callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                    }
                }
                if ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    if (scriptValue9 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine5 = new PolyClassMachine(object5);
                        v3 = ScriptValue.of((boolean)polyClassMachine5.tm$32_release_contraption());
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
            }
        } else {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                if (scriptValue14 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine6 = new PolyClassMachine(object);
                    v4 = ScriptValue.of((boolean)polyClassMachine6.tm$32_release_contraption());
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
