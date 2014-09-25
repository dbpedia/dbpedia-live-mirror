package org.dbpedia.extraction.live.mirror.main;

import org.dbpedia.extraction.live.mirror.helper.*;
import org.dbpedia.extraction.live.mirror.iterator.UpdatesIterator;
import org.dbpedia.extraction.live.mirror.sparul.JDBCPoolConnection;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/24/11
 * Time: 4:26 PM
 * This class is originally created from class defined in http://www.devdaily.com/java/edu/pj/pj010011
 * which is created by http://www.DevDaily.com
 */
public final class Main {


    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Main.class);

    private Main(){}

    public static void main(String[] args) {
        boolean deleteFiles = false;
        if ((Global.getOptions().get("deleteFilesAfterCompletion") != null) &&
                (Global.getOptions().get("deleteFilesAfterCompletion").compareTo("") != 0))
            deleteFiles = Boolean.parseBoolean(Global.getOptions().get("deleteFilesAfterCompletion"));
        //Initialize logger


        DownloadTimeCounter lastDownload = LastDownloadDateManager.getLastDownloadDate("lastDownloadDate.dat");


        UpdatesIterator iterator = new UpdatesIterator(lastDownload, 3);

        while (iterator.hasNext()) {
            DownloadTimeCounter cntr = iterator.next();

            //Since next returns null, if now new updates are available, then we should not go any further,
            //and just wait for mor updates
            if (cntr == null)
                continue;

            //u = new URL("http://dbpedia.aksw.org/updates.live.dbpedia.org/2011/05/22/00/000001.added.nt.gz");
//            String fullFilenameToBeDownloaded = Global.options.get("UpdateServerAddress") + lastDownload.getFormattedFilePath() + ".added.nt.gz";
            //String compressedDownloadedFile = FileDownloader.downloadFile(options.get("UpdateServerAddress") + "2011/05/22/00/000001.added.nt.gz",
            //         options.get("UpdatesDownloadFolder"));

            String addedTriplesFilename, deletedTriplesFilename;

            addedTriplesFilename = Global.getOptions().get("UpdateServerAddress") + cntr.getFormattedFilePath() +
                    Global.getOptions().get("addedTriplesFileExtension");

            deletedTriplesFilename = Global.getOptions().get("UpdateServerAddress") + cntr.getFormattedFilePath() +
                    Global.getOptions().get("removedTriplesFileExtension");

            // changesets default to empty
            List<String> triplesToDelete = Arrays.asList();
            List<String> triplesToAdd = Arrays.asList();

            //Download and decompress the file of deleted triples
            String deletedCompressedDownloadedFile = Utils.downloadFile(deletedTriplesFilename, Global.getOptions().get("UpdatesDownloadFolder"));

            if (deletedCompressedDownloadedFile.compareTo("") != 0) {

                String decompressedDeletedNTriplesFile = Utils.decompressGZipFile(deletedCompressedDownloadedFile, deleteFiles);
                triplesToDelete = Utils.getTriplesFromFile(decompressedDeletedNTriplesFile);
                if (deleteFiles) {
                    Utils.deleteFile(decompressedDeletedNTriplesFile);
                }

                //Reset the number of failed trails, since the file is found and downloaded successfully
                Global.setNumberOfSuccessiveFailedTrails(0);
            }

            //Download and decompress the file of added triples
            String addedCompressedDownloadedFile = Utils.downloadFile(addedTriplesFilename, Global.getOptions().get("UpdatesDownloadFolder"));

            if (addedCompressedDownloadedFile.compareTo("") != 0) {
                String decompressedAddedNTriplesFile = Utils.decompressGZipFile(addedCompressedDownloadedFile, deleteFiles);
                triplesToAdd = Utils.getTriplesFromFile(decompressedAddedNTriplesFile);
                if (deleteFiles) {
                    Utils.deleteFile(decompressedAddedNTriplesFile);
                }

                //Reset the number of failed trails, since the file is found and downloaded successfully
                Global.setNumberOfSuccessiveFailedTrails(0);
            }

            Changeset changeset = new Changeset(cntr.toString(), triplesToAdd, triplesToDelete);
            SPARULMediator.applyChangeset(changeset);


            //No files with that sequence so that indicates a failed trail, so we increment the counter of unsuccessful queries
            if ((addedCompressedDownloadedFile.compareTo("") == 0) && (deletedCompressedDownloadedFile.compareTo("") == 0)) {
                Global.setNumberOfSuccessiveFailedTrails(Global.getNumberOfSuccessiveFailedTrails() + 1);
            }
            LastDownloadDateManager.writeLastDownloadDate("lastDownloadDate.dat", cntr.toString());

        }

        JDBCPoolConnection.shutdown();

    }  // end of main

}
