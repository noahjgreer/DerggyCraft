/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.stat;

import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;

public class SimpleStat
extends Stat {
    public SimpleStat(int i, String string, StatFormatter statFormatter) {
        super(i, string, statFormatter);
    }

    public SimpleStat(int i, String string) {
        super(i, string);
    }

    public Stat addStat() {
        super.addStat();
        Stats.GENERAL_STATS.add(this);
        return this;
    }
}

