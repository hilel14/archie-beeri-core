## Partial update workflow

1. Export search results to csv
1. Edit in Excell - searh and replace values, delete irelevnt columns.
1. Use Archie util class to update Solr index (archie-beeri-core/src/main/java/org/hilel14/archie/beeri/core/cli/UpdateDocumentsJobCli.java).
1. Save original and transformed files for future reference.

### Liks

* <https://lucene.apache.org/solr/guide/7_7/uploading-data-with-index-handlers.html>
* <https://lucene.apache.org/solr/guide/7_7/updating-parts-of-documents.html>

### Post

`curl -X POST -H 'Content-Type: application/json' 'http://localhost:8983/solr/archie_beeri/update?commit=true' --data-binary @p1.json`
