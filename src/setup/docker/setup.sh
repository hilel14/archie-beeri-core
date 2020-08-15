#!/bin/sh
# Exit on first error
set -e
# config components
./config/packages.sh
./config/folders.sh
./config/mariadb.sh
./config/httpd.sh
./config/solr.sh
./config/activemq.sh
./config/tomcat.sh