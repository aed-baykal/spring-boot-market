package ru.gb.springbootmarket.enums;

public enum OrderStatus {
    NEW("Ожидает обработки"),
    IN_WORK("Обрабатывается"),
    SHIPPED("Отправлен покупателю"),
    DONE("Завершен");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
