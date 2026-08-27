/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
        PolyClassMachine_v2 polyClassMachine_v2;
        PolyClassMachine_v2 polyClassMachine_v22;
        PolyClassMachine_v2 polyClassMachine_v23;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("World");
        return ScriptValue.of((String)((scriptValue4 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassWorld.tg$31_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + "," + (scriptValue != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v23.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v22.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v2.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()));
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string2, scriptValue3));
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue5));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
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
            arrayList3.add(scriptContext.getClassOrVar("x"));
            ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"int", arrayList3, (ScriptContext)scriptContext);
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("y"));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext);
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("z"));
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", SpecializedTeleporter.class, 1)), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("center", scriptValue5);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "north"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "south"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "east"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "west"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "up"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList6));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("dir", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("center");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("dir"), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("b");
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue11 : list2) {
                        builder.val("line", scriptValue11);
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
        PolyClassMachine_v2 polyClassMachine_v2;
        PolyClassMachine_v2 polyClassMachine_v22;
        PolyClassMachine_v2 polyClassMachine_v23;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("World");
        builder2.val("world_name", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassWorld.pg$30_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        builder2.val("x", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v23.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        builder2.val("y", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v22.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        builder2.val("z", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v2.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue3);
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("entry", scriptValue5);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue4) ^ true)) continue;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("entries"));
                arrayList3.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$74_update());
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, (ScriptContext)scriptContext);
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$74_update());
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(scriptContext.getClassOrVar("lines"));
        arrayList7.add(ScriptValue.of((String)("<gray>Cost per jump: <white>" + scriptContext.getStr("TELEPORT_COST") + " CE")));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("out"));
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 45.0));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:prev_icon"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow><- Previous Page"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:prev_page"));
            arrayList2.add(ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext));
            ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
            builder.val("out", scriptValue6);
        }
        if (ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0))).asNum() < scriptValue5.asNum()) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("out"));
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 53.0));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "internal:next_page_0"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<yellow>Next Page ->"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:next_page"));
            arrayList4.add(ScriptFormula.callBuiltin((String)"make_map", arrayList5, (ScriptContext)scriptContext));
            ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
            builder.val("out", scriptValue7);
        }
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("out"));
        ArrayList<Object> arrayList7 = new ArrayList<Object>();
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 5.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<aqua>Tune via Dialog"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>Type a frequency instead of"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "<gray>inserting a renamed item."));
        arrayList7.add(new ScriptValue.Array(arrayList8));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "specialized_teleporter.pf:open_frequency_dialog"));
        arrayList6.add(ScriptFormula.callBuiltin((String)"make_map", arrayList7, (ScriptContext)scriptContext));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
        builder.val("out", scriptValue8);
        double d = scriptValue4.asNum() * scriptContext.getNum("DEST_PAGE_SIZE");
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        builder.val("start", scriptValue9);
        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
        arrayList9.add(scriptContext.getClassOrVar("DEST_PAGE_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList9, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue10 : list) {
                ScriptValue scriptValue11;
                builder.val("i", scriptValue10);
                ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue12);
                double d2 = scriptValue12.asNum();
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                arrayList10.add(scriptValue2);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList10, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entries");
                ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("entry", scriptValue14);
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(scriptValue14);
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
                ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"split", arrayList11, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue15);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                builder5.val("world_name", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                builder5.val("x", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                builder5.val("y", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
                builder5.val("z", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue20 = SpecializedTeleporter._signAliasAt(builder5);
                builder.val("alias", scriptValue20);
                ScriptValue scriptValue21 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue20, (String)"") ^ true ? scriptValue20 : ((scriptValue11 = scriptContext.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("display_name", scriptValue21);
                ArrayList arrayList12 = new ArrayList();
                ScriptValue.Array array2 = new ScriptValue.Array(arrayList12);
                builder.val("lore", (ScriptValue)array2);
                ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                arrayList13.add(scriptContext.getClassOrVar("lore"));
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                arrayList13.add(ScriptValue.of((String)("<gray>" + (scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())));
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"push", arrayList13, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue25);
                ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                arrayList14.add(scriptContext.getClassOrVar("lore"));
                arrayList14.add(ScriptValue.of((String)("<dark_gray>Click to teleport <white>(" + scriptContext.getStr("TELEPORT_COST") + " CE)")));
                ScriptValue scriptValue26 = ScriptFormula.callBuiltin((String)"push", arrayList14, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue26);
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "slot"));
                ScriptValue scriptValue27 = scriptContext.getClassOrVar("DEST_SLOTS");
                arrayList16.add((ScriptValue)(scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, (ScriptValue)scriptContext.getClassOrVar("i"), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "icon"));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "cml:teleporter_core"));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "name"));
                arrayList16.add(ScriptValue.of((String)("<yellow>" + scriptValue21.asStr())));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "lore"));
                arrayList16.add(scriptValue26);
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, "action"));
                arrayList16.add(ScriptValue.of((String)("specialized_teleporter.pf:do_teleport_slot:" + scriptContext.getStr("i"))));
                arrayList15.add(ScriptFormula.callBuiltin((String)"make_map", arrayList16, (ScriptContext)scriptContext));
                ScriptValue scriptValue28 = ScriptFormula.callBuiltin((String)"push", arrayList15, (ScriptContext)scriptContext);
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
        block36: {
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
                    var11_11 = new PolyClassMachine_v2(var10_10);
                    v0 /* !! */  = var11_11.tm$34_get_typed(var7_7, var8_8);
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var6_6, (ScriptValue)ScriptValue.of((String)var7_7), (ScriptValue)ScriptValue.of((String)var8_8), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            v1 = ScriptValue.of((double)(v0 /* !! */ .asNum() * var1_1.getNum("DEST_PAGE_SIZE")));
            var12_12 = new ArrayList<ScriptValue>();
            var12_12.add(var1_1.getClassOrVar("idx"));
            var13_13 = ScriptFormula.addPolymorphic((ScriptValue)v1, (ScriptValue)ScriptFormula.callBuiltin((String)"int", var12_12, (ScriptContext)var1_1));
            var0.val("global_idx", var13_13);
            if (var13_13.asNum() < 0.0) ** GOTO lbl-1000
            v2 = var13_13.asNum();
            var14_14 = new ArrayList<ScriptValue>();
            var14_14.add(var5_5);
            if (!(v2 >= ScriptFormula.callBuiltin((String)"len", var14_14, (ScriptContext)var1_1).asNum())) {
                v3 = false;
            } else lbl-1000:
            // 2 sources

            {
                v3 = true;
            }
            if (v3) {
                return ScriptValue.NULL;
            }
            var15_15 = var1_1.getClassOrVar("Machine");
            v4 = var15_15 != ScriptValue.NULL ? ((var16_16 = PolyClassMachine_v2.ofGuarded((ScriptValue)var15_15)) != null ? var16_16.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var15_15, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
            if (v4 < var1_1.getNum("TELEPORT_COST")) {
                var17_17 = var1_1.getClassOrVar("Player");
                if (var17_17 != ScriptValue.NULL) {
                    var18_18 = ScriptValue.of((String)("<red>Not enough energy - need " + var1_1.getStr("TELEPORT_COST") + " CE."));
                    if (var17_17 instanceof ScriptValue.Obj && (var20_20 = (var19_19 = (ScriptValue.Obj)var17_17).instance()) != null && !(var20_20 instanceof PolyClass) && var19_19.typeName().equals("Player")) {
                        var21_21 = new PolyClassPlayer_v2(var20_20);
                        v5 /* !! */  = ScriptValue.of((boolean)var21_21.tm$42_send_message(var18_18.asStr()));
                    } else {
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var17_17, (ScriptValue)var18_18, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var22_22 = var1_1.getClassOrVar("entries");
            var23_23 = var22_22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var22_22, (ScriptValue)var13_13, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("entry", var23_23);
            var24_24 = new ArrayList<ScriptValue>();
            var24_24.add(var23_23);
            var24_24.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
            var25_25 = ScriptFormula.callBuiltin((String)"split", var24_24, (ScriptContext)var1_1);
            var0.val("parts", var25_25);
            var26_26 = new ArrayList<ScriptValue>();
            var27_27 = var1_1.getClassOrVar("parts");
            var26_26.add((ScriptValue)(var27_27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var27_27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
            var28_28 = ScriptFormula.callBuiltin((String)"world", var26_26, (ScriptContext)var1_1);
            var0.val("target_world", var28_28);
            var29_29 = new ArrayList<ScriptValue>();
            var29_29.add(var28_28);
            if (ScriptFormula.callBuiltin((String)"is_empty", var29_29, (ScriptContext)var1_1).asBool()) {
                var30_30 = var1_1.getClassOrVar("Player");
                if (var30_30 != ScriptValue.NULL) {
                    var31_31 = "<red>That destination's world is not currently loaded.";
                    if (var30_30 instanceof ScriptValue.Obj && (var33_33 = (var32_32 = (ScriptValue.Obj)var30_30).instance()) != null && !(var33_33 instanceof PolyClass) && var32_32.typeName().equals("Player")) {
                        var34_34 = new PolyClassPlayer_v2(var33_33);
                        v6 /* !! */  = ScriptValue.of((boolean)var34_34.tm$42_send_message(var31_31));
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var30_30, (ScriptValue)ScriptValue.of((String)var31_31), (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var35_35 = var1_1.getClassOrVar("target_world");
            if (var35_35 != ScriptValue.NULL) {
                var36_36 = new ArrayList<ScriptValue>();
                var37_37 = var1_1.getClassOrVar("parts");
                var36_36.add((ScriptValue)(var37_37 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var37_37, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
                v7 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var36_36, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5)));
                var38_38 = new ArrayList<ScriptValue>();
                var39_39 = var1_1.getClassOrVar("parts");
                var38_38.add((ScriptValue)(var39_39 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var39_39, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
                v8 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var38_38, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5)));
                var40_40 = new ArrayList<ScriptValue>();
                var41_41 = var1_1.getClassOrVar("parts");
                var40_40.add((ScriptValue)(var41_41 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var41_41, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
                v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var35_35, (ScriptValue)v7, (ScriptValue)v8, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var40_40, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))), (ScriptContext)var1_1);
            } else {
                v9 /* !! */  = ScriptValue.NULL;
            }
            var42_42 = v9 /* !! */ ;
            var0.val("loc", var42_42);
            var43_43 = var1_1.getClassOrVar("Machine");
            if (var43_43 != ScriptValue.NULL) {
                var44_44 = var1_1.getClassOrVar("TELEPORT_COST");
                if (var43_43 instanceof ScriptValue.Obj && (var46_46 = (var45_45 = (ScriptValue.Obj)var43_43).instance()) != null && !(var46_46 instanceof PolyClass) && var45_45.typeName().equals("Machine")) {
                    var47_47 = new PolyClassMachine_v2(var46_46);
                    v10 /* !! */  = ScriptValue.of((boolean)var47_47.tm$11_consume_energy(var44_44.asNum()));
                } else {
                    v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)var43_43, (ScriptValue)var44_44, (ScriptContext)var1_1);
                }
            } else {
                v10 /* !! */  = ScriptValue.NULL;
            }
            if (!v10 /* !! */ .asBool()) break block36;
            var48_48 = var1_1.getClassOrVar("Machine");
            if (var48_48 != ScriptValue.NULL) {
                if (var48_48 instanceof ScriptValue.Obj && (var50_50 = (var49_49 = (ScriptValue.Obj)var48_48).instance()) != null && !(var50_50 instanceof PolyClass) && var49_49.typeName().equals("Machine")) {
                    var51_51 = new PolyClassMachine_v2(var50_50);
                    v11 /* !! */  = ScriptValue.of((boolean)var51_51.tm$92_close());
                } else {
                    v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)var48_48, (ScriptContext)var1_1);
                }
            } else {
                v11 /* !! */  = ScriptValue.NULL;
            }
            var52_52 = var1_1.getClassOrVar("Player");
            if (var52_52 != ScriptValue.NULL) {
                var53_53 = var42_42;
                if (var52_52 instanceof ScriptValue.Obj && (var55_55 = (var54_54 = (ScriptValue.Obj)var52_52).instance()) != null && !(var55_55 instanceof PolyClass) && var54_54.typeName().equals("Player")) {
                    var56_56 = new PolyClassPlayer_v2(var55_55);
                    v12 /* !! */  = ScriptValue.of((boolean)var56_56.tm$24_teleport_to(var53_53));
                } else {
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var52_52, (ScriptValue)var53_53, (ScriptContext)var1_1);
                }
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var57_57 = var1_1.getClassOrVar("Player");
            if (var57_57 != ScriptValue.NULL) {
                var58_59 = ScriptValue.of((String)("<green>Teleported to " + ((var59_58 = var1_1.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var59_58, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + "."));
                if (var57_57 instanceof ScriptValue.Obj && (var61_61 = (var60_60 = (ScriptValue.Obj)var57_57).instance()) != null && !(var61_61 instanceof PolyClass) && var60_60.typeName().equals("Player")) {
                    var62_62 = new PolyClassPlayer_v2(var61_61);
                    v13 /* !! */  = ScriptValue.of((boolean)var62_62.tm$42_send_message(var58_59.asStr()));
                } else {
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var57_57, (ScriptValue)var58_59, (ScriptContext)var1_1);
                }
            } else {
                v13 /* !! */  = ScriptValue.NULL;
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$74_update());
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                    object = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue6));
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                    object = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue4));
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
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                Object object;
                Object object2;
                PolyClassMachine_v2 polyClassMachine_v2;
                builder.val("entry", scriptValue4);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("entry"));
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", SpecializedTeleporter.class, ","));
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue5);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("parts");
                builder5.val("world_name", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("parts");
                builder5.val("x", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("parts");
                builder5.val("y", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("parts");
                builder5.val("z", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                if (!ScriptFormula.valuesEqual((ScriptValue)SpecializedTeleporter._signAliasAt(builder5), (ScriptValue)scriptValue2)) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                double d = scriptValue10 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassMachine_v2.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
                if (d < scriptContext.getNum("TELEPORT_COST")) {
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object3;
                        ScriptValue scriptValue12 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                        if (scriptValue11 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object3);
                            v1 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue12.asStr()));
                        } else {
                            v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                        }
                    } else {
                        v1 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                arrayList2.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"world", arrayList2, (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue14);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue14);
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool()) {
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                    if (scriptValue15 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object4;
                        String string = "<red>That destination's world is not currently loaded.";
                        if (scriptValue15 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object4);
                            v2 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
                        } else {
                            v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v2 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("target_world");
                if (scriptValue16 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                    arrayList4.add((ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5)));
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
                    arrayList5.add((ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 1.5)));
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("parts");
                    arrayList6.add((ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue16, (ScriptValue)scriptValue18, (ScriptValue)scriptValue20, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList6, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpecializedTeleporter.class, 0.5))), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue22 = object2;
                builder.val("loc", scriptValue22);
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                if (scriptValue23 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue23 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object5);
                        object = ScriptValue.of((boolean)polyClassMachine_v22.tm$11_consume_energy(scriptValue24.asNum()));
                    } else {
                        object = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                if (object.asBool()) {
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("Player");
                    if (scriptValue25 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object6;
                        ScriptValue scriptValue26 = scriptValue22;
                        if (scriptValue25 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object6);
                            v7 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$24_teleport_to(scriptValue26));
                        } else {
                            v7 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("Player");
                    if (scriptValue27 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object7;
                        ScriptValue scriptValue28 = ScriptValue.of((String)("<green>Teleported to <white>" + scriptValue2.asStr() + "<green>."));
                        if (scriptValue27 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object7);
                            v8 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue28.asStr()));
                        } else {
                            v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue27, (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                        }
                    } else {
                        v8 = ScriptValue.NULL;
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue29 = ScriptValue.of((String)("<red>No other teleporter labeled '" + scriptValue2.asStr() + "' found on this frequency."));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v9 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue29.asStr()));
            } else {
                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$24_open_menu(scriptValue2));
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
