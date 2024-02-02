package org.unina.project.social.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.unina.project.social.entities.PublishedContent;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Tipologie di analisi delle interazioni associate ai contenuti pubblicati.
 */
@RequiredArgsConstructor
public enum ReportEngagementType {
    PUBLICATIONS("Contenuti pubblicati", contents -> String.valueOf(contents.size()).describeConstable()),
    MAJOR_COMMENTS("Contenuti più commentati", contents -> contents.stream().max(Comparator.comparingInt(content -> content.getAllComments().size())).map(PublishedContent::getContent)),
    MINOR_COMMENTS("Contenuti meno commentati", contents -> contents.stream().min(Comparator.comparingInt(content -> content.getAllComments().size())).map(PublishedContent::getContent)),
    MAJOR_LIKES("Contenuti più piaciuti", contents -> contents.stream().max(Comparator.comparingInt(content -> content.getLikedBy().size())).map(PublishedContent::getContent)),
    MINOR_LIKES("Contenuti meno piaciuti", contents -> contents.stream().min(Comparator.comparingInt(content -> content.getLikedBy().size())).map(PublishedContent::getContent));

    @Getter
    private final String title;
    @Getter
    private final Function<Set<? extends PublishedContent<?>>, Optional<String>> extraction;
}
