package org.unina.project.social.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Tipologie di gruppo che definiscono la modalità di accesso e visualizzazione.
 */
@RequiredArgsConstructor
public enum GroupType {
    OPEN("open", "Aperto"),
    INVITE("invite", "Ad invito"),
    CLOSED("closed", "Chiuso");

    @Getter
    private final String sqlParamName, display;

    public static Optional<GroupType> get(String name) {
        return Arrays.stream(GroupType.values()).filter(type -> type.getSqlParamName().equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<GroupType> getFromDisplay(String display) {
        return Arrays.stream(GroupType.values()).filter(type -> type.getDisplay().equalsIgnoreCase(display)).findFirst();
    }
}
