package org.unina.project.social.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Tipologie di post che definiscono il tipo di contenuto.
 */
@RequiredArgsConstructor
public enum PostType {
    TEXT("text"),
    PHOTO("photo");

    @Getter
    private final String sqlParamName;

    public static Optional<PostType> get(String name) {
        return Arrays.stream(PostType.values()).filter(type -> type.getSqlParamName().equalsIgnoreCase(name)).findFirst();
    }
}
