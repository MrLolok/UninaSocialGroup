package org.unina.project.social.managers.impl;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.BasicCommentsResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommentsDAO extends DAO<Comment> {
    private final static @Language("SQL") String SELECT_COMMENTS_WITH_LIKES = "SELECT id_commento, commento.fk_utente AS author, fk_post, fk_commentopadre, contenuto, commento.data, likecommento.fk_utente AS liker FROM commento LEFT JOIN likecommento ON commento.id_commento = likecommento.fk_commento";

    public CommentsDAO(Database database) {
        super(database, new BasicCommentsResultSetExtractor());
    }

    public Set<Comment> getAll() {
        return getAll(SELECT_COMMENTS_WITH_LIKES);
    }

    public Optional<Comment> get(int id) {
        return get(SELECT_COMMENTS_WITH_LIKES + " WHERE id_commento = ?", new StatementMatch(1, id, Types.INTEGER));
    }

    public Set<Comment> getChildren(int parent) {
        return getAll(SELECT_COMMENTS_WITH_LIKES + " WHERE fk_commentopadre = ?", new StatementMatch(1, parent, Types.INTEGER));
    }

    public Set<Comment> getAll(Post post) {
        return getAll(SELECT_COMMENTS_WITH_LIKES + " WHERE fk_post = ? AND fk_commentopadre IS NULL", new StatementMatch(1, post.getId(), Types.INTEGER));
    }

    public Set<Comment> getAll(Group group) {
        return getAll(SELECT_COMMENTS_WITH_LIKES + " WHERE fk_post IN (SELECT id_post FROM post WHERE fk_gruppo = ?)", new StatementMatch(1, group.getId(), Types.INTEGER));
    }

    public Set<Comment> getAll(Group group, int month, int year) {
        return getAll(SELECT_COMMENTS_WITH_LIKES + " WHERE fk_post IN (SELECT id_post FROM post WHERE fk_gruppo = ?) AND EXTRACT(MONTH FROM commento.data) = ? AND EXTRACT(YEAR FROM commento.data) = ?",
                new StatementMatch(1, group.getId(), Types.INTEGER),
                new StatementMatch(2, month, Types.INTEGER),
                new StatementMatch(3, year, Types.INTEGER)
        );
    }

    public Optional<Comment> create(@NotNull User author, @NotNull Post post, @Nullable Comment parent, @NotNull String content) {
        return create("INSERT INTO commento (fk_utente, fk_post, fk_commentopadre, contenuto) VALUES (?, ?, ?, ?) RETURNING id_commento, fk_utente, fk_post, fk_commentopadre, contenuto, data",
                new StatementMatch(1, author.getId(), Types.INTEGER),
                new StatementMatch(2, post.getId(), Types.INTEGER),
                new StatementMatch(3, parent == null ? null : parent.getId(), parent == null ? Types.NULL : Types.INTEGER),
                new StatementMatch(4, content, Types.VARCHAR)
        );
    }

    public CompletableFuture<Integer> update(Comment comment) {
        comment.getComments().forEach(this::update);
        CompletableFuture<Integer> result = update("UPDATE commento SET fk_utente = ?, fk_post = ?, fk_commentopadre = ?, contenuto = ? WHERE id_commento= ?", comment);
        // Update likes
        getAsyncDatabase().update("DELETE FROM likecommento WHERE fk_commento = ?", statement -> statement.setInt(1, comment.getId())).whenComplete(((ignored, throwable) -> {
            if (throwable != null)
                return;
            for (User user : comment.getLikedBy())
                getAsyncDatabase().update("INSERT INTO likecommento (fk_utente, fk_commento) VALUES (?, ?)", statement -> {
                    statement.setInt(1, user.getId());
                    statement.setInt(2, comment.getId());
                });
        }));

        return result;
    }

    public CompletableFuture<Integer> delete(Post post) {
        return delete("DELETE FROM commento WHERE fk_post = ?", new StatementMatch(1, post.getId(), Types.INTEGER));
    }

    public CompletableFuture<Integer> delete(Comment comment) {
        return delete("DELETE FROM commento WHERE id_commento = ?", new StatementMatch(1, comment.getId(), Types.INTEGER));
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(Comment comment) {
        return new StatementMatch[] {
                new StatementMatch(1, comment.getAuthor().getId(), Types.INTEGER),
                new StatementMatch(2, comment.getPostId(), Types.INTEGER),
                new StatementMatch(3, comment.getParentComment() == null ? null : comment.getParentComment().getId(), comment.getParentComment() == null ? Types.NULL : Types.INTEGER),
                new StatementMatch(4, comment.getContent(), Types.VARCHAR),
                new StatementMatch(5, comment.getId(), Types.INTEGER)
        };
    }
}
