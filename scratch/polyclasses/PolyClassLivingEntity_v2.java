/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassLivingEntity_v2
extends PolyClassEntity {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler0 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler2 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.TypedPropertyHandler tp$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.PropertyHandler p$17;
    private static volatile PolyType.TypedPropertyHandler tp$18;
    private static volatile PolyType.PropertyHandler p$19;
    private static volatile PolyType.TypedPropertyHandler tp$20;
    private static volatile PolyType.PropertyHandler p$21;
    private static volatile PolyType.PropertyHandler p$22;
    private static volatile PolyType.TypedPropertyHandler tp$23;
    private static volatile PolyType.PropertyHandler p$24;
    private static volatile PolyType.PropertyHandler p$25;
    private static volatile PolyType.TypedPropertyHandler tp$26;
    private static volatile PolyType.PropertyHandler p$27;
    private static volatile PolyType.TypedPropertyHandler tp$28;
    private static volatile PolyType.PropertyHandler p$29;
    private static volatile PolyType.TypedPropertyHandler tp$30;
    private static volatile PolyType.PropertyHandler p$31;
    private static volatile PolyType.TypedPropertyHandler tp$32;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"damage", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"damage");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"freeze", (String)"D:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"freeze");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"set_health", (String)"D:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"set_health");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"give_item", (String)"R:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"give_item");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"fire", (String)"D:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"fire");
        h$10 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"kill", (String)":Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"kill");
        h$12 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"LivingEntity", (String)"set_equipment", (String)"SR:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"LivingEntity", (String)"set_equipment");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"is_on_fire");
        tp$15 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"is_on_fire", (String)"Z");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"main_hand");
        p$17 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"max_health");
        tp$18 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"max_health", (String)"D");
        p$19 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"armor");
        tp$20 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"armor", (String)"D");
        p$21 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"off_hand");
        p$22 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"health");
        tp$23 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"health", (String)"D");
        p$24 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"equipment");
        p$25 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"is_dead");
        tp$26 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"is_dead", (String)"Z");
        p$27 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"last_damage");
        tp$28 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"last_damage", (String)"S");
        p$29 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"fire_ticks");
        tp$30 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"fire_ticks", (String)"D");
        p$31 = PolyClassRuntime.resolvePropertyHandler((String)"LivingEntity", (String)"frozen_ticks");
        tp$32 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"LivingEntity", (String)"frozen_ticks", (String)"D");
    }

    public boolean tm$0_damage(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"damage", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_damage(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"damage", (Object)this.instance, (List)list);
    }

    public boolean tm$2_freeze(double d) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"freeze", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$3_freeze(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"freeze", (Object)this.instance, (List)list);
    }

    public boolean tm$4_set_health(double d) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"set_health", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$5_set_health(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"set_health", (Object)this.instance, (List)list);
    }

    public boolean tm$6_give_item(ScriptValue scriptValue) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"give_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$7_give_item(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"give_item", (Object)this.instance, (List)list);
    }

    public boolean tm$8_fire(double d) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"fire", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$9_fire(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"fire", (Object)this.instance, (List)list);
    }

    public boolean tm$10_kill() {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"kill", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$11_kill(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"kill", (Object)this.instance, (List)list);
    }

    public boolean tm$12_set_equipment(String string, ScriptValue scriptValue) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"LivingEntity", (String)"set_equipment", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue}).asBool();
    }

    public ScriptValue um$13_set_equipment(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"LivingEntity", (String)"set_equipment", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$14_is_on_fire() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"is_on_fire", (Object)this.instance);
    }

    public boolean tg$15_is_on_fire() {
        if (tp$15 != null) {
            return (Boolean)tp$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"is_on_fire", (Object)this.instance).asBool();
    }

    public ScriptValue pg$16_main_hand() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"main_hand", (Object)this.instance);
    }

    public ScriptValue pg$17_max_health() {
        if (p$17 != null) {
            return p$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"max_health", (Object)this.instance);
    }

    public double tg$18_max_health() {
        if (tp$18 != null) {
            return (Double)tp$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"max_health", (Object)this.instance).asNum();
    }

    public ScriptValue pg$19_armor() {
        if (p$19 != null) {
            return p$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"armor", (Object)this.instance);
    }

    public double tg$20_armor() {
        if (tp$20 != null) {
            return (Double)tp$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"armor", (Object)this.instance).asNum();
    }

    public ScriptValue pg$21_off_hand() {
        if (p$21 != null) {
            return p$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"off_hand", (Object)this.instance);
    }

    public ScriptValue pg$22_health() {
        if (p$22 != null) {
            return p$22.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"health", (Object)this.instance);
    }

    public double tg$23_health() {
        if (tp$23 != null) {
            return (Double)tp$23.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"health", (Object)this.instance).asNum();
    }

    public ScriptValue pg$24_equipment() {
        if (p$24 != null) {
            return p$24.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"equipment", (Object)this.instance);
    }

    public ScriptValue pg$25_is_dead() {
        if (p$25 != null) {
            return p$25.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"is_dead", (Object)this.instance);
    }

    public boolean tg$26_is_dead() {
        if (tp$26 != null) {
            return (Boolean)tp$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"is_dead", (Object)this.instance).asBool();
    }

    public ScriptValue pg$27_last_damage() {
        if (p$27 != null) {
            return p$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"last_damage", (Object)this.instance);
    }

    public String tg$28_last_damage() {
        if (tp$28 != null) {
            return (String)tp$28.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"last_damage", (Object)this.instance).asStr();
    }

    public ScriptValue pg$29_fire_ticks() {
        if (p$29 != null) {
            return p$29.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"fire_ticks", (Object)this.instance);
    }

    public double tg$30_fire_ticks() {
        if (tp$30 != null) {
            return (Double)tp$30.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"fire_ticks", (Object)this.instance).asNum();
    }

    public ScriptValue pg$31_frozen_ticks() {
        if (p$31 != null) {
            return p$31.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"frozen_ticks", (Object)this.instance);
    }

    public double tg$32_frozen_ticks() {
        if (tp$32 != null) {
            return (Double)tp$32.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"LivingEntity", (String)"frozen_ticks", (Object)this.instance).asNum();
    }

    public PolyClassLivingEntity_v2(Object object) {
        super(object);
    }

    public static PolyClassLivingEntity_v2 of(Object object) {
        return new PolyClassLivingEntity_v2(object);
    }

    public static PolyClassLivingEntity_v2 ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("LivingEntity")) {
            return new PolyClassLivingEntity_v2(object);
        }
        return null;
    }
}
