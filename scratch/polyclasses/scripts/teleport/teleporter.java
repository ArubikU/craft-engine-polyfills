/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Teleporter {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue getFrequency(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "frequency";
            String string2 = "string";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue freqKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("teleporter_freq_" + scriptContext.getStr("freq")));
    }

    public static ScriptValue myPosStr(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptValue scriptValue4;
        ScriptContext scriptContext = builder.peek();
        PolyClassWorld polyClassWorld = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
        StringBuilder stringBuilder = new StringBuilder().append(polyClassWorld != null ? polyClassWorld.tg$31_name() : ((scriptValue4 = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())).append(",");
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        return ScriptValue.of((String)stringBuilder.append((polyClassMachine_v3 != null ? polyClassMachine_v3.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine_v32 != null ? polyClassMachine_v32.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine_v33 != null ? polyClassMachine_v33.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).toString());
    }

    public static ScriptValue registerSelf(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        ScriptValue scriptValue = Teleporter.freqKey(builder2);
        builder.val("key", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 = scriptValue;
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue3.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("existing", scriptValue4);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptValue5)) continue;
                return ScriptValue.NULL;
            }
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue scriptValue7 = scriptValue;
                String string = "string";
                ScriptValue scriptValue8 = scriptValue5;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
                v1 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue7.asStr(), string, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue9 = scriptValue;
            String string = "string";
            ScriptValue scriptValue10 = ScriptValue.of((String)(scriptValue4.asStr() + ";" + scriptValue5.asStr()));
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
            v2 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue9.asStr(), string, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue unregisterSelf(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        ScriptValue scriptValue = Teleporter.freqKey(builder2);
        builder.val("key", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 = scriptValue;
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue3.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("existing", scriptValue4);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue5);
        ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
        builder.val("result", scriptValue6);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue7 = scriptValue6;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                builder.val("entry", scriptValue8);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptValue5) ^ true)) continue;
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"")) {
                    ScriptValue scriptValue9 = scriptValue8;
                    builder.val("result", scriptValue9);
                    scriptValue7 = scriptValue9;
                    continue;
                }
                ScriptValue scriptValue10 = ScriptValue.of((String)(scriptValue7.asStr() + ";" + scriptValue8.asStr()));
                builder.val("result", scriptValue10);
                scriptValue7 = scriptValue10;
            }
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue11 = scriptValue;
            String string = "string";
            ScriptValue scriptValue12 = scriptValue7;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
            v1 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue11.asStr(), string, scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue setFrequency(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Teleporter.getFrequency(builder2);
        builder.val("old", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("freq"))) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        Teleporter.unregisterSelf(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "frequency";
            String string2 = "string";
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("freq");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue3)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptContext.getClassOrVar("freq"));
        Teleporter.registerSelf(builder4);
        return ScriptValue.NULL;
    }

    public static ScriptValue _signAliasAt(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)scriptContext.getClassOrVar("world_name"), (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
        }
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue, (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporter.class, 1)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("center", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "north"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "south"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "east"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "west"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "up"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("line");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("dir", scriptValue5);
                ScriptValue scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue2, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue6);
                scriptValue3 = scriptValue6;
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue7 : list2) {
                        builder.val("line", scriptValue7);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"") ^ true)) continue;
                        return scriptValue7;
                    }
                }
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
    }

    public static ScriptValue linkedCount(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptContext.getClassOrVar("freq"));
            ScriptValue scriptValue2 = Teleporter.freqKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("existing", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue4 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue4);
        double d = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("n", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue6 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                builder.val("entry", scriptValue7);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptValue4) ^ true)) continue;
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)));
                builder.val("n", scriptValue8);
                scriptValue6 = scriptValue8;
            }
        }
        return scriptValue6;
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "cml:teleporter_core");
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("click_type").equals("right") || scriptContext.getStr("click_type").equals("shift_right")) {
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ""));
            Teleporter.setFrequency(builder2);
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$74_update()) : PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("clicked_item"), (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("clicked_item");
        builder3.val("freq", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
        Teleporter.setFrequency(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$74_update()) : PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue statusName(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptValue scriptValue4;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<red>No frequency tuned");
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        PolyClassWorld polyClassWorld = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
        builder3.val("world_name", (ScriptValue)(polyClassWorld != null ? polyClassWorld.pg$30_name() : ((scriptValue4 = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("x", (ScriptValue)(polyClassMachine_v3 != null ? polyClassMachine_v3.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("y", (ScriptValue)(polyClassMachine_v32 != null ? polyClassMachine_v32.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("z", (ScriptValue)(polyClassMachine_v33 != null ? polyClassMachine_v33.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        ScriptValue scriptValue6 = Teleporter._signAliasAt(builder3);
        builder.val("alias", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"") ^ true) {
            return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue5.asStr() + " <gray>(" + scriptValue6.asStr() + ")"));
        }
        return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue5.asStr()));
    }

    public static ScriptValue statusLore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("lines", (ScriptValue)array);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<gray>Insert a renamed item below to tune one.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue2);
            return scriptValue2;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        ScriptValue scriptValue3 = Teleporter.linkedCount(builder3);
        builder.val("n", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<green>" + scriptValue3.asStr() + " other teleporter(s) linked.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue4);
        } else {
            ScriptValue scriptValue5 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<red>No other teleporter tuned to this frequency yet.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue5);
        }
        ScriptValue scriptValue6 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<gray>Cost per jump: <white>" + scriptContext.getStr("TELEPORT_COST") + " CE")), (ScriptContext)scriptContext);
        builder.val("lines", scriptValue6);
        return scriptValue6;
    }

    public static ScriptValue doTeleport(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                String string = "<red>No frequency set - insert a renamed item to tune one first.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue4);
                v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        double d = polyClassMachine_v3 != null ? polyClassMachine_v3.tg$126_energy_stored() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum());
        if (d < scriptContext.getNum("TELEPORT_COST")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue scriptValue6 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue5);
                v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue6.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("freq", scriptValue3);
            ScriptValue scriptValue8 = Teleporter.freqKey(builder3);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue7);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("existing", scriptValue9);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue10 = Teleporter.myPosStr(builder4);
        builder.val("mine", scriptValue10);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("loc");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("alias");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("target_world");
        if (list != null) {
            for (ScriptValue scriptValue15 : list) {
                Object object2;
                builder.val("entry", scriptValue15);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)scriptValue10) ^ true)) continue;
                ScriptValue scriptValue16 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue16);
                scriptValue12 = scriptValue16;
                ScriptValue scriptValue17 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue17);
                scriptValue14 = scriptValue17;
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue18 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue14, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5))), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("loc", scriptValue18);
                scriptValue11 = scriptValue18;
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("TELEPORT_COST");
                    PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue19);
                    object2 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$11_consume_energy(scriptValue20.asNum())) : PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                if (object2.asBool()) {
                    PolyClassMachine_v3 polyClassMachine_v33;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                    Object object3 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21)) != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$92_close()) : PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Player");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue scriptValue23 = scriptValue11;
                        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue22);
                        v6 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$24_teleport_to(scriptValue23)) : PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    builder5.val("world_name", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    builder5.val("x", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    builder5.val("y", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    builder5.val("z", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue24 = Teleporter._signAliasAt(builder5);
                    builder.val("alias", scriptValue24);
                    scriptValue13 = scriptValue24;
                    if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue13, (String)"") ^ true) {
                        if (scriptValue22 != ScriptValue.NULL) {
                            ScriptValue scriptValue25 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue3.asStr() + "' to <white>" + scriptValue13.asStr() + "<green>."));
                            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue22);
                            v7 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue25.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue22, (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                        } else {
                            v7 = ScriptValue.NULL;
                        }
                    } else if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue scriptValue26 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue3.asStr() + "'."));
                        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue22);
                        v8 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue26.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue22, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                    } else {
                        v8 = ScriptValue.NULL;
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue scriptValue27 = ScriptValue.of((String)("<red>No other teleporter is currently reachable on '" + scriptValue3.asStr() + "'."));
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v9 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue27.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openFrequencyDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "Tune Frequency")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "Frequency")), (ScriptValue)Teleporter.getFrequency(builder2), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "Accept")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "teleporter.pf:on_frequency_dialog_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onFrequencyDialogSubmit(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("new_freq"));
        Teleporter.setFrequency(builder2);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$74_update()) : PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", Teleporter.getFrequency(builder3));
        Teleporter.unregisterSelf(builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 5000.0;
        ScriptValue scriptValue = ScriptValue.of((double)5000.0);
        builder.val("TELEPORT_COST", scriptValue);
        FILE_SCOPE = builder.build();
    }
}
