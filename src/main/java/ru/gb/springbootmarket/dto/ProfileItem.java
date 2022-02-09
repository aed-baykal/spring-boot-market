package ru.gb.springbootmarket.dto;

import lombok.Data;

@Data
public class ProfileItem {
    private String name;
    private Long workTime;

    public ProfileItem(String name, Long workTime) {
        this.name = name;
        this.workTime = workTime;
    }
}
