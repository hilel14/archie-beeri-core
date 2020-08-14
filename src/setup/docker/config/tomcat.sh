#!/bin/sh

# apache-tomcat-8.5.53.tar.gz
# https://downloads.apache.org/tomcat/tomcat-8/v8.5.57/bin/apache-tomcat-8.5.57.tar.gz
VERSION=8.5.57

# download
if [ -f "apache-tomcat-$VERSION.tar.gz" ]; then
    echo "Solr installation file exists."
else 
    wget https://downloads.apache.org/tomcat/tomcat-8/v$VERSION/bin/apache-tomcat-$VERSION.tar.gz
fi

# extract
mkdir -p /opt/apache
tar xf apache-tomcat-$VERSION.tar.gz
mv apache-tomcat-$VERSION /opt/apache/tomcat

# Set permissions
chown -R apache /opt/apache/tomcat