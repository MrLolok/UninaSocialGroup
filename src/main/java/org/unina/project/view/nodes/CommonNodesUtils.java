package org.unina.project.view.nodes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.users.User;

/**
 * Utilities per creare nuovi {@link Node} o apportare
 * modifiche di comune utilizzo.
 */
public final class CommonNodesUtils {
    /**
     * Crea uno distanziatore.
     * @return nuova istanza di un distanziatore
     */
    public static Node getSpacer() {
        HBox spacer = new HBox();
        VBox.setMargin(spacer, new Insets(8, 0, 8, 0));
        spacer.setMinHeight(Double.NEGATIVE_INFINITY);
        spacer.setPrefHeight(1);
        spacer.setStyle("-fx-background-color: #eee");
        return spacer;
    }

    /**
     * Imposta l'icona e il testo di un bottone per mettere
     * like in base al contenuto e all'utente che lo visualizza.
     * @param button da modificare
     * @param content relativo
     * @param user che visualizza il bottone
     */
    public static void setLikeButton(Button button, PublishedContent<?> content, User user) {
        button.setGraphic(new FontIcon(content.isLiked(user) ? "fas-heart" : "far-heart"));
        button.setText(String.valueOf(content.getLikedBy().size()));
    }
}
