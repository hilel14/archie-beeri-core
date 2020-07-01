curl http://localhost:8983/solr/archie_beeri/update --data '<delete><query>*:*</query></delete>' -H 'Content-type:text/xml; charset=utf-8'
curl http://localhost:8983/solr/archie_beeri/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'
cd `dirname $0`
/opt/apache/solr/bin/post -c archie_beeri ../resources/default-collection.xml
