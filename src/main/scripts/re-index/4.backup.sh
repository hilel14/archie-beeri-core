#!/bin/sh

# Backup Solr index
curl 'http://localhost:8983/solr/archie_beeri/replication?command=backup'
