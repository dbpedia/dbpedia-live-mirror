package org.dbpedia.downloader.download;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/25/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileDownloader {

    private static Logger logger;
    static {
        //Initialize logger
        logger = Logger.getLogger(FileDownloader.class);
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
    }
    /**
     * Downloads the file with passed URL to the passed folder
     * @param fileURL   URL of the file that should be downloaded
     * @param folderPath    The path to which this file should be saved
     * @return  The local full path of the downloaded file, empty string is returned if a problem occurs
     */
    public static String downloadFile(String fileURL, String folderPath){

      //-----------------------------------------------------//
      //  Step 1:  Start creating a few objects we'll need.
      //-----------------------------------------------------//

      URL u;
      InputStream is = null;
      DataInputStream dis;
      String fullFileName = "";
      FileOutputStream output = null;

      try {

              //Extract filename only without full path
              int lastSlashPos = fileURL.lastIndexOf("/");
              if(lastSlashPos < 0)
                  return "";

              //Initialize fullFileName with name of the file itself only
              fullFileName = fileURL.substring(lastSlashPos+1);

              //Create parent folder if it does not already exist
              File parentFolder = new File(folderPath);
              if(parentFolder != null)
                  parentFolder.mkdirs();

              //construct the full file path, including its parent folder
              fullFileName = folderPath + fullFileName;

             //------------------------------------------------------------//
             // Step 2:  Create the URL.                                   //
             //------------------------------------------------------------//
             // Note: Put your real URL here, or better yet, read it as a  //
             // command-line arg, or read it from a file.                  //
             //------------------------------------------------------------//

    //         u = new URL("http://live.dbpedia.org/liveupdates");

             u = new URL(fileURL);

             //----------------------------------------------//
             // Step 3:  Open an input stream from the url.  //
             //----------------------------------------------//

             is = u.openStream();         // throws an IOException

             //-------------------------------------------------------------//
             // Step 4:                                                     //
             //-------------------------------------------------------------//
             // Convert the InputStream to a buffered DataInputStream.      //
             // Buffering the stream makes the reading faster; the          //
             // readLine() method of the DataInputStream makes the reading  //
             // easier.                                                     //
             //-------------------------------------------------------------//

             dis = new DataInputStream(new BufferedInputStream(is));

             //------------------------------------------------------------//
             // Step 5:                                                    //
             //------------------------------------------------------------//
             // Now just read each record of the input stream, and print   //
             // it out.  Note that it's assumed that this problem is run   //
             // from a command-line, not from an application or applet.    //
             //------------------------------------------------------------//
             output = new FileOutputStream(fullFileName);
             byte []fileContents = new byte[dis.available()];
             while (dis.available() != 0) {
                output.write(dis.readByte());
             }

            logger.info("File : " + fileURL + " has been successfully downloaded");

          } catch (MalformedURLException mue) {


           //logger.warn("File : " + fileURL + " cannot be downloaded as it does not exist");
//           mue.printStackTrace();
           fullFileName = "";
//             System.exit(1);

          } catch (IOException ioe) {

             //logger.warn("File : " + fileURL + " cannot be downloaded as it does not exist");
//             ioe.printStackTrace();
//             System.exit(1);
          fullFileName = "";

          }
      finally {

             //---------------------------------//
             // Step 6:  Close the InputStream  //
             //---------------------------------//

             try {

                 if(is != null)
                    is.close();
                 if(output != null){
                    output.flush();
                    output.close();
                 }
             } catch (IOException ioe) {
                // just going to ignore this one
             }

          } // end of 'finally' clause

        //Decompressor.decompressGZipFile(fullFileName);

       return fullFileName;
    }

}
