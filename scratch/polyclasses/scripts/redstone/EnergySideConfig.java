/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class EnergySideConfig {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue cycleEnergy(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "energy";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$72_io_get(string, string2, scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("has_in", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "energy";
            String string3 = "output";
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$72_io_get(string, string3, scriptValue5.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("has_out", scriptValue6);
        if (scriptValue3.asBool() ^ true && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string = "energy";
                String string4 = "input";
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string4, scriptValue8.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                String string = "energy";
                String string5 = "input";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                v3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string5, scriptValue10.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                String string = "energy";
                String string6 = "output";
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                v4 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string6, scriptValue12.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() ^ true && scriptValue6.asBool()) {
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                String string = "energy";
                String string7 = "input";
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
                v5 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string7, scriptValue14.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                String string = "energy";
                String string8 = "input";
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                v6 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string8, scriptValue16.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                String string = "energy";
                String string9 = "output";
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17);
                v7 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string9, scriptValue18.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue energyNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "north"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue energySouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "south"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue energyEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "east"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue energyWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "west"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue energyUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "up"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue energyDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", EnergySideConfig.class, "down"));
        EnergySideConfig.cycleEnergy(builder2);
        return ScriptValue.NULL;
    }
}
