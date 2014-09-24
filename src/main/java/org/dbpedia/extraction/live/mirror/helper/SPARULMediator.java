package org.dbpedia.extraction.live.mirror.helper;

import org.apache.log4j.Logger;
import org.dbpedia.extraction.live.mirror.sparul.*;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/29/11
 * Time: 5:10 PM
 * Formulates SPARUL insert and delete statements based on data stored in files that were downloaded from DBpedia-Live
 * server.
 */
public class SPARULMediator {

    private static Logger logger  = Logger.getLogger(SPARULMediator.class);

    private static SPARULExecutor sparulExecutor = new SPARULVosExecutor();

    private static SPARULGenerator sparulGenerator = new SPARULGenerator(Global.options.get("graphURI"));

    /**
     * Inserts the triples stored in the passed file into Virtuoso store
     * @param  filename Name of N-Triples file containing the triples that should be added
     * @param deleteNTriplesFile    Whether to delete the file upon completion or not
     * @return  True if insertion process was successful, and false otherwise
     */
    public static boolean insertIntoGraph(String filename, boolean deleteNTriplesFile){



        try{
            List<String> triples = Utils.getLinesFromFile(filename);
            String pattern = Utils.generateStringFromList(triples, "\n");


            //First try to insert all triples at once
            boolean successfulInsertion = insert(pattern.toString());

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
            logger.error("Error: " + e.getMessage(), e);
            return false;
        }

    }

    private static boolean insert(String pattern) {
        String sparul = sparulGenerator.insert(pattern);
        try {
            sparulExecutor.executeSPARUL(sparul);
        } catch (SPARULException e) {
            // TODO fix
            logger.warn("Error in query execution: ", e);
            return false;
        }
        return true;
    }


    /**
     * Deletes the triples stored in the passed file from Virtuoso store
     * @param  filename Name of N-Triples file containing the triples that should be deleted
     * @param deleteNTriplesFile    Whether to delete the file upon completion or not
     * @return  True if deletion process was successful, and false otherwise
     */
    public static boolean deleteFromGraph(String filename, boolean deleteNTriplesFile){

        try{
            List<String> triples = Utils.getLinesFromFile(filename);
            String pattern = Utils.generateStringFromList(triples, "\n");

            pattern = stripDuplicateTriples(pattern);

            //boolean successfulDeletion = delete(pattern);

            //For the deletion operation, we should delete triple by triple, as if one of the triples that must be deleted
            //is not there (for any reason) the whole deletion process will fail

            boolean successfulDeletion = false;

            for(String triple: triples){
                successfulDeletion = delete(triple);
            }

            //Insert the remaining triples, as the number of triples may not be divisible by 100


            if(deleteNTriplesFile)
                (new File(filename)).delete();

            return successfulDeletion;

        }catch (Exception e){//Catch exception if any
            logger.error("Error: " + e.getMessage(), e);
            return false;
        }

    }

    private static boolean delete(String pattern) {
        String sparul = sparulGenerator.delete(pattern);

        try {
            sparulExecutor.executeSPARUL(sparul);
        } catch (SPARULException e) {
            logger.warn("Error in query execution: ", e);
            return false;
        }
        return true;
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
