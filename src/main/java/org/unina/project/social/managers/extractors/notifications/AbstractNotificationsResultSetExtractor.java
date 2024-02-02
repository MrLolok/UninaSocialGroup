package org.unina.project.social.managers.extractors.notifications;

import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.notifications.DefaultNotification;
import org.unina.project.social.entities.notifications.Notification;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * Classe astratta che fornisce una struttura di base per l'estrazione di notifiche {@link ResultSet}.
 * @param <T> tipo generico che implementa {@link PublishedContent}.
 */
@RequiredArgsConstructor
public abstract class AbstractNotificationsResultSetExtractor<T extends PublishedContent<?>> extends AbstractPersistentEntityExtractor<Notification<T>> {
    // Colonna di riferimento per il contenuto nelle notifiche
    private final String contentReferenceColumn;

    /**
     * Carica le notifiche dal {@link ResultSet} e le aggiunge all'insieme corrente di entità caricate.
     * @param currentLoadedEntities insieme corrente di entità caricate.
     * @param set da operare
     * @throws SQLException nel caso si presentino errori durante operazioni SQL
     * @throws IllegalArgumentException nel caso siano presenti parametri invalidi per costruire un'entità
     */
    @Override
    protected void load(Set<Notification<T>> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        int contentId = set.getInt(contentReferenceColumn);
        T content = getPublishedContent(contentId).orElse(null);
        int userId = set.getInt("fk_utente");
        User user = App.getInstance().getUsers().get(userId).orElse(null);
        Notification<T> notification = new DefaultNotification<>(content, user);
        currentLoadedEntities.add(notification);
    }

    /**
     * Metodo astratto per ottenere un {@link PublishedContent} dato un ID.
     * @param id del {@link PublishedContent} da ottenere.
     * @return Optional contenente il PublishedContent corrispondente, o vuoto se non trovato.
     */
    protected abstract Optional<T> getPublishedContent(int id);
}