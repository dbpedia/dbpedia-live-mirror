package org.dbpedia.extraction.live.mirror.helper;

/**
 * Defines how the ontology update iteration will behave
 *
 * @author Dimitris Kontokostas
 * @since 9/26/14 11:41 AM
 */
public enum UpdateStrategy {
    /**
     * Keeps looping
     */
    Endless,

    /**
     * Updates once and then exits
     */
    Onetime
}
