package org.dbpedia.downloader.helper;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
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

    private static Logger logger;

    static {
        try{
            logger = Logger.getLogger(Global.class);
            logger.addAppender(new ConsoleAppender(new SimpleLayout()));
            Global.options = new Options(new File("dbpedia_updates_downloader.ini"));
            logger.info("Options file read successfully");
        }
        catch (Exception exp){
            logger.error("Options file cannot be read, download process cannot continue");
            System.exit(0);
        }
    }

}
