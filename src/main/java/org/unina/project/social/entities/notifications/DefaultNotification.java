package org.unina.project.social.entities.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.users.User;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter @Setter
public class DefaultNotification<T extends PublishedContent<?>> implements Notification<T> {
    private T publishedContent;
    private User target;

    @Override
    public Timestamp getTimestamp() {
        return this.publishedContent.getTimestamp();
    }
}
