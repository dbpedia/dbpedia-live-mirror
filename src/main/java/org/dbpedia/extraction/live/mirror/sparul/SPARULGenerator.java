package org.dbpedia.extraction.live.mirror.sparul;

/**
 * Generates a SPARUL Query that is bound to a graph
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 10:22 AM
 */
public class SPARULGenerator {
    private final String graph;

    public SPARULGenerator(String graph) {
        this.graph = graph;

    }

    public String insert(String triples) {
        return generate(triples, true);
    }

    public String delete(String triples) {
        return generate(triples, false);
    }

    public String deleteResource(String resource) {
        return "DELETE FROM <" + graph + "> {" +
                "  ?s ?p ?o" +
                " } WHERE {" +
                "  ?s ?p ?o." +
                "  FILTER ( ?s = <" + resource + ">)" +
                "}";
    }

    public String clearGraph() {
        return "CLEAR GRAPH <" + graph + "> ";
    }


    private String generate(String triples, boolean toAdd) {
        return (toAdd ? "INSERT DATA INTO" : "DELETE DATA FROM") + " <" + graph + "> {\n" + triples + "\n} ";
    }
}
