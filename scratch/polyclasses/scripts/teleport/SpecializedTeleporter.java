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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
        PolyClassMachine_v4 polyClassMachine_v4;
        PolyClassMachine_v4 polyClassMachine_v42;
        PolyClassMachine_v4 polyClassMachine_v43;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("World");
        return ScriptValue.of((String)((scriptValue4 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassWorld.tg$31_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + "," + (scriptValue != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v43.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v42.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v4.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()));
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("existing", scriptValue4);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = SpecializedTeleporter.myPosStr(builder3);
        builder.val("mine", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue4);
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue5)) continue;
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
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue8);
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(scriptValue9);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue11);
                    arrayList3.add(ScriptValue.of((String)string));
                    arrayList3.add(scriptValue12);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, arrayList3, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                builder.val("entry", scriptValue8);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue6) ^ true)) continue;
                if (scriptContext.getStr("result").equals("")) {
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("entry");
                    builder.val("result", scriptValue9);
                    continue;
                }
                ScriptValue scriptValue10 = ScriptValue.of((String)(scriptContext.getStr("result") + ";" + scriptContext.getStr("entry")));
                builder.val("result", scriptValue10);
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue11 = scriptValue2;
            String string = "string";
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("result");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object3);
                v1 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue11.asStr(), string, scriptValue12));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue11);
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(scriptValue12);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue3);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue5);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _signAliasAt(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("world_name"));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"world", arrayList, (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("x"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext));
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("y"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext));
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(scriptContext.getClassOrVar("z"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList6, (ScriptContext)scriptContext));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", SpecializedTeleporter.class, 1));
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("center", scriptValue3);
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "north"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "south"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "east"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "west"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "up"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList7));
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                Object object2;
                builder.val("dir", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("center");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptContext.getClassOrVar("dir"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue5, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object2;
                builder.val("b", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("b");
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue9 : list2) {
                        builder.val("line", scriptValue9);
                        if (!(scriptContext.getStr("line").equals("") ^ true)) continue;
                        return scriptContext.getClassOrVar("line");
                    }
                }
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "");
    }

    public static ScriptValue _ownAlias(ScriptContext.Builder builder) {
        PolyClassMachine_v4 polyClassMachine_v4;
        PolyClassMachine_v4 polyClassMachine_v42;
        PolyClassMachine_v4 polyClassMachine_v43;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("World");
        builder2.val("world_name", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassWorld.pg$30_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        builder2.val("x", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v43.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        builder2.val("y", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v42.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        builder2.val("z", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v4.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue2);
                arrayList2.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("existing", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue4 = SpecializedTeleporter.myPosStr(builder3);
        builder.val("mine", scriptValue4);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptValue3);
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("entry", scriptValue5);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue4) ^ true)) continue;
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("entries"));
                arrayList4.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue6);
            }
        }
        return scriptContext.getClassOrVar("entries");
    }

    public static ScriptValue totalPages(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("freq"));
        arrayList.add(SpecializedTeleporter.frequencyEntries(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("DEST_PAGE_SIZE");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)));
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
                    ArrayList arrayList = new ArrayList();
                    v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("clicked_item"));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
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
                ArrayList arrayList2 = new ArrayList();
                v1 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
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
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("lines"));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>Insert a renamed item below to tune one."));
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue2);
            return scriptValue2;
        }
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        arrayList3.add(SpecializedTeleporter.frequencyEntries(builder3));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"len", arrayList3, (ScriptContext)scriptContext);
        builder.val("n", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("lines"));
            arrayList4.add(ScriptValue.of((String)("<green>" + scriptValue3.asStr() + " destination(s) available.")));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue4);
        } else {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("lines"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<red>No other teleporter tuned to this frequency yet."));
            ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue5);
        }
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("lines"));
        StringBuilder stringBuilder = new StringBuilder().append("<gray>Page <white>");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "page_offset";
            String string2 = "int";
            if (scriptValue6 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((String)string));
                arrayList7.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList7, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        StringBuilder stringBuilder2 = stringBuilder.append(ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asStr()).append("<gray>/<white>");
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptValue);
        arrayList6.add(ScriptValue.of((String)stringBuilder2.append(SpecializedTeleporter.totalPages(builder4).asStr()).toString()));
        ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
        builder.val("lines", scriptValue7);
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(scriptContext.getClassOrVar("lines"));
        arrayList8.add(ScriptValue.of((String)("<gray>Cost per jump: <white>" + scriptContext.getStr("TELEPORT_COST") + " CE")));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList8, (ScriptContext)scriptContext);
        builder.val("lines", scriptValue8);
        return scriptValue8;
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
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
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
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptContext.getClassOrVar("out"));
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 45.0));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:prev_icon"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow><- Previous Page"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:prev_page"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"make_map", arrayList4, (ScriptContext)scriptContext));
            ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
            builder.val("out", scriptValue6);
        }
        if (ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asNum() < scriptValue5.asNum()) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("out"));
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 53.0));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "internal:next_page_0"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow>Next Page ->"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:next_page"));
            arrayList5.add(ScriptFormula.callBuiltin((String)"make_map", arrayList6, (ScriptContext)scriptContext));
            ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
            builder.val("out", scriptValue7);
        }
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(scriptContext.getClassOrVar("out"));
        ArrayList<Object> arrayList8 = new ArrayList<Object>();
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 5.0));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<aqua>Tune via Dialog"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
        arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>Type a frequency instead of"));
        arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>inserting a renamed item."));
        arrayList8.add(new ScriptValue.Array(arrayList9));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:open_frequency_dialog"));
        arrayList7.add(ScriptFormula.callBuiltin((String)"make_map", arrayList8, (ScriptContext)scriptContext));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
        builder.val("out", scriptValue8);
        double d = scriptValue4.asNum() * scriptContext.getNum("DEST_PAGE_SIZE");
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        builder.val("start", scriptValue9);
        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
        arrayList10.add(scriptContext.getClassOrVar("DEST_PAGE_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList10, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue10 : list) {
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                Object object8;
                Object object9;
                Object object10;
                Object object11;
                Object object12;
                builder.val("i", scriptValue10);
                ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue11);
                double d2 = scriptValue11.asNum();
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(scriptValue2);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList11, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entries");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add(scriptValue11);
                    object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object12 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = object12;
                builder.val("entry", scriptValue13);
                ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                arrayList13.add(scriptValue13);
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"split", arrayList13, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue14);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                    object11 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object11 = ScriptValue.NULL;
                }
                builder5.val("world_name", object11);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                if (scriptValue16 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                    arrayList15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0));
                    object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, arrayList15, (ScriptContext)scriptContext);
                } else {
                    object10 = ScriptValue.NULL;
                }
                builder5.val("x", object10);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                if (scriptValue17 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                    arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0));
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList16, (ScriptContext)scriptContext);
                } else {
                    object9 = ScriptValue.NULL;
                }
                builder5.val("y", object9);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                if (scriptValue18 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                    arrayList17.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList17, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                builder5.val("z", object8);
                ScriptValue scriptValue19 = SpecializedTeleporter._signAliasAt(builder5);
                builder.val("alias", scriptValue19);
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue19, (String)"") ^ true) {
                    object7 = scriptValue19;
                } else {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                    if (scriptValue20 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, arrayList18, (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                }
                ScriptValue scriptValue21 = object7;
                builder.val("display_name", scriptValue21);
                ArrayList arrayList19 = new ArrayList();
                ScriptValue.Array array2 = new ScriptValue.Array(arrayList19);
                builder.val("lore", (ScriptValue)array2);
                ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                arrayList20.add(scriptContext.getClassOrVar("lore"));
                StringBuilder stringBuilder = new StringBuilder().append("<gray>");
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                if (scriptValue22 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
                    arrayList21.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, arrayList21, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                StringBuilder stringBuilder2 = stringBuilder.append(object6.asStr()).append(", ");
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                if (scriptValue23 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList22 = new ArrayList<ScriptValue>();
                    arrayList22.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, arrayList22, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                StringBuilder stringBuilder3 = stringBuilder2.append(object5.asStr()).append(", ");
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                if (scriptValue24 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList23 = new ArrayList<ScriptValue>();
                    arrayList23.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, arrayList23, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList20.add(ScriptValue.of((String)stringBuilder3.append(object4.asStr()).toString()));
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"push", arrayList20, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue25);
                ArrayList<ScriptValue> arrayList24 = new ArrayList<ScriptValue>();
                arrayList24.add(scriptContext.getClassOrVar("lore"));
                arrayList24.add(ScriptValue.of((String)("<dark_gray>Click to teleport <white>(" + scriptContext.getStr("TELEPORT_COST") + " CE)")));
                ScriptValue scriptValue26 = ScriptFormula.callBuiltin((String)"push", arrayList24, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue26);
                ArrayList<ScriptValue> arrayList25 = new ArrayList<ScriptValue>();
                arrayList25.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList26 = new ArrayList<ScriptValue>();
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
                ScriptValue scriptValue27 = scriptContext.getClassOrVar("DEST_SLOTS");
                if (scriptValue27 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList27 = new ArrayList<ScriptValue>();
                    arrayList27.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, arrayList27, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList26.add((ScriptValue)object3);
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
                arrayList26.add(ScriptValue.of((String)("<yellow>" + scriptValue21.asStr())));
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
                arrayList26.add(scriptValue26);
                arrayList26.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
                arrayList26.add(ScriptValue.of((String)("specialized_teleporter.pf:do_teleport_slot:" + scriptContext.getStr("i"))));
                arrayList25.add(ScriptFormula.callBuiltin((String)"make_map", arrayList26, (ScriptContext)scriptContext));
                ScriptValue scriptValue28 = ScriptFormula.callBuiltin((String)"push", arrayList25, (ScriptContext)scriptContext);
                builder.val("out", scriptValue28);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue doTeleportSlot(ScriptContext.Builder var0) {
        block48: {
            var1_1 = var0.peek();
            var2_2 = ScriptContext.builder().copyFrom(var1_1);
            var3_3 = SpecializedTeleporter.getFrequency(var2_2);
            var0.val("freq", var3_3);
            if (ScriptFormula.valuesEqualStr((ScriptValue)var3_3, (String)"")) {
                return ScriptValue.NULL;
            }
            var4_4 = ScriptContext.builder().copyFrom(var1_1);
            var4_4.val("freq", var3_3);
            var5_5 = SpecializedTeleporter.frequencyEntries(var4_4);
            var0.val("entries", var5_5);
            var6_6 = var1_1.getClassOrVar("Machine");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = "page_offset";
                var8_8 = "int";
                if (var6_6 instanceof ScriptValue.Obj && (var10_10 = (var9_9 = (ScriptValue.Obj)var6_6).instance()) != null && !(var10_10 instanceof PolyClass) && var9_9.typeName().equals("Machine")) {
                    var11_11 = new PolyClassMachine_v4(var10_10);
                    v0 /* !! */  = var11_11.tm$34_get_typed(var7_7, var8_8);
                } else {
                    var12_12 = new ArrayList<ScriptValue>();
                    var12_12.add(ScriptValue.of((String)var7_7));
                    var12_12.add(ScriptValue.of((String)var8_8));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var6_6, var12_12, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            v1 = ScriptValue.of((double)(v0 /* !! */ .asNum() * var1_1.getNum("DEST_PAGE_SIZE")));
            var13_13 = new ArrayList<ScriptValue>();
            var13_13.add(var1_1.getClassOrVar("idx"));
            var14_14 = ScriptFormula.addPolymorphic((ScriptValue)v1, (ScriptValue)ScriptFormula.callBuiltin((String)"int", var13_13, (ScriptContext)var1_1));
            var0.val("global_idx", var14_14);
            if (var14_14.asNum() < 0.0) ** GOTO lbl-1000
            v2 = var14_14.asNum();
            var15_15 = new ArrayList<ScriptValue>();
            var15_15.add(var5_5);
            if (!(v2 >= ScriptFormula.callBuiltin((String)"len", var15_15, (ScriptContext)var1_1).asNum())) {
                v3 = false;
            } else lbl-1000:
            // 2 sources

            {
                v3 = true;
            }
            if (v3) {
                return ScriptValue.NULL;
            }
            var16_16 = var1_1.getClassOrVar("Machine");
            v4 = var16_16 != ScriptValue.NULL ? ((var17_17 = PolyClassMachine_v4.ofGuarded((ScriptValue)var16_16)) != null ? var17_17.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var16_16, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
            if (v4 < var1_1.getNum("TELEPORT_COST")) {
                var18_18 = var1_1.getClassOrVar("Player");
                if (var18_18 != ScriptValue.NULL) {
                    var19_19 = ScriptValue.of((String)("<red>Not enough energy - need " + var1_1.getStr("TELEPORT_COST") + " CE."));
                    if (var18_18 instanceof ScriptValue.Obj && (var21_21 = (var20_20 = (ScriptValue.Obj)var18_18).instance()) != null && !(var21_21 instanceof PolyClass) && var20_20.typeName().equals("Player")) {
                        var22_22 = new PolyClassPlayer(var21_21);
                        v5 /* !! */  = ScriptValue.of((boolean)var22_22.tm$42_send_message(var19_19.asStr()));
                    } else {
                        var23_23 = new ArrayList<ScriptValue>();
                        var23_23.add(var19_19);
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var18_18, var23_23, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var24_24 = var1_1.getClassOrVar("entries");
            if (var24_24 != ScriptValue.NULL) {
                var25_25 = new ArrayList<ScriptValue>();
                var25_25.add(var14_14);
                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var24_24, var25_25, (ScriptContext)var1_1);
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            var26_26 = v6 /* !! */ ;
            var0.val("entry", var26_26);
            var27_27 = new ArrayList<ScriptValue>();
            var27_27.add(var26_26);
            var27_27.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
            var28_28 = ScriptFormula.callBuiltin((String)"split", var27_27, (ScriptContext)var1_1);
            var0.val("parts", var28_28);
            var29_29 = new ArrayList<ScriptValue>();
            var30_30 = var1_1.getClassOrVar("parts");
            if (var30_30 != ScriptValue.NULL) {
                var31_31 = new ArrayList<ScriptValue>();
                var31_31.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var30_30, var31_31, (ScriptContext)var1_1);
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            var29_29.add(v7 /* !! */ );
            var32_32 = ScriptFormula.callBuiltin((String)"world", var29_29, (ScriptContext)var1_1);
            var0.val("target_world", var32_32);
            var33_33 = new ArrayList<ScriptValue>();
            var33_33.add(var32_32);
            if (ScriptFormula.callBuiltin((String)"is_empty", var33_33, (ScriptContext)var1_1).asBool()) {
                var34_34 = var1_1.getClassOrVar("Player");
                if (var34_34 != ScriptValue.NULL) {
                    var35_35 = "<red>That destination's world is not currently loaded.";
                    if (var34_34 instanceof ScriptValue.Obj && (var37_37 = (var36_36 = (ScriptValue.Obj)var34_34).instance()) != null && !(var37_37 instanceof PolyClass) && var36_36.typeName().equals("Player")) {
                        var38_38 = new PolyClassPlayer(var37_37);
                        v8 /* !! */  = ScriptValue.of((boolean)var38_38.tm$42_send_message(var35_35));
                    } else {
                        var39_39 = new ArrayList<ScriptValue>();
                        var39_39.add(ScriptValue.of((String)var35_35));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var34_34, var39_39, (ScriptContext)var1_1);
                    }
                } else {
                    v8 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var40_40 = var1_1.getClassOrVar("target_world");
            if (var40_40 != ScriptValue.NULL) {
                var41_41 = new ArrayList<ScriptValue>();
                var42_42 = new ArrayList<ScriptValue>();
                var43_43 = var1_1.getClassOrVar("parts");
                if (var43_43 != ScriptValue.NULL) {
                    var44_44 = new ArrayList<ScriptValue>();
                    var44_44.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0));
                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var43_43, var44_44, (ScriptContext)var1_1);
                } else {
                    v9 /* !! */  = ScriptValue.NULL;
                }
                var42_42.add(v9 /* !! */ );
                var41_41.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var42_42, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))));
                var45_45 = new ArrayList<ScriptValue>();
                var46_46 = var1_1.getClassOrVar("parts");
                if (var46_46 != ScriptValue.NULL) {
                    var47_47 = new ArrayList<ScriptValue>();
                    var47_47.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0));
                    v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var46_46, var47_47, (ScriptContext)var1_1);
                } else {
                    v10 /* !! */  = ScriptValue.NULL;
                }
                var45_45.add(v10 /* !! */ );
                var41_41.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var45_45, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5))));
                var48_48 = new ArrayList<ScriptValue>();
                var49_49 = var1_1.getClassOrVar("parts");
                if (var49_49 != ScriptValue.NULL) {
                    var50_50 = new ArrayList<ScriptValue>();
                    var50_50.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0));
                    v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var49_49, var50_50, (ScriptContext)var1_1);
                } else {
                    v11 /* !! */  = ScriptValue.NULL;
                }
                var48_48.add(v11 /* !! */ );
                var41_41.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var48_48, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))));
                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var40_40, var41_41, (ScriptContext)var1_1);
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var51_51 = v12 /* !! */ ;
            var0.val("loc", var51_51);
            var52_52 = var1_1.getClassOrVar("Machine");
            if (var52_52 != ScriptValue.NULL) {
                var53_53 = var1_1.getClassOrVar("TELEPORT_COST");
                if (var52_52 instanceof ScriptValue.Obj && (var55_55 = (var54_54 = (ScriptValue.Obj)var52_52).instance()) != null && !(var55_55 instanceof PolyClass) && var54_54.typeName().equals("Machine")) {
                    var56_56 = new PolyClassMachine_v4(var55_55);
                    v13 /* !! */  = ScriptValue.of((boolean)var56_56.tm$11_consume_energy(var53_53.asNum()));
                } else {
                    var57_57 = new ArrayList<ScriptValue>();
                    var57_57.add(var53_53);
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)var52_52, var57_57, (ScriptContext)var1_1);
                }
            } else {
                v13 /* !! */  = ScriptValue.NULL;
            }
            if (!v13 /* !! */ .asBool()) break block48;
            var58_58 = var1_1.getClassOrVar("Machine");
            if (var58_58 != ScriptValue.NULL) {
                if (var58_58 instanceof ScriptValue.Obj && (var60_60 = (var59_59 = (ScriptValue.Obj)var58_58).instance()) != null && !(var60_60 instanceof PolyClass) && var59_59.typeName().equals("Machine")) {
                    var61_61 = new PolyClassMachine_v4(var60_60);
                    v14 /* !! */  = ScriptValue.of((boolean)var61_61.tm$92_close());
                } else {
                    var62_62 = new ArrayList<E>();
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)var58_58, var62_62, (ScriptContext)var1_1);
                }
            } else {
                v14 /* !! */  = ScriptValue.NULL;
            }
            var63_63 = var1_1.getClassOrVar("Player");
            if (var63_63 != ScriptValue.NULL) {
                var64_64 = var51_51;
                if (var63_63 instanceof ScriptValue.Obj && (var66_66 = (var65_65 = (ScriptValue.Obj)var63_63).instance()) != null && !(var66_66 instanceof PolyClass) && var65_65.typeName().equals("Player")) {
                    var67_67 = new PolyClassPlayer(var66_66);
                    v15 /* !! */  = ScriptValue.of((boolean)var67_67.tm$24_teleport_to(var64_64));
                } else {
                    var68_68 = new ArrayList<ScriptValue>();
                    var68_68.add(var64_64);
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var63_63, var68_68, (ScriptContext)var1_1);
                }
            } else {
                v15 /* !! */  = ScriptValue.NULL;
            }
            var69_69 = var1_1.getClassOrVar("Player");
            if (var69_69 != ScriptValue.NULL) {
                v16 = new StringBuilder().append("<green>Teleported to ");
                var71_70 = var1_1.getClassOrVar("parts");
                if (var71_70 != ScriptValue.NULL) {
                    var72_71 = new ArrayList<ScriptValue>();
                    var72_71.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                    v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var71_70, var72_71, (ScriptContext)var1_1);
                } else {
                    v17 /* !! */  = ScriptValue.NULL;
                }
                var70_72 = ScriptValue.of((String)v16.append(v17 /* !! */ .asStr()).append(".").toString());
                if (var69_69 instanceof ScriptValue.Obj && (var74_74 = (var73_73 = (ScriptValue.Obj)var69_69).instance()) != null && !(var74_74 instanceof PolyClass) && var73_73.typeName().equals("Player")) {
                    var75_75 = new PolyClassPlayer(var74_74);
                    v18 /* !! */  = ScriptValue.of((boolean)var75_75.tm$42_send_message(var70_72.asStr()));
                } else {
                    var76_76 = new ArrayList<ScriptValue>();
                    var76_76.add(var70_72);
                    v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var69_69, var76_76, (ScriptContext)var1_1);
                }
            } else {
                v18 /* !! */  = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openFrequencyDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Machine"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Accept"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:on_frequency_dialog_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Frequency"));
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList3.add(SpecializedTeleporter.getFrequency(builder2));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "Tune Frequency"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
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
                ArrayList arrayList = new ArrayList();
                v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue6);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                Object object;
                Object object2;
                Object object3;
                PolyClassMachine_v4 polyClassMachine_v4;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                builder.val("entry", scriptValue4);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("entry"));
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue5);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("parts");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                builder5.val("world_name", object7);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("parts");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                builder5.val("x", object6);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("parts");
                if (scriptValue8 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                builder5.val("y", object5);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("parts");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                builder5.val("z", object4);
                if (!ScriptFormula.valuesEqual((ScriptValue)SpecializedTeleporter._signAliasAt(builder5), (ScriptValue)scriptValue2)) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                double d = scriptValue10 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassMachine_v4.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
                if (d < scriptContext.getNum("TELEPORT_COST")) {
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object8;
                        ScriptValue scriptValue12 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                        if (scriptValue11 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                            v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue12.asStr()));
                        } else {
                            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                            arrayList6.add(scriptValue12);
                            v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList6, (ScriptContext)scriptContext);
                        }
                    } else {
                        v5 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object3);
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"world", arrayList7, (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue14);
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue14);
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList9, (ScriptContext)scriptContext).asBool()) {
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                    if (scriptValue15 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object9;
                        String string = "<red>That destination's world is not currently loaded.";
                        if (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                            v7 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                        } else {
                            ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                            arrayList10.add(ScriptValue.of((String)string));
                            v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, arrayList10, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("target_world");
                if (scriptValue16 != ScriptValue.NULL) {
                    Object object10;
                    Object object11;
                    Object object12;
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                    if (scriptValue17 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                        arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0));
                        object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList13, (ScriptContext)scriptContext);
                    } else {
                        object12 = ScriptValue.NULL;
                    }
                    arrayList12.add((ScriptValue)object12);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList12, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))));
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                    if (scriptValue18 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                        arrayList15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0));
                        object11 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList15, (ScriptContext)scriptContext);
                    } else {
                        object11 = ScriptValue.NULL;
                    }
                    arrayList14.add((ScriptValue)object11);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList14, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5))));
                    ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
                    if (scriptValue19 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                        arrayList17.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0));
                        object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, arrayList17, (ScriptContext)scriptContext);
                    } else {
                        object10 = ScriptValue.NULL;
                    }
                    arrayList16.add((ScriptValue)object10);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList16, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue16, arrayList11, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue20 = object2;
                builder.val("loc", scriptValue20);
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                if (scriptValue21 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object13;
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue21 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object13);
                        object = ScriptValue.of((boolean)polyClassMachine_v42.tm$11_consume_energy(scriptValue22.asNum()));
                    } else {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(scriptValue22);
                        object = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue21, arrayList18, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                if (object.asBool()) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object14;
                        ScriptValue scriptValue24 = scriptValue20;
                        if (scriptValue23 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object14);
                            v13 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue24));
                        } else {
                            ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                            arrayList19.add(scriptValue24);
                            v13 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue23, arrayList19, (ScriptContext)scriptContext);
                        }
                    } else {
                        v13 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("Player");
                    if (scriptValue25 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object15;
                        ScriptValue scriptValue26 = ScriptValue.of((String)("<green>Teleported to <white>" + scriptValue2.asStr() + "<green>."));
                        if (scriptValue25 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object15);
                            v14 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue26.asStr()));
                        } else {
                            ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                            arrayList20.add(scriptValue26);
                            v14 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue25, arrayList20, (ScriptContext)scriptContext);
                        }
                    } else {
                        v14 = ScriptValue.NULL;
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue27 = ScriptValue.of((String)("<red>No other teleporter labeled '" + scriptValue2.asStr() + "' found on this frequency."));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v15 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue27.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue27);
                v15 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v15 = ScriptValue.NULL;
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "open_menu", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
