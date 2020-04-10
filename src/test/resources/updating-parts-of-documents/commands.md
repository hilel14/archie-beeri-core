# Links
https://lucene.apache.org/solr/guide/7_7/updating-parts-of-documents.html


# Ad new documents to Solr index
/opt/apache/solr/bin/post -c archie_beeri resources/updating-parts-of-documents/add.json

# Update part of a document
curl -X POST --header 'Content-Type: application/json' --data @resources/updating-parts-of-documents/update.json 'http://localhost:8983/solr/archie_beeri/update'
curl http://localhost:8983/solr/archie_beeri/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'