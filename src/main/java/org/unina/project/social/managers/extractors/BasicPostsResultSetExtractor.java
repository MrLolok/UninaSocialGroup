package org.unina.project.social.managers.extractors;

import org.unina.project.App;
import org.unina.project.database.utils.ResultSetUtils;
import org.unina.project.social.entities.posts.DefaultPost;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.enums.PostType;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class BasicPostsResultSetExtractor extends AbstractPersistentEntityExtractor<Post> {
    @Override
    protected void load(Set<Post> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        int id = set.getInt("ID_post");
        int liker = ResultSetUtils.hasColumn(set, "liker") ? set.getInt("liker") : -1;
        int comment = ResultSetUtils.hasColumn(set, "comment") ? set.getInt("comment") : -1;

        Post post = null;
        for (Post loaded : currentLoadedEntities)
            if (loaded.getId() == id) {
                post = loaded;
                break;
            }
        if (post == null) {
            post = new DefaultPost(id);
            post.setContent(set.getString("contenuto"));
            post.setSilent(set.getBoolean("silenzioso"));
            post.setLastEdit(set.getTimestamp("ultima_mod"));
            post.setTimestamp(set.getTimestamp("data"));
            Object rawTypeName = set.getObject("tipo");
            if (rawTypeName != null) {
                Optional<PostType> type = PostType.get(rawTypeName.toString());
                type.ifPresent(post::setType);
            }
            int userAuthorId = ResultSetUtils.hasColumn(set, "author") ? set.getInt("author") : set.getInt("fk_utente");
            App.getInstance().getUsers().get(userAuthorId).ifPresent(post::setAuthor);
            int groupAssociatedId = set.getInt("FK_Gruppo");
            App.getInstance().getGroups().get(groupAssociatedId).ifPresent(post::setGroup);
            currentLoadedEntities.add(post);
        }

        // Variabile final per le espressioni lambda
        Post result = post;
        if (liker > 0)
            App.getInstance().getUsers().get(liker).ifPresent(user -> result.getLikedBy().add(user));
        if (comment > 0 && result.getComments().stream().noneMatch(comm -> comm.getId() == comment))
            App.getInstance().getComments().get(comment).ifPresent(comm -> result.getComments().add(comm));
    }

}