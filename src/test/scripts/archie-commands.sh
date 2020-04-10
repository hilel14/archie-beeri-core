#!/bin/sh


DATA=~/GithubProjects/archie/beeri/projects/api/src/test/data
URL=http://localhost:8080/archie-beeri-api/rest

#curl -X POST --header 'Content-Type: application/json' --data @$DATA/import-attributes.json $URL/docs/folder

curl -X POST --header 'Content-Type: application/json' --data @src/test/data/import-attributes.json http://localhost:8080/archie-beeri-api/rest/docs/folder