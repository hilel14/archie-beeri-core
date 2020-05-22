#!/bin/sh

set -e

# Solr
curl http://localhost:8983/solr/archie_beeri/replication?command=backup
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE /opt/apache/solr/server/solr/archie_beeri/data/ s3://archie-beeri-backup/solr

# Asset store
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE s3://archie-beeri-asset-store s3://archie-beeri-backup

# Report done
echo `date` >> /var/opt/archie/beeri/logs/backup.log 
