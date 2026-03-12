/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;

public class OctaveSimplexNoiseSampler
extends NoiseSampler {
    private SimplexNoiseSampler[] octaveSamplers;
    private int octaves;

    public OctaveSimplexNoiseSampler(Random random, int octaves) {
        this.octaves = octaves;
        this.octaveSamplers = new SimplexNoiseSampler[octaves];
        for (int i = 0; i < octaves; ++i) {
            this.octaveSamplers[i] = new SimplexNoiseSampler(random);
        }
    }

    public double[] sample(double[] map, double x, double y, int width, int height, double d, double e, double f) {
        return this.sample(map, x, y, width, height, d, e, f, 0.5);
    }

    public double[] sample(double[] map, double x, double y, int width, int height, double d, double e, double f, double g) {
        d /= 1.5;
        e /= 1.5;
        if (map == null || map.length < width * height) {
            map = new double[width * height];
        } else {
            for (int i = 0; i < map.length; ++i) {
                map[i] = 0.0;
            }
        }
        double d2 = 1.0;
        double d3 = 1.0;
        for (int i = 0; i < this.octaves; ++i) {
            this.octaveSamplers[i].create(map, x, y, width, height, d * d3, e * d3, 0.55 / d2);
            d3 *= f;
            d2 *= g;
        }
        return map;
    }
}

