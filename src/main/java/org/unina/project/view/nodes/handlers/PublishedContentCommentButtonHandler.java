package org.unina.project.view.nodes.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.custom.CommentCreationDialog;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

import java.util.Optional;

/**
 * Gestore dei click al bottone per commentare
 * un contenuto pubblicato (post/commento).
 * @param <T> tipo di contenuto
 */
@RequiredArgsConstructor
public class PublishedContentCommentButtonHandler<T extends PublishedContent<?>> implements EventHandler<ActionEvent> {
    private final T content;
    private final User user;

    /**
     * Crea una nuova finestra in cui inserire un commento.
     * Una volta premuto il bottone "OK", verrà creato un nuovo commento.
     * @param event relativo al click del bottone
     */
    @Override
    public void handle(ActionEvent event) {
        Dialog<String> dialog = CommentCreationDialog.create();
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            Optional<Post> optional;
            Comment parent;
            if (this.content instanceof Post post) {
                optional = Optional.of(post);
                parent = null;
            } else if (this.content instanceof Comment comment) {
                optional = comment.getPost();
                parent = comment;
            } else {
                optional = Optional.empty();
                parent = null;
            }
            optional.ifPresentOrElse(post -> {
                App.getInstance().getComments().create(user, post, parent, content);
                Session.getGlobalSession().setGroup(post.getGroup());
                App.getInstance().setScene(SceneType.GROUP.getScene());
                SceneAlertUtils.alert(Alert.AlertType.INFORMATION, "Commento inviato", "Commento inviato", "Hai inviato con successo il commento " + content, false);
            }, () -> SceneAlertUtils.alert(Alert.AlertType.ERROR, "Errore", "Post non trovato", "Non è stato trovato il post associato al contenuto che stai commentando", false));
        });
    }
}
