package org.dbpedia.extraction.live.mirror.sparul;

/**
 * Custom SPARUL execution exception
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 11:13 AM
 */
public class SPARULException extends Exception {

    public SPARULException() {
        super();
    }

    public SPARULException(String message, Throwable e) {
        super(message, e);
    }

    public SPARULException(String message) {
        super(message);
    }

    public SPARULException(Throwable e) {
        super(e);
    }

    public SPARULException(Exception e) {
        super(e);
    }
}
