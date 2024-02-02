package org.unina.project.social.entities.tags;

import org.unina.project.social.entities.IdentifiableEntity;

public interface Tag extends IdentifiableEntity<String> {
    /**
     * Restituisce la keyword, o scritta, associata al tag.
     * @return keyword associata al tag
     */
    String getKeyword();

    /**
     * Restituisce la keyword del tag con la prima lettera maiuscola.
     * @return keyword associata al tag con la prima lettera maiuscola
     */
    String getCapitalizedKeyword();
}
