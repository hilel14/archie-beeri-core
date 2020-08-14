#!/bin/sh
runuser -u mysql -- /usr/bin/mysqld_safe &
/usr/sbin/httpd &
runuser -u apache -- /opt/apache/solr/bin/solr start
runuser -u apache -- /opt/apache/activemq/bin/activemq start
runuser -u apache -- /opt/apache/tomcat/bin/startup.sh
/bin/bash