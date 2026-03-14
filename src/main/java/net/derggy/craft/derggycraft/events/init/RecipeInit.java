package net.derggy.craft.derggycraft.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

import java.lang.invoke.MethodHandles;

public class RecipeInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    @EventListener
    private static void registerRecipes(RecipeRegisterEvent event) {
        RecipeRegisterEvent.Vanilla type = RecipeRegisterEvent.Vanilla.fromType(event.recipeId);
        if (type == null) return;

        switch (type) {
            case CRAFTING_SHAPED:
                registerShapedRecipes();
                break;
            case CRAFTING_SHAPELESS:
                registerShapelessRecipes();
                break;
            case SMELTING:
                registerSmeltingRecipes();
                break;
        }
    }

    private static void registerShapedRecipes() {
        // 9 Leather Scraps → 3 Leather
        CraftingRegistry.addShapedRecipe(
                new ItemStack(Item.LEATHER, 3),
                "SSS", "SSS", "SSS",
                'S', ItemInit.LEATHER_SCRAP
        );

        // Iron Compass: 4 iron + 1 redstone (replaces vanilla compass recipe)
        CraftingRegistry.addShapedRecipe(
                new ItemStack(ItemInit.IRON_COMPASS),
                " I ", "IRI", " I ",
                'I', Item.IRON_INGOT,
                'R', Item.REDSTONE
        );

        // Golden Compass: 4 gold + 1 diamond
        CraftingRegistry.addShapedRecipe(
                new ItemStack(ItemInit.GOLDEN_COMPASS),
                " G ", "GDG", " G ",
                'G', Item.GOLD_INGOT,
                'D', Item.DIAMOND
        );
    }

    private static void registerShapelessRecipes() {
    }

    private static void registerSmeltingRecipes() {
        // Rotten Flesh → Leather Scrap
        SmeltingRegistry.addSmeltingRecipe(
                new ItemStack(ItemInit.ROTTEN_FLESH),
                new ItemStack(ItemInit.LEATHER_SCRAP)
        );
    }
}
