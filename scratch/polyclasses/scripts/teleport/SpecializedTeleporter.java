/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
public final class SpecializedTeleporter {
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "frequency";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        return ScriptValue.of((String)stringBuilder.append((polyClassMachine_v4 != null ? polyClassMachine_v4.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine_v42 != null ? polyClassMachine_v42.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine_v43 != null ? polyClassMachine_v43.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).toString());
    }

    public static ScriptValue registerSelf(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        ScriptValue scriptValue = SpecializedTeleporter.freqKey(builder2);
        builder.val("key", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = scriptValue;
            String string = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue3.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("existing", scriptValue4);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = SpecializedTeleporter.myPosStr(builder3);
        builder.val("mine", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";")), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptValue5)) continue;
                return ScriptValue.NULL;
            }
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue8 = scriptValue;
                String string = "string";
                ScriptValue scriptValue9 = scriptValue5;
                if (scriptValue7 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object3);
                    v1 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue8.asStr(), string, scriptValue9));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue11 = scriptValue;
                String string = "string";
                ScriptValue scriptValue12 = ScriptValue.of((String)(scriptValue4.asStr() + ";" + scriptValue5.asStr()));
                if (scriptValue10 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object4);
                    v2 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue11.asStr(), string, scriptValue12));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue unregisterSelf(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        ScriptValue scriptValue2 = SpecializedTeleporter.freqKey(builder2);
        builder.val("key", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue4 = scriptValue2;
            String string = "string";
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("existing", scriptValue5);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue6 = SpecializedTeleporter.myPosStr(builder3);
        builder.val("mine", scriptValue6);
        ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
        builder.val("result", scriptValue7);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue8 = scriptValue7;
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("entry", scriptValue9);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue9, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptValue6) ^ true)) continue;
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"")) {
                    ScriptValue scriptValue10 = scriptValue9;
                    builder.val("result", scriptValue10);
                    scriptValue8 = scriptValue10;
                    continue;
                }
                ScriptValue scriptValue11 = ScriptValue.of((String)(scriptValue8.asStr() + ";" + scriptValue9.asStr()));
                builder.val("result", scriptValue11);
                scriptValue8 = scriptValue11;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue12 = scriptValue2;
            String string = "string";
            ScriptValue scriptValue13 = scriptValue8;
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object3);
                v1 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue12.asStr(), string, scriptValue13));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue setFrequency(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = SpecializedTeleporter.getFrequency(builder2);
        builder.val("old", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("freq"))) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        SpecializedTeleporter.unregisterSelf(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "frequency";
            String string2 = "string";
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("freq");
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue3));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptContext.getClassOrVar("freq"));
        SpecializedTeleporter.registerSelf(builder4);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "page_offset";
            String string3 = "int";
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0);
            if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue5));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _signAliasAt(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)scriptContext.getClassOrVar("world_name"), (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", SpecializedTeleporter.class, 1)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("center", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "north"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "south"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "east"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "west"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "up"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("line");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("dir", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("center");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue7, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue8);
                scriptValue4 = scriptValue8;
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue9 : list2) {
                        builder.val("line", scriptValue9);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue9, (String)"") ^ true)) continue;
                        return scriptValue9;
                    }
                }
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
    }

    public static ScriptValue _ownAlias(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptValue scriptValue4;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyClassWorld polyClassWorld = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
        builder2.val("world_name", (ScriptValue)(polyClassWorld != null ? polyClassWorld.pg$30_name() : ((scriptValue4 = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder2.val("x", (ScriptValue)(polyClassMachine_v4 != null ? polyClassMachine_v4.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder2.val("y", (ScriptValue)(polyClassMachine_v42 != null ? polyClassMachine_v42.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder2.val("z", (ScriptValue)(polyClassMachine_v43 != null ? polyClassMachine_v43.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        return SpecializedTeleporter._signAliasAt(builder2);
    }

    public static ScriptValue frequencyEntries(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("entries", (ScriptValue)array);
        if (scriptContext.getStr("freq").equals("")) {
            return array;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptContext.getClassOrVar("freq"));
            ScriptValue scriptValue2 = SpecializedTeleporter.freqKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("existing", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue4 = SpecializedTeleporter.myPosStr(builder3);
        builder.val("mine", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("entry", scriptValue5);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptValue4) ^ true)) continue;
                ScriptValue scriptValue6 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue6);
                array2 = scriptValue6;
            }
        }
        return array2;
    }

    public static ScriptValue totalPages(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)SpecializedTeleporter.frequencyEntries(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0);
        }
        double d = scriptContext.getNum("DEST_PAGE_SIZE");
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)));
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core");
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("click_type").equals("right") || scriptContext.getStr("click_type").equals("shift_right")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ""));
            SpecializedTeleporter.setFrequency(builder2);
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$74_update());
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("clicked_item"), (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("clicked_item");
        builder3.val("freq", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
        SpecializedTeleporter.setFrequency(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$74_update());
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue statusName(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = SpecializedTeleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<red>No frequency tuned");
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = SpecializedTeleporter._ownAlias(builder3);
        builder.val("alias", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue.asStr() + " <gray>(" + scriptValue2.asStr() + ")"));
        }
        return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue.asStr()));
    }

    public static ScriptValue statusLore(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = SpecializedTeleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("lines", (ScriptValue)array);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>Insert a renamed item below to tune one.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue2);
            return scriptValue2;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)SpecializedTeleporter.frequencyEntries(builder3), (ScriptContext)scriptContext);
        builder.val("n", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<green>" + scriptValue3.asStr() + " destination(s) available.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue4);
        } else {
            ScriptValue scriptValue5 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<red>No other teleporter tuned to this frequency yet.")), (ScriptContext)scriptContext);
            builder.val("lines", scriptValue5);
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("lines");
        StringBuilder stringBuilder = new StringBuilder().append("<gray>Page <white>");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "page_offset";
            String string2 = "int";
            if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        StringBuilder stringBuilder2 = stringBuilder.append(ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asStr()).append("<gray>/<white>");
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptValue);
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)stringBuilder2.append(SpecializedTeleporter.totalPages(builder4).asStr()).toString()), (ScriptContext)scriptContext);
        builder.val("lines", scriptValue8);
        ScriptValue scriptValue9 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<gray>Cost per jump: <white>" + scriptContext.getStr("TELEPORT_COST") + " CE")), (ScriptContext)scriptContext);
        builder.val("lines", scriptValue9);
        return scriptValue9;
    }

    public static ScriptValue generateButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = SpecializedTeleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        ScriptValue scriptValue2 = SpecializedTeleporter.frequencyEntries(builder3);
        builder.val("entries", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "page_offset";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("offset", scriptValue4);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptValue);
        ScriptValue scriptValue5 = SpecializedTeleporter.totalPages(builder4);
        builder.val("tp", scriptValue5);
        if (scriptValue4.asNum() > 0.0) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("out");
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 45.0));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:prev_icon"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow><- Previous Page"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:prev_page"));
            ScriptValue scriptValue7 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue6, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
            builder.val("out", scriptValue7);
        }
        if (ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asNum() < scriptValue5.asNum()) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("out");
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 53.0));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "internal:next_page_0"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow>Next Page ->"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:next_page"));
            ScriptValue scriptValue9 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue8, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
            builder.val("out", scriptValue9);
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("out");
        ArrayList<Object> arrayList4 = new ArrayList<Object>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 5.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<aqua>Tune via Dialog"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>Type a frequency instead of"));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>inserting a renamed item."));
        arrayList4.add(new ScriptValue.Array(arrayList5));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:open_frequency_dialog"));
        ScriptValue scriptValue11 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue10, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList4, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
        builder.val("out", scriptValue11);
        double d = scriptValue4.asNum() * scriptContext.getNum("DEST_PAGE_SIZE");
        ScriptValue scriptValue12 = ScriptValue.of((double)d);
        builder.val("start", scriptValue12);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)scriptContext.getClassOrVar("DEST_PAGE_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("entry");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("lore");
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("alias");
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("display_name");
        ScriptValue scriptValue19 = scriptValue11;
        if (list != null) {
            for (ScriptValue scriptValue20 : list) {
                ScriptValue scriptValue21;
                builder.val("i", scriptValue20);
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue20);
                builder.val("global_idx", scriptValue22);
                scriptValue14 = scriptValue22;
                if (scriptValue14.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("entries");
                ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("entry", scriptValue24);
                scriptValue13 = scriptValue24;
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue25);
                scriptValue16 = scriptValue25;
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue26 = scriptContext.getClassOrVar("parts");
                builder5.val("world_name", (ScriptValue)(scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue27 = scriptContext.getClassOrVar("parts");
                builder5.val("x", (ScriptValue)(scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue28 = scriptContext.getClassOrVar("parts");
                builder5.val("y", (ScriptValue)(scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue29 = scriptContext.getClassOrVar("parts");
                builder5.val("z", (ScriptValue)(scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue30 = SpecializedTeleporter._signAliasAt(builder5);
                builder.val("alias", scriptValue30);
                scriptValue17 = scriptValue30;
                ScriptValue scriptValue31 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue17, (String)"") ^ true ? scriptValue17 : ((scriptValue21 = scriptContext.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("display_name", scriptValue31);
                scriptValue18 = scriptValue31;
                ArrayList arrayList6 = new ArrayList();
                ScriptValue.Array array2 = new ScriptValue.Array(arrayList6);
                builder.val("lore", (ScriptValue)array2);
                scriptValue15 = array2;
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue33 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue34 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue35 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)("<gray>" + (scriptValue32 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue32, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue33, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue34, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
                builder.val("lore", scriptValue35);
                scriptValue15 = scriptValue35;
                ScriptValue scriptValue36 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)("<dark_gray>Click to teleport <white>(" + scriptContext.getStr("TELEPORT_COST") + " CE)")), (ScriptContext)scriptContext);
                builder.val("lore", scriptValue36);
                scriptValue15 = scriptValue36;
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
                ScriptValue scriptValue37 = scriptContext.getClassOrVar("DEST_SLOTS");
                arrayList7.add((ScriptValue)(scriptValue37 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue37, (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
                arrayList7.add(ScriptValue.of((String)("<yellow>" + scriptValue18.asStr())));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
                arrayList7.add(scriptValue15);
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
                arrayList7.add(ScriptValue.of((String)("specialized_teleporter.pf:do_teleport_slot:" + scriptValue20.asStr())));
                ScriptValue scriptValue38 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue19, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList7, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue38);
                scriptValue19 = scriptValue38;
            }
        }
        return scriptValue19;
    }

    public static ScriptValue doTeleportSlot(ScriptContext.Builder builder) {
        block34: {
            Object object;
            Object object2;
            ScriptValue scriptValue;
            Object object3;
            ScriptContext scriptContext = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue2 = SpecializedTeleporter.getFrequency(builder2);
            builder.val("freq", scriptValue2);
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("freq", scriptValue2);
            ScriptValue scriptValue3 = SpecializedTeleporter.frequencyEntries(builder3);
            builder.val("entries", scriptValue3);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "page_offset";
                String string2 = "int";
                if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    object3 = polyClassMachine_v4.tm$34_get_typed(string, string2);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)(object3.asNum() * scriptContext.getNum("DEST_PAGE_SIZE"))), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptContext)scriptContext));
            builder.val("global_idx", scriptValue5);
            if (scriptValue5.asNum() < 0.0 || scriptValue5.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asNum()) {
                return ScriptValue.NULL;
            }
            PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            double d = polyClassMachine_v4 != null ? polyClassMachine_v4.tg$126_energy_stored() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum());
            if (d < scriptContext.getNum("TELEPORT_COST")) {
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    ScriptValue scriptValue7 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                    if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                        v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                    } else {
                        v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("entries");
            ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("entry", scriptValue9);
            ScriptValue scriptValue10 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ",")), (ScriptContext)scriptContext);
            builder.val("parts", scriptValue10);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("parts");
            ScriptValue scriptValue12 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
            builder.val("target_world", scriptValue12);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue12, (ScriptContext)scriptContext).asBool()) {
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object6;
                    String string = "<red>That destination's world is not currently loaded.";
                    if (scriptValue13 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                        v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("target_world");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5)));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue14, (ScriptValue)scriptValue16, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue19 = object2;
            builder.val("loc", scriptValue19);
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("TELEPORT_COST");
                if (scriptValue20 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object7);
                    object = ScriptValue.of((boolean)polyClassMachine_v42.tm$11_consume_energy(scriptValue21.asNum()));
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            if (!object.asBool()) break block34;
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
            if (scriptValue22 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                if (scriptValue22 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object8);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v43.tm$92_close());
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                ScriptValue scriptValue24 = scriptValue19;
                if (scriptValue23 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                    v8 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue24));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Player");
            if (scriptValue25 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                ScriptValue scriptValue26;
                ScriptValue scriptValue27 = ScriptValue.of((String)("<green>Teleported to " + ((scriptValue26 = scriptContext.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "."));
                if (scriptValue25 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                    v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue27.asStr()));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue25, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openFrequencyDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Tune Frequency")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Frequency")), (ScriptValue)SpecializedTeleporter.getFrequency(builder2), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Accept")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:on_frequency_dialog_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onFrequencyDialogSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("new_freq"));
        SpecializedTeleporter.setFrequency(builder2);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$74_update());
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextPage(ScriptContext.Builder builder) {
        block8: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue = SpecializedTeleporter.getFrequency(builder2);
            builder.val("freq", scriptValue);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("freq", scriptValue);
            ScriptValue scriptValue2 = SpecializedTeleporter.totalPages(builder3);
            builder.val("tp", scriptValue2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "page_offset";
                String string2 = "int";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                    object = polyClassMachine_v4.tm$34_get_typed(string, string2);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = object;
            builder.val("cur", scriptValue4);
            if (!(ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asNum() < scriptValue2.asNum())) break block8;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "page_offset";
                String string3 = "int";
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)));
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue prevPage(ScriptContext.Builder builder) {
        block8: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "page_offset";
                String string2 = "int";
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                    object = polyClassMachine_v4.tm$34_get_typed(string, string2);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue2 = object;
            builder.val("cur", scriptValue2);
            if (!(scriptValue2.asNum() > 0.0)) break block8;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "page_offset";
                String string3 = "int";
                ScriptValue scriptValue4 = ScriptValue.of((double)(scriptValue2.asNum() - 1.0));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue4));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onRedstoneActuator(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = SpecializedTeleporter._ownAlias(builder2);
        builder.val("alias", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = SpecializedTeleporter.getFrequency(builder3);
        builder.val("freq", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)SpecializedTeleporter.frequencyEntries(builder4));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("loc");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("parts");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("target_world");
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                Object object;
                Object object2;
                ScriptValue scriptValue8;
                builder.val("entry", scriptValue7);
                ScriptValue scriptValue9 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue9);
                scriptValue5 = scriptValue9;
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("parts");
                builder5.val("world_name", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("parts");
                builder5.val("x", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
                builder5.val("y", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                builder5.val("z", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                if (!ScriptFormula.valuesEqual((ScriptValue)SpecializedTeleporter._signAliasAt(builder5), (ScriptValue)scriptValue2)) continue;
                PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                double d = polyClassMachine_v4 != null ? polyClassMachine_v4.tg$126_energy_stored() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum());
                if (d < scriptContext.getNum("TELEPORT_COST")) {
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
                    if (scriptValue14 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object3;
                        ScriptValue scriptValue15 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                        if (scriptValue14 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                            v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue15.asStr()));
                        } else {
                            v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                        }
                    } else {
                        v1 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue17 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue17);
                scriptValue6 = scriptValue17;
                if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool()) {
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
                    if (scriptValue18 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object4;
                        String string = "<red>That destination's world is not currently loaded.";
                        if (scriptValue18 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                            v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                        } else {
                            v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v2 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("target_world");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                    ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5)));
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                    object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue19, (ScriptValue)scriptValue21, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue24 = object2;
                builder.val("loc", scriptValue24);
                scriptValue4 = scriptValue24;
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
                if (scriptValue25 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue25 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object5);
                        object = ScriptValue.of((boolean)polyClassMachine_v42.tm$11_consume_energy(scriptValue26.asNum()));
                    } else {
                        object = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                if (object.asBool()) {
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("Player");
                    if (scriptValue27 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object6;
                        ScriptValue scriptValue28 = scriptValue4;
                        if (scriptValue27 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                            v6 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue28));
                        } else {
                            v6 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue27, (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue29 = scriptContext.getClassOrVar("Player");
                    if (scriptValue29 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object7;
                        ScriptValue scriptValue30 = ScriptValue.of((String)("<green>Teleported to <white>" + scriptValue2.asStr() + "<green>."));
                        if (scriptValue29 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                            v7 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue30.asStr()));
                        } else {
                            v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue29, (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue31 = ScriptValue.of((String)("<red>No other teleporter labeled '" + scriptValue2.asStr() + "' found on this frequency."));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v8 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue31.asStr()));
            } else {
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue31, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$24_open_menu(scriptValue2));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "open_menu", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", SpecializedTeleporter.getFrequency(builder3));
        SpecializedTeleporter.unregisterSelf(builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 10000.0;
        ScriptValue scriptValue = ScriptValue.of((double)10000.0);
        builder.val("TELEPORT_COST", scriptValue);
        double d2 = 28.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)28.0);
        builder.val("DEST_PAGE_SIZE", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 10.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 11.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 12.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 13.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 14.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 15.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 16.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 19.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 20.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 21.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 22.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 23.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 24.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 25.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 28.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 29.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 30.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 31.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 32.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 33.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 34.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 37.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 38.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 39.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 40.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 41.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 42.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 43.0));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("DEST_SLOTS", (ScriptValue)array);
        FILE_SCOPE = builder.build();
    }
}
