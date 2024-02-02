package org.unina.project.social.managers.extractors;

import org.unina.project.social.entities.tags.DefaultTag;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class BasicTagsResultSetExtractor extends AbstractPersistentEntityExtractor<Tag> {
    @Override
    protected void load(Set<Tag> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        // L'indice della colonna = 1 vale sia per "tag.keyword" e "classificazione.fk_tag"
        String keyword = set.getString(1);
        Tag tag = new DefaultTag(keyword);
        currentLoadedEntities.add(tag);
    }
}