package org.unina.project.social.managers.extractors;

import org.unina.project.App;
import org.unina.project.database.utils.ResultSetUtils;
import org.unina.project.social.entities.groups.DefaultGroup;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.enums.GroupType;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class BasicGroupsResultSetExtractor extends AbstractPersistentEntityExtractor<Group> {
    @Override
    protected void load(Set<Group> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        int id = set.getInt("ID_Gruppo");
        int requester = -1, member = -1;
        String tag = null;

        if (ResultSetUtils.hasColumn(set, "requester"))
            requester = set.getInt("requester");
        if (ResultSetUtils.hasColumn(set, "member"))
            member = set.getInt("member");
        if (ResultSetUtils.hasColumn(set, "fk_tag"))
            tag = set.getString("fk_tag");

        Group group = null;
        for (Group loaded : currentLoadedEntities)
            if (loaded.getId() == id) {
                group = loaded;
                break;
            }
        if (group == null) {
            group = new DefaultGroup(id);
            group.setName(set.getString("nome"));
            group.setDescription(set.getString("descrizione"));
            group.setCreation(set.getTimestamp("creazione"));
            Object rawTypeName = set.getObject("tipo");
            if (rawTypeName != null) {
                Optional<GroupType> type = GroupType.get(rawTypeName.toString());
                type.ifPresent(group::setType);
            }
            int userCreatorId = set.getInt("FK_Creatore");
            App.getInstance().getUsers().get(userCreatorId).ifPresent(group::setCreator);
            currentLoadedEntities.add(group);
        }

        // Variabile final per le espressioni lambda
        Group result = group;
        if (requester > 0)
            App.getInstance().getUsers().get(requester).ifPresent(user -> result.getRequesters().add(user));
        if (member > 0)
            App.getInstance().getUsers().get(member).ifPresent(user -> result.getMembers().add(user));
        if (tag != null)
            group.addTag(tag);
    }
}