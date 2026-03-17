package net.noahsarch.derggycraft.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(targets = "com.tom.cpl.config.ConfigEntry", remap = false)
public abstract class CPMConfigEntryThreadSafetyMixin {
    @Shadow
    protected Map<String, Object> entries;

    @Shadow
    protected Map<String, Object> lists;

    @Shadow
    protected Map<String, Object> data;

    @Shadow
    protected Runnable changeListener;

    private static Constructor<?> derggycraft$configEntryCtor;
    private static Constructor<?> derggycraft$configEntryListCtor;

    @Inject(method = "getEntry", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void derggycraft$synchronizedGetEntry(String name, CallbackInfoReturnable<Object> cir) {
        synchronized (this) {
            Object existing = this.entries.get(name);
            if (existing != null) {
                cir.setReturnValue(existing);
                return;
            }

            Map<String, Object> rawEntryMap = this.derggycraft$getOrCreateEntryMap(name);
            Object created = this.derggycraft$newConfigEntry(rawEntryMap);
            if (created == null) {
                return;
            }
            this.entries.put(name, created);
            cir.setReturnValue(created);
        }
    }

    @Inject(method = "getEntryList", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void derggycraft$synchronizedGetEntryList(String name, CallbackInfoReturnable<Object> cir) {
        synchronized (this) {
            Object existing = this.lists.get(name);
            if (existing != null) {
                cir.setReturnValue(existing);
                return;
            }

            List<Object> rawEntryList = this.derggycraft$getOrCreateEntryList(name);
            Object created = this.derggycraft$newConfigEntryList(rawEntryList);
            if (created == null) {
                return;
            }
            this.lists.put(name, created);
            cir.setReturnValue(created);
        }
    }

    private Object derggycraft$newConfigEntry(Map<String, Object> rawEntryMap) {
        try {
            if (derggycraft$configEntryCtor == null) {
                Class<?> configEntryClass = Class.forName("com.tom.cpl.config.ConfigEntry");
                derggycraft$configEntryCtor = configEntryClass.getDeclaredConstructor(Map.class, Runnable.class);
                derggycraft$configEntryCtor.setAccessible(true);
            }
            return derggycraft$configEntryCtor.newInstance(rawEntryMap, this.changeListener);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private Object derggycraft$newConfigEntryList(List<Object> rawEntryList) {
        try {
            if (derggycraft$configEntryListCtor == null) {
                Class<?> configEntryListClass = Class.forName("com.tom.cpl.config.ConfigEntry$ConfigEntryList");
                derggycraft$configEntryListCtor = configEntryListClass.getDeclaredConstructor(List.class, Runnable.class);
                derggycraft$configEntryListCtor.setAccessible(true);
            }
            return derggycraft$configEntryListCtor.newInstance(rawEntryList, this.changeListener);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> derggycraft$getOrCreateEntryMap(String name) {
        Object existingData = this.data.get(name);
        if (existingData instanceof Map<?, ?> existingMap) {
            return (Map<String, Object>) existingMap;
        }

        Map<String, Object> createdMap = new HashMap<>();
        this.data.put(name, createdMap);
        if (this.changeListener != null) {
            this.changeListener.run();
        }
        return createdMap;
    }

    @SuppressWarnings("unchecked")
    private List<Object> derggycraft$getOrCreateEntryList(String name) {
        Object existingData = this.data.get(name);
        if (existingData instanceof List<?> existingList) {
            return (List<Object>) existingList;
        }

        List<Object> createdList = new ArrayList<>();
        this.data.put(name, createdList);
        if (this.changeListener != null) {
            this.changeListener.run();
        }
        return createdList;
    }
}