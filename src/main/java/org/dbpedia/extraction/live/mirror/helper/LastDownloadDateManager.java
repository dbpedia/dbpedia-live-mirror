package org.dbpedia.extraction.live.mirror.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Morsey
 * Date: Jul 28, 2010
 * Time: 6:26:07 PM
 * This class is responsible for reading and writing the response dates to files, in order to enable resume starting
 * from the last working point both for live extraction and for mapping update
 */
public final class LastDownloadDateManager {

    private static final Logger logger = LoggerFactory.getLogger(LastDownloadDateManager.class);

    private LastDownloadDateManager() {}

    public static DownloadTimeCounter getLastDownloadDate(String strFileName) {
        String strLastResponseDate = null;
        FileInputStream fsLastResponseDateFile = null;

        try {
            fsLastResponseDateFile = new FileInputStream(strFileName);

            int ch;
            strLastResponseDate = "";
            while ((ch = fsLastResponseDateFile.read()) != -1) {
                strLastResponseDate += (char) ch;
            }


        } catch (Exception exp) {
            throw new RuntimeException("Cannot read latest download date", exp);
        } finally {
            try {
                if (fsLastResponseDateFile != null)
                    fsLastResponseDateFile.close();

            } catch (Exception exp) {
                logger.warn("File " + strFileName + " cannot be closed due to " + exp.getMessage(), exp);
            }

        }

        return new DownloadTimeCounter(strLastResponseDate);

    }

    public static void writeLastDownloadDate(String strFileName, String strLastResponseDate) {
        FileOutputStream fsLastResponseDateFile = null;
        OutputStreamWriter osWriter = null;

        try {
            fsLastResponseDateFile = new FileOutputStream(strFileName);
            osWriter = new OutputStreamWriter(fsLastResponseDateFile);
            osWriter.write(strLastResponseDate);
            osWriter.flush();
        } catch (Exception exp) {
            logger.warn("Last download date cannot be written to file : " + strLastResponseDate + ", due to " + exp, exp);
        } finally {
            try {
                if (osWriter != null)
                    osWriter.close();

                if (fsLastResponseDateFile != null)
                    fsLastResponseDateFile.close();
            } catch (Exception exp) {
                logger.warn("File " + strFileName + " cannot be closed due to " + exp.getMessage(), exp);
            }
        }
    }
}
