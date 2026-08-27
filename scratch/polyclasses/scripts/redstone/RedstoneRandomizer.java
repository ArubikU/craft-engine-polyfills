/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RedstoneRandomizer {
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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object2.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("cur_power", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "prev_power";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                object = polyClassMachine_v42.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("prev_power", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "prev_power";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine_v43.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        if (d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneRandomizer.class, 0.0)))) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneRandomizer.class, 4.0));
            double d2 = Math.floor(ScriptFormula.callBuiltin((String)"random", arrayList, (ScriptContext)scriptContext).asNum());
            ScriptValue scriptValue7 = ScriptValue.of((double)d2);
            builder.val("dir", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "rand_dir";
                String string4 = "int";
                ScriptValue scriptValue9 = ScriptValue.of((double)d2);
                if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v44 = new PolyClassMachine_v4(object5);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v44.tm$82_set_typed(string, string4, scriptValue9));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(ScriptValue.of((String)string4));
                    arrayList2.add(scriptValue9);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d3 = 15.0;
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object6);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v45.tm$108_emit_redstone(d3));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((double)d3));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue10, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        } else if (d == 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d4 = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v46 = new PolyClassMachine_v4(object7);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v46.tm$108_emit_redstone(d4));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d4));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
