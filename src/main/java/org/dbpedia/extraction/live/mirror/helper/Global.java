package org.dbpedia.extraction.live.mirror.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ini4j.Options;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/28/11
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Global {

    public static Options options;
    public static int numberOfSuccessiveFailedTrails = 0;

    private static Logger logger = LoggerFactory.getLogger(Global.class);

    static {
        try{
            Global.options = new Options(new File("mirror-live.ini"));
            logger.info("Options file read successfully");
        }
        catch (Exception exp){
            logger.error("Options file cannot be read, download process cannot continue", exp);
            System.exit(0);
        }
    }

}
