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

public final class XpCollector {
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
            double d = 6.0;
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$94_nearby_entities(d);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("entities", scriptValue2);
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("total_xp", scriptValue3);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue2, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object3;
                ScriptValue scriptValue4;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:experience_orb")) continue;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("total_xp"), (ScriptValue)((scriptValue4 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "xp_value", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder.val("total_xp", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList arrayList = new ArrayList();
                    object3 = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        if (scriptContext.getNum("total_xp") > 0.0) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "experience";
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("total_xp");
                if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$98_fill_fluid(string, scriptValue9.asNum()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue9);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "fill_fluid", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
