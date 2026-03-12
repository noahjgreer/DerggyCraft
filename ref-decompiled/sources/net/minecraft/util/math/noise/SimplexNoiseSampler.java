/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math.noise;

import java.util.Random;

public class SimplexNoiseSampler {
    private static int[][] grads = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    private int[] perm = new int[512];
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    public SimplexNoiseSampler() {
        this(new Random());
    }

    public SimplexNoiseSampler(Random random) {
        int n;
        this.offsetX = random.nextDouble() * 256.0;
        this.offsetY = random.nextDouble() * 256.0;
        this.offsetZ = random.nextDouble() * 256.0;
        for (n = 0; n < 256; ++n) {
            this.perm[n] = n;
        }
        for (n = 0; n < 256; ++n) {
            int n2 = random.nextInt(256 - n) + n;
            int n3 = this.perm[n];
            this.perm[n] = this.perm[n2];
            this.perm[n2] = n3;
            this.perm[n + 256] = this.perm[n];
        }
    }

    private static int fastFloor(double x) {
        return x > 0.0 ? (int)x : (int)x - 1;
    }

    private static double dot(int[] grad, double x, double y) {
        return (double)grad[0] * x + (double)grad[1] * y;
    }

    public void create(double[] map, double x, double z, int width, int depth, double d, double e, double f) {
        int n = 0;
        for (int i = 0; i < width; ++i) {
            double d2 = (x + (double)i) * d + this.offsetX;
            for (int j = 0; j < depth; ++j) {
                double d3;
                double d4;
                double d5;
                int n2;
                int n3;
                double d6;
                double d7;
                int n4;
                double d8;
                double d9 = (z + (double)j) * e + this.offsetY;
                double d10 = (d2 + d9) * F2;
                int n5 = SimplexNoiseSampler.fastFloor(d2 + d10);
                double d11 = (double)n5 - (d8 = (double)(n5 + (n4 = SimplexNoiseSampler.fastFloor(d9 + d10))) * G2);
                double d12 = d2 - d11;
                if (d12 > (d7 = d9 - (d6 = (double)n4 - d8))) {
                    n3 = 1;
                    n2 = 0;
                } else {
                    n3 = 0;
                    n2 = 1;
                }
                double d13 = d12 - (double)n3 + G2;
                double d14 = d7 - (double)n2 + G2;
                double d15 = d12 - 1.0 + 2.0 * G2;
                double d16 = d7 - 1.0 + 2.0 * G2;
                int n6 = n5 & 0xFF;
                int n7 = n4 & 0xFF;
                int n8 = this.perm[n6 + this.perm[n7]] % 12;
                int n9 = this.perm[n6 + n3 + this.perm[n7 + n2]] % 12;
                int n10 = this.perm[n6 + 1 + this.perm[n7 + 1]] % 12;
                double d17 = 0.5 - d12 * d12 - d7 * d7;
                if (d17 < 0.0) {
                    d5 = 0.0;
                } else {
                    d17 *= d17;
                    d5 = d17 * d17 * SimplexNoiseSampler.dot(grads[n8], d12, d7);
                }
                double d18 = 0.5 - d13 * d13 - d14 * d14;
                if (d18 < 0.0) {
                    d4 = 0.0;
                } else {
                    d18 *= d18;
                    d4 = d18 * d18 * SimplexNoiseSampler.dot(grads[n9], d13, d14);
                }
                double d19 = 0.5 - d15 * d15 - d16 * d16;
                if (d19 < 0.0) {
                    d3 = 0.0;
                } else {
                    d19 *= d19;
                    d3 = d19 * d19 * SimplexNoiseSampler.dot(grads[n10], d15, d16);
                }
                int n11 = n++;
                map[n11] = map[n11] + 70.0 * (d5 + d4 + d3) * f;
            }
        }
    }
}

