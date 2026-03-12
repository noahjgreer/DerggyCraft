/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneTorchBurnoutEntry;
import net.minecraft.block.TorchBlock;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneTorchBlock
extends TorchBlock {
    private boolean lit = false;
    private static List burnoutEntries = new ArrayList();

    public int getTexture(int side, int meta) {
        if (side == 1) {
            return Block.REDSTONE_WIRE.getTexture(side, meta);
        }
        return super.getTexture(side, meta);
    }

    private boolean isBurnedOut(World world, int x, int y, int z, boolean addNew) {
        if (addNew) {
            burnoutEntries.add(new RedstoneTorchBurnoutEntry(x, y, z, world.getTime()));
        }
        int n = 0;
        for (int i = 0; i < burnoutEntries.size(); ++i) {
            RedstoneTorchBurnoutEntry redstoneTorchBurnoutEntry = (RedstoneTorchBurnoutEntry)burnoutEntries.get(i);
            if (redstoneTorchBurnoutEntry.x != x || redstoneTorchBurnoutEntry.y != y || redstoneTorchBurnoutEntry.z != z || ++n < 8) continue;
            return true;
        }
        return false;
    }

    public RedstoneTorchBlock(int id, int textureId, boolean lit) {
        super(id, textureId);
        this.lit = lit;
        this.setTickRandomly(true);
    }

    public int getTickRate() {
        return 2;
    }

    public void onPlaced(World world, int x, int y, int z) {
        if (world.getBlockMeta(x, y, z) == 0) {
            super.onPlaced(world, x, y, z);
        }
        if (this.lit) {
            world.notifyNeighbors(x, y - 1, z, this.id);
            world.notifyNeighbors(x, y + 1, z, this.id);
            world.notifyNeighbors(x - 1, y, z, this.id);
            world.notifyNeighbors(x + 1, y, z, this.id);
            world.notifyNeighbors(x, y, z - 1, this.id);
            world.notifyNeighbors(x, y, z + 1, this.id);
        }
    }

    public void onBreak(World world, int x, int y, int z) {
        if (this.lit) {
            world.notifyNeighbors(x, y - 1, z, this.id);
            world.notifyNeighbors(x, y + 1, z, this.id);
            world.notifyNeighbors(x - 1, y, z, this.id);
            world.notifyNeighbors(x + 1, y, z, this.id);
            world.notifyNeighbors(x, y, z - 1, this.id);
            world.notifyNeighbors(x, y, z + 1, this.id);
        }
    }

    public boolean isPoweringSide(BlockView blockView, int x, int y, int z, int side) {
        if (!this.lit) {
            return false;
        }
        int n = blockView.getBlockMeta(x, y, z);
        if (n == 5 && side == 1) {
            return false;
        }
        if (n == 3 && side == 3) {
            return false;
        }
        if (n == 4 && side == 2) {
            return false;
        }
        if (n == 1 && side == 5) {
            return false;
        }
        return n != 2 || side != 4;
    }

    private boolean shouldUnpower(World world, int x, int y, int z) {
        int n = world.getBlockMeta(x, y, z);
        if (n == 5 && world.isPoweringSide(x, y - 1, z, 0)) {
            return true;
        }
        if (n == 3 && world.isPoweringSide(x, y, z - 1, 2)) {
            return true;
        }
        if (n == 4 && world.isPoweringSide(x, y, z + 1, 3)) {
            return true;
        }
        if (n == 1 && world.isPoweringSide(x - 1, y, z, 4)) {
            return true;
        }
        return n == 2 && world.isPoweringSide(x + 1, y, z, 5);
    }

    public void onTick(World world, int x, int y, int z, Random random) {
        boolean bl = this.shouldUnpower(world, x, y, z);
        while (burnoutEntries.size() > 0 && world.getTime() - ((RedstoneTorchBurnoutEntry)RedstoneTorchBlock.burnoutEntries.get((int)0)).time > 100L) {
            burnoutEntries.remove(0);
        }
        if (this.lit) {
            if (bl) {
                world.setBlock(x, y, z, Block.REDSTONE_TORCH.id, world.getBlockMeta(x, y, z));
                if (this.isBurnedOut(world, x, y, z, true)) {
                    world.playSound((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, "random.fizz", 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                    for (int i = 0; i < 5; ++i) {
                        double d = (double)x + random.nextDouble() * 0.6 + 0.2;
                        double d2 = (double)y + random.nextDouble() * 0.6 + 0.2;
                        double d3 = (double)z + random.nextDouble() * 0.6 + 0.2;
                        world.addParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
                    }
                }
            }
        } else if (!bl && !this.isBurnedOut(world, x, y, z, false)) {
            world.setBlock(x, y, z, Block.LIT_REDSTONE_TORCH.id, world.getBlockMeta(x, y, z));
        }
    }

    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        world.scheduleBlockUpdate(x, y, z, this.id, this.getTickRate());
    }

    public boolean isStrongPoweringSide(World world, int x, int y, int z, int side) {
        if (side == 0) {
            return this.isPoweringSide(world, x, y, z, side);
        }
        return false;
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return Block.LIT_REDSTONE_TORCH.id;
    }

    public boolean canEmitRedstonePower() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (!this.lit) {
            return;
        }
        int n = world.getBlockMeta(x, y, z);
        double d = (double)((float)x + 0.5f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        double d2 = (double)((float)y + 0.7f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        double d3 = (double)((float)z + 0.5f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        double d4 = 0.22f;
        double d5 = 0.27f;
        if (n == 1) {
            world.addParticle("reddust", d - d5, d2 + d4, d3, 0.0, 0.0, 0.0);
        } else if (n == 2) {
            world.addParticle("reddust", d + d5, d2 + d4, d3, 0.0, 0.0, 0.0);
        } else if (n == 3) {
            world.addParticle("reddust", d, d2 + d4, d3 - d5, 0.0, 0.0, 0.0);
        } else if (n == 4) {
            world.addParticle("reddust", d, d2 + d4, d3 + d5, 0.0, 0.0, 0.0);
        } else {
            world.addParticle("reddust", d, d2, d3, 0.0, 0.0, 0.0);
        }
    }
}

