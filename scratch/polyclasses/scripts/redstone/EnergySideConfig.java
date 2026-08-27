/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
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
            ScriptValue.Obj obj;
            Object object3;
            String string = "energy";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$72_io_get(string, string2, scriptValue2.asStr()));
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("has_in", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "energy";
            String string3 = "output";
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
            if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                object = ScriptValue.of((boolean)polyClassMachine_v3.tm$72_io_get(string, string3, scriptValue5.asStr()));
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("has_out", scriptValue6);
        if (scriptValue3.asBool() ^ true && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "energy";
                String string4 = "input";
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string4, scriptValue8.asStr(), bl));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "energy";
                String string5 = "input";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue9 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string5, scriptValue10.asStr(), bl));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "energy";
                String string6 = "output";
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string6, scriptValue12.asStr(), bl));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() ^ true && scriptValue6.asBool()) {
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                String string = "energy";
                String string7 = "input";
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue13 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string7, scriptValue14.asStr(), bl));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                String string = "energy";
                String string8 = "input";
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string8, scriptValue16.asStr(), bl));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "energy";
                String string9 = "output";
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue17 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$54_io_set(string, string9, scriptValue18.asStr(), bl));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                }
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
