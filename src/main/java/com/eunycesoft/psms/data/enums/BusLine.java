package com.eunycesoft.psms.data.enums;

import lombok.Getter;

@Getter
public enum BusLine {
    AKWA_VITA("Akwa vita"),
    GENIE_MILITAIRE("Génie militaire"),
    MBWANG("Mbwang"),
    VILLAGE("Village"),
    YATSHIKA("Yatchika");

    private final String lineName;

    BusLine(String lineName) {
        this.lineName = lineName;
    }

    @Override
    public String toString() {
        return lineName;
    }
}
