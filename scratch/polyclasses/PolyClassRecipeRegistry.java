/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassRecipeRegistry {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"RecipeRegistry", (String)"smoker");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"RecipeRegistry", (String)"furnace");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"RecipeRegistry", (String)"stonecutter");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"RecipeRegistry", (String)"campfire");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"RecipeRegistry", (String)"blast_furnace");
    }

    public ScriptValue pg$0_smoker() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RecipeRegistry", (String)"smoker", (Object)this.instance);
    }

    public ScriptValue pg$1_furnace() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RecipeRegistry", (String)"furnace", (Object)this.instance);
    }

    public ScriptValue pg$2_stonecutter() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RecipeRegistry", (String)"stonecutter", (Object)this.instance);
    }

    public ScriptValue pg$3_campfire() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RecipeRegistry", (String)"campfire", (Object)this.instance);
    }

    public ScriptValue pg$4_blast_furnace() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RecipeRegistry", (String)"blast_furnace", (Object)this.instance);
    }

    public PolyClassRecipeRegistry(Object object) {
        this.instance = object;
    }

    public static PolyClassRecipeRegistry of(Object object) {
        return new PolyClassRecipeRegistry(object);
    }

    public static PolyClassRecipeRegistry ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("RecipeRegistry")) {
            return new PolyClassRecipeRegistry(object);
        }
        return null;
    }

    public static PolyClassRecipeRegistry ofVar(ScriptContext scriptContext, String string) {
        return PolyClassRecipeRegistry.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
