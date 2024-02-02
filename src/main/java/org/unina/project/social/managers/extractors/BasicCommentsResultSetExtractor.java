package org.unina.project.social.managers.extractors;

import org.unina.project.App;
import org.unina.project.database.utils.ResultSetUtils;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.comments.DefaultComment;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class BasicCommentsResultSetExtractor extends AbstractPersistentEntityExtractor<Comment> {
    @Override
    protected void load(Set<Comment> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        int id = set.getInt("id_commento");
        int liker = ResultSetUtils.hasColumn(set, "liker") ? set.getInt("liker") : -1;
        Comment comment = null;
        for (Comment loaded : currentLoadedEntities)
            if (loaded.getId() == id) {
                comment = loaded;
                break;
            }
        if (comment == null) {
            comment = new DefaultComment(id);
            int userAuthorId = ResultSetUtils.hasColumn(set, "author") ? set.getInt("author") : set.getInt("fk_utente");
            App.getInstance().getUsers().get(userAuthorId).ifPresent(comment::setAuthor);
            comment.setPostId(set.getInt("fk_post"));
            comment.setContent(set.getString("contenuto"));
            comment.setTimestamp(set.getTimestamp("data"));
            currentLoadedEntities.add(comment);
        }
        Comment result = comment;
        App.getInstance().getComments().getChildren(id).forEach(child -> {
            result.getComments().add(child);
            child.setParentComment(result);
        });
        if (liker > 0)
            App.getInstance().getUsers().get(liker).ifPresent(user -> result.getLikedBy().add(user));
    }
}