/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassLecternMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"LecternMetadata", (String)"has_book");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"LecternMetadata", (String)"book");
    }

    public ScriptValue pg$0_has_book() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LecternMetadata", (String)"has_book", (Object)this.instance);
    }

    public ScriptValue pg$1_book() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LecternMetadata", (String)"book", (Object)this.instance);
    }

    public PolyClassLecternMetadata(Object object) {
        super(object);
    }

    public static PolyClassLecternMetadata of(Object object) {
        return new PolyClassLecternMetadata(object);
    }

    public static PolyClassLecternMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("LecternMetadata")) {
            return new PolyClassLecternMetadata(object);
        }
        return null;
    }
}
