package org.unina.project.social.entities.comments;

import org.jetbrains.annotations.Nullable;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.posts.Post;

import java.util.Optional;

public interface Comment extends PublishedContent<Integer> {
    /**
     * Restituisce l'ID del post a cui il commento è associato.
     * @return ID del post
     */
    int getPostId();

    /**
     * Restituisce il commento genitore, se presente.
     * @return oggetto {@link Comment} rappresentante il commento genitore
     */
    @Nullable Comment getParentComment();

    /**
     * Imposta l'ID del post a cui il commento è associato.
     * @param postId ID del post da impostare
     */
    void setPostId(int postId);

    /**
     * Imposta il commento genitore.
     * @param parentComment oggetto {@link Comment} rappresentante il commento genitore da impostare
     */
    void setParentComment(@Nullable Comment parentComment);

    /**
     * Restituisce un Optional contenente il post associato a questo commento.
     * @return Optional contenente il {@link Post} associato, o vuoto se non presente
     */
    Optional<Post> getPost();

    /**
     * Imposta il post associato a questo commento.
     * @param post Oggetto {@link Post} rappresentante il post da associare al commento
     */
    void setPost(Post post);
}
