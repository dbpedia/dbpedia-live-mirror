package org.dbpedia.extraction.live.mirror.helper;

import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/28/11
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Global {
    private static final Logger logger = LoggerFactory.getLogger(Global.class);

    private static Options options;

    static {
        try {
            setOptions(new Options(new File("mirror-live.ini")));
            logger.info("Options file read successfully");
        } catch (Exception exp) {
            logger.error("Options file cannot be read, download process cannot continue", exp);
            System.exit(0);
        }
    }

    private Global(){}

    public static Options getOptions() {
        return options;
    }

    public static void setOptions(Options options) {
        Global.options = options;
    }
}
