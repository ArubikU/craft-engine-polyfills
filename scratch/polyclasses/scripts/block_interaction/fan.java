/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public final class Fan {
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
        Object object2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("power", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        double d = 15.0;
        double d2 = 15.0 == 0.0 ? 0.0 : scriptValue2.asNum() / d;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("strength", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "inversed";
            String string2 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object2 = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("inversed", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d3 = 5.0;
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object4);
                object = polyClassMachine3.tm$94_nearby_entities(d3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("entities", scriptValue7);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("dist_sq");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("dx");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("dy");
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("dz");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("dist");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("force");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("impulse");
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("dir");
        if (list != null) {
            for (ScriptValue scriptValue16 : list) {
                ScriptValue scriptValue17;
                ScriptValue scriptValue18;
                ScriptValue scriptValue19;
                ScriptValue scriptValue20;
                ScriptValue scriptValue21;
                ScriptValue scriptValue22;
                ScriptValue scriptValue23;
                PolyClassMachine polyClassMachine4;
                builder.val("entity", scriptValue16);
                if (!(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                double d4 = (scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - ((polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine4.tg$201_x() : ((scriptValue23 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue23, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()));
                ScriptValue scriptValue24 = ScriptValue.of((double)d4);
                builder.val("dx", scriptValue24);
                scriptValue9 = scriptValue24;
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                double d5 = (scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (polyClassMachine5 != null ? polyClassMachine5.tg$203_y() : ((scriptValue22 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue22, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()));
                ScriptValue scriptValue25 = ScriptValue.of((double)d5);
                builder.val("dy", scriptValue25);
                scriptValue10 = scriptValue25;
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                double d6 = (scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (polyClassMachine6 != null ? polyClassMachine6.tg$207_z() : ((scriptValue21 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue21, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()));
                ScriptValue scriptValue26 = ScriptValue.of((double)d6);
                builder.val("dz", scriptValue26);
                scriptValue11 = scriptValue26;
                double d7 = scriptValue9.asNum() * scriptValue9.asNum() + scriptValue10.asNum() * scriptValue10.asNum() + scriptValue11.asNum() * scriptValue11.asNum();
                ScriptValue scriptValue27 = ScriptValue.of((double)d7);
                builder.val("dist_sq", scriptValue27);
                scriptValue8 = scriptValue27;
                if (!(scriptValue8.asNum() > 0.1)) continue;
                double d8 = Math.sqrt(scriptValue8.asNum());
                ScriptValue scriptValue28 = ScriptValue.of((double)d8);
                builder.val("dist", scriptValue28);
                scriptValue12 = scriptValue28;
                double d9 = scriptValue12.asNum();
                double d10 = d9 == 0.0 ? 0.0 : d2 / d9;
                ScriptValue scriptValue29 = ScriptValue.of((double)d10);
                builder.val("force", scriptValue29);
                scriptValue13 = scriptValue29;
                PolyClassMachine polyClassMachine7 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine8 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine9 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue30 = ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)(polyClassMachine7 != null ? polyClassMachine7.pg$133_facing_dx() : ((scriptValue20 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine8 != null ? polyClassMachine8.pg$129_facing_dy() : ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine9 != null ? polyClassMachine9.pg$131_facing_dz() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
                builder.val("dir", scriptValue30);
                scriptValue15 = scriptValue30;
                if (scriptValue5.asNum() > 0.0) {
                    ScriptValue scriptValue31 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("dir", scriptValue31);
                    scriptValue15 = scriptValue31;
                }
                ScriptValue scriptValue32 = (scriptValue17 = scriptContext.getClassOrVar("dir")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue17, (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("impulse", scriptValue32);
                scriptValue14 = scriptValue32;
                Object object5 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue16, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
