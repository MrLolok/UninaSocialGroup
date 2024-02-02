package org.unina.project.social.managers.extractors.notifications;

import org.unina.project.App;
import org.unina.project.social.entities.posts.Post;

import java.util.Optional;

public class PostNotificationsResultSetExtractor extends AbstractNotificationsResultSetExtractor<Post> {
    public PostNotificationsResultSetExtractor() {
        super("fk_post");
    }

    @Override
    protected Optional<Post> getPublishedContent(int id) {
        return App.getInstance().getPosts().get(id);
    }
}
