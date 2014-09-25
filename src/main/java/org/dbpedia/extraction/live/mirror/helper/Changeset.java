package org.dbpedia.extraction.live.mirror.helper;

import java.util.*;

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

    public Changeset(String id, Collection<String> additions, Collection<String> deletions) {
        this.id = id;

        // Keep the changeset unique
        this.additions = Collections.unmodifiableCollection(new LinkedHashSet<String>(additions));
        this.deletions = Collections.unmodifiableCollection(new LinkedHashSet<String>(deletions));
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
}
