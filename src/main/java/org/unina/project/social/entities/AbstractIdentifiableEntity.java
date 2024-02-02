package org.unina.project.social.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractIdentifiableEntity<T> implements IdentifiableEntity<T> {
    @Getter
    private final T id;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IdentifiableEntity<?> identifiable && identifiable.getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
