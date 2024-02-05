package org.unina.project.social.sessions;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.users.User;

@Data
public class GlobalSession implements Session {
    private @Nullable User user;
    private @Nullable Group group;

    @Override
    public void reset() {
        this.user = null;
        this.group = null;
    }
}
