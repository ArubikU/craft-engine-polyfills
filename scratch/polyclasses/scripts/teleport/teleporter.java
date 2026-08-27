/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

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
            ScriptValue.Obj obj;
            Object object2;
            String string = "frequency";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        PolyClassMachine polyClassMachine3;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("World");
        return ScriptValue.of((String)((scriptValue4 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassWorld.tg$31_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + "," + (scriptValue != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine3.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue2 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine2.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue3 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()));
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
        ScriptValue scriptValue5 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue4);
        arrayList.add(ScriptValue.of((String)";"));
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
        ScriptValue scriptValue2 = Teleporter.freqKey(builder2);
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
        ScriptValue scriptValue6 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue6);
        ScriptValue scriptValue7 = ScriptValue.of((String)"");
        builder.val("result", scriptValue7);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        arrayList.add(ScriptValue.of((String)";"));
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
            ScriptValue.Obj obj;
            Object object;
            String string = "frequency";
            String string2 = "string";
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("freq");
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue3));
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
        Teleporter.registerSelf(builder4);
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
                return ScriptValue.of((String)"");
            }
        }
        return ScriptValue.of((String)"");
    }

    public static ScriptValue linkedCount(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("freq").equals("")) {
            return ScriptValue.of((double)0.0);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptContext.getClassOrVar("freq"));
            ScriptValue scriptValue2 = Teleporter.freqKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue3);
        arrayList.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue4) ^ true)) continue;
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("n"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue7);
            }
        }
        return scriptContext.getClassOrVar("n");
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)"cml:teleporter_core");
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("click_type").equals("right") || scriptContext.getStr("click_type").equals("shift_right")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", ScriptValue.of((String)""));
            Teleporter.setFrequency(builder2);
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
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
        Teleporter.setFrequency(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
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
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        PolyClassMachine polyClassMachine3;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            return ScriptValue.of((String)"<red>No frequency tuned");
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("World");
        builder3.val("world_name", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassWorld.pg$30_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        builder3.val("x", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine3.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        builder3.val("y", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine2.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        builder3.val("z", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue6 = Teleporter._signAliasAt(builder3);
        builder.val("alias", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"") ^ true) {
            return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue.asStr() + " <gray>(" + scriptValue6.asStr() + ")"));
        }
        return ScriptValue.of((String)("<yellow>Frequency: <white>" + scriptValue.asStr()));
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
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("lines"));
            arrayList2.add(ScriptValue.of((String)"<gray>Insert a renamed item below to tune one."));
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue2);
            return scriptValue2;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("freq", scriptValue);
        ScriptValue scriptValue3 = Teleporter.linkedCount(builder3);
        builder.val("n", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptContext.getClassOrVar("lines"));
            arrayList3.add(ScriptValue.of((String)("<green>" + scriptValue3.asStr() + " other teleporter(s) linked.")));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue4);
        } else {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("lines"));
            arrayList4.add(ScriptValue.of((String)"<red>No other teleporter tuned to this frequency yet."));
            ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
            builder.val("lines", scriptValue5);
        }
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(scriptContext.getClassOrVar("lines"));
        arrayList5.add(ScriptValue.of((String)("<gray>Cost per jump: <white>" + scriptContext.getStr("TELEPORT_COST") + " CE")));
        ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
        builder.val("lines", scriptValue6);
        return scriptValue6;
    }

    public static ScriptValue doTeleport(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>No frequency set - insert a renamed item to tune one first.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        double d = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
        if (d < scriptContext.getNum("TELEPORT_COST")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue6 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue6);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("freq", scriptValue2);
            ScriptValue scriptValue8 = Teleporter.freqKey(builder3);
            String string = "string";
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object4);
                object = polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("existing", scriptValue9);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue10 = Teleporter.myPosStr(builder4);
        builder.val("mine", scriptValue10);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue9);
        arrayList.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object5;
                Object object6;
                Object object7;
                builder.val("entry", scriptValue11);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue10) ^ true)) continue;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("entry"));
                arrayList2.add(ScriptValue.of((String)","));
                ScriptValue scriptValue12 = ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue12);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(ScriptValue.of((double)0.0));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object7);
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"world", arrayList3, (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue14);
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue14);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("target_world");
                if (scriptValue15 != ScriptValue.NULL) {
                    Object object8;
                    Object object9;
                    Object object10;
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                    if (scriptValue16 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                        arrayList8.add(ScriptValue.of((double)1.0));
                        object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, arrayList8, (ScriptContext)scriptContext);
                    } else {
                        object10 = ScriptValue.NULL;
                    }
                    arrayList7.add((ScriptValue)object10);
                    arrayList6.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList7, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)0.5)));
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                    if (scriptValue17 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                        arrayList10.add(ScriptValue.of((double)2.0));
                        object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList10, (ScriptContext)scriptContext);
                    } else {
                        object9 = ScriptValue.NULL;
                    }
                    arrayList9.add((ScriptValue)object9);
                    arrayList6.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList9, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)1.5)));
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                    if (scriptValue18 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                        arrayList12.add(ScriptValue.of((double)3.0));
                        object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList12, (ScriptContext)scriptContext);
                    } else {
                        object8 = ScriptValue.NULL;
                    }
                    arrayList11.add((ScriptValue)object8);
                    arrayList6.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList11, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)0.5)));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue15, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object6;
                builder.val("loc", scriptValue19);
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                if (scriptValue20 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue20 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine2 = new PolyClassMachine(object11);
                        object5 = ScriptValue.of((boolean)polyClassMachine2.tm$11_consume_energy(scriptValue21.asNum()));
                    } else {
                        ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                        arrayList13.add(scriptValue21);
                        object5 = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue20, arrayList13, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (object5.asBool()) {
                    Object object12;
                    Object object13;
                    Object object14;
                    Object object15;
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object16;
                        if (scriptValue22 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine3 = new PolyClassMachine(object16);
                            v10 = ScriptValue.of((boolean)polyClassMachine3.tm$92_close());
                        } else {
                            ArrayList arrayList14 = new ArrayList();
                            v10 = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)scriptValue22, arrayList14, (ScriptContext)scriptContext);
                        }
                    } else {
                        v10 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object17;
                        ScriptValue scriptValue24 = scriptValue19;
                        if (scriptValue23 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object17);
                            v11 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue24));
                        } else {
                            ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                            arrayList15.add(scriptValue24);
                            v11 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue23, arrayList15, (ScriptContext)scriptContext);
                        }
                    } else {
                        v11 = ScriptValue.NULL;
                    }
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("parts");
                    if (scriptValue25 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                        arrayList16.add(ScriptValue.of((double)0.0));
                        object15 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue25, arrayList16, (ScriptContext)scriptContext);
                    } else {
                        object15 = ScriptValue.NULL;
                    }
                    builder5.val("world_name", object15);
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("parts");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                        arrayList17.add(ScriptValue.of((double)1.0));
                        object14 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, arrayList17, (ScriptContext)scriptContext);
                    } else {
                        object14 = ScriptValue.NULL;
                    }
                    builder5.val("x", object14);
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("parts");
                    if (scriptValue27 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(ScriptValue.of((double)2.0));
                        object13 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, arrayList18, (ScriptContext)scriptContext);
                    } else {
                        object13 = ScriptValue.NULL;
                    }
                    builder5.val("y", object13);
                    ScriptValue scriptValue28 = scriptContext.getClassOrVar("parts");
                    if (scriptValue28 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                        arrayList19.add(ScriptValue.of((double)3.0));
                        object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue28, arrayList19, (ScriptContext)scriptContext);
                    } else {
                        object12 = ScriptValue.NULL;
                    }
                    builder5.val("z", object12);
                    ScriptValue scriptValue29 = Teleporter._signAliasAt(builder5);
                    builder.val("alias", scriptValue29);
                    if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue29, (String)"") ^ true) {
                        ScriptValue scriptValue30 = scriptContext.getClassOrVar("Player");
                        if (scriptValue30 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object18;
                            ScriptValue scriptValue31 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue2.asStr() + "' to <white>" + scriptValue29.asStr() + "<green>."));
                            if (scriptValue30 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object18);
                                v16 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue31.asStr()));
                            } else {
                                ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                                arrayList20.add(scriptValue31);
                                v16 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue30, arrayList20, (ScriptContext)scriptContext);
                            }
                        } else {
                            v16 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Player");
                        if (scriptValue32 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object19;
                            ScriptValue scriptValue33 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue2.asStr() + "'."));
                            if (scriptValue32 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object19);
                                v17 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue33.asStr()));
                            } else {
                                ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
                                arrayList21.add(scriptValue33);
                                v17 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue32, arrayList21, (ScriptContext)scriptContext);
                            }
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object20;
            ScriptValue scriptValue34 = ScriptValue.of((String)("<red>No other teleporter is currently reachable on '" + scriptValue2.asStr() + "'."));
            if (scriptValue instanceof ScriptValue.Obj && (object20 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object20 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object20);
                v18 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue34.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList22 = new ArrayList<ScriptValue>();
                arrayList22.add(scriptValue34);
                v18 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList22, (ScriptContext)scriptContext);
            }
        } else {
            v18 = ScriptValue.NULL;
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
        arrayList2.add(ScriptValue.of((String)"teleporter.pf:on_frequency_dialog_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"value"));
        arrayList3.add(ScriptValue.of((String)"Frequency"));
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList3.add(Teleporter.getFrequency(builder2));
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
        Teleporter.setFrequency(builder2);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
            } else {
                ArrayList arrayList = new ArrayList();
                v0 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
