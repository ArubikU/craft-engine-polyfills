/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class Windmill {
    public static ScriptValue _sailCount(ScriptContext.Builder builder) {
        ScriptContext scriptContext;
        ScriptValue scriptValue;
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0);
        builder.val("n", scriptValue2);
        List list = ScriptProgram.resolveForRows((String)"contraption.blocks()", (ScriptContext)builder.peek(), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("block", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext scriptContext3 = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue3 = scriptContext3.getClassInstance("block");
                if (scriptValue3 == ScriptValue.NULL) {
                    scriptValue3 = scriptContext3.getVar("block");
                }
                arrayList.add(scriptValue3 != ScriptValue.NULL ? ScriptFormula.memberGet((ScriptValue)scriptValue3, (String)"id", (ScriptContext)scriptContext3) : ScriptValue.NULL);
                arrayList.add(ScriptValue.of((String)"sail"));
                if (!ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext3).asBool()) continue;
                ScriptContext scriptContext4 = builder.peek();
                ScriptValue scriptValue4 = scriptContext4.getClassInstance("n");
                if (scriptValue4 == ScriptValue.NULL) {
                    scriptValue4 = scriptContext4.getVar("n");
                }
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue5);
            }
        }
        if ((scriptValue = (scriptContext = builder.peek()).getClassInstance("n")) == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("n");
        }
        return scriptValue;
    }

    public static ScriptValue _advanceHeadAngle(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext.getVar("Machine");
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"head_last_tick"));
            arrayList.add(ScriptValue.of((String)"int"));
            scriptValue = ScriptFormula.memberCall((ScriptValue)scriptValue2, (String)"get_typed", arrayList, (ScriptContext)scriptContext);
        } else {
            scriptValue = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptValue;
        builder.val("last", scriptValue3);
        ScriptContext scriptContext2 = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext2);
        builder.val("now", scriptValue4);
        ScriptContext scriptContext3 = builder.peek();
        ScriptValue scriptValue5 = scriptContext3.getClassInstance("last");
        if (scriptValue5 == ScriptValue.NULL) {
            scriptValue5 = scriptContext3.getVar("last");
        }
        if (scriptValue5.asNum() <= 0.0) {
            ScriptContext scriptContext4 = builder.peek();
            ScriptValue scriptValue6 = scriptContext4.getClassInstance("now");
            if (scriptValue6 == ScriptValue.NULL) {
                scriptValue6 = scriptContext4.getVar("now");
            }
            ScriptValue scriptValue7 = scriptValue6;
            builder.val("last", scriptValue7);
        }
        ScriptContext scriptContext5 = builder.peek();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        ScriptValue scriptValue8 = scriptContext5.getClassInstance("now");
        if (scriptValue8 == ScriptValue.NULL) {
            scriptValue8 = scriptContext5.getVar("now");
        }
        double d = scriptValue8.asNum();
        ScriptValue scriptValue9 = scriptContext5.getClassInstance("last");
        if (scriptValue9 == ScriptValue.NULL) {
            scriptValue9 = scriptContext5.getVar("last");
        }
        arrayList2.add(ScriptValue.of((double)(d - scriptValue9.asNum())));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)100.0));
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin((String)"clamp", arrayList2, (ScriptContext)scriptContext5);
        builder.val("elapsed", scriptValue10);
        ScriptContext scriptContext6 = builder.peek();
        ScriptValue scriptValue11 = scriptContext6.getClassInstance("Machine");
        if (scriptValue11 == ScriptValue.NULL) {
            scriptValue11 = scriptContext6.getVar("Machine");
        }
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue scriptValue12;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(ScriptValue.of((String)"head_angle"));
            arrayList3.add(ScriptValue.of((String)"int"));
            ScriptValue scriptValue13 = scriptContext6.getClassInstance("Machine");
            if (scriptValue13 == ScriptValue.NULL) {
                scriptValue13 = scriptContext6.getVar("Machine");
            }
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)"head_angle"));
                arrayList4.add(ScriptValue.of((String)"int"));
                scriptValue12 = ScriptFormula.memberCall((ScriptValue)scriptValue13, (String)"get_typed", arrayList4, (ScriptContext)scriptContext6);
            } else {
                scriptValue12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext6.getClassInstance("elapsed");
            if (scriptValue14 == ScriptValue.NULL) {
                scriptValue14 = scriptContext6.getVar("elapsed");
            }
            double d2 = scriptValue14.asNum();
            ScriptValue scriptValue15 = scriptContext6.getClassInstance("rpm_value");
            if (scriptValue15 == ScriptValue.NULL) {
                scriptValue15 = scriptContext6.getVar("rpm_value");
            }
            arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)(d2 * scriptValue15.asNum() * 0.3))));
            v4 = ScriptFormula.memberCall((ScriptValue)scriptValue11, (String)"set_typed", arrayList3, (ScriptContext)scriptContext6);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptContext scriptContext7 = builder.peek();
        ScriptValue scriptValue16 = scriptContext7.getClassInstance("Machine");
        if (scriptValue16 == ScriptValue.NULL) {
            scriptValue16 = scriptContext7.getVar("Machine");
        }
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((String)"head_last_tick"));
            arrayList5.add(ScriptValue.of((String)"int"));
            ScriptValue scriptValue17 = scriptContext7.getClassInstance("now");
            if (scriptValue17 == ScriptValue.NULL) {
                scriptValue17 = scriptContext7.getVar("now");
            }
            arrayList5.add(scriptValue17);
            v5 = ScriptFormula.memberCall((ScriptValue)scriptValue16, (String)"set_typed", arrayList5, (ScriptContext)scriptContext7);
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _stop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            v0 = ScriptFormula.memberCall((ScriptValue)scriptValue, (String)"set_rpm_output", arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue2 = scriptContext2.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext2.getVar("Machine");
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            v1 = ScriptFormula.memberCall((ScriptValue)scriptValue2, (String)"report_su", arrayList, (ScriptContext)scriptContext2);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptContext scriptContext3 = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext3);
        builder2.val("rpm_value", ScriptValue.of((double)0.0));
        Windmill._advanceHeadAngle(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue _spinAxis(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue != ScriptValue.NULL ? ScriptFormula.memberGet((ScriptValue)scriptValue, (String)"facing_dy", (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            ScriptContext scriptContext2 = builder.peek();
            return ScriptValue.of((String)"y");
        }
        ScriptContext scriptContext3 = builder.peek();
        ScriptValue scriptValue2 = scriptContext3.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext3.getVar("Machine");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ScriptFormula.memberGet((ScriptValue)scriptValue2, (String)"facing_dx", (ScriptContext)scriptContext3) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            ScriptContext scriptContext4 = builder.peek();
            return ScriptValue.of((String)"x");
        }
        ScriptContext scriptContext5 = builder.peek();
        return ScriptValue.of((String)"z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("rpm");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("rpm");
        }
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            v0 = ScriptFormula.memberCall((ScriptValue)scriptValue, (String)"set_rpm_output", arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue2 = scriptContext2.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext2.getVar("Machine");
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            v1 = ScriptFormula.memberCall((ScriptValue)scriptValue2, (String)"report_su", arrayList, (ScriptContext)scriptContext2);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        double d;
        double d2;
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue4 = ScriptValue.of((double)8.0);
        builder.val("SAILS_PER_RPM", scriptValue4);
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue5 = ScriptValue.of((double)16.0);
        builder.val("MAX_RPM", scriptValue5);
        ScriptContext scriptContext3 = builder.peek();
        ScriptValue scriptValue6 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue6);
        ScriptContext scriptContext4 = builder.peek();
        ScriptValue scriptValue7 = ScriptValue.of((double)8.0);
        builder.val("CAPACITY_PER_RPM", scriptValue7);
        ScriptContext scriptContext5 = builder.peek();
        ScriptValue scriptValue8 = scriptContext5.getClassInstance("Machine");
        if (scriptValue8 == ScriptValue.NULL) {
            scriptValue8 = scriptContext5.getVar("Machine");
        }
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"assembled"));
            arrayList.add(ScriptValue.of((String)"int"));
            scriptValue3 = ScriptFormula.memberCall((ScriptValue)scriptValue8, (String)"get_typed", arrayList, (ScriptContext)scriptContext5);
        } else {
            scriptValue3 = ScriptValue.NULL;
        }
        if (scriptValue3.asNum() <= 0.0) {
            ScriptContext scriptContext6 = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext6);
            Windmill._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext7 = builder.peek();
        ScriptValue scriptValue9 = scriptContext7.getClassInstance("Machine");
        if (scriptValue9 == ScriptValue.NULL) {
            scriptValue9 = scriptContext7.getVar("Machine");
        }
        ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ScriptFormula.memberGet((ScriptValue)scriptValue9, (String)"contraption", (ScriptContext)scriptContext7) : ScriptValue.NULL;
        builder.val("contraption", scriptValue10);
        ScriptContext scriptContext8 = builder.peek();
        ScriptValue scriptValue11 = scriptContext8.getClassInstance("contraption");
        if (scriptValue11 == ScriptValue.NULL) {
            scriptValue11 = scriptContext8.getVar("contraption");
        }
        if ((scriptValue2 = scriptContext8.getClassInstance("null")) == ScriptValue.NULL) {
            scriptValue2 = scriptContext8.getVar("null");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue11, (ScriptValue)scriptValue2)) {
            ScriptContext scriptContext9 = builder.peek();
            ScriptValue scriptValue12 = scriptContext9.getClassInstance("Machine");
            if (scriptValue12 == ScriptValue.NULL) {
                scriptValue12 = scriptContext9.getVar("Machine");
            }
            if (scriptValue12 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"assembled"));
                arrayList.add(ScriptValue.of((String)"int"));
                arrayList.add(ScriptValue.of((double)0.0));
                v1 = ScriptFormula.memberCall((ScriptValue)scriptValue12, (String)"set_typed", arrayList, (ScriptContext)scriptContext9);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptContext scriptContext10 = builder.peek();
            ScriptValue scriptValue13 = scriptContext10.getClassInstance("Machine");
            if (scriptValue13 == ScriptValue.NULL) {
                scriptValue13 = scriptContext10.getVar("Machine");
            }
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"contraption_uuid"));
                arrayList.add(ScriptValue.of((String)"string"));
                arrayList.add(ScriptValue.of((String)""));
                v2 = ScriptFormula.memberCall((ScriptValue)scriptValue13, (String)"set_typed", arrayList, (ScriptContext)scriptContext10);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext scriptContext11 = builder.peek();
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext11);
            Windmill._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext12 = builder.peek();
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext12);
        ScriptValue scriptValue14 = scriptContext12.getClassInstance("contraption");
        if (scriptValue14 == ScriptValue.NULL) {
            scriptValue14 = scriptContext12.getVar("contraption");
        }
        builder4.val("contraption", scriptValue14);
        ScriptValue scriptValue15 = Windmill._sailCount(builder4);
        builder.val("sails", scriptValue15);
        ScriptContext scriptContext13 = builder.peek();
        ScriptValue scriptValue16 = scriptContext13.getClassInstance("sails");
        if (scriptValue16 == ScriptValue.NULL) {
            scriptValue16 = scriptContext13.getVar("sails");
        }
        double d3 = scriptValue16.asNum();
        ScriptValue scriptValue17 = scriptContext13.getClassInstance("MIN_SAILS");
        if (scriptValue17 == ScriptValue.NULL) {
            scriptValue17 = scriptContext13.getVar("MIN_SAILS");
        }
        if (d3 < scriptValue17.asNum()) {
            ScriptContext scriptContext14 = builder.peek();
            ScriptValue scriptValue18 = scriptContext14.getClassInstance("contraption");
            if (scriptValue18 == ScriptValue.NULL) {
                scriptValue18 = scriptContext14.getVar("contraption");
            }
            if (scriptValue18 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext14);
                arrayList.add(Windmill._spinAxis(builder5));
                arrayList.add(ScriptValue.of((double)0.0));
                v4 = ScriptFormula.memberCall((ScriptValue)scriptValue18, (String)"set_spin", arrayList, (ScriptContext)scriptContext14);
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptContext scriptContext15 = builder.peek();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext15);
            Windmill._stop(builder6);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext16 = builder.peek();
        ScriptValue scriptValue19 = scriptContext16.getClassInstance("Machine");
        if (scriptValue19 == ScriptValue.NULL) {
            scriptValue19 = scriptContext16.getVar("Machine");
        }
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"windmill_dir"));
            arrayList.add(ScriptValue.of((String)"int"));
            scriptValue = ScriptFormula.memberCall((ScriptValue)scriptValue19, (String)"get_typed", arrayList, (ScriptContext)scriptContext16);
        } else {
            scriptValue = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptValue;
        builder.val("dir", scriptValue20);
        ScriptContext scriptContext17 = builder.peek();
        ScriptValue scriptValue21 = scriptContext17.getClassInstance("dir");
        if (scriptValue21 == ScriptValue.NULL) {
            scriptValue21 = scriptContext17.getVar("dir");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptContext scriptContext18 = builder.peek();
            ScriptValue scriptValue22 = ScriptValue.of((double)1.0);
            builder.val("dir", scriptValue22);
        }
        ScriptContext scriptContext19 = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue23 = scriptContext19.getClassInstance("SAILS_PER_RPM");
        if (scriptValue23 == ScriptValue.NULL) {
            scriptValue23 = scriptContext19.getVar("SAILS_PER_RPM");
        }
        if ((d2 = scriptValue23.asNum()) == 0.0) {
            d = 0.0;
        } else {
            ScriptValue scriptValue24 = scriptContext19.getClassInstance("sails");
            if (scriptValue24 == ScriptValue.NULL) {
                scriptValue24 = scriptContext19.getVar("sails");
            }
            d = scriptValue24.asNum() / d2;
        }
        arrayList.add(ScriptValue.of((double)Math.floor(d)));
        arrayList.add(ScriptValue.of((double)1.0));
        ScriptValue scriptValue25 = scriptContext19.getClassInstance("MAX_RPM");
        if (scriptValue25 == ScriptValue.NULL) {
            scriptValue25 = scriptContext19.getVar("MAX_RPM");
        }
        arrayList.add(scriptValue25);
        ScriptValue scriptValue26 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext19);
        builder.val("speed", scriptValue26);
        ScriptContext scriptContext20 = builder.peek();
        ScriptValue scriptValue27 = scriptContext20.getClassInstance("speed");
        if (scriptValue27 == ScriptValue.NULL) {
            scriptValue27 = scriptContext20.getVar("speed");
        }
        double d4 = scriptValue27.asNum();
        ScriptValue scriptValue28 = scriptContext20.getClassInstance("dir");
        if (scriptValue28 == ScriptValue.NULL) {
            scriptValue28 = scriptContext20.getVar("dir");
        }
        ScriptValue scriptValue29 = ScriptValue.of((double)(d4 * scriptValue28.asNum()));
        builder.val("rpm_out", scriptValue29);
        ScriptContext scriptContext21 = builder.peek();
        ScriptValue scriptValue30 = scriptContext21.getClassInstance("contraption");
        if (scriptValue30 == ScriptValue.NULL) {
            scriptValue30 = scriptContext21.getVar("contraption");
        }
        if (scriptValue30 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext21);
            arrayList2.add(Windmill._spinAxis(builder7));
            ScriptValue scriptValue31 = scriptContext21.getClassInstance("rpm_out");
            if (scriptValue31 == ScriptValue.NULL) {
                scriptValue31 = scriptContext21.getVar("rpm_out");
            }
            arrayList2.add(scriptValue31);
            v8 = ScriptFormula.memberCall((ScriptValue)scriptValue30, (String)"set_spin", arrayList2, (ScriptContext)scriptContext21);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptContext scriptContext22 = builder.peek();
        ScriptValue scriptValue32 = scriptContext22.getClassInstance("Machine");
        if (scriptValue32 == ScriptValue.NULL) {
            scriptValue32 = scriptContext22.getVar("Machine");
        }
        if (scriptValue32 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue33 = scriptContext22.getClassInstance("rpm_out");
            if (scriptValue33 == ScriptValue.NULL) {
                scriptValue33 = scriptContext22.getVar("rpm_out");
            }
            arrayList3.add(scriptValue33);
            v9 = ScriptFormula.memberCall((ScriptValue)scriptValue32, (String)"set_rpm_output", arrayList3, (ScriptContext)scriptContext22);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptContext scriptContext23 = builder.peek();
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext23);
        ScriptValue scriptValue34 = scriptContext23.getClassInstance("rpm_out");
        if (scriptValue34 == ScriptValue.NULL) {
            scriptValue34 = scriptContext23.getVar("rpm_out");
        }
        builder8.val("rpm_value", scriptValue34);
        Windmill._advanceHeadAngle(builder8);
        ScriptContext scriptContext24 = builder.peek();
        ScriptValue scriptValue35 = scriptContext24.getClassInstance("speed");
        if (scriptValue35 == ScriptValue.NULL) {
            scriptValue35 = scriptContext24.getVar("speed");
        }
        double d5 = scriptValue35.asNum();
        ScriptValue scriptValue36 = scriptContext24.getClassInstance("CAPACITY_PER_RPM");
        if (scriptValue36 == ScriptValue.NULL) {
            scriptValue36 = scriptContext24.getVar("CAPACITY_PER_RPM");
        }
        ScriptValue scriptValue37 = ScriptValue.of((double)(d5 * scriptValue36.asNum()));
        builder.val("su", scriptValue37);
        ScriptContext scriptContext25 = builder.peek();
        ScriptValue scriptValue38 = scriptContext25.getClassInstance("Machine");
        if (scriptValue38 == ScriptValue.NULL) {
            scriptValue38 = scriptContext25.getVar("Machine");
        }
        if (scriptValue38 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue39 = scriptContext25.getClassInstance("su");
            if (scriptValue39 == ScriptValue.NULL) {
                scriptValue39 = scriptContext25.getVar("su");
            }
            arrayList4.add(ScriptValue.of((double)(-scriptValue39.asNum())));
            v11 = ScriptFormula.memberCall((ScriptValue)scriptValue38, (String)"report_su", arrayList4, (ScriptContext)scriptContext25);
        } else {
            v11 = ScriptValue.NULL;
        }
    }
}
