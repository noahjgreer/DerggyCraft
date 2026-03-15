package net.noahsarch.derggycraft.server.gamerule;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class DerggyCraftGameRulesState extends PersistentState {
    public boolean sendDeathMessages = true;
    public boolean keepInventory = false;
    public boolean extinguishTorches = false;

    public DerggyCraftGameRulesState(String id) {
        super(id);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("sendDeathMessages")) {
            this.sendDeathMessages = nbt.getBoolean("sendDeathMessages");
        }
        if (nbt.contains("keepInventory")) {
            this.keepInventory = nbt.getBoolean("keepInventory");
        }
        if (nbt.contains("extinguishTorches")) {
            this.extinguishTorches = nbt.getBoolean("extinguishTorches");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("sendDeathMessages", this.sendDeathMessages);
        nbt.putBoolean("keepInventory", this.keepInventory);
        nbt.putBoolean("extinguishTorches", this.extinguishTorches);
    }
}