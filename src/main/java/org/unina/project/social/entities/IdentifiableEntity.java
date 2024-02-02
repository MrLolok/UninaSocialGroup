package org.unina.project.social.entities;

/**
 * Questa classe marca gli oggetti che possono essere identificati
 * tramite un attributo denominato "id". Esso si rifà alla PRIMARY
 * KEY della stessa entità nel database.
 * @see org.unina.project.social.entities.users.User
 * @see org.unina.project.social.entities.groups.Group
 * @see org.unina.project.social.entities.tags.Tag
 * @see org.unina.project.social.entities.posts.Post
 * @see org.unina.project.social.entities.comments.Comment
 * @param <T> tipo dell'identificatore
 */
public interface IdentifiableEntity<T> {
    /**
     * Restituisce l'ID associato all'entità.
     * @return ID associato all'entità
     */
    T getId();
}
