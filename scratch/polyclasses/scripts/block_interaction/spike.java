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
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Spike {
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue4));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            Object object4;
            ScriptValue scriptValue5;
            CallSite callSite;
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue6;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue7 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                callSite = polyClassContainer.tm$0_get_item(d);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("weapon", (ScriptValue)callSite2);
            ScriptValue scriptValue8 = ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 2.0)) : (callSite2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("base_dmg", scriptValue8);
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue9 = polyClassMachine2 != null ? polyClassMachine2.pg$210_owner_uuid() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("owner_id", scriptValue9);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object6;
                double d2 = 0.8;
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj2 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object6);
                    object4 = polyClassMachine3.tm$94_nearby_entities(d2);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object4;
            builder.val("entities", scriptValue11);
            double d3 = 0.0;
            ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
            builder.val("hit_count", scriptValue12);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue11);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("is_owner");
            ScriptValue scriptValue14 = ScriptValue.of((double)d3);
            if (list != null) {
                for (ScriptValue scriptValue15 : list) {
                    builder.val("entity", scriptValue15);
                    if (!((scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ScriptFormula.callBuiltin2((String)"instanceof", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Spike.class, "LivingEntity")), (ScriptContext)scriptContext).asBool())) continue;
                    boolean bl = ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue9);
                    ScriptValue scriptValue16 = ScriptValue.of((boolean)bl);
                    builder.val("is_owner", scriptValue16);
                    scriptValue13 = scriptValue16;
                    if (!(scriptValue13.asBool() ^ true)) continue;
                    Object object7 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)scriptValue15, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 1.0)));
                    builder.val("hit_count", scriptValue17);
                    scriptValue14 = scriptValue17;
                }
            }
            if (scriptValue14.asNum() > 0.0) {
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object8;
                    String string = "atk_cd";
                    String string4 = "int";
                    ScriptValue scriptValue19 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 10.0);
                    if (scriptValue18 instanceof ScriptValue.Obj && (object8 = (obj3 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object8 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine4 = new PolyClassMachine(object8);
                        v5 = ScriptValue.of((boolean)polyClassMachine4.tm$82_set_typed(string, string4, scriptValue19));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
