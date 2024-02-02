package org.unina.project.social.entities.posts;

import lombok.Getter;
import lombok.Setter;
import org.unina.project.social.entities.AbstractPublishedContent;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.enums.PostType;

import java.sql.Timestamp;

@Getter @Setter
public class DefaultPost extends AbstractPublishedContent<Integer> implements Post {
    private Group group;
    private PostType type;
    private boolean silent;
    private Timestamp lastEdit;

    public DefaultPost(int id) {
        super(id);
    }
}
