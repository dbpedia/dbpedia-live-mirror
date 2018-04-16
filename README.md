DBpedia Live Mirror
==========

[DBpedia-Live](http://live.dbpedia.org) continuously [generates zipped N-Triples files](http://live.dbpedia.org/changesets/) containing added/deleted triples upon its run.

This tool downloads those files and updates a local Virtuoso triple store.

Virtuoso (Enterprise or Open Source) Setup
=========
DBpedia Live triple store update happens on different Named Graphs.  We have the following enabled:

  * **`http://live.dbpedia.org`** — contains real time extracted data from Wikipedia
  * **`http://static.dbpedia.org`** — contains external datasets and data that cannot be extracted from Wikipedia but is useful to have.
  * **`http://dbpedia.org/resource/classes#`** — contains the up-to-date DBpedia ontology
  * **`http://dbpedia.org`** — virtual graph group that contains all the aforementioned graphs

To create graph groups in your local Virtuoso, you can adapt and run [this script](https://github.com/dbpedia/dbpedia-documentation/blob/master/scripts/virtuoso/create_graph_groups.sql).

Execution
=========
To execute from source:

1. Download the code from the repo
   ```bash
   git clone https://github.com/dbpedia/dbpedia-live-mirror.git
   ```
1. Set up your Virtuoso instance and `mirror-live.ini` file.
2. Download and load the [latest dump](http://live.dbpedia.org/dumps/)
3. Copy `lastDownloadDate.dat.default` to `lastDownloadDate.dat`, and adapt the date according to the dump file
3. Run one of the scripts in the `bin/` folder
    * **`sh bin/liveSync.sh`** — applies existing triple patches and waits until new ones get published 
    * **`sh bin/liveSyncOnce.sh Onetime`** — applies existing triple patches and exits.
    * **`sh bin/ontologySync.sh`** — keeps the DBpedia ontology up-to-date
    * **`sh bin/ontologySync.sh Onetime`** — updates the DBpedia ontology to the latest version and exists

Jar files are not distributed at the moment but will be made available on request.

Dependencies
=========
  * Maven 3
  * Java 7

Contact
=======
[DBpedia Developers mailing list](https://lists.sourceforge.net/lists/listinfo/dbpedia-developers)


