/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

public class Vec3i
implements Comparable {
    public int x;
    public int y;
    public int z;

    public Vec3i() {
    }

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(Vec3i vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Vec3i)) {
            return false;
        }
        Vec3i vec3i = (Vec3i)o;
        return this.x == vec3i.x && this.y == vec3i.y && this.z == vec3i.z;
    }

    public int hashCode() {
        return this.x + this.z << 8 + this.y << 16;
    }

    public int compareTo(Vec3i vec3i) {
        if (this.y == vec3i.y) {
            if (this.z == vec3i.z) {
                return this.x - vec3i.x;
            }
            return this.z - vec3i.z;
        }
        return this.y - vec3i.y;
    }

    public double distanceTo(int x, int y, int z) {
        int n = this.x - x;
        int n2 = this.y - y;
        int n3 = this.z - z;
        return Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
}

