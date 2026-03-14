package net.derggy.craft.derggycraft.network;

import net.derggy.craft.derggycraft.events.init.ItemInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.item.StationItemStack;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Namespace;

/**
 * Server-side tracker for golden compass entity tracking.
 * Called from a server tick mixin.
 */
public class GoldenCompassTracker {

    @SuppressWarnings("UnstableApiUsage")
    private static final Namespace NAMESPACE = Namespace.resolve();

    private static int tickCounter = 0;

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        // Send updates every 10 ticks (0.5 seconds)
        if (tickCounter % 10 != 0) return;

        for (Object obj : server.playerManager.players) {
            ServerPlayerEntity player = (ServerPlayerEntity) obj;
            processPlayer(server, player);
        }
    }

    private static void processPlayer(MinecraftServer server, ServerPlayerEntity player) {
        // Check all inventory slots for golden compass
        for (int slot = 0; slot < player.inventory.main.length; slot++) {
            ItemStack stack = player.inventory.main[slot];
            if (stack == null || stack.getItem() != ItemInit.GOLDEN_COMPASS) continue;

            NbtCompound nbt = ((StationItemStack) (Object) stack).getStationNbt();
            if (!nbt.contains("locked") || !nbt.getBoolean("locked")) continue;

            int targetId = nbt.getInt("targetId");
            String targetType = nbt.getString("targetType");
            String targetName = nbt.getString("targetName");

            Entity target = findTarget(server, player, targetId, targetType, targetName);

            MessagePacket msg = new MessagePacket(NAMESPACE.id("golden_compass_update"));

            if (target == null) {
                // Target not found
                msg.booleans = new boolean[]{false};
            } else {
                boolean self = (target == player);
                msg.doubles = new double[]{target.x, target.z};
                msg.booleans = new boolean[]{true, self};
            }

            PacketHelper.sendTo(player, msg);
            return; // Only track one compass per player
        }
    }

    private static Entity findTarget(MinecraftServer server, ServerPlayerEntity holder,
                                     int entityId, String type, String name) {
        // For players, search by name across all online players
        if ("player".equals(type)) {
            ServerPlayerEntity target = server.playerManager.getPlayer(name);
            return target;
        }

        // For mobs, search by entity ID in the holder's world
        World world = holder.world;
        for (Object obj : world.entities) {
            Entity e = (Entity) obj;
            if (e.id == entityId) {
                return e;
            }
        }

        return null;
    }
}
