/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.material;

import net.minecraft.block.MapColor;
import net.minecraft.block.material.AirMaterial;
import net.minecraft.block.material.FluidMaterial;
import net.minecraft.block.material.PassableMaterial;
import net.minecraft.block.material.PortalMaterial;

public class Material {
    public static final Material AIR = new AirMaterial(MapColor.CLEAR);
    public static final Material SOLID_ORGANIC = new Material(MapColor.PALE_GREEN);
    public static final Material SOIL = new Material(MapColor.ORANGE);
    public static final Material WOOD = new Material(MapColor.BROWN).setBurnable();
    public static final Material STONE = new Material(MapColor.GRAY).setRequiresTool();
    public static final Material METAL = new Material(MapColor.LIGHT_GRAY2).setRequiresTool();
    public static final Material WATER = new FluidMaterial(MapColor.BLUE).setDestroyPistonBehavior();
    public static final Material LAVA = new FluidMaterial(MapColor.RED).setDestroyPistonBehavior();
    public static final Material LEAVES = new Material(MapColor.GREEN).setBurnable().setTransparent().setDestroyPistonBehavior();
    public static final Material PLANT = new PassableMaterial(MapColor.GREEN).setDestroyPistonBehavior();
    public static final Material SPONGE = new Material(MapColor.LIGHT_GRAY);
    public static final Material WOOL = new Material(MapColor.LIGHT_GRAY).setBurnable();
    public static final Material FIRE = new AirMaterial(MapColor.CLEAR).setDestroyPistonBehavior();
    public static final Material SAND = new Material(MapColor.PALE_YELLOW);
    public static final Material PISTON_BREAKABLE = new PassableMaterial(MapColor.CLEAR).setDestroyPistonBehavior();
    public static final Material GLASS = new Material(MapColor.CLEAR).setTransparent();
    public static final Material TNT = new Material(MapColor.RED).setBurnable().setTransparent();
    public static final Material UNUSED = new Material(MapColor.GREEN).setDestroyPistonBehavior();
    public static final Material ICE = new Material(MapColor.LIGHT_BLUE).setTransparent();
    public static final Material SNOW_LAYER = new PassableMaterial(MapColor.WHITE).setReplaceable().setTransparent().setRequiresTool().setDestroyPistonBehavior();
    public static final Material SNOW_BLOCK = new Material(MapColor.WHITE).setRequiresTool();
    public static final Material CACTUS = new Material(MapColor.GREEN).setTransparent().setDestroyPistonBehavior();
    public static final Material CLAY = new Material(MapColor.SILVER);
    public static final Material PUMPKIN = new Material(MapColor.GREEN).setDestroyPistonBehavior();
    public static final Material NETHER_PORTAL = new PortalMaterial(MapColor.CLEAR).setUnpushablePistonBehavior();
    public static final Material CAKE = new Material(MapColor.CLEAR).setDestroyPistonBehavior();
    public static final Material COBWEB = new Material(MapColor.LIGHT_GRAY).setRequiresTool().setDestroyPistonBehavior();
    public static final Material PISTON = new Material(MapColor.GRAY).setUnpushablePistonBehavior();
    private boolean burnable;
    private boolean replaceable;
    private boolean transparent;
    public final MapColor mapColor;
    private boolean handHarvestable = true;
    private int pistonBehavior;

    public Material(MapColor mapColor) {
        this.mapColor = mapColor;
    }

    public boolean isFluid() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean blocksVision() {
        return true;
    }

    public boolean blocksMovement() {
        return true;
    }

    private Material setTransparent() {
        this.transparent = true;
        return this;
    }

    private Material setRequiresTool() {
        this.handHarvestable = false;
        return this;
    }

    private Material setBurnable() {
        this.burnable = true;
        return this;
    }

    public boolean isBurnable() {
        return this.burnable;
    }

    public Material setReplaceable() {
        this.replaceable = true;
        return this;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean suffocates() {
        if (this.transparent) {
            return false;
        }
        return this.blocksMovement();
    }

    public boolean isHandHarvestable() {
        return this.handHarvestable;
    }

    public int getPistonBehavior() {
        return this.pistonBehavior;
    }

    protected Material setDestroyPistonBehavior() {
        this.pistonBehavior = 1;
        return this;
    }

    protected Material setUnpushablePistonBehavior() {
        this.pistonBehavior = 2;
        return this;
    }
}

