#!/bin/sh

# delete assets and work files
rm -rf /var/opt/archie/beeri/import/*
find /var/opt/archie/beeri/ -type f -exec rm -rf {} \;

# delete solr documents
curl http://localhost:8983/solr/archie_beeri/update --data '<delete><query>*:*</query></delete>' -H 'Content-type:text/xml; charset=utf-8'
curl http://localhost:8983/solr/archie_beeri/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'

# add default collection
/opt/apache/solr/bin/post -c archie_beeri `dirname $0`/../resources/default-collection.xml

# delete content of all buckets
for bucket in archie-beeri-private-test archie-beeri-public-test archie-beeri-secret-test archie-import-test archie-mail-test
do
    aws s3 rm s3://$bucket --recursive
done

# recreate default directories
for bucket in archie-beeri-private-test archie-beeri-public-test archie-beeri-secret-test
do
    aws s3api put-object --bucket $bucket --key originals/
    aws s3api put-object --bucket $bucket --key thumbnails/
    aws s3api put-object --bucket $bucket --key text/
done

# upload to import folder
aws s3 sync /var/opt/data/archie-beeri/import/ s3://archie-import-test/

# upload to mail folder
aws s3 sync /var/opt/data/archie-beeri/mail/ s3://archie-mail-test/