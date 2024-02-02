package org.unina.project.social.managers.impl;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.App;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.GroupType;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.BasicGroupsResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GroupsDAO extends DAO<Group> {
    private final static @Language("SQL") String SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS = "SELECT id_gruppo, fk_creatore, nome, descrizione, tipo, creazione, fk_tag, group_with_tags_requesters.fk_utente AS requester, membro.fk_utente AS member FROM (SELECT * FROM (SELECT * FROM gruppo LEFT JOIN classificazione ON gruppo.id_gruppo = classificazione.fk_gruppo) AS group_with_tags LEFT JOIN richiestaaccesso ON richiestaaccesso.fk_gruppo = group_with_tags.id_gruppo) group_with_tags_requesters LEFT JOIN membro ON membro.fk_gruppo = group_with_tags_requesters.id_gruppo";

    public GroupsDAO(Database database) {
        super(database, new BasicGroupsResultSetExtractor());
    }

    public Set<Group> getAll() {
        return getAll(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS);
    }

    public Optional<Group> get(int id) {
        return get(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS + " WHERE id_gruppo = ?", new StatementMatch(1, id, Types.INTEGER));
    }

    public Optional<Group> create(@NotNull User creator, @NotNull String name, @Nullable String description, @NotNull GroupType type) {
        return create("INSERT INTO gruppo (nome, descrizione, tipo, fk_creatore) VALUES (?, ?, ?, ?) RETURNING id_gruppo, nome, descrizione, tipo, fk_creatore, creazione",
                new StatementMatch(1, name, Types.VARCHAR),
                new StatementMatch(2, description, Types.VARCHAR),
                new StatementMatch(3, type.getSqlParamName(), Types.OTHER),
                new StatementMatch(4, creator.getId(), Types.INTEGER));
    }

    public CompletableFuture<Integer> update(Group group) {
        CompletableFuture<Integer> result = update("UPDATE gruppo SET nome = ?, descrizione = ?, tipo = ?, fk_creatore = ? WHERE id_gruppo = ?", group);
        // Update members
        setGroupAssociatedUsers("membro", group, group.getMembers());
        // Update requesters
        setGroupAssociatedUsers("richiestaaccesso", group, group.getRequesters());
        // Update tags
        TagsDAO tags = App.getInstance().getTags();
        tags.delete(group).whenComplete((ignored, throwable) -> {
            if (throwable != null)
                return;
            for (Tag tag : group.getTags())
                tags.create(tag.getKeyword(), group);
        });
        return result;
    }

    public CompletableFuture<Integer> delete(Group group) {
        return delete("DELETE FROM gruppo WHERE id_gruppo = ?", new StatementMatch(1, group.getId(), Types.INTEGER));
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(Group group) {
        return new StatementMatch[]{
                new StatementMatch(1, group.getName(), Types.VARCHAR),
                new StatementMatch(2, group.getDescription(), Types.VARCHAR),
                new StatementMatch(3, group.getType().getSqlParamName(), Types.OTHER),
                new StatementMatch(4, group.getCreator().getId(), Types.INTEGER),
                new StatementMatch(5, group.getId(), Types.INTEGER)
        };
    }

    private void setGroupAssociatedUsers(String table, Group group, Set<User> users) {
        getAsyncDatabase().update("DELETE FROM " + table + " WHERE fk_utente <> ? AND fk_gruppo = ?", statement -> {
            statement.setInt(1, group.getCreator().getId());
            statement.setInt(2, group.getId());
        }).whenComplete((ignored, throwable) -> {
            if (throwable != null)
                return;
            users.remove(group.getCreator());
            for (User user : users) {
                getAsyncDatabase().update("INSERT INTO " + table + " (fk_utente, fk_gruppo) VALUES (?, ?)", statement -> {
                    statement.setInt(1, user.getId());
                    statement.setInt(2, group.getId());
                });
            }
        });
    }
}
