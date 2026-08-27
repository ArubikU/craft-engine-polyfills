/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.shafts;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class Cogwheel {
    public static ScriptValue _off(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"dx"));
        arrayList.add(scriptContext.getClassOrVar("dx"));
        arrayList.add(ScriptValue.of((String)"dy"));
        arrayList.add(scriptContext.getClassOrVar("dy"));
        arrayList.add(ScriptValue.of((String)"dz"));
        arrayList.add(scriptContext.getClassOrVar("dz"));
        return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue _block(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2;
            Object object3;
            Object object4;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("o");
            if (scriptValue3 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"dx"));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = object4;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("o");
            if (scriptValue5 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"dy"));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = object3;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("o");
            if (scriptValue7 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"dz"));
                v2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = scriptValue2 = ScriptValue.NULL;
            }
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$68_block_at(scriptValue4.asNum(), scriptValue6.asNum(), scriptValue2.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue6);
                arrayList.add(scriptValue2);
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue _comp(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("axis"), (String)"x")) {
            Object object2;
            ScriptValue scriptValue = scriptContext.getClassOrVar("o");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"dx"));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            return object2;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("axis"), (String)"y")) {
            Object object3;
            ScriptValue scriptValue = scriptContext.getClassOrVar("o");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"dy"));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            return object3;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("o");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"dz"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue _isSmall(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("id"));
        arrayList.add(ScriptValue.of((String)"cogwheel_small"));
        return ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue _isLarge(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("id"));
        arrayList.add(ScriptValue.of((String)"cogwheel_large"));
        return ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue _perpOffsets(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx", ScriptValue.of((double)1.0));
            builder2.val("dy", ScriptValue.of((double)0.0));
            builder2.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)(-1.0)));
            builder3.val("dy", ScriptValue.of((double)0.0));
            builder3.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx", ScriptValue.of((double)0.0));
            builder4.val("dy", ScriptValue.of((double)0.0));
            builder4.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx", ScriptValue.of((double)0.0));
            builder5.val("dy", ScriptValue.of((double)0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder5));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx", ScriptValue.of((double)0.0));
            builder6.val("dy", ScriptValue.of((double)1.0));
            builder6.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx", ScriptValue.of((double)0.0));
            builder7.val("dy", ScriptValue.of((double)(-1.0)));
            builder7.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx", ScriptValue.of((double)0.0));
            builder8.val("dy", ScriptValue.of((double)0.0));
            builder8.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx", ScriptValue.of((double)0.0));
            builder9.val("dy", ScriptValue.of((double)0.0));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
        builder10.val("dx", ScriptValue.of((double)1.0));
        builder10.val("dy", ScriptValue.of((double)0.0));
        builder10.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder10));
        ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
        builder11.val("dx", ScriptValue.of((double)(-1.0)));
        builder11.val("dy", ScriptValue.of((double)0.0));
        builder11.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder11));
        ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
        builder12.val("dx", ScriptValue.of((double)0.0));
        builder12.val("dy", ScriptValue.of((double)1.0));
        builder12.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder12));
        ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
        builder13.val("dx", ScriptValue.of((double)0.0));
        builder13.val("dy", ScriptValue.of((double)(-1.0)));
        builder13.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder13));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _planeDiagOffsets(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx", ScriptValue.of((double)1.0));
            builder2.val("dy", ScriptValue.of((double)0.0));
            builder2.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)1.0));
            builder3.val("dy", ScriptValue.of((double)0.0));
            builder3.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx", ScriptValue.of((double)(-1.0)));
            builder4.val("dy", ScriptValue.of((double)0.0));
            builder4.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx", ScriptValue.of((double)(-1.0)));
            builder5.val("dy", ScriptValue.of((double)0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder5));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx", ScriptValue.of((double)0.0));
            builder6.val("dy", ScriptValue.of((double)1.0));
            builder6.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx", ScriptValue.of((double)0.0));
            builder7.val("dy", ScriptValue.of((double)1.0));
            builder7.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx", ScriptValue.of((double)0.0));
            builder8.val("dy", ScriptValue.of((double)(-1.0)));
            builder8.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx", ScriptValue.of((double)0.0));
            builder9.val("dy", ScriptValue.of((double)(-1.0)));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
        builder10.val("dx", ScriptValue.of((double)1.0));
        builder10.val("dy", ScriptValue.of((double)1.0));
        builder10.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder10));
        ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
        builder11.val("dx", ScriptValue.of((double)1.0));
        builder11.val("dy", ScriptValue.of((double)(-1.0)));
        builder11.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder11));
        ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
        builder12.val("dx", ScriptValue.of((double)(-1.0)));
        builder12.val("dy", ScriptValue.of((double)1.0));
        builder12.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder12));
        ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
        builder13.val("dx", ScriptValue.of((double)(-1.0)));
        builder13.val("dy", ScriptValue.of((double)(-1.0)));
        builder13.val("dz", ScriptValue.of((double)0.0));
        arrayList.add(Cogwheel._off(builder13));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _crossDiagOffsets(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx", ScriptValue.of((double)1.0));
            builder2.val("dy", ScriptValue.of((double)1.0));
            builder2.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)1.0));
            builder3.val("dy", ScriptValue.of((double)(-1.0)));
            builder3.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx", ScriptValue.of((double)(-1.0)));
            builder4.val("dy", ScriptValue.of((double)1.0));
            builder4.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx", ScriptValue.of((double)(-1.0)));
            builder5.val("dy", ScriptValue.of((double)(-1.0)));
            builder5.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder5));
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx", ScriptValue.of((double)0.0));
            builder6.val("dy", ScriptValue.of((double)1.0));
            builder6.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx", ScriptValue.of((double)0.0));
            builder7.val("dy", ScriptValue.of((double)1.0));
            builder7.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx", ScriptValue.of((double)0.0));
            builder8.val("dy", ScriptValue.of((double)(-1.0)));
            builder8.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx", ScriptValue.of((double)0.0));
            builder9.val("dy", ScriptValue.of((double)(-1.0)));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("dx", ScriptValue.of((double)1.0));
            builder10.val("dy", ScriptValue.of((double)1.0));
            builder10.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder10));
            ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
            builder11.val("dx", ScriptValue.of((double)1.0));
            builder11.val("dy", ScriptValue.of((double)(-1.0)));
            builder11.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder11));
            ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
            builder12.val("dx", ScriptValue.of((double)(-1.0)));
            builder12.val("dy", ScriptValue.of((double)1.0));
            builder12.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder12));
            ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
            builder13.val("dx", ScriptValue.of((double)(-1.0)));
            builder13.val("dy", ScriptValue.of((double)(-1.0)));
            builder13.val("dz", ScriptValue.of((double)0.0));
            arrayList.add(Cogwheel._off(builder13));
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("dx", ScriptValue.of((double)1.0));
            builder14.val("dy", ScriptValue.of((double)0.0));
            builder14.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder14));
            ScriptContext.Builder builder15 = ScriptContext.builder().copyFrom(scriptContext);
            builder15.val("dx", ScriptValue.of((double)1.0));
            builder15.val("dy", ScriptValue.of((double)0.0));
            builder15.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder15));
            ScriptContext.Builder builder16 = ScriptContext.builder().copyFrom(scriptContext);
            builder16.val("dx", ScriptValue.of((double)(-1.0)));
            builder16.val("dy", ScriptValue.of((double)0.0));
            builder16.val("dz", ScriptValue.of((double)1.0));
            arrayList.add(Cogwheel._off(builder16));
            ScriptContext.Builder builder17 = ScriptContext.builder().copyFrom(scriptContext);
            builder17.val("dx", ScriptValue.of((double)(-1.0)));
            builder17.val("dy", ScriptValue.of((double)0.0));
            builder17.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder17));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder18 = ScriptContext.builder().copyFrom(scriptContext);
        builder18.val("dx", ScriptValue.of((double)1.0));
        builder18.val("dy", ScriptValue.of((double)0.0));
        builder18.val("dz", ScriptValue.of((double)1.0));
        arrayList.add(Cogwheel._off(builder18));
        ScriptContext.Builder builder19 = ScriptContext.builder().copyFrom(scriptContext);
        builder19.val("dx", ScriptValue.of((double)1.0));
        builder19.val("dy", ScriptValue.of((double)0.0));
        builder19.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder19));
        ScriptContext.Builder builder20 = ScriptContext.builder().copyFrom(scriptContext);
        builder20.val("dx", ScriptValue.of((double)(-1.0)));
        builder20.val("dy", ScriptValue.of((double)0.0));
        builder20.val("dz", ScriptValue.of((double)1.0));
        arrayList.add(Cogwheel._off(builder20));
        ScriptContext.Builder builder21 = ScriptContext.builder().copyFrom(scriptContext);
        builder21.val("dx", ScriptValue.of((double)(-1.0)));
        builder21.val("dy", ScriptValue.of((double)0.0));
        builder21.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder21));
        ScriptContext.Builder builder22 = ScriptContext.builder().copyFrom(scriptContext);
        builder22.val("dx", ScriptValue.of((double)0.0));
        builder22.val("dy", ScriptValue.of((double)1.0));
        builder22.val("dz", ScriptValue.of((double)1.0));
        arrayList.add(Cogwheel._off(builder22));
        ScriptContext.Builder builder23 = ScriptContext.builder().copyFrom(scriptContext);
        builder23.val("dx", ScriptValue.of((double)0.0));
        builder23.val("dy", ScriptValue.of((double)1.0));
        builder23.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder23));
        ScriptContext.Builder builder24 = ScriptContext.builder().copyFrom(scriptContext);
        builder24.val("dx", ScriptValue.of((double)0.0));
        builder24.val("dy", ScriptValue.of((double)(-1.0)));
        builder24.val("dz", ScriptValue.of((double)1.0));
        arrayList.add(Cogwheel._off(builder24));
        ScriptContext.Builder builder25 = ScriptContext.builder().copyFrom(scriptContext);
        builder25.val("dx", ScriptValue.of((double)0.0));
        builder25.val("dy", ScriptValue.of((double)(-1.0)));
        builder25.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder25));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _mesh(ScriptContext.Builder builder) {
        List list;
        List list2;
        List list3;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("axis", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object3;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("rpm");
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$106_set_rpm_output(scriptValue4.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$155_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("net", scriptValue6);
        if (scriptContext.getBool("is_large") ^ true && (list3 = ScriptProgram.resolveForRows((String)"_perp_offsets()", (ScriptContext)scriptContext, (int)1)) != null) {
            for (ScriptValue[] scriptValueArray : list3) {
                Object object4;
                Object object5;
                builder.val("o", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("o", scriptContext.getClassOrVar("o"));
                ScriptValue scriptValue7 = Cogwheel._block(builder2);
                builder.val("nb", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("nb");
                if (scriptValue8 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"axis"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (ScriptFormula.valuesEqual((ScriptValue)object5, (ScriptValue)scriptValue2) ^ true) continue;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("nb");
                builder3.val("id", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL));
                if (!Cogwheel._isSmall(builder3).asBool()) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue7);
                    arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm"))));
                    arrayList.add(scriptValue6);
                    if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        object4 = new PolyClassMachine_v2(object6).um$29_relay_to(arrayList);
                        continue;
                    }
                    object4 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object4 = ScriptValue.NULL;
            }
        }
        if ((list2 = ScriptProgram.resolveForRows((String)"_plane_diag_offsets()", (ScriptContext)scriptContext, (int)1)) != null) {
            for (ScriptValue[] scriptValueArray : list2) {
                Object object7;
                Object object8;
                builder.val("o", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("o", scriptContext.getClassOrVar("o"));
                ScriptValue scriptValue11 = Cogwheel._block(builder4);
                builder.val("nb", scriptValue11);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("nb");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"axis"));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                if (ScriptFormula.valuesEqual((ScriptValue)object8, (ScriptValue)scriptValue2) ^ true) continue;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("nb");
                ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue14);
                if (scriptContext.getBool("is_large")) {
                    Object object9;
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    builder5.val("id", scriptValue14);
                    if (!Cogwheel._isSmall(builder5).asBool()) continue;
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue15 != ScriptValue.NULL) {
                        ScriptValue.Obj obj5;
                        Object object10;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue11);
                        arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 2.0)));
                        arrayList.add(scriptValue6);
                        if (scriptValue15 instanceof ScriptValue.Obj && (object10 = (obj5 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object10 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                            object9 = new PolyClassMachine_v2(object10).um$29_relay_to(arrayList);
                            continue;
                        }
                        object9 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object9 = ScriptValue.NULL;
                    continue;
                }
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("id", scriptValue14);
                if (!Cogwheel._isLarge(builder6).asBool()) continue;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object11;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue11);
                    arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 0.5)));
                    arrayList.add(scriptValue6);
                    if (scriptValue16 instanceof ScriptValue.Obj && (object11 = (obj6 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object11 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                        object7 = new PolyClassMachine_v2(object11).um$29_relay_to(arrayList);
                        continue;
                    }
                    object7 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object7 = ScriptValue.NULL;
            }
        }
        if (scriptContext.getBool("is_large") && (list = ScriptProgram.resolveForRows((String)"_cross_diag_offsets()", (ScriptContext)scriptContext, (int)1)) != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object12;
                Object object13;
                builder.val("o", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("o", scriptContext.getClassOrVar("o"));
                ScriptValue scriptValue17 = Cogwheel._block(builder7);
                builder.val("nb", scriptValue17);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("nb");
                if (scriptValue18 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"axis"));
                    object13 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
                } else {
                    object13 = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object13;
                builder.val("their_axis", scriptValue19);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)scriptValue2)) continue;
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("nb");
                builder8.val("id", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL));
                if (Cogwheel._isLarge(builder8).asBool() ^ true) continue;
                ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
                builder9.val("o", scriptContext.getClassOrVar("o"));
                builder9.val("axis", scriptValue2);
                ScriptValue scriptValue21 = Cogwheel._comp(builder9);
                builder.val("mine", scriptValue21);
                ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
                builder10.val("o", scriptContext.getClassOrVar("o"));
                builder10.val("axis", scriptValue19);
                ScriptValue scriptValue22 = Cogwheel._comp(builder10);
                builder.val("theirs", scriptValue22);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)0.0)) || ScriptFormula.valuesEqual((ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)0.0))) continue;
                double d = (double)(scriptValue21.asNum() > 0.0 ? 1 : 0) != (double)(scriptValue22.asNum() > 0.0 ? 1 : 0) ? -1.0 : 1.0;
                ScriptValue scriptValue23 = ScriptValue.of((double)d);
                builder.val("sign", scriptValue23);
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                if (scriptValue24 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object14;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue17);
                    arrayList.add(ScriptValue.of((double)(scriptContext.getNum("rpm") * d)));
                    arrayList.add(scriptValue6);
                    if (scriptValue24 instanceof ScriptValue.Obj && (object14 = (obj7 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object14 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                        object12 = new PolyClassMachine_v2(object14).um$29_relay_to(arrayList);
                        continue;
                    }
                    object12 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object12 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue tickLarge(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large", ScriptValue.of((boolean)true));
        Cogwheel._mesh(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue tickSmall(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large", ScriptValue.of((boolean)false));
        Cogwheel._mesh(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
