/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMob;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassAnimal
extends PolyClassMob {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.TypedPropertyHandler tp$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.TypedPropertyHandler tp$9;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_in_love");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Animal", (String)"is_in_love", (String)"Z");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"in_love_time");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Animal", (String)"in_love_time", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"age");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Animal", (String)"age", (String)"D");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_animal");
        tp$7 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Animal", (String)"is_animal", (String)"Z");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_baby");
        tp$9 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Animal", (String)"is_baby", (String)"Z");
    }

    public ScriptValue pg$0_is_in_love() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_in_love", (Object)this.instance);
    }

    public boolean tg$1_is_in_love() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_in_love", (Object)this.instance).asBool();
    }

    public ScriptValue pg$2_in_love_time() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"in_love_time", (Object)this.instance);
    }

    public double tg$3_in_love_time() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"in_love_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_age() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"age", (Object)this.instance);
    }

    public double tg$5_age() {
        if (tp$5 != null) {
            return (Double)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"age", (Object)this.instance).asNum();
    }

    public ScriptValue pg$6_is_animal() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_animal", (Object)this.instance);
    }

    public boolean tg$7_is_animal() {
        if (tp$7 != null) {
            return (Boolean)tp$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_animal", (Object)this.instance).asBool();
    }

    public ScriptValue pg$8_is_baby() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_baby", (Object)this.instance);
    }

    public boolean tg$9_is_baby() {
        if (tp$9 != null) {
            return (Boolean)tp$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_baby", (Object)this.instance).asBool();
    }

    public PolyClassAnimal(Object object) {
        super(object);
    }

    public static PolyClassAnimal of(Object object) {
        return new PolyClassAnimal(object);
    }

    public static PolyClassAnimal ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Animal")) {
            return new PolyClassAnimal(object);
        }
        return null;
    }
}
