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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        return ScriptValue.of((String)stringBuilder.append((polyClassMachine != null ? polyClassMachine.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine2 != null ? polyClassMachine2.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine3 != null ? polyClassMachine3.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).toString());
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
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
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
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)scriptContext.getClassOrVar("world_name"), (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporter.class, 1)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("center", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "north"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "south"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "east"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "west"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "up"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, "down"));
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
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
        Teleporter.setFrequency(builder3);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "update", (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("x", (ScriptValue)(polyClassMachine != null ? polyClassMachine.pg$200_x() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("y", (ScriptValue)(polyClassMachine2 != null ? polyClassMachine2.pg$202_y() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        builder3.val("z", (ScriptValue)(polyClassMachine3 != null ? polyClassMachine3.pg$206_z() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
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
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>No frequency set - insert a renamed item to tune one first.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        double d = polyClassMachine != null ? polyClassMachine.tg$126_energy_stored() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum());
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
            builder3.val("freq", scriptValue3);
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
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("loc");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("alias");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("target_world");
        if (list != null) {
            for (ScriptValue scriptValue15 : list) {
                Object object5;
                Object object6;
                builder.val("entry", scriptValue15);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"") ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)scriptValue10) ^ true)) continue;
                ScriptValue scriptValue16 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporter.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue16);
                scriptValue12 = scriptValue16;
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue18 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("target_world", scriptValue18);
                scriptValue14 = scriptValue18;
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("target_world");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                    ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5)));
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                    object6 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue19, (ScriptValue)scriptValue21, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.5))), (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue24 = object6;
                builder.val("loc", scriptValue24);
                scriptValue11 = scriptValue24;
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
                if (scriptValue25 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object7;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("TELEPORT_COST");
                    if (scriptValue25 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine2 = new PolyClassMachine(object7);
                        object5 = ScriptValue.of((boolean)polyClassMachine2.tm$11_consume_energy(scriptValue26.asNum()));
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "consume_energy", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (object5.asBool()) {
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue27 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object8;
                        if (scriptValue27 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine3 = new PolyClassMachine(object8);
                            v7 = ScriptValue.of((boolean)polyClassMachine3.tm$92_close());
                        } else {
                            v7 = PolyDispatch.bootstrapCall("memberCall", "close", (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = scriptContext.getClassOrVar("Player");
                    if (scriptValue28 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object9;
                        ScriptValue scriptValue29 = scriptValue11;
                        if (scriptValue28 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                            v8 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue29));
                        } else {
                            v8 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue28, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                        }
                    } else {
                        v8 = ScriptValue.NULL;
                    }
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    ScriptValue scriptValue30 = scriptContext.getClassOrVar("parts");
                    builder5.val("world_name", (ScriptValue)(scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue30, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue31 = scriptContext.getClassOrVar("parts");
                    builder5.val("x", (ScriptValue)(scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue31, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue32 = scriptContext.getClassOrVar("parts");
                    builder5.val("y", (ScriptValue)(scriptValue32 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue32, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue33 = scriptContext.getClassOrVar("parts");
                    builder5.val("z", (ScriptValue)(scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue33, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporter.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                    ScriptValue scriptValue34 = Teleporter._signAliasAt(builder5);
                    builder.val("alias", scriptValue34);
                    scriptValue13 = scriptValue34;
                    if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue13, (String)"") ^ true) {
                        ScriptValue scriptValue35 = scriptContext.getClassOrVar("Player");
                        if (scriptValue35 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object10;
                            ScriptValue scriptValue36 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue3.asStr() + "' to <white>" + scriptValue13.asStr() + "<green>."));
                            if (scriptValue35 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                                v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue36.asStr()));
                            } else {
                                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue35, (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                            }
                        } else {
                            v9 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue37 = scriptContext.getClassOrVar("Player");
                        if (scriptValue37 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object11;
                            ScriptValue scriptValue38 = ScriptValue.of((String)("<green>Teleported via frequency '" + scriptValue3.asStr() + "'."));
                            if (scriptValue37 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue37).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object11);
                                v10 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue38.asStr()));
                            } else {
                                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue37, (ScriptValue)scriptValue38, (ScriptContext)scriptContext);
                            }
                        } else {
                            v10 = ScriptValue.NULL;
                        }
                    }
                }
                return ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            ScriptValue scriptValue39 = ScriptValue.of((String)("<red>No other teleporter is currently reachable on '" + scriptValue3.asStr() + "'."));
            if (scriptValue instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object12);
                v11 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue39.asStr()));
            } else {
                v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue39, (ScriptContext)scriptContext);
            }
        } else {
            v11 = ScriptValue.NULL;
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$74_update());
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
