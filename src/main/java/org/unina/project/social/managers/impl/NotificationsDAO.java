package org.unina.project.social.managers.impl;

import org.jetbrains.annotations.NotNull;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.notifications.Notification;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.notifications.AbstractNotificationsResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NotificationsDAO<T extends PublishedContent<Integer>> extends DAO<Notification<T>> {
    private final String table, contentReferenceColumn;

    public NotificationsDAO(Database database, AbstractNotificationsResultSetExtractor<T> extractor, String table, String contentReferenceColumn) {
        super(database, extractor);
        this.table = table;
        this.contentReferenceColumn = contentReferenceColumn;
    }

    public Set<Notification<T>> getAll() {
        return getAll("SELECT * FROM " + table);
    }

    public Set<Notification<T>> getAll(User user) {
        return getAll("SELECT * FROM " + table + " WHERE fk_utente = ?", new StatementMatch(1, user.getId(), Types.INTEGER));
    }

    public Set<Notification<T>> getAll(T content) {
        return getAll("SELECT * FROM " + table + " WHERE " + contentReferenceColumn + " = ?", new StatementMatch(1, content.getId(), Types.INTEGER));
    }

    public Optional<Notification<T>> get(User user, T content) {
        return get("SELECT * FROM " + table + " WHERE fk_utente = ? AND " + contentReferenceColumn + " = 1 LIMIT 1",
                new StatementMatch(1, user.getId(), Types.INTEGER),
                new StatementMatch(2, content.getId(), Types.INTEGER)
        );
    }

    public Optional<Notification<T>> create(@NotNull User user, @NotNull T content) {
        return create("INSERT INTO " + table + " (fk_utente, " + contentReferenceColumn + ") VALUES (?, ?) RETURNING fk_utente, " + contentReferenceColumn,
                new StatementMatch(1, user.getId(), Types.INTEGER),
                new StatementMatch(2, content.getId(), Types.INTEGER)
        );
    }

    public CompletableFuture<Integer> update(Notification<T> notification, User oldUser, T oldContent) {
        return update("UPDATE " + table + " SET fk_utente = ?, " + contentReferenceColumn + " = ? WHERE fk_utente = ? AND " + contentReferenceColumn + " = ?", notification,
                new StatementMatch(3, oldUser.getId(), Types.INTEGER),
                new StatementMatch(4, oldContent.getId(), Types.INTEGER)
        );
    }

    public CompletableFuture<Integer> delete(Notification<T> notification) {
        return delete("DELETE FROM " + table + " WHERE fk_utente = ? AND " + contentReferenceColumn + " = ?",
                new StatementMatch(1, notification.getTarget().getId(), Types.INTEGER),
                new StatementMatch(1, notification.getPublishedContent().getId(), Types.INTEGER)
        );
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(Notification<T> notification) {
        return new StatementMatch[]{
                new StatementMatch(1, notification.getTarget().getId(), Types.INTEGER),
                new StatementMatch(2, notification.getPublishedContent().getId(), Types.INTEGER)
        };
    }
}
