package org.dbpedia.extraction.live.mirror.ontology;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.dbpedia.extraction.live.mirror.changesets.Changeset;
import org.dbpedia.extraction.live.mirror.helper.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/26/14 8:54 AM
 */
public class OntologyHandler {

    private static final Logger logger = LoggerFactory.getLogger(OntologyHandler.class);


    private final String ontologyURI;
    private final String ontologyFilename;

    private final List<String> remote;
    private final List<String> local;

    public OntologyHandler(String ontologyURI, String ontologyFilename) {
        this.ontologyURI = ontologyURI;
        this.ontologyFilename = ontologyFilename;

        this.remote = getRemoteOntologyTriples();
        this.local = getCachedOntologyTriples();
    }

    public Changeset getChangeset() {


        if (remote == null) {
            return null;
        }

        List<String> insertions = new ArrayList<>(remote);
        insertions.removeAll(local);

        List<String> deletions = new ArrayList<>(local);
        deletions.removeAll(remote);


        return new Changeset("Ontology-" + Utils.getTimestamp(), insertions, deletions, new ArrayList<String>());
    }

    private List<String> getRemoteOntologyTriples() {
        List<String> triples = null;

        try {
            Model model = ModelFactory.createDefaultModel();
            model.read(ontologyURI);

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            model.write(os, "N-TRIPLE");

            String ontology = os.toString("UTF8");

            triples = new ArrayList<>();
            for (String t : ontology.split("\n")) {
                triples.add(t.trim());
            }
            Collections.sort(triples);

        } catch (Exception e) {
            logger.warn("Cannot download remote ontology", e);
        } finally {
            return triples;
        }
    }

    private List<String> getCachedOntologyTriples() {
        List<String> triples = Arrays.asList();
        try {
            triples = Utils.getTriplesFromFile(ontologyFilename);
            Collections.sort(triples);
        } catch (Exception e) {
            logger.warn("Could not read cached file, assuming first run...");
        }
        return triples;
    }

    public void saveOntology() {
        if (remote != null) {
            Utils.writeTriplesToFile(remote, ontologyFilename);
        }
    }
}
