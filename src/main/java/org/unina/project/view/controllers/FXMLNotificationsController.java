package org.unina.project.view.controllers;

import javafx.scene.layout.VBox;
import org.unina.project.App;
import org.unina.project.social.entities.notifications.Notification;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.custom.PublishedContentCard;

import java.util.Set;

/**
 * FXML Controller per l'interfaccia delle notifiche di un utente.
 * Mostra tutti le notifiche ricevute dall'utente.
 */
public class FXMLNotificationsController extends FXMLEntityGridController<Notification<Post>> {
    @Override
    protected void setup() {
        setReversedOrder(true);
        refresh();
        show();
    }

    @Override
    protected Set<Notification<Post>> getAllEntities() {
        return App.getInstance().getPostNotifications().getAll(Session.getGlobalSession().getUser());
    }

    @Override
    protected VBox getEntityCard(Notification<Post> notification) {
        return new PublishedContentCard<>(getGrid(), notification.getPublishedContent(), true, false).create();
    }
}