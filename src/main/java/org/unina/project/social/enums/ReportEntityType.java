package org.unina.project.social.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.groups.Group;

import java.time.LocalDate;
import java.util.Set;

/**
 * Tipologie di entità analizzabili associate a un gruppo.
 * @see ReportEngagementType
 */
@RequiredArgsConstructor
public enum ReportEntityType {
    POST("Post", "far-edit"),
    COMMENT("Commento", "far-comment");

    @Getter
    private final String display, iconName;

    public Set<? extends PublishedContent<?>> getContents(Group group, LocalDate date) {
        return switch (this) {
            case POST -> App.getInstance().getPosts().getAll(group, date.getMonthValue(), date.getYear());
            case COMMENT -> App.getInstance().getComments().getAll(group, date.getMonthValue(), date.getYear());
        };
    }
}
