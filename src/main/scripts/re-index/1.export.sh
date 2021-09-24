#!/bin/sh

## Export all documents in the collection
fields="id,dcTitle,dcDate,dcCreator,dcDescription,dcSubject,dcFormat,dcType,dcIsPartOf,dcAccessRights,importTime,fileDigest,storageLocation,sortCode,content"
curl "http://localhost:8983/solr/archie_beeri/select?q=*%3A*&wt=json&rows=999999&fl=$fields" > export.json
