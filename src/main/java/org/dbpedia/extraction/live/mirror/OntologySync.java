package org.dbpedia.extraction.live.mirror;

import org.dbpedia.extraction.live.mirror.changesets.Changeset;
import org.dbpedia.extraction.live.mirror.changesets.ChangesetExecutor;
import org.dbpedia.extraction.live.mirror.helper.*;
import org.dbpedia.extraction.live.mirror.ontology.OntologyHandler;
import org.dbpedia.extraction.live.mirror.sparul.SPARULGenerator;
import org.dbpedia.extraction.live.mirror.sparul.SPARULVosExecutor;
import org.slf4j.Logger;


/**
 * Keeps a mirror of the DBpedia ontology
 *
 * @author Dimitris Kontokostas
 * @since 9/26/14 9:14 AM
 */
public final class OntologySync {


    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OntologySync.class);

    private OntologySync(){}

    public static void main(String[] args) {

        if (args == null || args.length != 1) {
            logger.error("Incorrect arguments in Main. Must be one of {Endless|Onetime}");
            System.exit(1);
        }
        UpdateStrategy strategy = UpdateStrategy.Endless; // default value
        try {
            strategy = UpdateStrategy.valueOf(args[0]);
        } catch (Exception e) {
            logger.error("Incorrect arguments in Main. Must be one of {Endless|Onetime}");
            System.exit(1);
        }


        String ontologyURL = Global.getOptions().get("OntologyURL");
        String ontologyCache = Global.getOptions().get("UpdatesDownloadFolder");
        String ontologyGraph = Global.getOptions().get("OntologyGraph");

        int updateInterval = Integer.parseInt(Global.getOptions().get("OntologyUpdateInterval"));

        if (ontologyURL == null || ontologyCache == null || ontologyGraph == null || updateInterval < 1) {
            logger.error("Incorrect ontology properties in config");
            System.exit(1);
        }
        ontologyCache = ontologyCache + "ontology.cache.nt";

        ChangesetExecutor changesetExecutor = new ChangesetExecutor(new SPARULVosExecutor(), new SPARULGenerator(ontologyGraph));

        /**
         * Starting update
         */

        while (true) {

            OntologyHandler ontology = new OntologyHandler(ontologyURL, ontologyCache);
            Changeset changeset = ontology.getChangeset();

            if (changeset != null) {
                changesetExecutor.applyChangeset(changeset);
                ontology.saveOntology();
            }
            else {
                logger.error("Error creating changeset, probably cannot download ontology, skipping...");
            }


            if (strategy.equals(UpdateStrategy.Onetime)) {
                break;
            }

            long timeout = updateInterval * 24l * 60l * 60l;

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                logger.warn("InterruptedException", e);
            }
        }


    }  // end of main

}
