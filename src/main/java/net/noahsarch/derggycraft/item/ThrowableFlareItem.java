package net.noahsarch.derggycraft.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class ThrowableFlareItem extends TemplateItem {
    private final int lifetimeTicks;

    public ThrowableFlareItem(Identifier identifier, int lifetimeTicks) {
        super(identifier);
        this.lifetimeTicks = lifetimeTicks;
    }

    @Override
    public int getMaxCount() {
        return 16;
    }

    public int getLifetimeTicks() {
        return this.lifetimeTicks;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if (stack == null || user == null) {
            return stack;
        }

        world.playSound(user, "random.bow", 0.45F, 0.65F / (random.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            ItemStack thrownStack = new ItemStack(this.id, 1, stack.getDamage());

            float yawRadians = user.yaw * 0.017453292F;
            float pitchRadians = user.pitch * 0.017453292F;
            double lookX = -MathHelper.sin(yawRadians) * MathHelper.cos(pitchRadians);
            double lookY = -MathHelper.sin(pitchRadians);
            double lookZ = MathHelper.cos(yawRadians) * MathHelper.cos(pitchRadians);

            // Start from body/chest level and slightly forward to avoid ceiling/body clipping.
            double spawnX = user.x + lookX * 0.35D;
            double spawnY = user.y + 0.9D + lookY * 0.08D;
            double spawnZ = user.z + lookZ * 0.35D;

            ItemEntity flareEntity = new ItemEntity(world, spawnX, spawnY, spawnZ, thrownStack);
            this.derggycraft$setThrowVelocity(flareEntity, user, 0.95F);
            flareEntity.yaw = world.random.nextFloat() * 360.0F;
            flareEntity.prevYaw = flareEntity.yaw;
            flareEntity.pitch = -70.0F + world.random.nextFloat() * 140.0F;
            flareEntity.prevPitch = flareEntity.pitch;
            flareEntity.pickupDelay = Integer.MAX_VALUE;
            world.spawnEntity(flareEntity);
        }

        --stack.count;
        return stack;
    }

    private void derggycraft$setThrowVelocity(ItemEntity entity, PlayerEntity user, float speed) {
        float yawRadians = user.yaw * 0.017453292F;
        float pitchRadians = user.pitch * 0.017453292F;

        double lookX = -MathHelper.sin(yawRadians) * MathHelper.cos(pitchRadians);
        double lookY = -MathHelper.sin(pitchRadians);
        double lookZ = MathHelper.cos(yawRadians) * MathHelper.cos(pitchRadians);

        entity.velocityX = lookX * speed + user.velocityX * 0.35D;
        entity.velocityY = lookY * speed + user.velocityY * 0.35D + 0.1D;
        entity.velocityZ = lookZ * speed + user.velocityZ * 0.35D;
    }
}
