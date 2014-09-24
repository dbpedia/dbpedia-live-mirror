package org.dbpedia.extraction.live.mirror.helper;

import org.apache.log4j.Logger;
import org.dbpedia.extraction.live.mirror.compression.Decompressor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 1:16 PM
 */
public final class Utils {

    private static Logger logger = Logger.getLogger(Decompressor.class);

    private Utils() {}

    public static List<String> getLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {

            String line = null;
            while ((line = in.readLine()) != null) {

                lines.add(line.trim());

            }

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + filename + " not fount!", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException: ", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException in file " + filename, e);
        }

        return lines;

    }

    public static boolean deleteFile(String filename) {
        try {
            return new File(filename).delete();
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateStringFromList(List<String> strList,String sep) {

        StringBuilder finalString = new StringBuilder();


        for (String str: strList) {
            finalString.append(str);
            finalString.append(sep);
        }

        return finalString.toString();
    }

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
