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
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassMachine;
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
public final class WaterWheel {
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
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dx"));
        arrayList.add(scriptContext.getClassOrVar("dx"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dy"));
        arrayList.add(scriptContext.getClassOrVar("dy"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dz"));
        arrayList.add(scriptContext.getClassOrVar("dz"));
        return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue _smallOffsets(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("axis").equals("y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder2.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder2.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)(-1.0)));
            builder3.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder3.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder4.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder4.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder5.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder5));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("axis").equals("x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder6.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder6.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder7.val("dy", ScriptValue.of((double)(-1.0)));
            builder7.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder8.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder8.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder9.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder9.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder9));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
        builder10.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder10.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder10.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder10));
        ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
        builder11.val("dx", ScriptValue.of((double)(-1.0)));
        builder11.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder11.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder11));
        ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
        builder12.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder12.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder12.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder12));
        ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
        builder13.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder13.val("dy", ScriptValue.of((double)(-1.0)));
        builder13.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder13));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _largeOffsets(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("axis").equals("y")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder2.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder2.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("dx", ScriptValue.of((double)(-1.0)));
            builder3.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder3.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder3));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder4.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder4.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder5.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder5.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder5));
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder6.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder6.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder6));
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder7.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder7.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder7));
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder8.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder8.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder8));
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("dx", ScriptValue.of((double)(-2.0)));
            builder9.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder9.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder9));
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("dx", ScriptValue.of((double)(-2.0)));
            builder10.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder10.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder10));
            ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
            builder11.val("dx", ScriptValue.of((double)(-2.0)));
            builder11.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder11.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder11));
            ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
            builder12.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder12.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder12.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder12));
            ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
            builder13.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder13.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder13.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder13));
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("dx", ScriptValue.of((double)(-1.0)));
            builder14.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder14.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder14));
            ScriptContext.Builder builder15 = ScriptContext.builder().copyFrom(scriptContext);
            builder15.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder15.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder15.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder15));
            ScriptContext.Builder builder16 = ScriptContext.builder().copyFrom(scriptContext);
            builder16.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder16.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder16.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder16));
            ScriptContext.Builder builder17 = ScriptContext.builder().copyFrom(scriptContext);
            builder17.val("dx", ScriptValue.of((double)(-1.0)));
            builder17.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder17.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder17));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("axis").equals("x")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder18 = ScriptContext.builder().copyFrom(scriptContext);
            builder18.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder18.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder18.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder18));
            ScriptContext.Builder builder19 = ScriptContext.builder().copyFrom(scriptContext);
            builder19.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder19.val("dy", ScriptValue.of((double)(-1.0)));
            builder19.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder19));
            ScriptContext.Builder builder20 = ScriptContext.builder().copyFrom(scriptContext);
            builder20.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder20.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder20.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder20));
            ScriptContext.Builder builder21 = ScriptContext.builder().copyFrom(scriptContext);
            builder21.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder21.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder21.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder21));
            ScriptContext.Builder builder22 = ScriptContext.builder().copyFrom(scriptContext);
            builder22.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder22.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder22.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder22));
            ScriptContext.Builder builder23 = ScriptContext.builder().copyFrom(scriptContext);
            builder23.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder23.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder23.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder23));
            ScriptContext.Builder builder24 = ScriptContext.builder().copyFrom(scriptContext);
            builder24.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder24.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            builder24.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder24));
            ScriptContext.Builder builder25 = ScriptContext.builder().copyFrom(scriptContext);
            builder25.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder25.val("dy", ScriptValue.of((double)(-2.0)));
            builder25.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            arrayList.add(WaterWheel._off(builder25));
            ScriptContext.Builder builder26 = ScriptContext.builder().copyFrom(scriptContext);
            builder26.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder26.val("dy", ScriptValue.of((double)(-2.0)));
            builder26.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            arrayList.add(WaterWheel._off(builder26));
            ScriptContext.Builder builder27 = ScriptContext.builder().copyFrom(scriptContext);
            builder27.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder27.val("dy", ScriptValue.of((double)(-2.0)));
            builder27.val("dz", ScriptValue.of((double)(-1.0)));
            arrayList.add(WaterWheel._off(builder27));
            ScriptContext.Builder builder28 = ScriptContext.builder().copyFrom(scriptContext);
            builder28.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder28.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder28.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder28));
            ScriptContext.Builder builder29 = ScriptContext.builder().copyFrom(scriptContext);
            builder29.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder29.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder29.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder29));
            ScriptContext.Builder builder30 = ScriptContext.builder().copyFrom(scriptContext);
            builder30.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder30.val("dy", ScriptValue.of((double)(-1.0)));
            builder30.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
            arrayList.add(WaterWheel._off(builder30));
            ScriptContext.Builder builder31 = ScriptContext.builder().copyFrom(scriptContext);
            builder31.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder31.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder31.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder31));
            ScriptContext.Builder builder32 = ScriptContext.builder().copyFrom(scriptContext);
            builder32.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder32.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
            builder32.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder32));
            ScriptContext.Builder builder33 = ScriptContext.builder().copyFrom(scriptContext);
            builder33.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
            builder33.val("dy", ScriptValue.of((double)(-1.0)));
            builder33.val("dz", ScriptValue.of((double)(-2.0)));
            arrayList.add(WaterWheel._off(builder33));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder34 = ScriptContext.builder().copyFrom(scriptContext);
        builder34.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder34.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder34.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder34));
        ScriptContext.Builder builder35 = ScriptContext.builder().copyFrom(scriptContext);
        builder35.val("dx", ScriptValue.of((double)(-1.0)));
        builder35.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder35.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder35));
        ScriptContext.Builder builder36 = ScriptContext.builder().copyFrom(scriptContext);
        builder36.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder36.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder36.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder36));
        ScriptContext.Builder builder37 = ScriptContext.builder().copyFrom(scriptContext);
        builder37.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder37.val("dy", ScriptValue.of((double)(-1.0)));
        builder37.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder37));
        ScriptContext.Builder builder38 = ScriptContext.builder().copyFrom(scriptContext);
        builder38.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder38.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder38.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder38));
        ScriptContext.Builder builder39 = ScriptContext.builder().copyFrom(scriptContext);
        builder39.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder39.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder39.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder39));
        ScriptContext.Builder builder40 = ScriptContext.builder().copyFrom(scriptContext);
        builder40.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder40.val("dy", ScriptValue.of((double)(-1.0)));
        builder40.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder40));
        ScriptContext.Builder builder41 = ScriptContext.builder().copyFrom(scriptContext);
        builder41.val("dx", ScriptValue.of((double)(-2.0)));
        builder41.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder41.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder41));
        ScriptContext.Builder builder42 = ScriptContext.builder().copyFrom(scriptContext);
        builder42.val("dx", ScriptValue.of((double)(-2.0)));
        builder42.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder42.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder42));
        ScriptContext.Builder builder43 = ScriptContext.builder().copyFrom(scriptContext);
        builder43.val("dx", ScriptValue.of((double)(-2.0)));
        builder43.val("dy", ScriptValue.of((double)(-1.0)));
        builder43.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder43));
        ScriptContext.Builder builder44 = ScriptContext.builder().copyFrom(scriptContext);
        builder44.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder44.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder44.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder44));
        ScriptContext.Builder builder45 = ScriptContext.builder().copyFrom(scriptContext);
        builder45.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder45.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder45.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder45));
        ScriptContext.Builder builder46 = ScriptContext.builder().copyFrom(scriptContext);
        builder46.val("dx", ScriptValue.of((double)(-1.0)));
        builder46.val("dy",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 2.0));
        builder46.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder46));
        ScriptContext.Builder builder47 = ScriptContext.builder().copyFrom(scriptContext);
        builder47.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        builder47.val("dy", ScriptValue.of((double)(-2.0)));
        builder47.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder47));
        ScriptContext.Builder builder48 = ScriptContext.builder().copyFrom(scriptContext);
        builder48.val("dx",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        builder48.val("dy", ScriptValue.of((double)(-2.0)));
        builder48.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder48));
        ScriptContext.Builder builder49 = ScriptContext.builder().copyFrom(scriptContext);
        builder49.val("dx", ScriptValue.of((double)(-1.0)));
        builder49.val("dy", ScriptValue.of((double)(-2.0)));
        builder49.val("dz",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0));
        arrayList.add(WaterWheel._off(builder49));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _flowScore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("score", scriptValue);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("offsets"));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("flen");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("px");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("dot");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("py");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("nx");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("pz");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ny");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("nz");
        ScriptValue scriptValue11 = ScriptValue.of((double)d);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("fx");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("fy");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("dx");
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("fz");
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("nlen");
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("dy");
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("dz");
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("flow");
        if (list != null) {
            for (ScriptValue scriptValue20 : list) {
                Object object;
                builder.val("o", scriptValue20);
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dx")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dx", scriptValue21);
                scriptValue14 = scriptValue21;
                ScriptValue scriptValue22 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dy")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dy", scriptValue22);
                scriptValue17 = scriptValue22;
                ScriptValue scriptValue23 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dz")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dz", scriptValue23);
                scriptValue18 = scriptValue23;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                if (scriptValue24 != ScriptValue.NULL) {
                    ScriptValue scriptValue25 = scriptValue14;
                    ScriptValue scriptValue26 = scriptValue17;
                    ScriptValue scriptValue27 = scriptValue18;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue24);
                    object = polyClassMachine != null ? polyClassMachine.tm$68_block_at(scriptValue25.asNum(), scriptValue26.asNum(), scriptValue27.asNum()) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue28 = object;
                builder.val("b", scriptValue28);
                scriptValue3 = scriptValue28;
                if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_fluid", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue29 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fluid_flow", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("flow", scriptValue29);
                scriptValue19 = scriptValue29;
                ScriptValue scriptValue30 = scriptContext.getStr("axis").equals("x") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : (scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fx", scriptValue30);
                scriptValue12 = scriptValue30;
                ScriptValue scriptValue31 = scriptContext.getStr("axis").equals("y") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : (scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fy", scriptValue31);
                scriptValue13 = scriptValue31;
                ScriptValue scriptValue32 = scriptContext.getStr("axis").equals("z") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : (scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fz", scriptValue32);
                scriptValue15 = scriptValue32;
                double d2 = Math.sqrt(scriptValue12.asNum() * scriptValue12.asNum() + scriptValue13.asNum() * scriptValue13.asNum() + scriptValue15.asNum() * scriptValue15.asNum());
                ScriptValue scriptValue33 = ScriptValue.of((double)d2);
                builder.val("flen", scriptValue33);
                scriptValue2 = scriptValue33;
                if (scriptValue2.asNum() <= 0.0) continue;
                double d3 = scriptValue2.asNum();
                double d4 = d3 == 0.0 ? 0.0 : scriptValue12.asNum() / d3;
                ScriptValue scriptValue34 = ScriptValue.of((double)d4);
                builder.val("fx", scriptValue34);
                scriptValue12 = scriptValue34;
                double d5 = scriptValue2.asNum();
                double d6 = d5 == 0.0 ? 0.0 : scriptValue13.asNum() / d5;
                ScriptValue scriptValue35 = ScriptValue.of((double)d6);
                builder.val("fy", scriptValue35);
                scriptValue13 = scriptValue35;
                double d7 = scriptValue2.asNum();
                double d8 = d7 == 0.0 ? 0.0 : scriptValue15.asNum() / d7;
                ScriptValue scriptValue36 = ScriptValue.of((double)d8);
                builder.val("fz", scriptValue36);
                scriptValue15 = scriptValue36;
                double d9 = Math.sqrt(scriptValue14.asNum() * scriptValue14.asNum() + scriptValue17.asNum() * scriptValue17.asNum() + scriptValue18.asNum() * scriptValue18.asNum());
                ScriptValue scriptValue37 = ScriptValue.of((double)d9);
                builder.val("nlen", scriptValue37);
                scriptValue16 = scriptValue37;
                if (scriptValue16.asNum() <= 0.0) continue;
                double d10 = scriptValue16.asNum();
                double d11 = d10 == 0.0 ? 0.0 : scriptValue14.asNum() / d10;
                ScriptValue scriptValue38 = ScriptValue.of((double)d11);
                builder.val("nx", scriptValue38);
                scriptValue7 = scriptValue38;
                double d12 = scriptValue16.asNum();
                double d13 = d12 == 0.0 ? 0.0 : scriptValue17.asNum() / d12;
                ScriptValue scriptValue39 = ScriptValue.of((double)d13);
                builder.val("ny", scriptValue39);
                scriptValue9 = scriptValue39;
                double d14 = scriptValue16.asNum();
                double d15 = d14 == 0.0 ? 0.0 : scriptValue18.asNum() / d14;
                ScriptValue scriptValue40 = ScriptValue.of((double)d15);
                builder.val("nz", scriptValue40);
                scriptValue10 = scriptValue40;
                if (scriptContext.getStr("axis").equals("x")) {
                    ScriptValue scriptValue41 = scriptValue7;
                    builder.val("px", scriptValue41);
                    scriptValue4 = scriptValue41;
                    double d16 = -scriptValue10.asNum();
                    ScriptValue scriptValue42 = ScriptValue.of((double)d16);
                    builder.val("py", scriptValue42);
                    scriptValue6 = scriptValue42;
                    ScriptValue scriptValue43 = scriptValue9;
                    builder.val("pz", scriptValue43);
                    scriptValue8 = scriptValue43;
                } else if (scriptContext.getStr("axis").equals("y")) {
                    ScriptValue scriptValue44 = scriptValue10;
                    builder.val("px", scriptValue44);
                    scriptValue4 = scriptValue44;
                    ScriptValue scriptValue45 = scriptValue9;
                    builder.val("py", scriptValue45);
                    scriptValue6 = scriptValue45;
                    double d17 = -scriptValue7.asNum();
                    ScriptValue scriptValue46 = ScriptValue.of((double)d17);
                    builder.val("pz", scriptValue46);
                    scriptValue8 = scriptValue46;
                } else {
                    double d18 = -scriptValue9.asNum();
                    ScriptValue scriptValue47 = ScriptValue.of((double)d18);
                    builder.val("px", scriptValue47);
                    scriptValue4 = scriptValue47;
                    ScriptValue scriptValue48 = scriptValue7;
                    builder.val("py", scriptValue48);
                    scriptValue6 = scriptValue48;
                    ScriptValue scriptValue49 = scriptValue10;
                    builder.val("pz", scriptValue49);
                    scriptValue8 = scriptValue49;
                }
                double d19 = scriptValue12.asNum() * scriptContext.getNum("px") + scriptValue13.asNum() * scriptContext.getNum("py") + scriptValue15.asNum() * scriptContext.getNum("pz");
                ScriptValue scriptValue50 = ScriptValue.of((double)d19);
                builder.val("dot", scriptValue50);
                scriptValue5 = scriptValue50;
                if (scriptValue5.asNum() > 0.5) {
                    ScriptValue scriptValue51 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0)));
                    builder.val("score", scriptValue51);
                    scriptValue11 = scriptValue51;
                }
                if (!(scriptValue5.asNum() < -0.5)) continue;
                double d20 = scriptContext.getNum("score") - 1.0;
                ScriptValue scriptValue52 = ScriptValue.of((double)d20);
                builder.val("score", scriptValue52);
                scriptValue11 = scriptValue52;
            }
        }
        return scriptValue11;
    }

    public static ScriptValue _run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("axis", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "z");
            builder.val("axis", scriptValue4);
        }
        if (scriptContext.getBool("is_large")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("axis", scriptContext.getClassOrVar("axis"));
            scriptValue = WaterWheel._largeOffsets(builder2);
        } else {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("axis", scriptContext.getClassOrVar("axis"));
            scriptValue = WaterWheel._smallOffsets(builder3);
        }
        ScriptValue scriptValue5 = scriptValue;
        builder.val("offsets", scriptValue5);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("axis", scriptContext.getClassOrVar("axis"));
        builder4.val("offsets", scriptValue5);
        ScriptValue scriptValue6 = WaterWheel._flowScore(builder4);
        builder.val("score", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getBool("is_large") ? scriptContext.getClassOrVar("LARGE_SPEED") : scriptContext.getClassOrVar("SMALL_SPEED");
        builder.val("speed", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getBool("is_large") ? scriptContext.getClassOrVar("LARGE_CAPACITY") : scriptContext.getClassOrVar("SMALL_CAPACITY");
        builder.val("capacity", scriptValue8);
        double d = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0)), (ScriptContext)scriptContext).asNum() * scriptValue7.asNum();
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        builder.val("rpm_out", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
        if (scriptValue10 != ScriptValue.NULL) {
            double d2 = d;
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
            v1 = polyClassMachine2 != null ? ScriptValue.of((boolean)polyClassMachine2.tm$106_set_rpm_output(d2)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        if (d == 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                double d3 = 0.0;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                v2 = polyClassMachine3 != null ? ScriptValue.of((boolean)polyClassMachine3.tm$56_report_su(d3)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                double d4 = -scriptValue8.asNum();
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                v3 = polyClassMachine4 != null ? ScriptValue.of((boolean)polyClassMachine4.tm$56_report_su(d4)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue tickSmall(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", WaterWheel.class, 0));
        WaterWheel._run(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue tickLarge(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("is_large",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", WaterWheel.class, 1));
        WaterWheel._run(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0))) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "false"));
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 8.0;
        ScriptValue scriptValue = ScriptValue.of((double)8.0);
        builder.val("SMALL_SPEED", scriptValue);
        double d2 = 4.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)4.0);
        builder.val("LARGE_SPEED", scriptValue2);
        double d3 = 16.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)16.0);
        builder.val("SMALL_CAPACITY", scriptValue3);
        double d4 = 32.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)32.0);
        builder.val("LARGE_CAPACITY", scriptValue4);
        FILE_SCOPE = builder.build();
    }
}
