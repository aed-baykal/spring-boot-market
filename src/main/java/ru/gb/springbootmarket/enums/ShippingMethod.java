package ru.gb.springbootmarket.enums;

public enum ShippingMethod {
    SELF("Самовывоз"),
    DELIVERY("Доставка");

    private final String name;

    ShippingMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
