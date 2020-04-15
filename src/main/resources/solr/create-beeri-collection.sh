#!/bin/sh
set -e
#su -c "/opt/apache/solr/bin/solr delete -c archie_beeri" archie
#su -c "/opt/apache/solr/bin/solr create -c archie_beeri" archie
CONFIG_FOLDER=`dirname $0`
curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @$CONFIG_FOLDER/solr.beeri.config.json
curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @$CONFIG_FOLDER/solr.beeri.schema.json
