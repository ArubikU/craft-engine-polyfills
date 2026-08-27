/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$72_io_get(string, string2, scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("has_in", scriptValue3);
        if (scriptValue != ScriptValue.NULL) {
            String string = "energy";
            String string3 = "output";
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("dir");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$72_io_get(string, string3, scriptValue4.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("has_out", scriptValue5);
        if (scriptValue3.asBool() ^ true && scriptValue5.asBool() ^ true) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string4 = "input";
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string4, scriptValue6.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() && scriptValue5.asBool() ^ true) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string5 = "input";
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string5, scriptValue7.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string6 = "output";
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string6, scriptValue8.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() ^ true && scriptValue5.asBool()) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string7 = "input";
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v5 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string7, scriptValue9.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
        } else {
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string8 = "input";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v6 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string8, scriptValue10.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            if (scriptValue != ScriptValue.NULL) {
                String string = "energy";
                String string9 = "output";
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v7 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string9, scriptValue11.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
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
