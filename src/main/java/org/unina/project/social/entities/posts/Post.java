package org.unina.project.social.entities.posts;

import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.enums.PostType;

import java.sql.Timestamp;

public interface Post extends PublishedContent<Integer> {
    /**
     * Restituisce il gruppo a cui appartiene il post.
     * @return gruppo a cui appartiene il post
     */
    Group getGroup();

    /**
     * Restituisce il tipo di post.
     * @return tipo di post
     */
    PostType getType();

    /**
     * Verifica se il post è silenzioso.
     * @return true se il post è silenzioso
     */
    boolean isSilent();

    /**
     * Restituisce data e ora dell'ultima modifica al post.
     * @return data e ora dell'ultima modifica al post
     */
    Timestamp getLastEdit();

    /**
     * Imposta il gruppo a cui appartiene il post.
     * @param group da associare al post
     */
    void setGroup(Group group);

    /**
     * Imposta il tipo di post.
     * @param type di post da impostare
     */
    void setType(PostType type);

    /**
     * Imposta lo stato di silenziosità del post.
     * @param silent true se il post deve essere silenzioso
     */
    void setSilent(boolean silent);

    /**
     * Imposta il timestamp dell'ultima modifica al post.
     * @param lastEdit data e ora dell'ultima modifica da impostare per il post
     */
    void setLastEdit(Timestamp lastEdit);
}
