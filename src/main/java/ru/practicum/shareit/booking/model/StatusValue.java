package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusValue {
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private String title;

    StatusValue(String title){
        this.title = title;
    }

    @JsonValue
    public String getTitle() {
        return title;
    }



}


