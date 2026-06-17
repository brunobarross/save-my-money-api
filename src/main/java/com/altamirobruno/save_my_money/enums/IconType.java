package com.altamirobruno.save_my_money.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.HashMap;

@Getter
@RequiredArgsConstructor
public enum IconType {
    WALLET("account_balance_wallet"),
    CARD("credit_card"),
    BANK("account_balance"),
    PAYMENTS("payments"),
    SAVINGS("savings");

    @JsonValue
    private final String materialIcon;

    // Fast lookup cache
    private static final Map<String, IconType> BY_LABEL = new HashMap<>();

    static {
        for (IconType e : values()) {
            BY_LABEL.put(e.materialIcon.toLowerCase(), e);
        }
    }

    /**
     * @JsonCreator allows Jackson to automatically deserialize
     * the frontend string back into this Enum.
     */
    @JsonCreator
    public static IconType fromString(String text) {
        if (text == null) {
            return WALLET;
        }
        return BY_LABEL.getOrDefault(text.toLowerCase(), WALLET);
    }
}