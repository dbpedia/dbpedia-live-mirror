#!/bin/bash


MAIN_CLS="org.dbpedia.extraction.live.mirror.OntologySync"
MVN="mvn"


$MVN install exec:java -q -Dexec.mainClass="$MAIN_CLS" -Dexec.args="$*"

