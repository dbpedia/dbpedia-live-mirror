package org.dbpedia.extraction.live.mirror.compression;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/25/11
 * Time: 5:45 PM
 * The update files of DBpedia are always published as compressed N-Triples files.
 * A decompressor is needed to extract the original N-Triples file from compressed one
 */
public class Decompressor {

    private static Logger logger;

    static {
        logger = Logger.getLogger(Decompressor.class);
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
    }

    /*private void compressFileUsingGZip(String filename){

         FileInputStream in = null;

         File outputFile = null;
         OutputStream osCompressedFinal = null;
         OutputStream out = null;

        try{
            //Prepare required streams

            in = new FileInputStream(filename);

            outputFile = new File(filename + ".gz");
		    osCompressedFinal = new FileOutputStream(outputFile);
            out = new GzipCompressorOutputStream(osCompressedFinal);

            // Transfer bytes from the input file to the GZIP output stream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            // Complete the GZIP file
            out.flush();
            out.close();

            //delete TAR file as it is not need any more
            in.close();
            File tarFileToDelete = new File(filename);
            tarFileToDelete.delete();

            //out.write(new FileInputStream(tarOutputFilename).)
        }
        catch(IOException exp){
            logger.error("File: " + filename + " cannot be compressed", exp);
        }

    }*/

    /**
     * Decompresses the passed GZip file, and returns the filename of the decompressed file
     * @param filename  The filename of compressed file
     * @param deleteCompressedFile  Whether to delete the original compressed file upon completion
     * @return  The filename of the output file, or empty string if a problem occurs
     */
    public static String decompressGZipFile(String filename, boolean deleteCompressedFile){

        String outFilename = "" ;

        try{
            //The output filename is the same as input filename without last .gz
            int lastDotPosition = filename.lastIndexOf(".");
            outFilename = filename.substring(0, lastDotPosition);

             FileInputStream inStream = new FileInputStream(filename);
             GZIPInputStream gInStream =new GZIPInputStream(inStream);
             FileOutputStream outstream = new FileOutputStream(outFilename);
             byte[] buf = new byte[1024];
             int len;
             while ((len = gInStream.read(buf)) > 0)
            {
              outstream.write(buf, 0, len);
            }
            logger.info("File : " + filename +" decompressed successfully to " + outFilename);
            gInStream.close();
            outstream.close();


//            return outFilename;
        }
        catch(IOException ioe){
            logger.warn("File " + filename + " cannot be decompressed due to " + ioe.getMessage(), ioe);
            outFilename = "";
        }
        finally {
            if(deleteCompressedFile)
                (new File(filename)).delete();

            return outFilename;
        }
    }
}
