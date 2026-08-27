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
import java.util.ArrayList;
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
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                PolyClassMachine_v3 polyClassMachine_v32 = new PolyClassMachine_v3(object3);
                object2 = polyClassMachine_v32.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v3 polyClassMachine_v33 = new PolyClassMachine_v3(object4);
                object = polyClassMachine_v33.tm$94_nearby_entities(d3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("entities", scriptValue7);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue7);
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                ScriptValue scriptValue9;
                PolyClassMachine_v3 polyClassMachine_v34;
                PolyClassMachine_v3 polyClassMachine_v35;
                PolyClassMachine_v3 polyClassMachine_v36;
                PolyClassMachine_v3 polyClassMachine_v37;
                PolyClassMachine_v3 polyClassMachine_v38;
                PolyClassMachine_v3 polyClassMachine_v39;
                ScriptValue scriptValue10;
                builder.val("entity", scriptValue8);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                if (!(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entity");
                double d4 = (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassMachine_v39.tg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue13 = ScriptValue.of((double)d4);
                builder.val("dx", scriptValue13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                double d5 = (scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue15 != ScriptValue.NULL ? ((polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine_v38.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue15, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue16 = ScriptValue.of((double)d5);
                builder.val("dy", scriptValue16);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                double d6 = (scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v37.tg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue18, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue19 = ScriptValue.of((double)d6);
                builder.val("dz", scriptValue19);
                double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                ScriptValue scriptValue20 = ScriptValue.of((double)d7);
                builder.val("dist_sq", scriptValue20);
                if (!(d7 > 0.1)) continue;
                double d8 = Math.sqrt(d7);
                ScriptValue scriptValue21 = ScriptValue.of((double)d8);
                builder.val("dist", scriptValue21);
                double d9 = d8;
                double d10 = d9 == 0.0 ? 0.0 : d2 / d9;
                ScriptValue scriptValue22 = ScriptValue.of((double)d10);
                builder.val("force", scriptValue22);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue23 != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue23)) != null ? polyClassMachine_v36.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue23, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue24 != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue24)) != null ? polyClassMachine_v35.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue24, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
                arrayList.add((ScriptValue)(scriptValue25 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue25)) != null ? polyClassMachine_v34.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue25, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue26 = ScriptFormula.callBuiltin((String)"vec", arrayList, (ScriptContext)scriptContext);
                builder.val("dir", scriptValue26);
                if (scriptValue5.asNum() > 0.0) {
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("dir");
                    ScriptValue scriptValue28 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("dir", scriptValue28);
                }
                ScriptValue scriptValue29 = (scriptValue9 = scriptContext.getClassOrVar("dir")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("impulse", scriptValue29);
                ScriptValue scriptValue30 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue30, (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
