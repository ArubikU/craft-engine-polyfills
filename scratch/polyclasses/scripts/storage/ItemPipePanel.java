/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class ItemPipePanel {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue cycleMode(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "item";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$72_io_get(string, string2, scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                object3 = PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("has_in", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "item";
            String string3 = "output";
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
            if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                object2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$72_io_get(string, string3, scriptValue5.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue5);
                object2 = PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("has_out", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("dir");
            if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                object = polyClassMachine_v4.tm$64_neighbor_block(scriptValue8.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                object = PolyDispatch.bootstrapCall("memberCall", "neighbor_block", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ^ true) {
            if (scriptValue3.asBool() && scriptValue6.asBool()) {
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                if (scriptValue9 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object7;
                    String string = "item";
                    String string4 = "input";
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("dir");
                    boolean bl = false;
                    if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                        v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string4, scriptValue10.asStr(), bl));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string4));
                        arrayList.add(scriptValue10);
                        arrayList.add(ScriptValue.of((boolean)bl));
                        v3 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                if (scriptValue11 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "item";
                    String string5 = "output";
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("dir");
                    boolean bl = false;
                    if (scriptValue11 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object8);
                        v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string5, scriptValue12.asStr(), bl));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string5));
                        arrayList.add(scriptValue12);
                        arrayList.add(ScriptValue.of((boolean)bl));
                        v4 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    String string = "item";
                    String string6 = "input";
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                    boolean bl = true;
                    if (scriptValue13 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object9);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string6, scriptValue14.asStr(), bl));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string6));
                        arrayList.add(scriptValue14);
                        arrayList.add(ScriptValue.of((boolean)bl));
                        v5 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    String string = "item";
                    String string7 = "output";
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("dir");
                    boolean bl = true;
                    if (scriptValue15 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string7, scriptValue16.asStr(), bl));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string7));
                        arrayList.add(scriptValue16);
                        arrayList.add(ScriptValue.of((boolean)bl));
                        v6 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        if (scriptValue3.asBool() ^ true && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                String string = "item";
                String string8 = "input";
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue17 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object11);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string8, scriptValue18.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string8));
                    arrayList.add(scriptValue18);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v7 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
            if (scriptValue19 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                String string = "item";
                String string9 = "input";
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue19 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object12);
                    v8 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string9, scriptValue20.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string9));
                    arrayList.add(scriptValue20);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v8 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
            if (scriptValue21 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                String string = "item";
                String string10 = "output";
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue21 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object13);
                    v9 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string10, scriptValue22.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string10));
                    arrayList.add(scriptValue22);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v9 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() ^ true && scriptValue6.asBool()) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "item";
                String string11 = "input";
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue23 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object14);
                    v10 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string11, scriptValue24.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string11));
                    arrayList.add(scriptValue24);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v10 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
            if (scriptValue25 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                String string = "item";
                String string12 = "input";
                ScriptValue scriptValue26 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue25 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object15);
                    v11 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string12, scriptValue26.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string12));
                    arrayList.add(scriptValue26);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v11 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
            if (scriptValue27 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "item";
                String string13 = "output";
                ScriptValue scriptValue28 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue27 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object16);
                    v12 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string13, scriptValue28.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string13));
                    arrayList.add(scriptValue28);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v12 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue toggleRedstone(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "redstone";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = ScriptValue.of((boolean)polyClassMachine_v4.tm$72_io_get(string, string2, scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                object = PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("required", scriptValue3);
        if (scriptValue3.asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "redstone";
                String string3 = "input";
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string3, scriptValue5.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "redstone";
                String string4 = "input";
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$54_io_set(string, string4, scriptValue7.asStr(), bl));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue7);
                    arrayList.add(ScriptValue.of((boolean)bl));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue modeNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"north"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"south"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"east"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"west"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"up"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"down"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"north"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"south"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"east"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"west"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"up"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir", ScriptValue.of((String)"down"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue dirName(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)0.0))) {
            return ScriptValue.of((String)"north");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0))) {
            return ScriptValue.of((String)"south");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)2.0))) {
            return ScriptValue.of((String)"east");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)3.0))) {
            return ScriptValue.of((String)"west");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)4.0))) {
            return ScriptValue.of((String)"up");
        }
        return ScriptValue.of((String)"down");
    }

    public static ScriptValue openFilter(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("idx");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 1.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$52_page(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "page", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)0.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)1.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)2.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)3.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)4.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx", ScriptValue.of((double)5.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue toggleFilterWhitelist(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        builder2.val("idx", object2);
        ScriptValue scriptValue2 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue2);
        ScriptValue scriptValue3 = ScriptValue.of((String)("pipe_whitelist_" + scriptValue2.asStr()));
        builder.val("name", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue5 = scriptValue3;
            String string = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v4.tm$34_get_typed(scriptValue5.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("cur", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptValue scriptValue8 = scriptValue3;
                String string = "int";
                ScriptValue scriptValue9 = ScriptValue.of((double)1.0);
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue8);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue9);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue11 = scriptValue3;
                String string = "int";
                ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(scriptValue11.asStr(), string, scriptValue12));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue11);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue12);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterMatchmode(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = ScriptValue.of((String)("pipe_filter_matchmode_" + scriptContext.getStr("dir")));
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(scriptValue2.asStr(), string);
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
        builder.val("m", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            return ScriptValue.of((String)"seamsly");
        }
        return scriptValue3;
    }

    public static ScriptValue cycleFilterMatchmode(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        builder2.val("idx", object);
        ScriptValue scriptValue3 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("dir", scriptValue3);
        ScriptValue scriptValue4 = ItemPipePanel.filterMatchmode(builder3);
        builder.val("cur", scriptValue4);
        ScriptValue scriptValue5 = ScriptValue.of((String)"seamsly");
        builder.val("next", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"seamsly")) {
            ScriptValue scriptValue6 = ScriptValue.of((String)"id");
            builder.val("next", scriptValue6);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"id")) {
            ScriptValue scriptValue7 = ScriptValue.of((String)"equals");
            builder.val("next", scriptValue7);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"equals")) {
            ScriptValue scriptValue8 = ScriptValue.of((String)"seamsly");
            builder.val("next", scriptValue8);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue9 = ScriptValue.of((String)("pipe_filter_matchmode_" + scriptValue3.asStr()));
            String string = "string";
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("next");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(scriptValue9.asStr(), string, scriptValue10));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue9);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue10);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterMatchmodeLabel(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "pipe_filter_dir_index";
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
        builder2.val("idx", object);
        ScriptValue scriptValue2 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue2);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("dir", scriptValue2);
        ScriptValue scriptValue3 = ItemPipePanel.filterMatchmode(builder3);
        builder.val("m", scriptValue3);
        ScriptValue scriptValue4 = ScriptValue.of((String)"Seamsly");
        builder.val("text", scriptValue4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"id")) {
            ScriptValue scriptValue5 = ScriptValue.of((String)"Id");
            builder.val("text", scriptValue5);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"equals")) {
            ScriptValue scriptValue6 = ScriptValue.of((String)"Equals");
            builder.val("text", scriptValue6);
        }
        return ScriptValue.of((String)("<gray>Filter Mode: <yellow>" + scriptContext.getStr("text")));
    }

    /*
     * Enabled aggressive block sorting
     */
    public static ScriptValue itemMatches(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("matchmode").equals("id")) {
            Object object2;
            ScriptValue scriptValue = scriptContext.getClassOrVar("candidate");
            Object object3 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("saved");
            if (scriptValue2 != ScriptValue.NULL) {
                object2 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
                return ScriptValue.of((boolean)ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)object2));
            }
            object2 = ScriptValue.NULL;
            return ScriptValue.of((boolean)ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)object2));
        }
        if (scriptContext.getStr("matchmode").equals("equals")) {
            boolean bl;
            Object object4;
            ScriptValue scriptValue = scriptContext.getClassOrVar("candidate");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("saved"));
                object4 = PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            if (object4.asBool()) {
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("candidate");
                Object object5 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("saved");
                if (ScriptFormula.valuesEqual((ScriptValue)object5, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL))) {
                    bl = true;
                    return ScriptValue.of((boolean)bl);
                }
            }
            bl = false;
            return ScriptValue.of((boolean)bl);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("candidate");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("saved"));
            object = PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            return object;
        }
        object = ScriptValue.NULL;
        return object;
    }

    public static ScriptValue ghostGet(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        builder2.val("idx", object2);
        ScriptValue scriptValue2 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue4 = ScriptValue.of((String)("pipe_filter_" + scriptValue2.asStr() + "_" + scriptContext.getStr("slot")));
            String string = "item";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v4.tm$34_get_typed(scriptValue4.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue ghostSet(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "pipe_filter_dir_index";
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
        builder2.val("idx", object);
        ScriptValue scriptValue2 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue2);
        ScriptValue scriptValue3 = ScriptValue.of((String)("pipe_filter_" + scriptValue2.asStr() + "_" + scriptContext.getStr("slot")));
        builder.val("key", scriptValue3);
        if (scriptContext.getStr("click_type").equals("right") || scriptContext.getStr("click_type").equals("shift_right")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue5 = scriptValue3;
                String string = "item";
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("NULL");
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(scriptValue5.asStr(), string, scriptValue6));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue5);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue6);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("clicked_item"));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue8 = scriptValue3;
            String string = "item";
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("clicked_item");
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue8);
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(scriptValue9);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterItem(ScriptContext.Builder builder) {
        block15: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getStr("type").equals("item") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dir", scriptContext.getClassOrVar("direction"));
            ScriptValue scriptValue = ItemPipePanel.filterMatchmode(builder2);
            builder.val("matchmode", scriptValue);
            boolean bl = false;
            ScriptValue scriptValue2 = ScriptValue.of((boolean)false);
            builder.val("listed", scriptValue2);
            boolean bl2 = false;
            ScriptValue scriptValue3 = ScriptValue.of((boolean)false);
            builder.val("any_filter", scriptValue3);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)9.0));
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
            if (list != null) {
                for (ScriptValue scriptValue4 : list) {
                    Object object2;
                    builder.val("slot", scriptValue4);
                    ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue5 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object3;
                        ScriptValue scriptValue6 = ScriptValue.of((String)("pipe_filter_" + scriptContext.getStr("direction") + "_" + scriptContext.getStr("slot")));
                        String string = "item";
                        if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                            object2 = polyClassMachine_v4.tm$34_get_typed(scriptValue6.asStr(), string);
                        } else {
                            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                            arrayList2.add(scriptValue6);
                            arrayList2.add(ScriptValue.of((String)string));
                            object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
                        }
                    } else {
                        object2 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue7 = object2;
                    builder.val("filter_item_at_slot", scriptValue7);
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue7);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    boolean bl3 = true;
                    ScriptValue scriptValue8 = ScriptValue.of((boolean)true);
                    builder.val("any_filter", scriptValue8);
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("candidate", scriptContext.getClassOrVar("payload"));
                    builder3.val("saved", scriptValue7);
                    builder3.val("matchmode", scriptValue);
                    if (!ItemPipePanel.itemMatches(builder3).asBool()) continue;
                    boolean bl4 = true;
                    ScriptValue scriptValue9 = ScriptValue.of((boolean)true);
                    builder.val("listed", scriptValue9);
                }
            }
            if (scriptContext.getBool("any_filter") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue11 = ScriptValue.of((String)("pipe_whitelist_" + scriptContext.getStr("direction")));
                String string = "int";
                if (scriptValue10 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    object = polyClassMachine_v4.tm$34_get_typed(scriptValue11.asStr(), string);
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue11);
                    arrayList4.add(ScriptValue.of((String)string));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, arrayList4, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object;
            builder.val("whitelist", scriptValue12);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("listed");
            builder.val("passes", scriptValue13);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)0.0))) {
                boolean bl5 = scriptContext.getBool("listed") ^ true;
                ScriptValue scriptValue14 = ScriptValue.of((boolean)bl5);
                builder.val("passes", scriptValue14);
            }
            if (!(scriptContext.getBool("passes") ^ true)) break block15;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("event");
            if (scriptValue15 != ScriptValue.NULL) {
                ArrayList arrayList5 = new ArrayList();
                v2 = PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue15, arrayList5, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$60_pipe_register_network());
            } else {
                ArrayList arrayList = new ArrayList();
                v0 = PolyDispatch.bootstrapCall("memberCall", "pipe_register_network", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
