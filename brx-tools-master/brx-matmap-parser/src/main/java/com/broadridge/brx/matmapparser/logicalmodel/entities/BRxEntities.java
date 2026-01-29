package com.broadridge.brx.matmapparser.logicalmodel.entities;

import lombok.Getter;

@Getter
public enum BRxEntities {
    TRANSACTION("Transaction"),
    POSITION("Position"),
    PARTY("Party"),
    ANNOUNCEMENT("Announcement"),
    BALANCE("Balance"),
    PRICE("Price"),
    INSTRUMENT("Instrument"),
    COMMUNICATION("Communication"),
    ACKNOWLEDGEMENT("Acknowledgement"),
    RATE("Rate"),
    MARKET("Market");

    private final String entityName;

    BRxEntities(String entityName) {
        this.entityName = entityName;
    }

}
