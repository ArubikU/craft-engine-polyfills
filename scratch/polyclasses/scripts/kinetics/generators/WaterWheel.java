/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
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
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                ScriptValue scriptValue3;
                ScriptValue scriptValue4;
                ScriptValue scriptValue5;
                Object object;
                builder.val("o", scriptValue2);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("o");
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dx")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dx", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("o");
                ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dy")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dy", scriptValue9);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("o");
                ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WaterWheel.class, "dz")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dz", scriptValue11);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object2;
                    ScriptValue scriptValue13 = scriptValue7;
                    ScriptValue scriptValue14 = scriptValue9;
                    ScriptValue scriptValue15 = scriptValue11;
                    if (scriptValue12 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                        object = polyClassMachine_v3.tm$68_block_at(scriptValue13.asNum(), scriptValue14.asNum(), scriptValue15.asNum());
                    } else {
                        object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = object;
                builder.val("b", scriptValue16);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("b");
                if ((scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_fluid", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("b");
                ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fluid_flow", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("flow", scriptValue19);
                ScriptValue scriptValue20 = scriptContext.getStr("axis").equals("x") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : ((scriptValue5 = scriptContext.getClassOrVar("flow")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fx", scriptValue20);
                ScriptValue scriptValue21 = scriptContext.getStr("axis").equals("y") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : ((scriptValue4 = scriptContext.getClassOrVar("flow")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fy", scriptValue21);
                ScriptValue scriptValue22 = scriptContext.getStr("axis").equals("z") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 0.0)) : ((scriptValue3 = scriptContext.getClassOrVar("flow")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("fz", scriptValue22);
                double d2 = Math.sqrt(scriptValue20.asNum() * scriptValue20.asNum() + scriptValue21.asNum() * scriptValue21.asNum() + scriptValue22.asNum() * scriptValue22.asNum());
                ScriptValue scriptValue23 = ScriptValue.of((double)d2);
                builder.val("flen", scriptValue23);
                if (d2 <= 0.0) continue;
                double d3 = d2;
                double d4 = d3 == 0.0 ? 0.0 : scriptContext.getNum("fx") / d3;
                ScriptValue scriptValue24 = ScriptValue.of((double)d4);
                builder.val("fx", scriptValue24);
                double d5 = d2;
                double d6 = d5 == 0.0 ? 0.0 : scriptContext.getNum("fy") / d5;
                ScriptValue scriptValue25 = ScriptValue.of((double)d6);
                builder.val("fy", scriptValue25);
                double d7 = d2;
                double d8 = d7 == 0.0 ? 0.0 : scriptContext.getNum("fz") / d7;
                ScriptValue scriptValue26 = ScriptValue.of((double)d8);
                builder.val("fz", scriptValue26);
                double d9 = Math.sqrt(scriptValue7.asNum() * scriptValue7.asNum() + scriptValue9.asNum() * scriptValue9.asNum() + scriptValue11.asNum() * scriptValue11.asNum());
                ScriptValue scriptValue27 = ScriptValue.of((double)d9);
                builder.val("nlen", scriptValue27);
                if (d9 <= 0.0) continue;
                double d10 = d9;
                double d11 = d10 == 0.0 ? 0.0 : scriptValue7.asNum() / d10;
                ScriptValue scriptValue28 = ScriptValue.of((double)d11);
                builder.val("nx", scriptValue28);
                double d12 = d9;
                double d13 = d12 == 0.0 ? 0.0 : scriptValue9.asNum() / d12;
                ScriptValue scriptValue29 = ScriptValue.of((double)d13);
                builder.val("ny", scriptValue29);
                double d14 = d9;
                double d15 = d14 == 0.0 ? 0.0 : scriptValue11.asNum() / d14;
                ScriptValue scriptValue30 = ScriptValue.of((double)d15);
                builder.val("nz", scriptValue30);
                if (scriptContext.getStr("axis").equals("x")) {
                    double d16 = d11;
                    ScriptValue scriptValue31 = ScriptValue.of((double)d16);
                    builder.val("px", scriptValue31);
                    double d17 = -d15;
                    ScriptValue scriptValue32 = ScriptValue.of((double)d17);
                    builder.val("py", scriptValue32);
                    double d18 = d13;
                    ScriptValue scriptValue33 = ScriptValue.of((double)d18);
                    builder.val("pz", scriptValue33);
                } else if (scriptContext.getStr("axis").equals("y")) {
                    double d19 = d15;
                    ScriptValue scriptValue34 = ScriptValue.of((double)d19);
                    builder.val("px", scriptValue34);
                    double d20 = d13;
                    ScriptValue scriptValue35 = ScriptValue.of((double)d20);
                    builder.val("py", scriptValue35);
                    double d21 = -d11;
                    ScriptValue scriptValue36 = ScriptValue.of((double)d21);
                    builder.val("pz", scriptValue36);
                } else {
                    double d22 = -d13;
                    ScriptValue scriptValue37 = ScriptValue.of((double)d22);
                    builder.val("px", scriptValue37);
                    double d23 = d11;
                    ScriptValue scriptValue38 = ScriptValue.of((double)d23);
                    builder.val("py", scriptValue38);
                    double d24 = d15;
                    ScriptValue scriptValue39 = ScriptValue.of((double)d24);
                    builder.val("pz", scriptValue39);
                }
                double d25 = d4 * scriptContext.getNum("px") + d6 * scriptContext.getNum("py") + d8 * scriptContext.getNum("pz");
                ScriptValue scriptValue40 = ScriptValue.of((double)d25);
                builder.val("dot", scriptValue40);
                if (d25 > 0.5) {
                    ScriptValue scriptValue41 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("score"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0)));
                    builder.val("score", scriptValue41);
                }
                if (!(d25 < -0.5)) continue;
                double d26 = scriptContext.getNum("score") - 1.0;
                ScriptValue scriptValue42 = ScriptValue.of((double)d26);
                builder.val("score", scriptValue42);
            }
        }
        return scriptContext.getClassOrVar("score");
    }

    public static ScriptValue _run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v3.pg$127_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue6);
        arrayList.add(ScriptValue.of((double)(-1.0)));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WaterWheel.class, 1.0));
        double d = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext).asNum() * scriptValue7.asNum();
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        builder.val("rpm_out", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d2 = d;
            if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v32 = new PolyClassMachine_v3(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v32.tm$106_set_rpm_output(d2));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        if (d == 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                double d3 = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v33 = new PolyClassMachine_v3(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v33.tm$56_report_su(d3));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                double d4 = -scriptValue8.asNum();
                if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v34 = new PolyClassMachine_v3(object);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v34.tm$56_report_su(d4));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                }
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
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
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
