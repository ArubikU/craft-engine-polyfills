/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
            String string = "item";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$72_io_get(string, string2, scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("has_in", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "item";
            String string3 = "output";
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$72_io_get(string, string3, scriptValue5.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("has_out", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
            object = polyClassMachine != null ? polyClassMachine.tm$64_neighbor_block(scriptValue8.asStr()) : PolyDispatch.bootstrapCall("memberCall", "neighbor_block", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (PolyDispatch.bootstrapGet("memberGet", "is_container", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ^ true) {
            if (scriptValue3.asBool() && scriptValue6.asBool()) {
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                if (scriptValue9 != ScriptValue.NULL) {
                    String string = "item";
                    String string4 = "input";
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("dir");
                    boolean bl = false;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                    v3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string4, scriptValue10.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                } else {
                    v3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                if (scriptValue11 != ScriptValue.NULL) {
                    String string = "item";
                    String string5 = "output";
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("dir");
                    boolean bl = false;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                    v4 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string5, scriptValue12.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    String string = "item";
                    String string6 = "input";
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
                    boolean bl = true;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
                    v5 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string6, scriptValue14.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    String string = "item";
                    String string7 = "output";
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("dir");
                    boolean bl = true;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                    v6 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string7, scriptValue16.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
                } else {
                    v6 = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        if (scriptValue3.asBool() ^ true && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                String string = "item";
                String string8 = "input";
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17);
                v7 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string8, scriptValue18.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() && scriptValue6.asBool() ^ true) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
            if (scriptValue19 != ScriptValue.NULL) {
                String string = "item";
                String string9 = "input";
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue19);
                v8 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string9, scriptValue20.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
            if (scriptValue21 != ScriptValue.NULL) {
                String string = "item";
                String string10 = "output";
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue21);
                v9 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string10, scriptValue22.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v9 = ScriptValue.NULL;
            }
        } else if (scriptValue3.asBool() ^ true && scriptValue6.asBool()) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            if (scriptValue23 != ScriptValue.NULL) {
                String string = "item";
                String string11 = "input";
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue23);
                v10 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string11, scriptValue24.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string11), (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
            if (scriptValue25 != ScriptValue.NULL) {
                String string = "item";
                String string12 = "input";
                ScriptValue scriptValue26 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue25);
                v11 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string12, scriptValue26.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string12), (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
            if (scriptValue27 != ScriptValue.NULL) {
                String string = "item";
                String string13 = "output";
                ScriptValue scriptValue28 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue27);
                v12 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string13, scriptValue28.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string13), (ScriptValue)scriptValue28, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
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
            String string = "redstone";
            String string2 = "input";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("dir");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$72_io_get(string, string2, scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "io_get", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("required", scriptValue3);
        if (scriptValue3.asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                String string = "redstone";
                String string3 = "input";
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("dir");
                boolean bl = false;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
                v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string3, scriptValue5.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                String string = "redstone";
                String string4 = "input";
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("dir");
                boolean bl = true;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
                v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$54_io_set(string, string4, scriptValue7.asStr(), bl)) : PolyDispatch.bootstrapCall("memberCall", "io_set", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((boolean)bl), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue modeNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "north"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "south"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "east"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "west"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "up"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue modeDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "down"));
        ItemPipePanel.cycleMode(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "north"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "south"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "east"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "west"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "up"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue redstoneDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("dir",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "down"));
        ItemPipePanel.toggleRedstone(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue dirName(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "north");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 1.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "south");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 2.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "east");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 3.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "west");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 4.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "up");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "down");
    }

    public static ScriptValue openFilter(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("idx");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue2)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            double d = 1.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$52_page(d)) : PolyDispatch.bootstrapCall("memberCall", "page", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterNorth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterSouth(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 1.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterEast(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 2.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterWest(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 3.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterUp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 4.0));
        ItemPipePanel.openFilter(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue filterDown(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("idx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 5.0));
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
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue5 = scriptValue3;
            String string = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(scriptValue5.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("cur", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0)))) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue scriptValue8 = scriptValue3;
                String string = "int";
                ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 1.0);
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue scriptValue11 = scriptValue3;
                String string = "int";
                ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0);
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
                v3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue11.asStr(), string, scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
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
            ScriptValue scriptValue2 = ScriptValue.of((String)("pipe_filter_matchmode_" + scriptContext.getStr("dir")));
            String string = "string";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("m", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "seamsly");
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
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "seamsly");
        builder.val("next", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"seamsly")) {
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "id");
            builder.val("next", scriptValue6);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"id")) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "equals");
            builder.val("next", scriptValue7);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"equals")) {
            ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "seamsly");
            builder.val("next", scriptValue8);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue scriptValue9 = ScriptValue.of((String)("pipe_filter_matchmode_" + scriptValue3.asStr()));
            String string = "string";
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("next");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue9.asStr(), string, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
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
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "Seamsly");
        builder.val("text", scriptValue4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"id")) {
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "Id");
            builder.val("text", scriptValue5);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"equals")) {
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ItemPipePanel.class, "Equals");
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
            ScriptValue scriptValue = scriptContext.getClassOrVar("candidate");
            if ((scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("saved"), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("candidate");
                Object object4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("saved");
                if (ScriptFormula.valuesEqual((ScriptValue)object4, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL))) {
                    bl = true;
                    return ScriptValue.of((boolean)bl);
                }
            }
            bl = false;
            return ScriptValue.of((boolean)bl);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("candidate");
        if (scriptValue != ScriptValue.NULL) {
            object = PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("saved"), (ScriptContext)scriptContext);
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
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        builder2.val("idx", object2);
        ScriptValue scriptValue2 = ItemPipePanel.dirName(builder2);
        builder.val("dir", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue scriptValue4 = ScriptValue.of((String)("pipe_filter_" + scriptValue2.asStr() + "_" + scriptContext.getStr("slot")));
            String string = "item";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(scriptValue4.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
            String string = "pipe_filter_dir_index";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                ScriptValue scriptValue5 = scriptValue3;
                String string = "item";
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("NULL");
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
                v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue5.asStr(), string, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("clicked_item"), (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue scriptValue8 = scriptValue3;
            String string = "item";
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("clicked_item");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
            v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue filterItem(ScriptContext.Builder builder) {
        block9: {
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
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 9.0)), (ScriptContext)scriptContext));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("filter_item_at_slot");
            if (list != null) {
                for (ScriptValue scriptValue5 : list) {
                    Object object2;
                    builder.val("slot", scriptValue5);
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue6 != ScriptValue.NULL) {
                        ScriptValue scriptValue7 = ScriptValue.of((String)("pipe_filter_" + scriptContext.getStr("direction") + "_" + scriptValue5.asStr()));
                        String string = "item";
                        PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
                        object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(scriptValue7.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        object2 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue8 = object2;
                    builder.val("filter_item_at_slot", scriptValue8);
                    scriptValue4 = scriptValue8;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    boolean bl3 = true;
                    ScriptValue scriptValue9 = ScriptValue.of((boolean)true);
                    builder.val("any_filter", scriptValue9);
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("candidate", scriptContext.getClassOrVar("payload"));
                    builder3.val("saved", scriptValue4);
                    builder3.val("matchmode", scriptValue);
                    if (!ItemPipePanel.itemMatches(builder3).asBool()) continue;
                    boolean bl4 = true;
                    ScriptValue scriptValue10 = ScriptValue.of((boolean)true);
                    builder.val("listed", scriptValue10);
                }
            }
            if (scriptContext.getBool("any_filter") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue scriptValue12 = ScriptValue.of((String)("pipe_whitelist_" + scriptContext.getStr("direction")));
                String string = "int";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(scriptValue12.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object;
            builder.val("whitelist", scriptValue13);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("listed");
            builder.val("passes", scriptValue14);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemPipePanel.class, 0.0)))) {
                boolean bl5 = scriptContext.getBool("listed") ^ true;
                ScriptValue scriptValue15 = ScriptValue.of((boolean)bl5);
                builder.val("passes", scriptValue15);
            }
            if (!(scriptContext.getBool("passes") ^ true)) break block9;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("event");
            Object object3 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? ScriptValue.of((boolean)polyClassMachine.tm$60_pipe_register_network()) : PolyDispatch.bootstrapCall("memberCall", "pipe_register_network", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        FILE_SCOPE = builder.build();
    }
}
