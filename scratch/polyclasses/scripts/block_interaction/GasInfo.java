/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class GasInfo {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _getTank(ScriptContext.Builder builder) {
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$138_gas_tanks() : PolyDispatch.bootstrapGet("memberGet", "gas_tanks", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("tanks", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("tanks");
        Object object2 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (object2.asNum() <= 0.0) {
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tanks");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", GasInfo.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue item(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "minecraft:glass_bottle");
    }

    public static ScriptValue name(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = GasInfo._getTank(builder2);
        builder.val("tank", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "<gray>No gas tank");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tank");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "contents_name"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("gas_name", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "<gray>Gas Tank (empty)");
        }
        return ScriptValue.of((String)("<aqua>" + scriptValue3.asStr()));
    }

    public static ScriptValue lore(ScriptContext.Builder builder) {
        double d;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = GasInfo._getTank(builder2);
        builder.val("tank", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "<gray>No gas tank"));
            return new ScriptValue.Array(arrayList);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tank");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "level"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("level", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("tank");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", GasInfo.class, "capacity"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("cap", scriptValue5);
        double d2 = scriptValue5.asNum() > 0.0 ? Math.floor(((d = scriptValue5.asNum()) == 0.0 ? 0.0 : scriptValue3.asNum() / d) * 100.0) : 0.0;
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        builder.val("pct", scriptValue6);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)("<gray>Level: <white>" + scriptValue3.asStr() + " / " + scriptValue5.asStr() + " mB")));
        arrayList.add(ScriptValue.of((String)("<gray>Fill: <yellow>" + ScriptFormula.numToStr((double)d2) + "%")));
        return new ScriptValue.Array(arrayList);
    }
}
