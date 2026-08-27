/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.kinetics.shafts;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
public final class Cogwheel {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _off(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dx"));
        arrayList.add(scriptContext.getClassOrVar("dx"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dy"));
        arrayList.add(scriptContext.getClassOrVar("dy"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dz"));
        arrayList.add(scriptContext.getClassOrVar("dz"));
        return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue _block(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("o");
            ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dx")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("o");
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dy")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("o");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dz")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$68_block_at(scriptValue3.asNum(), scriptValue5.asNum(), scriptValue7.asNum()) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue3, (ScriptValue)scriptValue5, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue _comp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("axis").equals("x")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("o");
            return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dx")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if (scriptContext.getStr("axis").equals("y")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("o");
            return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dy")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("o");
        return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dz")), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue _isSmall(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptContext.getClassOrVar("id"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "cogwheel_small")), (ScriptContext)scriptContext);
    }

    public static ScriptValue _isLarge(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptContext.getClassOrVar("id"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "cogwheel_large")), (ScriptContext)scriptContext);
    }

    public static ScriptValue _perpOffsets(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder2.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder2.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)(-1.0)));
            builder3.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder3.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder4.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder4.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder5.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder5));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder6.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder6.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder7.val("dy", ScriptValue.of((double)(-1.0)));
            builder7.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder8.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder8.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder9.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
        builder10.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder10.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder10.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder10));
        ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
        builder11.val("dx", ScriptValue.of((double)(-1.0)));
        builder11.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder11.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder11));
        ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
        builder12.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder12.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder12.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder12));
        ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
        builder13.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder13.val("dy", ScriptValue.of((double)(-1.0)));
        builder13.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder13));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _planeDiagOffsets(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder2.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder2.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder3.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder3.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx", ScriptValue.of((double)(-1.0)));
            builder4.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder4.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx", ScriptValue.of((double)(-1.0)));
            builder5.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder5));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder6.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder6.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder7.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder7.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder8.val("dy", ScriptValue.of((double)(-1.0)));
            builder8.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder9.val("dy", ScriptValue.of((double)(-1.0)));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
        builder10.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder10.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder10.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder10));
        ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
        builder11.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder11.val("dy", ScriptValue.of((double)(-1.0)));
        builder11.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder11));
        ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
        builder12.val("dx", ScriptValue.of((double)(-1.0)));
        builder12.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder12.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder12));
        ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
        builder13.val("dx", ScriptValue.of((double)(-1.0)));
        builder13.val("dy", ScriptValue.of((double)(-1.0)));
        builder13.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        arrayList.add(Cogwheel._off(builder13));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _crossDiagOffsets(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("a", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder2.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder2.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder3.val("dy", ScriptValue.of((double)(-1.0)));
            builder3.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx", ScriptValue.of((double)(-1.0)));
            builder4.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder4.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx", ScriptValue.of((double)(-1.0)));
            builder5.val("dy", ScriptValue.of((double)(-1.0)));
            builder5.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder5));
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder6.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder6.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder7.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder7.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder8.val("dy", ScriptValue.of((double)(-1.0)));
            builder8.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder9.val("dy", ScriptValue.of((double)(-1.0)));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder10.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder10.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder10));
            ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
            builder11.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder11.val("dy", ScriptValue.of((double)(-1.0)));
            builder11.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder11));
            ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
            builder12.val("dx", ScriptValue.of((double)(-1.0)));
            builder12.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder12.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder12));
            ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
            builder13.val("dx", ScriptValue.of((double)(-1.0)));
            builder13.val("dy", ScriptValue.of((double)(-1.0)));
            builder13.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            arrayList.add(Cogwheel._off(builder13));
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder14.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder14.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder14));
            ScriptContext.Builder builder15 = ScriptContext.builder().copyFrom(scriptContext);
            builder15.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            builder15.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder15.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder15));
            ScriptContext.Builder builder16 = ScriptContext.builder().copyFrom(scriptContext);
            builder16.val("dx", ScriptValue.of((double)(-1.0)));
            builder16.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder16.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
            arrayList.add(Cogwheel._off(builder16));
            ScriptContext.Builder builder17 = ScriptContext.builder().copyFrom(scriptContext);
            builder17.val("dx", ScriptValue.of((double)(-1.0)));
            builder17.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
            builder17.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(Cogwheel._off(builder17));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder18 = ScriptContext.builder().copyFrom(scriptContext);
        builder18.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder18.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder18.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        arrayList.add(Cogwheel._off(builder18));
        ScriptContext.Builder builder19 = ScriptContext.builder().copyFrom(scriptContext);
        builder19.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder19.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder19.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder19));
        ScriptContext.Builder builder20 = ScriptContext.builder().copyFrom(scriptContext);
        builder20.val("dx", ScriptValue.of((double)(-1.0)));
        builder20.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder20.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        arrayList.add(Cogwheel._off(builder20));
        ScriptContext.Builder builder21 = ScriptContext.builder().copyFrom(scriptContext);
        builder21.val("dx", ScriptValue.of((double)(-1.0)));
        builder21.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder21.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder21));
        ScriptContext.Builder builder22 = ScriptContext.builder().copyFrom(scriptContext);
        builder22.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder22.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder22.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        arrayList.add(Cogwheel._off(builder22));
        ScriptContext.Builder builder23 = ScriptContext.builder().copyFrom(scriptContext);
        builder23.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder23.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        builder23.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder23));
        ScriptContext.Builder builder24 = ScriptContext.builder().copyFrom(scriptContext);
        builder24.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder24.val("dy", ScriptValue.of((double)(-1.0)));
        builder24.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 1.0));
        arrayList.add(Cogwheel._off(builder24));
        ScriptContext.Builder builder25 = ScriptContext.builder().copyFrom(scriptContext);
        builder25.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0));
        builder25.val("dy", ScriptValue.of((double)(-1.0)));
        builder25.val("dz", ScriptValue.of((double)(-1.0)));
        arrayList.add(Cogwheel._off(builder25));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _mesh(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("axis", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("rpm");
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
            v0 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$106_set_rpm_output(scriptValue4.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0)))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v3.pg$183_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("net", scriptValue5);
        if (scriptContext.getBool("is_large") ^ true) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            List list = ScriptProgram.elementsOf((ScriptValue)Cogwheel._perpOffsets(builder2));
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("nb");
            if (list != null) {
                for (ScriptValue scriptValue7 : list) {
                    Object object;
                    builder.val("o", scriptValue7);
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("o", scriptValue7);
                    ScriptValue scriptValue8 = Cogwheel._block(builder3);
                    builder.val("nb", scriptValue8);
                    scriptValue6 = scriptValue8;
                    if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue2) ^ true) continue;
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                    builder4.val("id", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    if (!Cogwheel._isSmall(builder4).asBool()) continue;
                    if (scriptValue3 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue6);
                        arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm"))));
                        arrayList.add(scriptValue5);
                        PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                        if (polyClassMachine_v34 != null) {
                            object = polyClassMachine_v34.um$29_relay_to(arrayList);
                            continue;
                        }
                        object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object = ScriptValue.NULL;
                }
            }
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        List list = ScriptProgram.elementsOf((ScriptValue)Cogwheel._planeDiagOffsets(builder5));
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("nb");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("nid");
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object;
                builder.val("o", scriptValue11);
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("o", scriptValue11);
                ScriptValue scriptValue12 = Cogwheel._block(builder6);
                builder.val("nb", scriptValue12);
                scriptValue9 = scriptValue12;
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue2) ^ true) continue;
                ScriptValue scriptValue13 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue13);
                scriptValue10 = scriptValue13;
                if (scriptContext.getBool("is_large")) {
                    Object object2;
                    ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                    builder7.val("id", scriptValue10);
                    if (!Cogwheel._isSmall(builder7).asBool()) continue;
                    if (scriptValue3 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue9);
                        arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 2.0)));
                        arrayList.add(scriptValue5);
                        PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                        if (polyClassMachine_v35 != null) {
                            object2 = polyClassMachine_v35.um$29_relay_to(arrayList);
                            continue;
                        }
                        object2 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object2 = ScriptValue.NULL;
                    continue;
                }
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("id", scriptValue10);
                if (!Cogwheel._isLarge(builder8).asBool()) continue;
                if (scriptValue3 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue9);
                    arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 0.5)));
                    arrayList.add(scriptValue5);
                    PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    if (polyClassMachine_v36 != null) {
                        object = polyClassMachine_v36.um$29_relay_to(arrayList);
                        continue;
                    }
                    object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
        if (scriptContext.getBool("is_large")) {
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            List list2 = ScriptProgram.elementsOf((ScriptValue)Cogwheel._crossDiagOffsets(builder9));
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("mine");
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("theirs");
            ScriptValue scriptValue16 = scriptValue9;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("sign");
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("their_axis");
            if (list2 != null) {
                for (ScriptValue scriptValue19 : list2) {
                    Object object;
                    builder.val("o", scriptValue19);
                    ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
                    builder10.val("o", scriptValue19);
                    ScriptValue scriptValue20 = Cogwheel._block(builder10);
                    builder.val("nb", scriptValue20);
                    scriptValue16 = scriptValue20;
                    ScriptValue scriptValue21 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("their_axis", scriptValue21);
                    scriptValue18 = scriptValue21;
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptValue2)) continue;
                    ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
                    builder11.val("id", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    if (Cogwheel._isLarge(builder11).asBool() ^ true) continue;
                    ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
                    builder12.val("o", scriptValue19);
                    builder12.val("axis", scriptValue2);
                    ScriptValue scriptValue22 = Cogwheel._comp(builder12);
                    builder.val("mine", scriptValue22);
                    scriptValue14 = scriptValue22;
                    ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
                    builder13.val("o", scriptValue19);
                    builder13.val("axis", scriptValue18);
                    ScriptValue scriptValue23 = Cogwheel._comp(builder13);
                    builder.val("theirs", scriptValue23);
                    scriptValue15 = scriptValue23;
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0))) || ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0)))) continue;
                    double d = (double)(scriptValue14.asNum() > 0.0 ? 1 : 0) != (double)(scriptValue15.asNum() > 0.0 ? 1 : 0) ? -1.0 : 1.0;
                    ScriptValue scriptValue24 = ScriptValue.of((double)d);
                    builder.val("sign", scriptValue24);
                    scriptValue17 = scriptValue24;
                    if (scriptValue3 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue16);
                        arrayList.add(ScriptValue.of((double)(scriptContext.getNum("rpm") * scriptValue17.asNum())));
                        arrayList.add(scriptValue5);
                        PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                        if (polyClassMachine_v37 != null) {
                            object = polyClassMachine_v37.um$29_relay_to(arrayList);
                            continue;
                        }
                        object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object = ScriptValue.NULL;
                }
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue tickLarge(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Cogwheel.class, 1));
        Cogwheel._mesh(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue tickSmall(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Cogwheel.class, 0));
        Cogwheel._mesh(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0))) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "false"));
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
