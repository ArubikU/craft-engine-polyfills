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
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class LavaSpike {
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "atk_cd";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("cooldown", scriptValue2);
        if (scriptValue2.asNum() > 0.0) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "atk_cd";
                String string3 = "int";
                ScriptValue scriptValue4 = ScriptValue.of((double)(scriptValue2.asNum() - 1.0));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue4));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            Object object4;
            ScriptValue scriptValue5;
            PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue6 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$210_owner_uuid() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("owner_id", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                double d = 0.8;
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object5);
                    object4 = polyClassMachine_v22.tm$94_nearby_entities(d);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object4;
            builder.val("entities", scriptValue8);
            double d = 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
            builder.val("hit_count", scriptValue9);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue8);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("is_owner");
            ScriptValue scriptValue11 = ScriptValue.of((double)d);
            if (list != null) {
                for (ScriptValue scriptValue12 : list) {
                    builder.val("entity", scriptValue12);
                    if (!((scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", LavaSpike.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                    boolean bl = ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue6);
                    ScriptValue scriptValue13 = ScriptValue.of((boolean)bl);
                    builder.val("is_owner", scriptValue13);
                    scriptValue10 = scriptValue13;
                    if (!(scriptValue10.asBool() ^ true)) continue;
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("entity");
                    Object object6 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "fire", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 80.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                    Object object7 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 1.0)));
                    builder.val("hit_count", scriptValue16);
                    scriptValue11 = scriptValue16;
                }
            }
            if (scriptValue11.asNum() > 0.0) {
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                if (scriptValue17 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "atk_cd";
                    String string4 = "int";
                    ScriptValue scriptValue18 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 15.0);
                    if (scriptValue17 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object8);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v23.tm$82_set_typed(string, string4, scriptValue18));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue18, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
