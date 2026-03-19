package net.noahsarch.derggycraft.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DerggyCraftMixinPlugin implements IMixinConfigPlugin {
    private static final String CPM_MOD_ID = "cpm";
    private static final String CPM_CONFIG_MIXIN = "net.noahsarch.derggycraft.mixin.client.CPMConfigEntryThreadSafetyMixin";
    private static final String CPM_CONFIG_ENTRY_CLASS = "com.tom.cpl.config.ConfigEntry";
    private static final String CPM_SINGLEPLAYER_COMMAND_MIXIN = "net.noahsarch.derggycraft.mixin.client.CPMSinglePlayerCommandSafetyMixin";
    private static final String CPM_SINGLEPLAYER_COMMAND_CLASS = "com.tom.cpm.client.SinglePlayerCommands";
    private static final String CPM_PLAYER_ANIM_HEALTH_MIXIN = "net.noahsarch.derggycraft.mixin.CPMPlayerAnimHealthSyncMixin";
    private static final String CPM_PLAYER_ANIM_UPDATER_CLASS = "com.tom.cpm.common.PlayerAnimUpdater";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (CPM_CONFIG_MIXIN.equals(mixinClassName)) {
            return this.derggycraft$isCpmLoaded() && this.derggycraft$classExists(CPM_CONFIG_ENTRY_CLASS);
        }
        if (CPM_SINGLEPLAYER_COMMAND_MIXIN.equals(mixinClassName)) {
            return this.derggycraft$isCpmLoaded();
        }
        if (CPM_PLAYER_ANIM_HEALTH_MIXIN.equals(mixinClassName)) {
            return this.derggycraft$isCpmLoaded() && this.derggycraft$classExists(CPM_PLAYER_ANIM_UPDATER_CLASS);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private boolean derggycraft$classExists(String className) {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class.forName(className, false, contextLoader);
            return true;
        } catch (Throwable ignored) {
            try {
                Class.forName(className, false, this.getClass().getClassLoader());
                return true;
            } catch (Throwable ignoredToo) {
                return false;
            }
        }
    }

    private boolean derggycraft$isCpmLoaded() {
        try {
            return FabricLoader.getInstance().isModLoaded(CPM_MOD_ID);
        } catch (Throwable ignored) {
            return this.derggycraft$classExists(CPM_SINGLEPLAYER_COMMAND_CLASS);
        }
    }
}