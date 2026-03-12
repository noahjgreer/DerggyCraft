/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resource.language;

import net.minecraft.client.resource.language.TranslationStorage;

public class I18n {
    private static TranslationStorage translations = TranslationStorage.getInstance();

    public static String getTranslation(String name) {
        return translations.get(name);
    }

    public static String getTranslation(String name, Object ... args) {
        return translations.get(name, args);
    }
}

