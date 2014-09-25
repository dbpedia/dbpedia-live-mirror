package org.dbpedia.extraction.live.mirror.helper;

import org.dbpedia.extraction.live.mirror.sparul.SPARULException;
import org.dbpedia.extraction.live.mirror.sparul.SPARULExecutor;
import org.dbpedia.extraction.live.mirror.sparul.SPARULGenerator;
import org.dbpedia.extraction.live.mirror.sparul.SPARULVosExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/29/11
 * Time: 5:10 PM
 * Formulates SPARUL insert and delete statements based on data stored in files that were downloaded from DBpedia-Live
 * server.
 */
public final class SPARULMediator {

    private static final Logger logger = LoggerFactory.getLogger(SPARULMediator.class);

    private static final SPARULExecutor sparulExecutor = new SPARULVosExecutor();

    private static final SPARULGenerator sparulGenerator = new SPARULGenerator(Global.getOptions().get("graphURI"));

    private enum Action {ADD, DELETE}

    private SPARULMediator(){}

    public static void applyChangeset(Changeset changeset) {

        // Deletions must be executed before additions

        executeAction(changeset.getDeletions(), Action.DELETE);
        logger.info("Patch " + changeset.getId() + " DELETED " + changeset.triplesDeleted() + " triples");

        executeAction(changeset.getAdditions(), Action.ADD);
        logger.info("Patch " + changeset.getId() + " ADDED " + changeset.triplesAdded() + " triples");

    }


    private static boolean executeAction(Collection<String> triples, Action action) {
        if (triples.isEmpty()) {
            return true;
        }

        String pattern = Utils.generateStringFromList(triples, "\n");
        String sparul = action.equals(Action.ADD) ? sparulGenerator.insert(pattern) : sparulGenerator.delete(pattern);


        boolean result = executeSparulWrapper(sparul);

        if (result == true) {
            return true;
        }

        // if only 1 triple just log and return
        if (triples.size() == 1) {
            // size = 1, get triple from collection
            String triple = "";
            for (String s : triples) {
                triple = s;
            }
            logger.error("Cannot " + action.toString() + " triple: \n" + triple);
            return false;
        }

        logger.warn("Tried to " + action.toString() + " " + triples.size() + " but failed, splitting into chunks to spot the error");
        // Split collection and retry
        // In the end we will go to one (or more) single problematic triples, log it (previous block) and finish
        for (Collection<String> subList : splitCollection(triples, 5)) {
            executeAction(subList, action);
        }


        return false;
    }

    private static <T> Collection<Collection<T>> splitCollection(Collection<T> collection, int chunks) {
        ArrayList<Collection<T>> lists = new ArrayList<>();
        for (int i = 0; i < chunks; i++) {
            lists.add(new ArrayList<T>());
        }
        int counter = 0;
        for (T item : collection) {
            int index = counter % chunks;
            lists.get(index).add(item);
            counter++;
        }
        return lists;
    }

    private static boolean executeSparulWrapper(String sparul) {
        try {
            sparulExecutor.executeSPARUL(sparul);
        } catch (SPARULException e) {
            logger.warn("Error in query execution: ", e);
            return false;
        }
        return true;
    }


}
