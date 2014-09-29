DBpedia Live Mirror
==========

[DBpedia-Live](http://live.dbpedia.org) continuously [generates zipped N-Triples files](http://live.dbpedia.org/changesets/) containing added/deleted triples upon its run.
This tool starts downloading those files and updates a local Virtuoso triple store.

VOS Setup
=========
DBpedia Live triple store update is happens on different Graphs and we have the following enabled:
  1. `http://live.dbpedia.org`: contains real time extracted data from Wikipedia
  2. `http://static.dbpedia.org`: contains external datasets and data that cannot be extracted from Wikipedia but is usefull to have.
  3. `http://dbpedia.org/resource/classes#`: contains the up-to-date DBpedia ontology
  4. `http://dbpedia.org`: virtual graph group that contains all the aforementioned graphs

to create graph groups in VOS you can adapt and run [this script](https://github.com/dbpedia/dbpedia-documentation/blob/master/scripts/virtuoso/create_graph_groups.sql)

Execution
=========
In order to run the application do the following:
1- Decompress the zipped file to any folder.
2- Download and decompress the latest DBpedia-Live dump from "http://live.dbpedia.org/dumps/".
3- Use virtload.sh script to load data from that file into your Virtuoso store.
2- Set the start date in file "lastDownloadDate.dat" to the date of that dump, e.g. for dump file "dbpedia_2012_02_27.nt.bz2", set the date to 2012-02-27-00-000000.
3- Set the configuration information in file "dbpedia_updates_downloader.ini", such as login credentials for Virtuoso, and GraphURI.
4- Run "java -jar dbpintegrator-1.1.jar" on the command line.

Contact
=======
Dimitris Kontokostas
Department of Computer Science
University of Leipzig

Licensing
=========
THIS PROGRAM IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL, BUT WITHOUT ANY WARRANTY. IT IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED.
