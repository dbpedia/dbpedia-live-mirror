package org.dbpedia.extraction.live.mirror.sparul;

/**
 * Interface to abstract different triple-store quirks
 *
 * @author Dimitris Kontokostas
 * @since 9 /24/14 11:12 AM
 */
public interface SPARULExecutor {

    /**
     * Executes a SPARUL Query
     *
     * @param sparulQuery the sparul query
     * @throws SPARULException the sPARUL exception
     */
    void executeSPARUL(String sparulQuery) throws SPARULException;


}
