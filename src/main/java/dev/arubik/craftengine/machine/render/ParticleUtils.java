/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Particle
 *  org.bukkit.World
 */
package dev.arubik.craftengine.machine.render;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Particle;
import org.bukkit.World;

public final class ParticleUtils {
    private ParticleUtils() {
    }

    public static void emit(World world, Particle particle, double cx, double cy, double cz, double rx, double ry, double rz, Shape shape, Direction direction, int count, double speed, double dvx, double dvy, double dvz) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        if (shape == Shape.POINT && direction == Direction.RANDOM) {
            world.spawnParticle(particle, cx, cy, cz, count, rx, ry, rz, speed);
            return;
        }
        for (int k = 0; k < count; ++k) {
            double[] pos = ParticleUtils.sampleShape(rng, shape, rx, ry, rz);
            double lx = pos[0];
            double ly = pos[1];
            double lz = pos[2];
            double wx = cx + lx;
            double wy = cy + ly;
            double wz = cz + lz;
            double[] vel = ParticleUtils.computeDirection(rng, direction, lx, ly, lz, dvx, dvy, dvz, speed);
            if (direction == Direction.RANDOM) {
                world.spawnParticle(particle, wx, wy, wz, 0, rx, ry, rz, speed);
                continue;
            }
            world.spawnParticle(particle, wx, wy, wz, 0, vel[0], vel[1], vel[2], speed);
        }
    }

    public static void emitRandom(World world, Particle particle, double cx, double cy, double cz, int count, double sx, double sy, double sz, double speed) {
        world.spawnParticle(particle, cx, cy, cz, count, sx, sy, sz, speed);
    }

    public static double[] sampleShape(ThreadLocalRandom rng, Shape shape, double rx, double ry, double rz) {
        double[] dArray;
        switch (shape.ordinal()) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                double[] dArray2 = new double[3];
                dArray2[0] = 0.0;
                dArray2[1] = 0.0;
                dArray = dArray2;
                dArray2[2] = 0.0;
                break;
            }
            case 1: {
                double z;
                double y;
                double x;
                while ((x = rng.nextDouble(-1.0, 1.0)) * x + (y = rng.nextDouble(-1.0, 1.0)) * y + (z = rng.nextDouble(-1.0, 1.0)) * z > 1.0) {
                }
                double[] dArray3 = new double[3];
                dArray3[0] = x * rx;
                dArray3[1] = y * rx;
                dArray = dArray3;
                dArray3[2] = z * rx;
                break;
            }
            case 2: {
                double w;
                double v;
                double u;
                double len;
                while ((len = Math.sqrt((u = rng.nextGaussian()) * u + (v = rng.nextGaussian()) * v + (w = rng.nextGaussian()) * w)) < 1.0E-9) {
                }
                double[] dArray4 = new double[3];
                dArray4[0] = u / len * rx;
                dArray4[1] = v / len * rx;
                dArray = dArray4;
                dArray4[2] = w / len * rx;
                break;
            }
            case 3: {
                double w;
                double v;
                double u;
                double len;
                while ((len = Math.sqrt((u = rng.nextGaussian()) * u + (v = rng.nextGaussian()) * v + (w = rng.nextGaussian()) * w)) < 1.0E-9) {
                }
                double[] dArray5 = new double[3];
                dArray5[0] = u / len * rx;
                dArray5[1] = Math.abs(v / len) * rx;
                dArray = dArray5;
                dArray5[2] = w / len * rx;
                break;
            }
            case 4: {
                double r = rx * Math.sqrt(rng.nextDouble());
                double theta = rng.nextDouble(0.0, Math.PI * 2);
                double[] dArray6 = new double[3];
                dArray6[0] = Math.cos(theta) * r;
                dArray6[1] = 0.0;
                dArray = dArray6;
                dArray6[2] = Math.sin(theta) * r;
                break;
            }
            case 5: {
                double theta = rng.nextDouble(0.0, Math.PI * 2);
                double[] dArray7 = new double[3];
                dArray7[0] = Math.cos(theta) * rx;
                dArray7[1] = 0.0;
                dArray = dArray7;
                dArray7[2] = Math.sin(theta) * rx;
                break;
            }
            case 6: {
                double[] dArray8 = new double[3];
                dArray8[0] = rng.nextDouble(-rx, rx);
                dArray8[1] = rng.nextDouble(-ry, ry);
                dArray = dArray8;
                dArray8[2] = rng.nextDouble(-rz, rz);
                break;
            }
            case 7: {
                double bz;
                double by;
                double bx;
                double aX = ry * rz;
                double aY = rx * rz;
                double aZ = rx * ry;
                double total = 2.0 * (aX + aY + aZ);
                double pick = rng.nextDouble(total);
                if (pick < 2.0 * aX) {
                    bx = pick < aX ? -rx : rx;
                    by = rng.nextDouble(-ry, ry);
                    bz = rng.nextDouble(-rz, rz);
                } else if (pick < 2.0 * (aX + aY)) {
                    bx = rng.nextDouble(-rx, rx);
                    by = pick < 2.0 * aX + aY ? -ry : ry;
                    bz = rng.nextDouble(-rz, rz);
                } else {
                    bx = rng.nextDouble(-rx, rx);
                    by = rng.nextDouble(-ry, ry);
                    bz = pick < 2.0 * (aX + aY) + aZ ? -rz : rz;
                }
                double[] dArray9 = new double[3];
                dArray9[0] = bx;
                dArray9[1] = by;
                dArray = dArray9;
                dArray9[2] = bz;
                break;
            }
            case 8: {
                double r = rx * Math.sqrt(rng.nextDouble());
                double theta = rng.nextDouble(0.0, Math.PI * 2);
                double h = rng.nextDouble(-ry, ry);
                double[] dArray10 = new double[3];
                dArray10[0] = Math.cos(theta) * r;
                dArray10[1] = h;
                dArray = dArray10;
                dArray10[2] = Math.sin(theta) * r;
                break;
            }
            case 9: {
                double theta = rng.nextDouble(0.0, Math.PI * 2);
                double h = rng.nextDouble(-ry, ry);
                double[] dArray11 = new double[3];
                dArray11[0] = Math.cos(theta) * rx;
                dArray11[1] = h;
                dArray = dArray11;
                dArray11[2] = Math.sin(theta) * rx;
                break;
            }
        }
        return dArray;
    }

    public static double[] computeDirection(ThreadLocalRandom rng, Direction direction, double lx, double ly, double lz, double dvx, double dvy, double dvz, double speed) {
        double[] dArray;
        switch (direction.ordinal()) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                double[] dArray2 = new double[3];
                dArray2[0] = 0.0;
                dArray2[1] = 0.0;
                dArray = dArray2;
                dArray2[2] = 0.0;
                break;
            }
            case 1: {
                dArray = ParticleUtils.normalize(lx, ly, lz, speed);
                break;
            }
            case 2: {
                dArray = ParticleUtils.normalize(-lx, -ly, -lz, speed);
                break;
            }
            case 3: {
                double[] dArray3 = new double[3];
                dArray3[0] = 0.0;
                dArray3[1] = speed;
                dArray = dArray3;
                dArray3[2] = 0.0;
                break;
            }
            case 4: {
                double[] dArray4 = new double[3];
                dArray4[0] = 0.0;
                dArray4[1] = -speed;
                dArray = dArray4;
                dArray4[2] = 0.0;
                break;
            }
            case 5: {
                double radXZ = Math.sqrt(lx * lx + lz * lz);
                if (radXZ < 1.0E-9) {
                    double theta = rng.nextDouble(0.0, Math.PI * 2);
                    double[] dArray5 = new double[3];
                    dArray5[0] = Math.cos(theta) * speed;
                    dArray5[1] = 0.0;
                    dArray = dArray5;
                    dArray5[2] = Math.sin(theta) * speed;
                    break;
                }
                double[] dArray6 = new double[3];
                dArray6[0] = -lz / radXZ * speed;
                dArray6[1] = 0.0;
                dArray = dArray6;
                dArray6[2] = lx / radXZ * speed;
                break;
            }
            case 6: {
                dArray = ParticleUtils.normalize(dvx, dvy, dvz, speed);
            }
        }
        return dArray;
    }

    public static double[] normalize(double x, double y, double z, double scale) {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1.0E-9) {
            return new double[]{0.0, scale, 0.0};
        }
        return new double[]{x / len * scale, y / len * scale, z / len * scale};
    }

    public static enum Shape {
        POINT,
        SPHERE,
        SPHERE_SURFACE,
        HEMISPHERE,
        DISC,
        DISC_EDGE,
        BOX,
        BOX_SURFACE,
        CYLINDER,
        CYLINDER_SURFACE;


        public static Shape fromName(String name, Shape fallback) {
            if (name == null) {
                return fallback;
            }
            try {
                return Shape.valueOf(name.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException ignored) {
                return fallback;
            }
        }
    }

    public static enum Direction {
        RANDOM,
        OUTWARD,
        INWARD,
        UP,
        DOWN,
        TANGENT,
        CUSTOM;


        public static Direction fromName(String name, Direction fallback) {
            if (name == null) {
                return fallback;
            }
            try {
                return Direction.valueOf(name.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException ignored) {
                return fallback;
            }
        }
    }
}

