/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer_v2;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"teleporter_freq_"), (ScriptValue)scriptContext.getClassOrVar("freq"));
    }

    public static ScriptValue myPosStr(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue.Obj obj3;
        Object object3;
        ScriptValue.Obj obj4;
        Object object4;
        ScriptContext scriptContext;
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        scriptContext = builder.peek();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("World");
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(scriptValue4 != ScriptValue.NULL ? (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("World") ? new PolyClassWorld(object4).pg$28_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
    }

    public static ScriptValue registerSelf(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("freq"), (String)"")) {
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
                PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object2);
                object = polyClassServer_v2.tm$6_get_typed(scriptValue3.asStr(), string);
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
        arrayList.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue5)) continue;
                return ScriptValue.NULL;
            }
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = scriptValue;
                String string = "string";
                ScriptValue scriptValue8 = scriptValue5;
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object3);
                    v1 = ScriptValue.of((boolean)polyClassServer_v2.tm$0_set_typed(scriptValue7.asStr(), string, scriptValue8));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue7);
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(scriptValue8);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue10 = scriptValue;
                String string = "string";
                ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)scriptValue5);
                if (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object4);
                    v2 = ScriptValue.of((boolean)polyClassServer_v2.tm$0_set_typed(scriptValue10.asStr(), string, scriptValue11));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue10);
                    arrayList3.add(ScriptValue.of((String)string));
                    arrayList3.add(scriptValue11);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
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
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("freq"), (String)"")) {
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
                PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object2);
                object = polyClassServer_v2.tm$6_get_typed(scriptValue4.asStr(), string);
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
        ScriptValue scriptValue7 = ScriptValue.of((String)"");
        builder.val("result", scriptValue7);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        arrayList.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("entry"), (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue6) ^ true)) continue;
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("result"), (String)"")) {
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("entry");
                    builder.val("result", scriptValue8);
                    continue;
                }
                ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("result"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)scriptContext.getClassOrVar("entry"));
                builder.val("result", scriptValue9);
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue10 = scriptValue2;
            String string = "string";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("result");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object3);
                v1 = ScriptValue.of((boolean)polyClassServer_v2.tm$0_set_typed(scriptValue10.asStr(), string, scriptValue11));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue10);
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(scriptValue11);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue3));
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
            ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
            if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5));
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
            return ScriptValue.of((String)"");
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
            arrayList3.add(ScriptValue.of((boolean)true));
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("center", scriptValue3);
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(ScriptValue.of((String)"north"));
        arrayList7.add(ScriptValue.of((String)"south"));
        arrayList7.add(ScriptValue.of((String)"east"));
        arrayList7.add(ScriptValue.of((String)"west"));
        arrayList7.add(ScriptValue.of((String)"up"));
        arrayList7.add(ScriptValue.of((String)"down"));
        List list = ScriptProgram.rowsOf((ScriptValue)new ScriptValue.Array(arrayList7), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object2;
                builder.val("dir", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("center");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptContext.getClassOrVar("dir"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue4, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue5 = object2;
                builder.val("b", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("b");
                List list2 = ScriptProgram.rowsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext), (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray2 : list2) {
                        builder.val("line", scriptValueArray2.length > 0 ? scriptValueArray2[0] : ScriptValue.NULL);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("line"), (String)"") ^ true)) continue;
                        return scriptContext.getClassOrVar("line");
                    }
                }
                return ScriptValue.of((String)"");
            }
        }
        return ScriptValue.of((String)"");
    }

    public static ScriptValue _ownAlias(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue.Obj obj3;
        Object object3;
        ScriptValue.Obj obj4;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("World");
        builder2.val("world_name", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("World") ? new PolyClassWorld(object4).pg$28_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        builder2.val("x", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        builder2.val("y", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        builder2.val("z", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        return SpecializedTeleporter._signAliasAt(builder2);
    }

    public static ScriptValue frequencyEntries(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("entries", (ScriptValue)array);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("freq"), (String)"")) {
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
                PolyClassServer_v2 polyClassServer_v2 = new PolyClassServer_v2(object2);
                object = polyClassServer_v2.tm$6_get_typed(scriptValue2.asStr(), string);
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
        arrayList3.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList3, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("entry"), (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue4) ^ true)) continue;
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("entries"));
                arrayList4.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue5);
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
            return ScriptValue.of((double)1.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("DEST_PAGE_SIZE");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)1.0));
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)"cml:teleporter_core");
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("click_type"), (String)"right") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("click_type"), (String)"shift_right")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", ScriptValue.of((String)""));
            SpecializedTeleporter.setFrequency(builder2);
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$74_update());
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$74_update());
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
            return ScriptValue.of((String)"<red>No frequency tuned");
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = SpecializedTeleporter._ownAlias(builder3);
        builder.val("alias", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>Frequency: <white>"), (ScriptValue)scriptValue), (ScriptValue)ScriptValue.of((String)" <gray>(")), (ScriptValue)scriptValue2), (ScriptValue)ScriptValue.of((String)")"));
        }
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>Frequency: <white>"), (ScriptValue)scriptValue);
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
            arrayList2.add(ScriptValue.of((String)"<gray>Insert a renamed item below to tune one."));
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
            arrayList4.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>"), (ScriptValue)scriptValue3), (ScriptValue)ScriptValue.of((String)" destination(s) available.")));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue4);
        } else {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("lines"));
            arrayList5.add(ScriptValue.of((String)"<red>No other teleporter tuned to this frequency yet."));
            ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue5);
        }
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("lines"));
        ScriptValue scriptValue6 = ScriptValue.of((String)"<gray>Page <white>");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "page_offset";
            String string2 = "int";
            if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((String)string));
                arrayList7.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList7, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)ScriptValue.of((double)1.0))), (ScriptValue)ScriptValue.of((String)"<gray>/<white>"));
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("freq", scriptValue);
        arrayList6.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)SpecializedTeleporter.totalPages(builder4)));
        ScriptValue scriptValue9 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
        builder.val("lines", scriptValue9);
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(scriptContext.getClassOrVar("lines"));
        arrayList8.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>Cost per jump: <white>"), (ScriptValue)scriptContext.getClassOrVar("TELEPORT_COST")), (ScriptValue)ScriptValue.of((String)" CE")));
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin((String)"push", arrayList8, (ScriptContext)scriptContext);
        builder.val("lines", scriptValue10);
        return scriptValue10;
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
            arrayList4.add(ScriptValue.of((String)"slot"));
            arrayList4.add(ScriptValue.of((double)45.0));
            arrayList4.add(ScriptValue.of((String)"icon"));
            arrayList4.add(ScriptValue.of((String)"cml:prev_icon"));
            arrayList4.add(ScriptValue.of((String)"name"));
            arrayList4.add(ScriptValue.of((String)"<yellow><- Previous Page"));
            arrayList4.add(ScriptValue.of((String)"action"));
            arrayList4.add(ScriptValue.of((String)"specialized_teleporter.pf:prev_page"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"make_map", arrayList4, (ScriptContext)scriptContext));
            ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
            builder.val("out", scriptValue6);
        }
        if (ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0)).asNum() < scriptValue5.asNum()) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("out"));
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(ScriptValue.of((String)"slot"));
            arrayList6.add(ScriptValue.of((double)53.0));
            arrayList6.add(ScriptValue.of((String)"icon"));
            arrayList6.add(ScriptValue.of((String)"internal:next_page_0"));
            arrayList6.add(ScriptValue.of((String)"name"));
            arrayList6.add(ScriptValue.of((String)"<yellow>Next Page ->"));
            arrayList6.add(ScriptValue.of((String)"action"));
            arrayList6.add(ScriptValue.of((String)"specialized_teleporter.pf:next_page"));
            arrayList5.add(ScriptFormula.callBuiltin((String)"make_map", arrayList6, (ScriptContext)scriptContext));
            ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
            builder.val("out", scriptValue7);
        }
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(scriptContext.getClassOrVar("out"));
        ArrayList<Object> arrayList8 = new ArrayList<Object>();
        arrayList8.add(ScriptValue.of((String)"slot"));
        arrayList8.add(ScriptValue.of((double)5.0));
        arrayList8.add(ScriptValue.of((String)"icon"));
        arrayList8.add(ScriptValue.of((String)"cml:teleporter_core"));
        arrayList8.add(ScriptValue.of((String)"name"));
        arrayList8.add(ScriptValue.of((String)"<aqua>Tune via Dialog"));
        arrayList8.add(ScriptValue.of((String)"lore"));
        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
        arrayList9.add(ScriptValue.of((String)"<gray>Type a frequency instead of"));
        arrayList9.add(ScriptValue.of((String)"<gray>inserting a renamed item."));
        arrayList8.add(new ScriptValue.Array(arrayList9));
        arrayList8.add(ScriptValue.of((String)"action"));
        arrayList8.add(ScriptValue.of((String)"specialized_teleporter.pf:open_frequency_dialog"));
        arrayList7.add(ScriptFormula.callBuiltin((String)"make_map", arrayList8, (ScriptContext)scriptContext));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
        builder.val("out", scriptValue8);
        double d = scriptValue4.asNum() * scriptContext.getNum("DEST_PAGE_SIZE");
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        builder.val("start", scriptValue9);
        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
        arrayList10.add(scriptContext.getClassOrVar("DEST_PAGE_SIZE"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList10, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
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
                builder.val("i", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue10);
                double d2 = scriptValue10.asNum();
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(scriptValue2);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList11, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entries");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add(scriptValue10);
                    object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object12 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object12;
                builder.val("entry", scriptValue12);
                ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                arrayList13.add(scriptValue12);
                arrayList13.add(ScriptValue.of((String)","));
                ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"split", arrayList13, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue13);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("parts");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(ScriptValue.of((double)0.0));
                    object11 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object11 = ScriptValue.NULL;
                }
                builder5.val("world_name", object11);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                    arrayList15.add(ScriptValue.of((double)1.0));
                    object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList15, (ScriptContext)scriptContext);
                } else {
                    object10 = ScriptValue.NULL;
                }
                builder5.val("x", object10);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                if (scriptValue16 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                    arrayList16.add(ScriptValue.of((double)2.0));
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, arrayList16, (ScriptContext)scriptContext);
                } else {
                    object9 = ScriptValue.NULL;
                }
                builder5.val("y", object9);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                if (scriptValue17 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                    arrayList17.add(ScriptValue.of((double)3.0));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList17, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                builder5.val("z", object8);
                ScriptValue scriptValue18 = SpecializedTeleporter._signAliasAt(builder5);
                builder.val("alias", scriptValue18);
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue18, (String)"") ^ true) {
                    object7 = scriptValue18;
                } else {
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
                    if (scriptValue19 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(ScriptValue.of((double)0.0));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, arrayList18, (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                }
                ScriptValue scriptValue20 = object7;
                builder.val("display_name", scriptValue20);
                ArrayList arrayList19 = new ArrayList();
                ScriptValue.Array array2 = new ScriptValue.Array(arrayList19);
                builder.val("lore", (ScriptValue)array2);
                ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                arrayList20.add(scriptContext.getClassOrVar("lore"));
                ScriptValue scriptValue21 = ScriptValue.of((String)"<gray>");
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                if (scriptValue22 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
                    arrayList21.add(ScriptValue.of((double)1.0));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, arrayList21, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue23 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue21, (ScriptValue)object6), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                if (scriptValue24 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList22 = new ArrayList<ScriptValue>();
                    arrayList22.add(ScriptValue.of((double)2.0));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, arrayList22, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue25 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue23, (ScriptValue)object5), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue26 = scriptContext.getClassOrVar("parts");
                if (scriptValue26 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList23 = new ArrayList<ScriptValue>();
                    arrayList23.add(ScriptValue.of((double)3.0));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, arrayList23, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList20.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue25, (ScriptValue)object4));
                ScriptValue scriptValue27 = ScriptFormula.callBuiltin((String)"push", arrayList20, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue27);
                ArrayList<ScriptValue> arrayList24 = new ArrayList<ScriptValue>();
                arrayList24.add(scriptContext.getClassOrVar("lore"));
                arrayList24.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<dark_gray>Click to teleport <white>("), (ScriptValue)scriptContext.getClassOrVar("TELEPORT_COST")), (ScriptValue)ScriptValue.of((String)" CE)")));
                ScriptValue scriptValue28 = ScriptFormula.callBuiltin((String)"push", arrayList24, (ScriptContext)scriptContext);
                builder.val("lore", scriptValue28);
                ArrayList<ScriptValue> arrayList25 = new ArrayList<ScriptValue>();
                arrayList25.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList26 = new ArrayList<ScriptValue>();
                arrayList26.add(ScriptValue.of((String)"slot"));
                ScriptValue scriptValue29 = scriptContext.getClassOrVar("DEST_SLOTS");
                if (scriptValue29 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList27 = new ArrayList<ScriptValue>();
                    arrayList27.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, arrayList27, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList26.add((ScriptValue)object3);
                arrayList26.add(ScriptValue.of((String)"icon"));
                arrayList26.add(ScriptValue.of((String)"cml:teleporter_core"));
                arrayList26.add(ScriptValue.of((String)"name"));
                arrayList26.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>"), (ScriptValue)scriptValue20));
                arrayList26.add(ScriptValue.of((String)"lore"));
                arrayList26.add(scriptValue28);
                arrayList26.add(ScriptValue.of((String)"action"));
                arrayList26.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"specialized_teleporter.pf:do_teleport_slot:"), (ScriptValue)scriptContext.getClassOrVar("i")));
                arrayList25.add(ScriptFormula.callBuiltin((String)"make_map", arrayList26, (ScriptContext)scriptContext));
                ScriptValue scriptValue30 = ScriptFormula.callBuiltin((String)"push", arrayList25, (ScriptContext)scriptContext);
                builder.val("out", scriptValue30);
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
                    var11_11 = new PolyClassMachine_v3(var10_10);
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
            v4 /* !! */  = var16_16 != ScriptValue.NULL ? (var16_16 instanceof ScriptValue.Obj && (var18_18 = (var17_17 = (ScriptValue.Obj)var16_16).instance()) != null && !(var18_18 instanceof PolyClass) && var17_17.typeName().equals("Machine") ? new PolyClassMachine_v3(var18_18).pg$122_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var16_16, (ScriptContext)var1_1)) : ScriptValue.NULL;
            if (v4 /* !! */ .asNum() < var1_1.getNum("TELEPORT_COST")) {
                var19_19 = var1_1.getClassOrVar("Player");
                if (var19_19 != ScriptValue.NULL) {
                    var20_20 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>Not enough energy - need "), (ScriptValue)var1_1.getClassOrVar("TELEPORT_COST")), (ScriptValue)ScriptValue.of((String)" CE."));
                    if (var19_19 instanceof ScriptValue.Obj && (var22_22 = (var21_21 = (ScriptValue.Obj)var19_19).instance()) != null && !(var22_22 instanceof PolyClass) && var21_21.typeName().equals("Player")) {
                        var23_23 = new PolyClassPlayer(var22_22);
                        v5 /* !! */  = ScriptValue.of((boolean)var23_23.tm$42_send_message(var20_20.asStr()));
                    } else {
                        var24_24 = new ArrayList<ScriptValue>();
                        var24_24.add(var20_20);
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var19_19, var24_24, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var25_25 = var1_1.getClassOrVar("entries");
            if (var25_25 != ScriptValue.NULL) {
                var26_26 = new ArrayList<ScriptValue>();
                var26_26.add(var14_14);
                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var25_25, var26_26, (ScriptContext)var1_1);
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            var27_27 = v6 /* !! */ ;
            var0.val("entry", var27_27);
            var28_28 = new ArrayList<ScriptValue>();
            var28_28.add(var27_27);
            var28_28.add(ScriptValue.of((String)","));
            var29_29 = ScriptFormula.callBuiltin((String)"split", var28_28, (ScriptContext)var1_1);
            var0.val("parts", var29_29);
            var30_30 = new ArrayList<ScriptValue>();
            var31_31 = var1_1.getClassOrVar("parts");
            if (var31_31 != ScriptValue.NULL) {
                var32_32 = new ArrayList<ScriptValue>();
                var32_32.add(ScriptValue.of((double)0.0));
                v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var31_31, var32_32, (ScriptContext)var1_1);
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            var30_30.add(v7 /* !! */ );
            var33_33 = ScriptFormula.callBuiltin((String)"world", var30_30, (ScriptContext)var1_1);
            var0.val("target_world", var33_33);
            var34_34 = new ArrayList<ScriptValue>();
            var34_34.add(var33_33);
            if (ScriptFormula.callBuiltin((String)"is_empty", var34_34, (ScriptContext)var1_1).asBool()) {
                var35_35 = var1_1.getClassOrVar("Player");
                if (var35_35 != ScriptValue.NULL) {
                    var36_36 = "<red>That destination's world is not currently loaded.";
                    if (var35_35 instanceof ScriptValue.Obj && (var38_38 = (var37_37 = (ScriptValue.Obj)var35_35).instance()) != null && !(var38_38 instanceof PolyClass) && var37_37.typeName().equals("Player")) {
                        var39_39 = new PolyClassPlayer(var38_38);
                        v8 /* !! */  = ScriptValue.of((boolean)var39_39.tm$42_send_message(var36_36));
                    } else {
                        var40_40 = new ArrayList<ScriptValue>();
                        var40_40.add(ScriptValue.of((String)var36_36));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var35_35, var40_40, (ScriptContext)var1_1);
                    }
                } else {
                    v8 /* !! */  = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            var41_41 = var1_1.getClassOrVar("target_world");
            if (var41_41 != ScriptValue.NULL) {
                var42_42 = new ArrayList<ScriptValue>();
                var43_43 = new ArrayList<ScriptValue>();
                var44_44 = var1_1.getClassOrVar("parts");
                if (var44_44 != ScriptValue.NULL) {
                    var45_45 = new ArrayList<ScriptValue>();
                    var45_45.add(ScriptValue.of((double)1.0));
                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var44_44, var45_45, (ScriptContext)var1_1);
                } else {
                    v9 /* !! */  = ScriptValue.NULL;
                }
                var43_43.add(v9 /* !! */ );
                var42_42.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var43_43, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
                var46_46 = new ArrayList<ScriptValue>();
                var47_47 = var1_1.getClassOrVar("parts");
                if (var47_47 != ScriptValue.NULL) {
                    var48_48 = new ArrayList<ScriptValue>();
                    var48_48.add(ScriptValue.of((double)2.0));
                    v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var47_47, var48_48, (ScriptContext)var1_1);
                } else {
                    v10 /* !! */  = ScriptValue.NULL;
                }
                var46_46.add(v10 /* !! */ );
                var42_42.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var46_46, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)1.5)));
                var49_49 = new ArrayList<ScriptValue>();
                var50_50 = var1_1.getClassOrVar("parts");
                if (var50_50 != ScriptValue.NULL) {
                    var51_51 = new ArrayList<ScriptValue>();
                    var51_51.add(ScriptValue.of((double)3.0));
                    v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var50_50, var51_51, (ScriptContext)var1_1);
                } else {
                    v11 /* !! */  = ScriptValue.NULL;
                }
                var49_49.add(v11 /* !! */ );
                var42_42.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var49_49, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var41_41, var42_42, (ScriptContext)var1_1);
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var52_52 = v12 /* !! */ ;
            var0.val("loc", var52_52);
            var53_53 = var1_1.getClassOrVar("Machine");
            if (var53_53 != ScriptValue.NULL) {
                var54_54 = var1_1.getClassOrVar("TELEPORT_COST");
                if (var53_53 instanceof ScriptValue.Obj && (var56_56 = (var55_55 = (ScriptValue.Obj)var53_53).instance()) != null && !(var56_56 instanceof PolyClass) && var55_55.typeName().equals("Machine")) {
                    var57_57 = new PolyClassMachine_v3(var56_56);
                    v13 /* !! */  = ScriptValue.of((boolean)var57_57.tm$11_consume_energy(var54_54.asNum()));
                } else {
                    var58_58 = new ArrayList<ScriptValue>();
                    var58_58.add(var54_54);
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)var53_53, var58_58, (ScriptContext)var1_1);
                }
            } else {
                v13 /* !! */  = ScriptValue.NULL;
            }
            if (!v13 /* !! */ .asBool()) break block48;
            var59_59 = var1_1.getClassOrVar("Machine");
            if (var59_59 != ScriptValue.NULL) {
                if (var59_59 instanceof ScriptValue.Obj && (var61_61 = (var60_60 = (ScriptValue.Obj)var59_59).instance()) != null && !(var61_61 instanceof PolyClass) && var60_60.typeName().equals("Machine")) {
                    var62_62 = new PolyClassMachine_v3(var61_61);
                    v14 /* !! */  = ScriptValue.of((boolean)var62_62.tm$92_close());
                } else {
                    var63_63 = new ArrayList<E>();
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)var59_59, var63_63, (ScriptContext)var1_1);
                }
            } else {
                v14 /* !! */  = ScriptValue.NULL;
            }
            var64_64 = var1_1.getClassOrVar("Player");
            if (var64_64 != ScriptValue.NULL) {
                var65_65 = var52_52;
                if (var64_64 instanceof ScriptValue.Obj && (var67_67 = (var66_66 = (ScriptValue.Obj)var64_64).instance()) != null && !(var67_67 instanceof PolyClass) && var66_66.typeName().equals("Player")) {
                    var68_68 = new PolyClassPlayer(var67_67);
                    v15 /* !! */  = ScriptValue.of((boolean)var68_68.tm$24_teleport_to(var65_65));
                } else {
                    var69_69 = new ArrayList<ScriptValue>();
                    var69_69.add(var65_65);
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var64_64, var69_69, (ScriptContext)var1_1);
                }
            } else {
                v15 /* !! */  = ScriptValue.NULL;
            }
            var70_70 = var1_1.getClassOrVar("Player");
            if (var70_70 != ScriptValue.NULL) {
                v16 = ScriptValue.of((String)"<green>Teleported to ");
                var72_71 = var1_1.getClassOrVar("parts");
                if (var72_71 != ScriptValue.NULL) {
                    var73_72 = new ArrayList<ScriptValue>();
                    var73_72.add(ScriptValue.of((double)0.0));
                    v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var72_71, var73_72, (ScriptContext)var1_1);
                } else {
                    v17 /* !! */  = ScriptValue.NULL;
                }
                var71_73 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)v16, (ScriptValue)v17 /* !! */ ), (ScriptValue)ScriptValue.of((String)"."));
                if (var70_70 instanceof ScriptValue.Obj && (var75_75 = (var74_74 = (ScriptValue.Obj)var70_70).instance()) != null && !(var75_75 instanceof PolyClass) && var74_74.typeName().equals("Player")) {
                    var76_76 = new PolyClassPlayer(var75_75);
                    v18 /* !! */  = ScriptValue.of((boolean)var76_76.tm$42_send_message(var71_73.asStr()));
                } else {
                    var77_77 = new ArrayList<ScriptValue>();
                    var77_77.add(var71_73);
                    v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var70_70, var77_77, (ScriptContext)var1_1);
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
        arrayList2.add(ScriptValue.of((String)"Accept"));
        arrayList2.add(ScriptValue.of((String)"specialized_teleporter.pf:on_frequency_dialog_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"value"));
        arrayList3.add(ScriptValue.of((String)"Frequency"));
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList3.add(SpecializedTeleporter.getFrequency(builder2));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((String)"Tune Frequency"));
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$74_update());
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
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                    object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
            if (!(ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0)).asNum() < scriptValue2.asNum())) break block8;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "page_offset";
                String string3 = "int";
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0));
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue6));
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
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                    object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue4));
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
        List list = ScriptProgram.rowsOf((ScriptValue)SpecializedTeleporter.frequencyEntries(builder4), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object;
                Object object2;
                Object object3;
                ScriptValue.Obj obj;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                Object object8;
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("entry"));
                arrayList.add(ScriptValue.of((String)","));
                ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue4);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("parts");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((double)0.0));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                builder5.val("world_name", object8);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("parts");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((double)1.0));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                builder5.val("x", object7);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("parts");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(ScriptValue.of((double)2.0));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                builder5.val("y", object6);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("parts");
                if (scriptValue8 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((double)3.0));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                builder5.val("z", object5);
                if (!ScriptFormula.valuesEqual((ScriptValue)SpecializedTeleporter._signAliasAt(builder5), (ScriptValue)scriptValue2)) continue;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                Object object9 = scriptValue9 != ScriptValue.NULL ? (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object4).pg$122_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (object9.asNum() < scriptContext.getNum("TELEPORT_COST")) {
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
                    if (scriptValue10 != ScriptValue.NULL) {
                        ScriptValue.Obj obj2;
                        Object object10;
                        ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>Not enough energy - need "), (ScriptValue)scriptContext.getClassOrVar("TELEPORT_COST")), (ScriptValue)ScriptValue.of((String)" CE."));
                        if (scriptValue10 instanceof ScriptValue.Obj && (object10 = (obj2 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object10 instanceof PolyClass) && obj2.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                            v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                        } else {
                            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                            arrayList6.add(scriptValue11);
                            v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
                        }
                    } else {
                        v5 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((double)0.0));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object3);
                ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"world", arrayList7, (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue13);
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue13);
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList9, (ScriptContext)scriptContext).asBool()) {
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
                    if (scriptValue14 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object11;
                        String string = "<red>That destination's world is not currently loaded.";
                        if (scriptValue14 instanceof ScriptValue.Obj && (object11 = (obj3 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object11 instanceof PolyClass) && obj3.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object11);
                            v7 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                        } else {
                            ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                            arrayList10.add(ScriptValue.of((String)string));
                            v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList10, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("target_world");
                if (scriptValue15 != ScriptValue.NULL) {
                    Object object12;
                    Object object13;
                    Object object14;
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                    if (scriptValue16 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                        arrayList13.add(ScriptValue.of((double)1.0));
                        object14 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, arrayList13, (ScriptContext)scriptContext);
                    } else {
                        object14 = ScriptValue.NULL;
                    }
                    arrayList12.add((ScriptValue)object14);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList12, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)0.5)));
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                    if (scriptValue17 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                        arrayList15.add(ScriptValue.of((double)2.0));
                        object13 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList15, (ScriptContext)scriptContext);
                    } else {
                        object13 = ScriptValue.NULL;
                    }
                    arrayList14.add((ScriptValue)object13);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList14, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)1.5)));
                    ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                    if (scriptValue18 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                        arrayList17.add(ScriptValue.of((double)3.0));
                        object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList17, (ScriptContext)scriptContext);
                    } else {
                        object12 = ScriptValue.NULL;
                    }
                    arrayList16.add((ScriptValue)object12);
                    arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList16, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)0.5)));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue15, arrayList11, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object2;
                builder.val("loc", scriptValue19);
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                if (scriptValue20 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object15;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue20 instanceof ScriptValue.Obj && (object15 = (obj4 = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object15 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object15);
                        object = ScriptValue.of((boolean)polyClassMachine_v3.tm$11_consume_energy(scriptValue21.asNum()));
                    } else {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(scriptValue21);
                        object = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue20, arrayList18, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                if (object.asBool()) {
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Player");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue.Obj obj5;
                        Object object16;
                        ScriptValue scriptValue23 = scriptValue19;
                        if (scriptValue22 instanceof ScriptValue.Obj && (object16 = (obj5 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object16 instanceof PolyClass) && obj5.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object16);
                            v13 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue23));
                        } else {
                            ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                            arrayList19.add(scriptValue23);
                            v13 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue22, arrayList19, (ScriptContext)scriptContext);
                        }
                    } else {
                        v13 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
                    if (scriptValue24 != ScriptValue.NULL) {
                        ScriptValue.Obj obj6;
                        Object object17;
                        ScriptValue scriptValue25 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>Teleported to <white>"), (ScriptValue)scriptValue2), (ScriptValue)ScriptValue.of((String)"<green>."));
                        if (scriptValue24 instanceof ScriptValue.Obj && (object17 = (obj6 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object17 instanceof PolyClass) && obj6.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object17);
                            v14 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue25.asStr()));
                        } else {
                            ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                            arrayList20.add(scriptValue25);
                            v14 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, arrayList20, (ScriptContext)scriptContext);
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
            ScriptValue scriptValue26 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>No other teleporter labeled '"), (ScriptValue)scriptValue2), (ScriptValue)ScriptValue.of((String)"' found on this frequency."));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v15 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue26.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue26);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$24_open_menu(scriptValue2));
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
        arrayList.add(ScriptValue.of((double)10.0));
        arrayList.add(ScriptValue.of((double)11.0));
        arrayList.add(ScriptValue.of((double)12.0));
        arrayList.add(ScriptValue.of((double)13.0));
        arrayList.add(ScriptValue.of((double)14.0));
        arrayList.add(ScriptValue.of((double)15.0));
        arrayList.add(ScriptValue.of((double)16.0));
        arrayList.add(ScriptValue.of((double)19.0));
        arrayList.add(ScriptValue.of((double)20.0));
        arrayList.add(ScriptValue.of((double)21.0));
        arrayList.add(ScriptValue.of((double)22.0));
        arrayList.add(ScriptValue.of((double)23.0));
        arrayList.add(ScriptValue.of((double)24.0));
        arrayList.add(ScriptValue.of((double)25.0));
        arrayList.add(ScriptValue.of((double)28.0));
        arrayList.add(ScriptValue.of((double)29.0));
        arrayList.add(ScriptValue.of((double)30.0));
        arrayList.add(ScriptValue.of((double)31.0));
        arrayList.add(ScriptValue.of((double)32.0));
        arrayList.add(ScriptValue.of((double)33.0));
        arrayList.add(ScriptValue.of((double)34.0));
        arrayList.add(ScriptValue.of((double)37.0));
        arrayList.add(ScriptValue.of((double)38.0));
        arrayList.add(ScriptValue.of((double)39.0));
        arrayList.add(ScriptValue.of((double)40.0));
        arrayList.add(ScriptValue.of((double)41.0));
        arrayList.add(ScriptValue.of((double)42.0));
        arrayList.add(ScriptValue.of((double)43.0));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("DEST_SLOTS", (ScriptValue)array);
        FILE_SCOPE = builder.build();
    }
}
