package org.unina.project.social.entities;

import lombok.Getter;
import lombok.Setter;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.entities.users.User;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public abstract class AbstractPublishedContent<T> extends AbstractIdentifiableEntity<T> implements PublishedContent<T> {
    private User author;
    private String content;
    private Set<User> likedBy = new HashSet<>();
    private Set<Comment> comments = new HashSet<>();
    private Timestamp timestamp;

    public AbstractPublishedContent(T id) {
        super(id);
    }

    @Override
    public boolean isLiked(User user) {
        return this.likedBy.contains(user);
    }

    @Override
    public Set<Comment> getAllComments() {
        Set<Comment> comments = new HashSet<>();
        for (Comment comment : this.comments) {
            comments.add(comment);
            Set<Comment> children = comment.getAllComments();
            comments.addAll(children);
        }
        return comments;
    }
}
