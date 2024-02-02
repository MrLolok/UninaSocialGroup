package org.unina.project.social.entities;

import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.users.User;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Questa classe marca gli oggetti considerati come contenuti pubblicati
 * da utenti sulla piattaforma.
 * @see org.unina.project.social.entities.posts.Post
 * @see org.unina.project.social.entities.comments.Comment
 * @param <T> tipo dell'identificatore
 */
public interface PublishedContent<T> extends IdentifiableEntity<T> {
    /**
     * Restituisce l'autore del contenuto.
     * @return oggetto {@link User} rappresentante l'autore
     */
    User getAuthor();

    /**
     * Restituisce il testo del contenuto.
     * @return stringa contenente il testo del contenuto
     */
    String getContent();

    /**
     * Restituisce l'insieme di utenti che hanno messo "like" al contenuto.
     * @return insieme di utenti che hanno messo "like"
     */
    Set<User> getLikedBy();

    /**
     * Restituisce l'insieme di commenti associati al contenuto.
     * @return insieme di oggetti {@link Comment} rappresentanti i commenti
     */
    Set<Comment> getComments();

    /**
     * Restituisce la data e l'ora di creazione del contenuto.
     * @return oggetto Timestamp rappresentante la data e l'ora di creazione
     */
    Timestamp getTimestamp();

    /**
     * Imposta l'autore del contenuto.
     * @param author oggetto {@link User} rappresentante l'autore da impostare
     */
    void setAuthor(User author);

    /**
     * Imposta il testo del contenuto.
     * @param content stringa contenente il testo da impostare
     */
    void setContent(String content);

    /**
     * Imposta l'insieme di utenti che hanno messo "mi piace" al contenuto.
     * @param users insieme di utenti da impostare
     */
    void setLikedBy(Set<User> users);

    /**
     * Imposta l'insieme di commenti associati al contenuto.
     * @param comments insieme di oggetti {@link Comment} rappresentanti i commenti da impostare
     */
    void setComments(Set<Comment> comments);

    /**
     * Imposta la data e l'ora di creazione del contenuto.
     * @param timestamp oggetto {@link Timestamp} rappresentante la data e l'ora da impostare
     */
    void setTimestamp(Timestamp timestamp);

    /**
     * Verifica se un determinato utente ha messo "mi piace" al contenuto.
     * @param user Utente da verificare
     * @return true se l'utente ha messo "like"
     */
    boolean isLiked(User user);

    /**
     * Restituisce l'insieme completo di commenti associati al contenuto.
     * @return insieme di oggetti Comment rappresentanti tutti i commenti
     */
    Set<Comment> getAllComments();
}
