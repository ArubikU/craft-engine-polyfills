/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 */
package dev.arubik.craftengine.contraption.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.phys.AABB;

public final class ShulkerBoxFit {
    public static final double MIN_CUBE_SIZE = 0.0625;

    private ShulkerBoxFit() {
    }

    public static List<Cube> fit(List<AABB> boxes, int budget) {
        return ShulkerBoxFit.fit(boxes, budget, false);
    }

    public static List<Cube> fit(List<AABB> boxes, int budget, boolean fillBudget) {
        if (boxes == null || boxes.isEmpty() || budget < 1) {
            return List.of();
        }
        if (budget == 1) {
            return List.of(ShulkerBoxFit.coarseCube(ShulkerBoxFit.overallBounds(boxes)));
        }
        List<AABB> targets = boxes.size() <= budget ? boxes : List.of(ShulkerBoxFit.overallBounds(boxes));
        int[] perBox = ShulkerBoxFit.allocate(targets, budget);
        ArrayList<Cube> out = new ArrayList<Cube>(budget);
        for (int i = 0; i < targets.size(); ++i) {
            out.addAll(ShulkerBoxFit.fitBox(targets.get(i), perBox[i], fillBudget));
        }
        return out;
    }

    private static Cube coarseCube(AABB b) {
        double height = b.maxY - b.minY;
        double side = height <= 0.0 || height > 1.0 ? 1.0 : Math.max(height, 0.0625);
        double cx = (b.minX + b.maxX) / 2.0;
        double cz = (b.minZ + b.maxZ) / 2.0;
        return new Cube(cx - side / 2.0, b.minY, cz - side / 2.0, side);
    }

    private static AABB overallBounds(List<AABB> boxes) {
        AABB out = boxes.get(0);
        for (int i = 1; i < boxes.size(); ++i) {
            out = out.minmax(boxes.get(i));
        }
        return out;
    }

    private static int[] allocate(List<AABB> boxes, int budget) {
        int i2;
        int k = boxes.size();
        int[] out = new int[k];
        Arrays.fill(out, 1);
        int remaining = budget - k;
        if (remaining <= 0) {
            return out;
        }
        double[] vol = new double[k];
        double total = 0.0;
        for (int i3 = 0; i3 < k; ++i3) {
            vol[i3] = ShulkerBoxFit.volumeOf(boxes.get(i3));
            total += vol[i3];
        }
        if (total <= 0.0) {
            return out;
        }
        double[] remainder = new double[k];
        int used = 0;
        int i4 = 0;
        while (i4 < k) {
            double want = (double)remaining * vol[i4] / total;
            int whole = (int)Math.floor(want);
            remainder[i4] = want - (double)whole;
            int n = i4++;
            out[n] = out[n] + whole;
            used += whole;
        }
        Integer[] order = new Integer[k];
        for (i2 = 0; i2 < k; ++i2) {
            order[i2] = i2;
        }
        Arrays.sort(order, Comparator.<Integer>comparingDouble(i -> remainder[i]).reversed());
        for (i2 = 0; i2 < remaining - used; ++i2) {
            int n = order[i2 % k];
            out[n] = out[n] + 1;
        }
        return out;
    }

    private static double volumeOf(AABB b) {
        return Math.max(b.maxX - b.minX, 0.0625) * Math.max(b.maxY - b.minY, 0.0625) * Math.max(b.maxZ - b.minZ, 0.0625);
    }

    private static List<Cube> fitBox(AABB b, int budget, boolean fillBudget) {
        double s;
        double sx = Math.max(b.maxX - b.minX, 0.0625);
        double sy = Math.max(b.maxY - b.minY, 0.0625);
        double sz = Math.max(b.maxZ - b.minZ, 0.0625);
        double lo = fillBudget ? 0.0625 : Math.max(Math.min(sx, Math.min(sy, sz)), 0.0625);
        double hi = Math.max(sx, Math.max(sy, sz));
        if (ShulkerBoxFit.gridCount(sx, sy, sz, lo) <= (long)budget) {
            s = lo;
        } else {
            for (int i = 0; i < 40; ++i) {
                double mid = (lo + hi) / 2.0;
                if (ShulkerBoxFit.gridCount(sx, sy, sz, mid) <= (long)budget) {
                    hi = mid;
                    continue;
                }
                lo = mid;
            }
            s = hi;
        }
        int nx = ShulkerBoxFit.axisCount(sx, s);
        int ny = ShulkerBoxFit.axisCount(sy, s);
        int nz = ShulkerBoxFit.axisCount(sz, s);
        double side = Math.max(0.0625, Math.max(sx / (double)nx, Math.max(sy / (double)ny, sz / (double)nz)));
        double[] xs = ShulkerBoxFit.axisPositions(b.minX, sx, nx, side);
        double[] ys = ShulkerBoxFit.axisPositions(b.minY, sy, ny, side);
        double[] zs = ShulkerBoxFit.axisPositions(b.minZ, sz, nz, side);
        ArrayList<Cube> out = new ArrayList<Cube>(nx * ny * nz);
        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    out.add(new Cube(x, y, z, side));
                }
            }
        }
        return out;
    }

    private static long gridCount(double sx, double sy, double sz, double s) {
        return (long)ShulkerBoxFit.axisCount(sx, s) * (long)ShulkerBoxFit.axisCount(sy, s) * (long)ShulkerBoxFit.axisCount(sz, s);
    }

    private static int axisCount(double size, double s) {
        return Math.max(1, (int)Math.ceil(size / s - 1.0E-9));
    }

    private static double[] axisPositions(double min, double size, int n, double side) {
        double[] out = new double[n];
        if (n == 1) {
            out[0] = min + (size - side) / 2.0;
            return out;
        }
        double step = (size - side) / (double)(n - 1);
        for (int i = 0; i < n; ++i) {
            out[i] = min + (double)i * step;
        }
        return out;
    }

    public record Cube(double x0, double y0, double z0, double size) {
        public double centerX() {
            return this.x0 + this.size / 2.0;
        }

        public double centerZ() {
            return this.z0 + this.size / 2.0;
        }
    }
}

