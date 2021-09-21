#!/bin/sh

# Exit on first error
set -e

# Backup Solr index
curl 'http://localhost:8983/solr/archie_beeri/replication?command=backup'

# Export all documents in the collection
curl 'http://localhost:8983/solr/archie_beeri/select?q=*%3A*&wt=json&rows=999999&fl=id,dcTitle,dcDate,dcCreator,dcDescription,dcSubject,dcFormat,dcType,dcIsPartOf,dcAccessRights,importTime,fileDigest,storageLocation,sortCode,content' > export.json

# Delete all documents from the collection
curl http://localhost:8983/solr/archie_beeri/update --data '<delete><query>*:*</query></delete>' -H 'Content-type:text/xml; charset=utf-8'
curl http://localhost:8983/solr/archie_beeri/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'

# Extract docs array from export file and save in a new file
jq .response.docs export.json > import.json

# Import back to Solr
curl 'http://localhost:8983/solr/archie_beeri/update/json/docs' -H 'Content-type:application/json' -d @import.json

function recreateCollection {
    runuser -u archie -- /opt/apache/solr/bin/solr delete -c archie_beeri
    runuser -u archie -- /opt/apache/solr/bin/solr create -c archie_beeri
    curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
    curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./solr.config.json
    curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @./solr.schema.json
}

function recreateCollection2 {
    sudo docker exec -u archie archie.beeri.2 /opt/apache/solr/bin/solr delete -c archie_beeri
    sudo docker exec -u archie archie.beeri.2 /opt/apache/solr/bin/solr create -c archie_beeri
    curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
    curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./solr.config.json
    curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @./solr.schema.json
}