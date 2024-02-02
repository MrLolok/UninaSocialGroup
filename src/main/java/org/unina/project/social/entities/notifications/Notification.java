package org.unina.project.social.entities.notifications;

import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.users.User;

import java.sql.Timestamp;

/**
 * Questa interfaccia rappresenta una notifica associata a un contenuto pubblicato.
 * @param <T> tipo di {@link PublishedContent}
 */
public interface Notification<T extends PublishedContent<?>> {
    /**
     * Restituisce data e ora della notifica.
     * @return data e ora della notifica
     */
    Timestamp getTimestamp();

    /**
     * Restituisce il contenuto pubblicato associato alla notifica.
     * @return contenuto pubblicato associato alla notifica
     */
    T getPublishedContent();

    /**
     * Restituisce l'utente destinatario della notifica.
     * @return utente destinatario della notifica
     */
    User getTarget();

    /**
     * Imposta il contenuto pubblicato associato alla notifica.
     * @param publishedContent da associare alla notifica
     */
    void setPublishedContent(T publishedContent);

    /**
     * Imposta l'utente destinatario della notifica.
     * @param target da associare alla notifica
     */
    void setTarget(User target);
}
