#!/bin/sh

# Run from the same folder as solr.config (e.g: src/setup/docker/) as archie user

/opt/apache/solr/bin/solr delete -c archie_beeri
/opt/apache/solr/bin/solr create -c archie_beeri
curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @./solr.config/schema.json
curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./solr.config/requesthandler/search.json
curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./solr.config/requesthandler/replication.json
