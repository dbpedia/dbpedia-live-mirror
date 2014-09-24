package org.dbpedia.extraction.live.mirror.helper;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.dbpedia.extraction.live.mirror.connection.JDBC;

import java.io.*;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/29/11
 * Time: 5:10 PM
 * Formulates SPARUL insert and delete statements based on data stored in files that were downloaded from DBpedia-Live
 * server.
 */
public class SPARULFormulator {

    private static Logger logger  = Logger.getLogger(SPARULFormulator.class);

    /**
     * Inserts the triples stored in the passed file into Virtuoso store
     * @param  filename Name of N-Triples file containing the triples that should be added
     * @param deleteNTriplesFile    Whether to delete the file upon completion or not
     * @return  True if insertion process was successful, and false otherwise
     */
    public static boolean insertIntoGraph(String filename, boolean deleteNTriplesFile){

        String pattern = "";

        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {

                //read a Triple from file
                pattern += strLine + "\n";

            }
            //Close the input stream
            in.close();

            //First try to insert all triples at once
            boolean successfulInsertion = insert(pattern);

            //If the insertion was not successful, then we should divide the pattern into chunks of 100 triples each
            if(!successfulInsertion){
                logger.info("Inserting all triples at once failed, retrying to divide them into chunks and reinsert.");
                String []tripleLines = pattern.split("\n");

                //Divide the array to chunks of 100 triples, and insert them at once
                StringBuilder tripleChunk = new StringBuilder();
                for(int i = 0; i < tripleLines.length; i++){
                    
                    if((i > 0) && (i % 100 == 0) ){
                        insert(tripleChunk.toString());
                        tripleChunk.delete(0, tripleChunk.length());
                    }

                    tripleChunk.append("\n");
                    tripleChunk.append(tripleLines[i]);
                }

                //Insert the remaining triples, as the number of triples may not be divisible by 100
                successfulInsertion = insert(tripleChunk.toString());
            }

            if(deleteNTriplesFile)
                (new File(filename)).delete();

            return successfulInsertion;

        }catch (Exception e){//Catch exception if any
            logger.error("Error: " + e.getMessage());
            return false;
        }

    }

    private static boolean insert(String pattern) {
        String sparul = "INSERT IN GRAPH <" + Global.options.get("graphURI") + "> { \n  " + pattern + "}";
//            String sparul = "INSERT IN GRAPH <http://dbpedia.org> \n" +
//                    "\n" +
//                    "{ <http://dbpedia.org/resource/Johann_Gottfried_Galle> <http://dbpedia.org/property/wikilink> \"Hello\" } ";

        JDBC jdbc = JDBC.getDefaultConnection();

        String virtuosoPl = "sparql " + sparul + "";

        //jdbc.exec(virtuosoPl);
        PreparedStatement stmt = jdbc.prepare(virtuosoPl);
        return jdbc.executeStatement(stmt, new String[]{});
    }


    /**
     * Deletes the triples stored in the passed file from Virtuoso store
     * @param  filename Name of N-Triples file containing the triples that should be deleted
     * @param deleteNTriplesFile    Whether to delete the file upon completion or not
     * @return  True if deletion process was successful, and false otherwise
     */
    public static boolean deleteFromGraph(String filename, boolean deleteNTriplesFile){

        //String sparul = "DELETE FROM <" + Global.options.get("graphURI") + "> { \n  " +   " }" + " WHERE {\n" +  " }";

        String pattern = "";

        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {

                //read a Triple from file
                pattern += strLine + "\n";

            }
            //Close the input stream
            in.close();

            pattern = stripDuplicateTriples(pattern);

            //boolean successfulDeletion = delete(pattern);

            //For the deletion operation, we should delete triple by triple, as if one of the triples that must be deleted
            //is not there (for any reason) the whole deletion process will fail
            String []tripleLines = pattern.split("\n");

            boolean successfulDeletion = false;

            StringBuilder tripleChunk = new StringBuilder();
            for(int i = 0; i < tripleLines.length; i++){
                successfulDeletion = delete(tripleLines[i]);
            }

            //Insert the remaining triples, as the number of triples may not be divisible by 100


            if(deleteNTriplesFile)
                (new File(filename)).delete();

            return successfulDeletion;

        }catch (Exception e){//Catch exception if any
            logger.error("Error: " + e.getMessage());
            return false;
        }

    }

    private static boolean delete(String pattern) {
        String sparul = "DELETE FROM <" + Global.options.get("graphURI") + "> { \n  " + pattern
                +" }" + " WHERE {\n" +  pattern + " }";

//            String sparul = "INSERT IN GRAPH <" + Global.options.get("graphURI") + "> { \n  " + pattern + "}";
//            String sparul = "INSERT IN GRAPH <http://dbpedia.org> \n" +
//                    "\n" +
//                    "{ <http://dbpedia.org/resource/Johann_Gottfried_Galle> <http://dbpedia.org/property/wikilink> \"Hello\" } ";

        JDBC jdbc = JDBC.getDefaultConnection();

        String virtuosoPl = "sparql " + sparul + "";

        PreparedStatement stmt = jdbc.prepare(virtuosoPl);
        return jdbc.executeStatement(stmt, new String[]{});
    }

    /**
     * Removes the duplicated triples that may exist in
     * @param   triplesToClean  A string may contain duplicate triples
     * @return  A string containing all deleted triples without any duplicates
     */
    private static String stripDuplicateTriples(String triplesToClean) {

        if(triplesToClean.compareTo("") == 0)
            return "";

        StringBuilder result = new StringBuilder();
        Set<String> uniqueLines = new LinkedHashSet<String>();

        String[] chunks = triplesToClean.split("\n");
        uniqueLines.addAll(Arrays.asList(chunks));

        for (String chunk : uniqueLines) {
            if(chunk.compareTo("") != 0)
                result.append(chunk).append("\n");
        }

        return result.toString();
    }


}
