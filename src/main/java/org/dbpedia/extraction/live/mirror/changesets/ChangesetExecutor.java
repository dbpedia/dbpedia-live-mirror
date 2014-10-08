package org.dbpedia.extraction.live.mirror.changesets;

import org.dbpedia.extraction.live.mirror.helper.Utils;
import org.dbpedia.extraction.live.mirror.sparul.SPARULException;
import org.dbpedia.extraction.live.mirror.sparul.SPARULExecutor;
import org.dbpedia.extraction.live.mirror.sparul.SPARULGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Applies a changeset in a SPARULExecutor using a SPARULGenerator
 *
 * @author Dimitris Kontokostas
 * @since 9/26/14 9:34 AM
 */
public class ChangesetExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ChangesetExecutor.class);

    private final SPARULExecutor sparulExecutor;

    private final SPARULGenerator sparulGenerator;

    private enum Action {ADD, DELETE}

    public ChangesetExecutor(SPARULExecutor sparulExecutor, SPARULGenerator sparulGenerator) {
        this.sparulExecutor = sparulExecutor;
        this.sparulGenerator = sparulGenerator;
    }

    public void applyChangeset(Changeset changeset) {

        // First clear resources (if any)
        if (changeset.triplesCleared() > 0) {
            executeClearResources(changeset.getCleared());
            logger.info("Patch " + changeset.getId() + " CLEARED " + changeset.triplesCleared() + " resources");
        }

        // Deletions must be executed before additions

        if (changeset.triplesDeleted() > 0) {
            executeAction(changeset.getDeletions(), Action.DELETE);
            logger.info("Patch " + changeset.getId() + " DELETED " + changeset.triplesDeleted() + " triples");
        }

        if (changeset.triplesAdded() > 0) {
            executeAction(changeset.getAdditions(), Action.ADD);
            logger.info("Patch " + changeset.getId() + " ADDED " + changeset.triplesAdded() + " triples");
        }

    }

    public void clearGraph() {
        executeSparulWrapper(sparulGenerator.clearGraph());
    }

    private boolean executeClearResources(Collection<String> resources) {
        boolean status = true;
        for (String resource : resources) {
            boolean result = executeSparulWrapper(sparulGenerator.deleteResource(resource));
            if (!result) {
                logger.error("Could not clear triples for <" + resource + ">");
            }
            status = status && result;

        }
        return status;
    }


    private boolean executeAction(Collection<String> triples, Action action) {
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

    private <T> Collection<Collection<T>> splitCollection(Collection<T> collection, int chunks) {
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

    private boolean executeSparulWrapper(String sparul) {
        try {
            sparulExecutor.executeSPARUL(sparul);
        } catch (SPARULException e) {
            logger.warn("Error in query execution: ", e);
            return false;
        }
        return true;
    }


}
