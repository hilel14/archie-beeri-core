#!/bin/sh

# exit on first error
set -e

# Set env for cron
export PATH=/opt/aws/cli/bin:$PATH
export HOME=/home/centos

# Solr
curl http://localhost:8983/solr/archie_beeri/replication?command=backup
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE /opt/apache/solr/server/solr/archie_beeri/data/ s3://archie-beeri-backup/solr

# Asset store
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE s3://archie-beeri-private/originals s3://archie-beeri-backup/private/assets
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE s3://archie-beeri-public/originals  s3://archie-beeri-backup/public/assets
aws s3 sync --only-show-errors --storage-class DEEP_ARCHIVE s3://archie-beeri-secret/originals  s3://archie-beeri-backup/secret/assets

# Report done
echo `date` >> /var/opt/archie/beeri/logs/backup.log 
