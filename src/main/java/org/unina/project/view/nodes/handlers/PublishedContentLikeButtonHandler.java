package org.unina.project.view.nodes.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.entities.users.User;
import org.unina.project.view.nodes.CommonNodesUtils;

/**
 * Gestore dei click al bottone per mettere o togliere "like"
 * a un contenuto pubblicato (post/commento).
 * @param <T> tipo di contenuto
 */
@RequiredArgsConstructor
public class PublishedContentLikeButtonHandler<T extends PublishedContent<?>> implements EventHandler<ActionEvent> {
    private final T content;
    private final User user;

    /**
     * Aggiungi o togli i like in base allo stato attuale
     * e aggiorna il design e il contenuto dell'bottone.
     * @see CommonNodesUtils#setLikeButton(Button, PublishedContent, User)
     * @param event relativo al click del bottone
     */
    @Override
    public void handle(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) return;
        if (content.isLiked(user))
            content.getLikedBy().remove(user);
        else
            content.getLikedBy().add(user);
        CommonNodesUtils.setLikeButton(button, content, user);
        if (content instanceof Post post)
            App.getInstance().getPosts().update(post);
        else if (content instanceof Comment comment)
            App.getInstance().getComments().update(comment);
    }
}
