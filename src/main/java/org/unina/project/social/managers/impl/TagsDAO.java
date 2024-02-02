package org.unina.project.social.managers.impl;

import org.jetbrains.annotations.NotNull;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.BasicTagsResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TagsDAO extends DAO<Tag> {
    public TagsDAO(Database database) {
        super(database, new BasicTagsResultSetExtractor());
    }

    public Set<Tag> getAll() {
        return getAll("SELECT * FROM tag");
    }

    public Optional<Tag> get(String keyword) {
        return get("SELECT * FROM tag WHERE keyword = ?", new StatementMatch(1, keyword, Types.VARCHAR));
    }

    public Optional<Tag> create(@NotNull String keyword) {
        return create("INSERT INTO tag (keyword) VALUES (?) RETURNING keyword", new StatementMatch(1, keyword, Types.VARCHAR));
    }

    public Optional<Tag> create(@NotNull String keyword, @NotNull Group group) {
        return create("INSERT INTO classificazione (fk_tag, fk_gruppo) VALUES (?, ?) RETURNING fk_tag",
                new StatementMatch(1, keyword, Types.VARCHAR),
                new StatementMatch(2, group.getId(), Types.INTEGER));
    }

    public CompletableFuture<Integer> update(Tag tag, String oldKeyword) {
        return update("UPDATE tag SET keyword = ? WHERE keyword = ?", tag, new StatementMatch(1, oldKeyword, Types.VARCHAR));
    }

    public CompletableFuture<Integer> delete(Group group) {
        return delete("DELETE FROM classificazione WHERE fk_gruppo = ?", new StatementMatch(1, group.getId(), Types.INTEGER));
    }

    public CompletableFuture<Integer> delete(Tag tag) {
        return delete("DELETE FROM tag WHERE keyword = ?", new StatementMatch(1, tag.getId(), Types.VARCHAR));
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(Tag tag) {
        return new StatementMatch[] {
                new StatementMatch(1, tag.getKeyword(), Types.VARCHAR)
        };
    }
}
