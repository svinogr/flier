package com.svinogr.flier.model;

public enum TabsOfShopProperty {
    FOOD("ПРОДУКТЫ"), CLOTHES("ОДЕЖДА"), BUILDING("СТРОИТЕЛЬНЫЙ");

    String title;

    TabsOfShopProperty(String title) {
        this.title = title;
    }
}
