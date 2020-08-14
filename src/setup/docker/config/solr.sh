#!/bin/sh

# solr-7.7.2.tgz
VERSION=7.7.2

# download
if [ -f "solr-$VERSION.tgz" ]; then
    echo "Solr installation file exists."
else 
    wget https://archive.apache.org/dist/lucene/solr/$VERSION/solr-$VERSION.tgz
fi

# extract
mkdir -p /opt/apache
tar xf solr-$VERSION.tgz
mv solr-$VERSION /opt/apache/solr
chown -R apache /opt/apache/solr

# create beeri collection
runuser -u apache -- /opt/apache/solr/bin/solr start
runuser -u apache -- /opt/apache/solr/bin/solr create -c archie_beeri
curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./config/solr.config.json
curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @./config/solr.schema.json
