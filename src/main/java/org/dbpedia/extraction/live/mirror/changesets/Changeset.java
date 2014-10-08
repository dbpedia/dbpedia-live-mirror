package org.dbpedia.extraction.live.mirror.changesets;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Holds a single changeset, must not be initialized with multiple changesets
 *
 * @author Dimitris Kontokostas
 * @since 9/25/14 11:05 AM
 */
public final class Changeset {
    private final String id;
    private final Collection<String> additions;
    private final Collection<String> deletions;
    private final Collection<String> cleared;

    public Changeset(String id, Collection<String> additions, Collection<String> deletions, Collection<String> cleared) {
        this.id = id;

        // Keep the changeset unique
        this.additions = Collections.unmodifiableCollection(new LinkedHashSet<>(additions));
        this.deletions = Collections.unmodifiableCollection(new LinkedHashSet<>(deletions));
        this.cleared = Collections.unmodifiableCollection(new LinkedHashSet<>(cleared));
    }

    public String getId() {
        return id;
    }

    public Collection<String> getAdditions() {
        return additions;
    }

    public Collection<String> getDeletions() {
        return deletions;
    }

    public int triplesAdded() {
        return additions.size();
    }

    public int triplesDeleted() {
        return deletions.size();
    }

    public int triplesCleared() {
        return cleared.size();
    }

    public Collection<String> getCleared() {
        return cleared;
    }
}
