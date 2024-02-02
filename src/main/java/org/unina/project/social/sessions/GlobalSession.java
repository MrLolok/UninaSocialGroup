package org.unina.project.social.sessions;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.users.User;

import java.sql.Timestamp;

@Data
public class GlobalSession implements Session {
    private @Nullable User user;
    private @Nullable Group group;
    private @Nullable Timestamp start, end;

    @Override
    public void setUser(User user) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user != null) {
            start = now; end = null;
        } else
            end = now;
        this.user = user;
    }

    @Override
    public void reset() {
        this.user = null;
        this.group = null;
    }
}
