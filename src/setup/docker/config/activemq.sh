#!/bin/sh

# https://downloads.apache.org/activemq/5.15.13/apache-activemq-5.15.13-bin.tar.gz
VERSION=5.15.13

# download
if [ -f "apache-activemq-$VERSION-bin.tar.gz" ]; then
    echo "exists."
else 
    wget https://downloads.apache.org/activemq/$VERSION/apache-activemq-$VERSION-bin.tar.gz
fi

# extract
mkdir -p /opt/apache
tar xf apache-activemq-$VERSION-bin.tar.gz
mv apache-activemq-$VERSION /opt/apache/activemq

# Set permissions
chown -R apache /opt/apache/activemq