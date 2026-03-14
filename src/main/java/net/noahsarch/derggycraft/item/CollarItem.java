package net.noahsarch.derggycraft.item;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.template.item.TemplateItem;

public class CollarItem extends TemplateItem {
    public CollarItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }
}
