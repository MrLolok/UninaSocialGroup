package org.unina.project.social.enums;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.unina.project.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * Tipologie di gender di un utente.
 */
@RequiredArgsConstructor
public enum Gender {
    MALE("male", "male.png"),
    FEMALE("female", "female.png"),
    OTHER("other", "other.png");

    @Getter
    private final String sqlParamName, avatar;

    public static Optional<Gender> get(String name) {
        return Arrays.stream(Gender.values()).filter(gender -> gender.getSqlParamName().equalsIgnoreCase(name)).findFirst();
    }

    public @Nullable Image getImage() {
        Image image;
        try (InputStream in = App.class.getClassLoader().getResourceAsStream(String.format("imgs/%s", avatar))) {
            if (in == null)
                throw new RuntimeException(String.format("Non è presente alcuna immagine per il gender %s.", name()));
            image = new Image(in);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Impossibile recuperare l'immagine del gender %s.", name()), e);
        }
        return image;
    }
}
