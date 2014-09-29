DBpedia Live Mirror
==========

[DBpedia-Live](http://live.dbpedia.org) continuously [generates zipped N-Triples files](http://live.dbpedia.org/changesets/) containing added/deleted triples upon its run.
This tool starts downloading those files and updates a local Virtuoso triple store.

VOS Setup
=========
DBpedia Live triple store update happens on different Graphs and we have the following enabled:
  1. `http://live.dbpedia.org`: contains real time extracted data from Wikipedia
  2. `http://static.dbpedia.org`: contains external datasets and data that cannot be extracted from Wikipedia but is usefull to have.
  3. `http://dbpedia.org/resource/classes#`: contains the up-to-date DBpedia ontology
  4. `http://dbpedia.org`: virtual graph group that contains all the aforementioned graphs

to create graph groups in VOS you can adapt and run [this script](https://github.com/dbpedia/dbpedia-documentation/blob/master/scripts/virtuoso/create_graph_groups.sql)

Execution
=========
In order to execute from source, download the code from the repo
`git clone https://github.com/dbpedia/dbpedia-live-mirror.git`

  1. Setup your VOS instance and `mirror-live.ini` file.
  2. Download and load the [latest dump](http://live.dbpedia.org/dumps/)
  3. Copy `lastDownloadDate.dat.default' to `lastDownloadDate.dat` and adapt the date according to the dump file
  3. run one of the scripts in the `bin/` folder
    1. `sh bin/liveSync.sh` script that applies existing triple patches and waits until new ones get published 
    2. `sh bin/liveSyncOnce.sh` (to be released soon) script that applies existing triple patches and exits.
    3. `sh bin/ontologySync.sh` script that keeps the DBpedia ontology up-to-date
    4. `sh bin/ontologySyncOnce.sh` script that updates the DBpedia ontology to the latest version and exists

Dependencies
=========
  1. Maven 3
  2. Java 7

Contact
=======
[DBpedia Developers mailing list](https://lists.sourceforge.net/lists/listinfo/dbpedia-developers)


