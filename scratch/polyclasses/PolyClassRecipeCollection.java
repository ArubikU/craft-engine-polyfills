/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypeCodec
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassRecipeCollection {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.TypeCodec c$0_r;
    private static volatile PolyType.MethodHandler m$1;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"RecipeCollection", (String)"for_input", (String)"S:L");
        c$0_r = PolyClassRuntime.resolveListCodec((String)"RecipeCollection", (String)"for_input", (int)-1);
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"RecipeCollection", (String)"for_input");
    }

    public ScriptValue tm$0_for_input(String string) {
        if (h$0 != null && c$0_r != null) {
            return c$0_r.encode(h$0.call(this.instance, (Object)string));
        }
        return PolyClassRuntime.genericCall((String)"RecipeCollection", (String)"for_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$1_for_input(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"RecipeCollection", (String)"for_input", (Object)this.instance, (List)list);
    }

    public PolyClassRecipeCollection(Object object) {
        this.instance = object;
    }

    public static PolyClassRecipeCollection of(Object object) {
        return new PolyClassRecipeCollection(object);
    }

    public static PolyClassRecipeCollection ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("RecipeCollection")) {
            return new PolyClassRecipeCollection(object);
        }
        return null;
    }

    public static PolyClassRecipeCollection ofVar(ScriptContext scriptContext, String string) {
        return PolyClassRecipeCollection.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
