# Re-index

## Export all documents in the collection

<pre>

curl "http://localhost:8983/solr/archie_beeri/select?q=*%3A*&wt=json&rows=999999&fl=id,dcTitle,dcDate,dcCreator,dcDescription,dcSubject,dcFormat,dcType,dcIsPartOf,dcAccessRights,importTime,fileDigest,storageLocation,sortCode,content" > export.json

</pre>

## Delete all documents from the collection

<pre>

curl http://localhost:8983/solr/archie_beeri/update --data '<delete><query>*:*</query></delete>' -H 'Content-type:text/xml; charset=utf-8'

curl http://localhost:8983/solr/archie_beeri/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'

</pre>

## Extract docs from export file and save in a new file

jq .response.docs export.json > import.json

## Import

<pre>

curl 'http://localhost:8983/solr/archie_beeri/update/json/docs' -H 'Content-type:application/json' -d @import.json

</pre>