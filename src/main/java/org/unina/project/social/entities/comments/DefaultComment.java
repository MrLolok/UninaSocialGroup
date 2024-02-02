package org.unina.project.social.entities.comments;

import lombok.Getter;
import lombok.Setter;
import org.unina.project.App;
import org.unina.project.social.entities.AbstractPublishedContent;
import org.unina.project.social.entities.posts.Post;

import java.util.Optional;

@Getter @Setter
public class DefaultComment extends AbstractPublishedContent<Integer> implements Comment {
    private int postId;
    private Comment parentComment;

    public DefaultComment(int id) {
        super(id);
    }

    @Override
    public Optional<Post> getPost() {
        return App.getInstance().getPosts().get(this.postId);
    }

    @Override
    public void setPost(Post post) {
        this.postId = post.getId();
    }
}
