/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class MobGenerator {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "mob_index";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                object = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mob_idx", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("mob_idx", scriptValue3);
        }
        ScriptValue scriptValue4 = ScriptValue.of((String)"minecraft:zombie");
        builder.val("mob_type", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)2.0))) {
            ScriptValue scriptValue5 = ScriptValue.of((String)"minecraft:skeleton");
            builder.val("mob_type", scriptValue5);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)3.0))) {
            ScriptValue scriptValue6 = ScriptValue.of((String)"minecraft:spider");
            builder.val("mob_type", scriptValue6);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)4.0))) {
            ScriptValue scriptValue7 = ScriptValue.of((String)"minecraft:wither_skeleton");
            builder.val("mob_type", scriptValue7);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)5.0))) {
            ScriptValue scriptValue8 = ScriptValue.of((String)"minecraft:creeper");
            builder.val("mob_type", scriptValue8);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)6.0))) {
            ScriptValue scriptValue9 = ScriptValue.of((String)"minecraft:piglin");
            builder.val("mob_type", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)7.0))) {
            ScriptValue scriptValue10 = ScriptValue.of((String)"minecraft:enderman");
            builder.val("mob_type", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)8.0))) {
            ScriptValue scriptValue11 = ScriptValue.of((String)"minecraft:iron_golem");
            builder.val("mob_type", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("mob_idx"), (ScriptValue)ScriptValue.of((double)9.0))) {
            ScriptValue scriptValue12 = ScriptValue.of((String)"minecraft:slime");
            builder.val("mob_type", scriptValue12);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("fuel", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true) {
            PolyClassMachine polyClassMachine3;
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("World");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                PolyClassMachine polyClassMachine4;
                PolyClassMachine polyClassMachine5;
                PolyClassMachine polyClassMachine6;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("mob_type");
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine6.pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.5));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue19 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue18 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine5.pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)1.0));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)(scriptValue20 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassMachine4.pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.5));
                if (scriptValue14 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object3);
                    v1 = polyClassWorld.tm$8_spawn_entity(scriptValue15.asStr(), scriptValue17.asNum(), scriptValue19.asNum(), scriptValue21.asNum());
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue15);
                    arrayList3.add(scriptValue17);
                    arrayList3.add(scriptValue19);
                    arrayList3.add(scriptValue21);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "spawn_entity", (ScriptValue)scriptValue14, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((double)0.0));
            arrayList4.add(ScriptValue.of((double)1.0));
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
            PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22)) != null ? polyClassMachine3.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue22, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList4, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
