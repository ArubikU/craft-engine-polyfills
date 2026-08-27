/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class JumpPad {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "force";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("force", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)1.0);
            builder.val("force", scriptValue4);
        }
        double d = 1.0 * scriptContext.getNum("force");
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("horiz_scale", scriptValue5);
        double d2 = 0.8 * scriptContext.getNum("force");
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        builder.val("vert_power", scriptValue6);
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v2 != null ? polyClassMachine_v2.pg$129_facing_dy() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0))) ^ true;
        ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
        builder.val("is_vertical", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d3 = 0.7;
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object4);
                object = polyClassMachine_v22.tm$94_nearby_entities(d3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        Object object5 = scriptContext.getClassOrVar("launch");
        Object object6 = scriptContext.getClassOrVar("dir");
        if (list != null) {
            for (ScriptValue scriptValue10 : list) {
                ScriptValue scriptValue11;
                ScriptValue scriptValue12;
                builder.val("entity", scriptValue10);
                if (!(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                if (bl) {
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                    Object object7 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    continue;
                }
                PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)(polyClassMachine_v24 != null ? polyClassMachine_v24.pg$133_facing_dx() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)(polyClassMachine_v23 != null ? polyClassMachine_v23.pg$131_facing_dz() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("dir", (ScriptValue)callSite);
                object6 = callSite;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "add", (ScriptValue)(scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)ScriptValue.of((double)(d2 * 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("launch", (ScriptValue)callSite2);
                object5 = callSite2;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                Object object8 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue15, (ScriptValue)object5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
