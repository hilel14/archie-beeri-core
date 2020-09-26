#!/bin/bash
# ~/archie-soft/core/src/setup/docker/archie.core.setup.sh

set -e

export JAVA_HOME=/usr/lib/jvm/java-11
echo Java home is $JAVA_HOME

function findHome {
    DEV_HOME=`dirname "$0"`      #~/archie-soft/core/src/setup/docker
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core/src/setup
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core/src
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft
    # one liner:
    #DEV_HOME=$(dirname $(dirname $(dirname $(dirname $(dirname $0)))))
}

function setup {
    findHome
    # core
    cd $DEV_HOME/core
    mvn clean deploy
    mv target/lib/* /opt/hilel14/archie/beeri/lib
    mv target/archie-beeri-core*.jar /opt/hilel14/archie/beeri/lib
    cp src/main/scripts/* /opt/hilel14/archie/beeri/bin
    cp src/main/resources/* /opt/hilel14/archie/beeri/resources
    chmod 755 /opt/hilel14/archie/beeri/bin/*
    # ws
    cd $DEV_HOME/ws
    mvn clean install
    mv target/archie-beeri-ws*.war /opt/apache/tomcat/webapps/
}

function update-core {
    findHome
    cd $DEV_HOME/core
    mvn clean install -DskipTests
    rm /opt/hilel14/archie/beeri/lib/archie-beeri-core*.jar
    mv target/archie-beeri-core*.jar /opt/hilel14/archie/beeri/lib
}

function update-ws {
    findHome
    cd $DEV_HOME/ws
    mvn clean install -DskipTests
    mv target/archie-beeri-ws*.war /opt/apache/tomcat/webapps/
}

case "$1" in
    setup)
        setup
    ;;
    update-core)
        update-core
    ;;
    update-ws)
        update-ws
    ;;
    *)
        echo $"Usage: $0 {setup|update-core|update-ws}"
        exit 1
esac