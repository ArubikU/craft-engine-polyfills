/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("o");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dx")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("o");
            ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dy")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("o");
            Object object3 = scriptValue2 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "dz")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$68_block_at(scriptValue4.asNum(), scriptValue6.asNum(), scriptValue2.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue4, (ScriptValue)scriptValue6, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("axis", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("rpm");
            if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine2.tm$106_set_rpm_output(scriptValue5.asNum()));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0)))) {
            return ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine3 != null ? polyClassMachine3.pg$183_rpm_network() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("net", scriptValue6);
        if (scriptContext.getBool("is_large") ^ true) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            List list = ScriptProgram.elementsOf((ScriptValue)Cogwheel._perpOffsets(builder2));
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("nb");
            if (list != null) {
                for (ScriptValue scriptValue8 : list) {
                    Object object;
                    builder.val("o", scriptValue8);
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("o", scriptValue8);
                    ScriptValue scriptValue9 = Cogwheel._block(builder3);
                    builder.val("nb", scriptValue9);
                    scriptValue7 = scriptValue9;
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("nb");
                    if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue3) ^ true) continue;
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                    builder4.val("id", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    if (!Cogwheel._isSmall(builder4).asBool()) continue;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object2;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue7);
                        arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm"))));
                        arrayList.add(scriptValue6);
                        if (scriptValue11 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            object = new PolyClassMachine(object2).um$29_relay_to(arrayList);
                            continue;
                        }
                        object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object = ScriptValue.NULL;
                }
            }
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        List list = ScriptProgram.elementsOf((ScriptValue)Cogwheel._planeDiagOffsets(builder5));
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("nb");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("nid");
        if (list != null) {
            for (ScriptValue scriptValue14 : list) {
                Object object;
                builder.val("o", scriptValue14);
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("o", scriptValue14);
                ScriptValue scriptValue15 = Cogwheel._block(builder6);
                builder.val("nb", scriptValue15);
                scriptValue12 = scriptValue15;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("nb");
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue3) ^ true) continue;
                ScriptValue scriptValue17 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue17);
                scriptValue13 = scriptValue17;
                if (scriptContext.getBool("is_large")) {
                    Object object3;
                    ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                    builder7.val("id", scriptValue13);
                    if (!Cogwheel._isSmall(builder7).asBool()) continue;
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue18 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object4;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue12);
                        arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 2.0)));
                        arrayList.add(scriptValue6);
                        if (scriptValue18 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            object3 = new PolyClassMachine(object4).um$29_relay_to(arrayList);
                            continue;
                        }
                        object3 = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
                        continue;
                    }
                    object3 = ScriptValue.NULL;
                    continue;
                }
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("id", scriptValue13);
                if (!Cogwheel._isLarge(builder8).asBool()) continue;
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue12);
                    arrayList.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 0.5)));
                    arrayList.add(scriptValue6);
                    if (scriptValue19 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        object = new PolyClassMachine(object5).um$29_relay_to(arrayList);
                        continue;
                    }
                    object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
        if (scriptContext.getBool("is_large")) {
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            List list2 = ScriptProgram.elementsOf((ScriptValue)Cogwheel._crossDiagOffsets(builder9));
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("mine");
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("theirs");
            ScriptValue scriptValue22 = scriptValue12;
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("sign");
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("their_axis");
            if (list2 != null) {
                for (ScriptValue scriptValue25 : list2) {
                    Object object;
                    builder.val("o", scriptValue25);
                    ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
                    builder10.val("o", scriptValue25);
                    ScriptValue scriptValue26 = Cogwheel._block(builder10);
                    builder.val("nb", scriptValue26);
                    scriptValue22 = scriptValue26;
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("nb");
                    ScriptValue scriptValue28 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Cogwheel.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("their_axis", scriptValue28);
                    scriptValue24 = scriptValue28;
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue24, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue24, (ScriptValue)scriptValue3)) continue;
                    ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
                    builder11.val("id", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    if (Cogwheel._isLarge(builder11).asBool() ^ true) continue;
                    ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
                    builder12.val("o", scriptValue25);
                    builder12.val("axis", scriptValue3);
                    ScriptValue scriptValue29 = Cogwheel._comp(builder12);
                    builder.val("mine", scriptValue29);
                    scriptValue20 = scriptValue29;
                    ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
                    builder13.val("o", scriptValue25);
                    builder13.val("axis", scriptValue24);
                    ScriptValue scriptValue30 = Cogwheel._comp(builder13);
                    builder.val("theirs", scriptValue30);
                    scriptValue21 = scriptValue30;
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0))) || ScriptFormula.valuesEqual((ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Cogwheel.class, 0.0)))) continue;
                    double d = (double)(scriptValue20.asNum() > 0.0 ? 1 : 0) != (double)(scriptValue21.asNum() > 0.0 ? 1 : 0) ? -1.0 : 1.0;
                    ScriptValue scriptValue31 = ScriptValue.of((double)d);
                    builder.val("sign", scriptValue31);
                    scriptValue23 = scriptValue31;
                    ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue32 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object6;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue22);
                        arrayList.add(ScriptValue.of((double)(scriptContext.getNum("rpm") * scriptValue23.asNum())));
                        arrayList.add(scriptValue6);
                        if (scriptValue32 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            object = new PolyClassMachine(object6).um$29_relay_to(arrayList);
                            continue;
                        }
                        object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue32, arrayList, (ScriptContext)scriptContext);
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
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
