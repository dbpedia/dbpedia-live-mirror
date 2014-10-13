package org.dbpedia.extraction.live.mirror.download;

import org.dbpedia.extraction.live.mirror.changesets.ChangesetCounter;
import org.dbpedia.extraction.live.mirror.helper.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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

    private LastDownloadDateManager() {
    }

    public static ChangesetCounter getLastDownloadDate(String strFileName) {
        String strLastResponseDate = Utils.getFileAsString(strFileName).trim();

        if (strLastResponseDate.isEmpty()) {
            throw new RuntimeException("Cannot read latest download date from " + strFileName);
        }

        return new ChangesetCounter(strLastResponseDate);

    }

    public static void writeLastDownloadDate(String strFileName, String strLastResponseDate) {

        try (OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(strFileName), "UTF8");) {

            osWriter.write(strLastResponseDate);
            osWriter.flush();

        } catch (IOException e) {
            logger.warn("Last download date cannot be written to file : " + strLastResponseDate, e);
        }
    }
}
