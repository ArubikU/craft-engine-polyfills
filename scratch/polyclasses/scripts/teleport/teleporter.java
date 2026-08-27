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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";"));
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("existing", scriptValue5);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue6 = Teleporter.myPosStr(builder3);
        builder.val("mine", scriptValue6);
        ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
        builder.val("result", scriptValue7);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";"));
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
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
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
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporter.class, 1)), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("center", scriptValue5);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "north"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "south"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "east"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "west"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "up"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "down"));
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue4) ^ true)) continue;
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("n"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)));
                builder.val("n", scriptValue7);
            }
        }
        return scriptContext.getClassOrVar("n");
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "cml:teleporter_core");
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("click_type").equals("right") || scriptContext.getStr("click_type").equals("shift_right")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ""));
            Teleporter.setFrequency(builder2);
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
        Teleporter.setFrequency(builder3);
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
        PolyClassMachine_v2 polyClassMachine_v2;
        PolyClassMachine_v2 polyClassMachine_v22;
        PolyClassMachine_v2 polyClassMachine_v23;
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Teleporter.getFrequency(builder2);
        builder.val("freq", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<red>No frequency tuned");
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("World");
        builder3.val("world_name", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassWorld.pg$30_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        builder3.val("x", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v23.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        builder3.val("y", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v22.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        builder3.val("z", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine_v2.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<gray>Insert a renamed item below to tune one."));
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
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "<red>No other teleporter tuned to this frequency yet."));
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
        PolyClassMachine_v2 polyClassMachine_v2;
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
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        double d = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v2.tg$126_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
        if (d < scriptContext.getNum("TELEPORT_COST")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue6 = ScriptValue.of((String)("<red>Not enough energy - need " + scriptContext.getStr("TELEPORT_COST") + " CE."));
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue6.asStr()));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object5;
                Object object6;
                builder.val("entry", scriptValue11);
                if (!(scriptContext.getStr("entry").equals("") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("entry"), (ScriptValue)scriptValue10) ^ true)) continue;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("entry"));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ","));
                ScriptValue scriptValue12 = ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue12);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                arrayList3.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"world", arrayList3, (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue14);
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue14);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("target_world");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                    arrayList5.add((ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5)));
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                    arrayList6.add((ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue19 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList6, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.5)));
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                    arrayList7.add((ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue15, (ScriptValue)scriptValue17, (ScriptValue)scriptValue19, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList7, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5))), (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue21 = object6;
                builder.val("loc", scriptValue21);
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                if (scriptValue22 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object7;
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue22 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object7);
                        object5 = ScriptValue.of((boolean)polyClassMachine_v22.tm$11_consume_energy(scriptValue23.asNum()));
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (object5.asBool()) {
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue24 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object8;
                        if (scriptValue24 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object8);
                            v8 = ScriptValue.of((boolean)polyClassMachine_v23.tm$92_close());
                        } else {
                            v8 = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                        }
                    } else {
                        v8 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("Player");
                    if (scriptValue25 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object9;
                        ScriptValue scriptValue26 = scriptValue21;
                        if (scriptValue25 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object9);
                            v9 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$24_teleport_to(scriptValue26));
                        } else {
                            v9 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                        }
                    } else {
                        v9 = ScriptValue.NULL;
                    }
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("parts");
                    builder5.val("world_name", (ScriptValue)(scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue28 = scriptContext.getClassOrVar("parts");
                    builder5.val("x", (ScriptValue)(scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue29 = scriptContext.getClassOrVar("parts");
                    builder5.val("y", (ScriptValue)(scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue30 = scriptContext.getClassOrVar("parts");
                    builder5.val("z", (ScriptValue)(scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue30, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue31 = Teleporter._signAliasAt(builder5);
                    builder.val("alias", scriptValue31);
                    if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue31, (String)"") ^ true) {
                        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Player");
                        if (scriptValue32 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object10;
                            ScriptValue scriptValue33 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue2.asStr() + "' to <white>" + scriptValue31.asStr() + "<green>."));
                            if (scriptValue32 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object10);
                                v10 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue33.asStr()));
                            } else {
                                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue32, (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                            }
                        } else {
                            v10 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue34 = scriptContext.getClassOrVar("Player");
                        if (scriptValue34 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object11;
                            ScriptValue scriptValue35 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue2.asStr() + "'."));
                            if (scriptValue34 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object11);
                                v11 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue35.asStr()));
                            } else {
                                v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue34, (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
                            }
                        } else {
                            v11 = ScriptValue.NULL;
                        }
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            ScriptValue scriptValue36 = ScriptValue.of((String)("<red>No other teleporter is currently reachable on '" + scriptValue2.asStr() + "'."));
            if (scriptValue instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object12);
                v12 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue36.asStr()));
            } else {
                v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
            }
        } else {
            v12 = ScriptValue.NULL;
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
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("freq", scriptContext.getClassOrVar("new_freq"));
        Teleporter.setFrequency(builder2);
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
