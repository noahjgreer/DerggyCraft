package net.derggy.craft.derggycraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateFoodItem;
import net.modificationstation.stationapi.api.util.Identifier;

/**
 * Rotten Flesh: dropped by zombies, inedible by players, but feedable to wolves.
 * Extends TemplateFoodItem with isMeat=true so wolves accept it.
 */
public class RottenFleshItem extends TemplateFoodItem {

    public RottenFleshItem(Identifier id) {
        super(id, 0, true);
        this.setMaxCount(64);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        // Inedible by players - do nothing
        return stack;
    }
}
