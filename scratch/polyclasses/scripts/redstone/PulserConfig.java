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
public final class PulserConfig {
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "dur_idx";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("idx", scriptValue3);
        double d = 6.0;
        double d2 = 6.0 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulserConfig.class, 1.0))).asNum() % d;
        ScriptValue scriptValue4 = ScriptValue.of((double)d2);
        builder.val("next_idx", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "dur_idx";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d2);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue6);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        double d3 = 5.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)5.0);
        builder.val("dur", scriptValue7);
        if (d2 == 1.0) {
            double d4 = 10.0;
            ScriptValue scriptValue8 = ScriptValue.of((double)10.0);
            builder.val("dur", scriptValue8);
        }
        if (d2 == 2.0) {
            double d5 = 20.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)20.0);
            builder.val("dur", scriptValue9);
        }
        if (d2 == 3.0) {
            double d6 = 40.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)40.0);
            builder.val("dur", scriptValue10);
        }
        if (d2 == 4.0) {
            double d7 = 60.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)60.0);
            builder.val("dur", scriptValue11);
        }
        if (d2 == 5.0) {
            double d8 = 100.0;
            ScriptValue scriptValue12 = ScriptValue.of((double)100.0);
            builder.val("dur", scriptValue12);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "duration";
            String string4 = "int";
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("dur");
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string4, scriptValue13));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                arrayList.add(scriptValue13);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "minecraft:block.stone_button.click_on";
            double d9 = 0.5;
            double d10 = 1.0;
            if (scriptValue14 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$14_play_sound(string, d9, d10));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((double)d9));
                arrayList.add(ScriptValue.of((double)d10));
                v3 = PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
