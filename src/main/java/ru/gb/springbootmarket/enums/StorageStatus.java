package ru.gb.springbootmarket.enums;

public enum StorageStatus {
    IN_SELECTION("Собирается"),
    SELECTED("Собран");

    private final String name;

    StorageStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
