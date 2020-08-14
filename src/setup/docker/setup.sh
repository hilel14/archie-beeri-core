#!/bin/sh
# Exit on first error
set -e
# config components
./config/folders.sh
./config/packages.sh
./config/mariadb.sh
./config/httpd.sh
./config/solr.sh
./config/activemq.sh
./config/tomcat.sh