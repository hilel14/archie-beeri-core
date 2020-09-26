#!/bin/bash

# Exit on first error
set -e

function prepareSystem {
    adduser archie
    ln --symbolic --force /usr/share/zoneinfo/Asia/Jerusalem  /etc/localtime
}

function installPackages {
    # EPEL repository
    yum -y install epel-release
    # NodeJS repository (only in development)
    curl -sL https://rpm.nodesource.com/setup_14.x | bash -
    # Core packages
    LIST="java-11-openjdk mariadb-server httpd ImageMagick ghostscript tesseract tesseract-langpack-heb lsof curl wget"
    # Development packages
    LIST="$LIST vim bash-completion iproute java-11-openjdk-devel maven git nodejs"
    # Install
    yum -y install $LIST
    # Install Angular
    yes | npm install --silent -g @angular/cli
}

function createFolders {
    # main data folders
    for d in assetstore import logs work mail; do
        mkdir -p /var/opt/archie/beeri/$d
    done
    # asset store sub-folders
    for d1 in public private secret; do
        mkdir /var/opt/archie/beeri/assetstore/$d1
        for d2 in originals thumbnails text; do
            mkdir /var/opt/archie/beeri/assetstore/$d1/$d2
        done
    done
    mkdir /var/opt/archie/beeri/work/import
    mkdir /var/opt/maven
    # application folder
    for d in bin lib resources; do
        mkdir -p /opt/hilel14/archie/beeri/$d
    done
    # web-site folder
    mkdir -p /var/www/archie/beeri
    # make archie the owner
    chown archie /var/opt/maven
    chown -R archie /var/opt/archie/beeri/
    chown -R archie /opt/hilel14/archie/
    chown -R archie /var/www/archie/
}

function installMariaDb {
    runuser -u mysql -- /usr/libexec/mariadb-prepare-db-dir
    runuser -u mysql -- /usr/bin/mysqld_safe &
    sleep 10
    mysqladmin -u root create archie_beeri
    mysql -u root -e "GRANT ALL ON archie_beeri.* TO 'archie'@'localhost' IDENTIFIED BY '1234'";
    mysql -u root -e "GRANT ALL ON archie_beeri.* TO 'archie'@'172.16.0.0/255.240.0.0' IDENTIFIED BY '1234'";
    cat ./mariadb.tables.sql | mysql -u root archie_beeri
}

function installHttpd {
    cp ./httpd.archie.conf /etc/httpd/conf.d/archie.conf
}

function installSolr {
    # extract
    mkdir -p /opt/apache
    tar xf ./download/solr-7.7.2.tgz
    mv solr-7.7.2 /opt/apache/solr
    chown -R archie /opt/apache/solr
    # create beeri collection
    runuser -u archie -- /opt/apache/solr/bin/solr start
    runuser -u archie -- /opt/apache/solr/bin/solr create -c archie_beeri
    curl http://localhost:8983/solr/archie_beeri/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
    curl http://localhost:8983/solr/archie_beeri/config -H 'Content-type:application/json' -d @./solr.config.json
    curl http://localhost:8983/solr/archie_beeri/schema -H 'Content-type:application/json' -d @./solr.schema.json
}

function installActiveMq {
    # extract
    mkdir -p /opt/apache
    tar xf download/apache-activemq-5.15.10-bin.tar.gz
    mv apache-activemq-5.15.10 /opt/apache/activemq
    # Set permissions
    chown -R archie /opt/apache/activemq
}

function installTomcat {
    # extract
    mkdir -p /opt/apache
    tar xf ./download/apache-tomcat-8.5.53.tar.gz
    mv apache-tomcat-8.5.53 /opt/apache/tomcat
    cp ./tomcat.setenv.sh /opt/apache/tomcat/bin/setenv.sh
    # Set permissions
    chown -R archie /opt/apache/tomcat
}

prepareSystem
installPackages
createFolders
installMariaDb
installHttpd
installSolr
installActiveMq
installTomcat
