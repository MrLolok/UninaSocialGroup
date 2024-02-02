package org.unina.project.view.nodes.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

/**
 * Gestore dei click al bottone per eliminare
 * un contenuto pubblicato (post/commento).
 * @param <T> tipo di contenuto
 */
@RequiredArgsConstructor
public class PublishedContentDeleteButtonHandler<T extends PublishedContent<?>> implements EventHandler<ActionEvent> {
    // Contenitore del nodo che rappresenta il contenuto da eliminare
    private final Pane parent;
    // Nodo (o card) che rappresenta questo il contenuto da eliminare
    private final Node child;
    private final T content;

    /**
     * Richiedi, tramite un avviso, la conferma di eliminazione.
     * Una volta confermata, verrà eliminato il contenuto dal
     * database e dall'interfaccia.
     * @param event relativo al click del bottone
     */
    @Override
    public void handle(ActionEvent event) {
        Alert alert = SceneAlertUtils.alert(Alert.AlertType.CONFIRMATION, "Eliminazione contenuto", "Sicuro di voler eliminare il seguente contenuto?", content.getContent(), true);
        if (alert.getResult() == ButtonType.OK) {
            parent.getChildren().remove(child);
            if (content instanceof Post post)
                App.getInstance().getPosts().delete(post);
            else if (content instanceof Comment comment)
                App.getInstance().getComments().delete(comment);
        }
    }
}
