/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassContraptionContainer
extends PolyClassContainer {
    public static void refresh() {
    }

    public PolyClassContraptionContainer(Object object) {
        super(object);
    }

    public static PolyClassContraptionContainer of(Object object) {
        return new PolyClassContraptionContainer(object);
    }

    public static PolyClassContraptionContainer ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ContraptionContainer")) {
            return new PolyClassContraptionContainer(object);
        }
        return null;
    }

    public static PolyClassContraptionContainer ofVar(ScriptContext scriptContext, String string) {
        return PolyClassContraptionContainer.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
