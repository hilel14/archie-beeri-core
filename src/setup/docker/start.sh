#!/bin/sh
runuser -u mysql -- /usr/bin/mysqld_safe & # mysqladmin shutdown
/usr/sbin/httpd & # -k start|restart|graceful|graceful-stop|stop
runuser -u archie -- /opt/apache/solr/bin/solr start
runuser -u archie -- /opt/apache/activemq/bin/activemq start
runuser -u archie -- /opt/apache/tomcat/bin/startup.sh
/bin/bash