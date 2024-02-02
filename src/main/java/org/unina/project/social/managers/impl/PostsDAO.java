package org.unina.project.social.managers.impl;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.unina.project.App;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.PostType;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.BasicPostsResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PostsDAO extends DAO<Post> {
    private final static @Language("SQL") String SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS = "SELECT id_post, author, fk_gruppo, tipo, post_with_likes.contenuto, silenzioso, ultima_mod, post_with_likes.data, liker, commento.id_commento AS comment FROM (SELECT id_post, post.fk_utente AS author, fk_gruppo, tipo, contenuto, silenzioso, ultima_mod, post.data, likepost.fk_utente AS liker FROM post LEFT JOIN likepost ON id_post = likepost.fk_post) AS post_with_likes LEFT JOIN (SELECT * FROM commento WHERE fk_commentopadre IS NULL) AS commento ON id_post = commento.fk_post";

    public PostsDAO(Database database) {
        super(database, new BasicPostsResultSetExtractor());
    }

    public Set<Post> getAll() {
        return getAll(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS);
    }

    public Set<Post> getAll(Group group) {
        return getAll(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS + " WHERE fk_gruppo = ?", new StatementMatch(1, group.getId(), Types.INTEGER));
    }

    public Set<Post> getAll(Group group, int month, int year) {
        return getAll(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS + " WHERE fk_gruppo = ? AND EXTRACT(MONTH FROM post_with_likes.data) = ? AND EXTRACT(YEAR FROM post_with_likes.data) = ?",
                new StatementMatch(1, group.getId(), Types.INTEGER),
                new StatementMatch(2, month, Types.INTEGER),
                new StatementMatch(3, year, Types.INTEGER)
        );
    }

    public Optional<Post> get(int id) {
        return get(SELECT_GROUPS_WITH_TAGS_AND_REQUESTERS + " WHERE id_post = ?", new StatementMatch(1, id, Types.INTEGER));
    }

    public Optional<Post> create(@NotNull User author, @NotNull Group group, @NotNull PostType type, @NotNull String content, boolean silent) {
        return create("INSERT INTO post (fk_utente, fk_gruppo, tipo, contenuto, silenzioso) VALUES (?, ?, ?, ?, ?) RETURNING id_post, fk_utente, fk_gruppo, tipo, contenuto, silenzioso, ultima_mod, data",
                new StatementMatch(1, author.getId(), Types.INTEGER),
                new StatementMatch(2, group.getId(), Types.INTEGER),
                new StatementMatch(3, type.getSqlParamName(), Types.OTHER),
                new StatementMatch(4, content, Types.VARCHAR),
                new StatementMatch(5, silent, Types.BOOLEAN));
    }

    public CompletableFuture<Integer> update(Post post) {
        CompletableFuture<Integer> result = update("UPDATE post SET fk_utente = ?, fk_gruppo = ?, tipo = ?, contenuto = ?, silenzioso = ?, ultima_mod = ? WHERE id_post = ?", post);
        // Update comments
        post.getComments().forEach(App.getInstance().getComments()::update);
        // Update likes
        getAsyncDatabase().update("DELETE FROM likepost WHERE fk_post = ?", statement -> statement.setInt(1, post.getId())).whenComplete((ignored, throwable) -> {
            if (throwable != null)
                return;
            for (User user : post.getLikedBy())
                getAsyncDatabase().update("INSERT INTO likepost (fk_utente, fk_post) VALUES (?, ?)", statement -> {
                    statement.setInt(1, user.getId());
                    statement.setInt(2, post.getId());
                });
        });
        return result;
    }

    public CompletableFuture<Integer> delete(Post post) {
        return delete("DELETE FROM post WHERE id_post = ?", new StatementMatch(1, post.getId(), Types.INTEGER));
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(Post post) {
        return new StatementMatch[]{
                new StatementMatch(1, post.getAuthor().getId(), Types.INTEGER),
                new StatementMatch(2, post.getGroup().getId(), Types.INTEGER),
                new StatementMatch(3, post.getType().getSqlParamName(), Types.OTHER),
                new StatementMatch(4, post.getContent(), Types.VARCHAR),
                new StatementMatch(5, post.isSilent(), Types.BOOLEAN),
                new StatementMatch(6, post.getLastEdit(), Types.TIMESTAMP),
                new StatementMatch(7, post.getId(), Types.INTEGER)
        };
    }
}
