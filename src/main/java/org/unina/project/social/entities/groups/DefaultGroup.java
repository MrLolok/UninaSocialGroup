package org.unina.project.social.entities.groups;

import lombok.Getter;
import lombok.Setter;
import org.unina.project.social.entities.AbstractIdentifiableEntity;
import org.unina.project.social.entities.tags.DefaultTag;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.GroupType;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class DefaultGroup extends AbstractIdentifiableEntity<Integer> implements Group {
    private User creator;
    private String name;
    private String description;
    private GroupType type;
    private Timestamp creation;
    private Set<User> members = new HashSet<>(), requesters = new HashSet<>();
    private Set<Tag> tags = new HashSet<>();

    public DefaultGroup(int id) {
        super(id);
    }

    @Override
    public void addTag(String keyword) {
        Tag tag = new DefaultTag(keyword);
        this.tags.add(tag);
    }

    @Override
    public void removeTag(String keyword) {
        this.tags.removeIf(tag -> tag.getKeyword().equalsIgnoreCase(keyword));
    }

    @Override
    public boolean isMember(User user) {
        return members.contains(user);
    }

    @Override
    public boolean isRequester(User user) {
        return requesters.contains(user);
    }
}
