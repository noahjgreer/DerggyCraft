package net.noahsarch.derggycraft.item;

import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class LeatherScrapItem extends TemplateItem {
    public LeatherScrapItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getMaxCount() {
        return 64;
    }
}
