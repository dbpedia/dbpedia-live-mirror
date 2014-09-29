Application Name: DBpedia Updates Integrator
Version: 1.1
Date: 05/03/2012
Author: Mohamed Morsey, AKSW Group, University Of Leipzig, Germany
Source: https://dbpintegrator.svn.sourceforge.net/svnroot/dbpintegrator

Description
===========
DBpedia-Live continuously generates zipped N-Triples files containing added/deleted triples upon its run.
This tool starts downloading those files in order starting from start date stated in "lastDownloadDate.dat" file.
After downloading the file compressed file, it does the following:
1- decompresses that file, in order to get the N-Triples file out of it.
2- determines the purpose of that file, i.e. if the file name is XYZ.added.nt then it is for newly added triples, and if its name is XYZ.removed.nt then it 
   is for deleted triples.
3- connects to the local Virtuoso server that should be synchronized with our DBpedia-Live, using login credentials written in "mirror-live.ini".
4- uses the downloaded file to either add or delete triples from that store.

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
