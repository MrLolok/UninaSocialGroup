package org.unina.project.social.entities.tags;

import org.unina.project.social.entities.AbstractIdentifiableEntity;

public class DefaultTag extends AbstractIdentifiableEntity<String> implements Tag {
    public DefaultTag(String id) {
        super(id);
    }

    @Override
    public String getKeyword() {
        return super.getId();
    }

    @Override
    public String getCapitalizedKeyword() {
        return this.getKeyword().substring(0, 1).toUpperCase() + this.getKeyword().substring(1);
    }
}
