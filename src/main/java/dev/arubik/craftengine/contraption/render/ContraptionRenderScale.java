/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.arubik.craftengine.contraption.render;

import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ContraptionRenderScale {
    private ContraptionRenderScale() {
    }

    public static void applyTo(List<Object> values, double scale) {
        ContraptionRenderScale.applyTo(values, scale, null);
    }

    public static void applyTo(List<Object> values, double scale, Quaternionf rotation) {
        if (scale == 1.0 && rotation == null) {
            return;
        }
        boolean scaled = scale != 1.0;
        float s = (float)scale;
        int scaleId = DisplayData.Scale.id();
        int translationId = DisplayData.Translation.id();
        int leftRotationId = DisplayData.LeftRotation.id();
        boolean sawScale = false;
        boolean sawLeftRotation = false;
        for (int i = 0; i < values.size(); ++i) {
            Vector3f t;
            Object object;
            Object object2 = values.get(i);
            if (!(object2 instanceof SynchedEntityData.DataValue)) continue;
            SynchedEntityData.DataValue dv = (SynchedEntityData.DataValue)object2;
            if (dv.id() == leftRotationId && (object = dv.value()) instanceof Quaternionfc) {
                Quaternionfc authored = (Quaternionfc)object;
                sawLeftRotation = true;
                if (rotation == null) continue;
                values.set(i, DisplayData.LeftRotation.createEntityData(new Quaternionf((Quaternionfc)rotation).mul(authored)));
                continue;
            }
            object = dv.value();
            if (!(object instanceof Vector3f)) continue;
            Vector3f v = (Vector3f)object;
            if (dv.id() == scaleId) {
                if (scaled) {
                    values.set(i, DisplayData.Scale.createEntityData(new Vector3f(v.x * s, v.y * s, v.z * s)));
                }
                sawScale = true;
                continue;
            }
            if (dv.id() != translationId) continue;
            Vector3f vector3f = t = scaled ? new Vector3f(v.x * s, v.y * s, v.z * s) : new Vector3f((Vector3fc)v);
            if (rotation != null) {
                rotation.transform(t);
            }
            values.set(i, DisplayData.Translation.createEntityData(t));
        }
        if (scaled && !sawScale) {
            DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
        }
        if (rotation != null && !sawLeftRotation) {
            DisplayData.LeftRotation.addEntityData(new Quaternionf((Quaternionfc)rotation), values);
        }
    }
}

