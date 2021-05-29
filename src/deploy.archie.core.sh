set -e

export JAVA_HOME=/usr/lib/jvm/java-11
echo Java home is $JAVA_HOME

function setup {
    mvn clean deploy
    mv target/lib/* /opt/hilel14/archie/beeri/lib
    mv target/archie-beeri-core*.jar /opt/hilel14/archie/beeri/lib
    cp src/main/scripts/* /opt/hilel14/archie/beeri/bin
    cp src/main/resources/* /opt/hilel14/archie/beeri/resources
    cp src/setup/docker/logback.xml /opt/hilel14/archie/beeri/resources
    chmod 755 /opt/hilel14/archie/beeri/bin/*
}

function update {
    PID_FILE=/var/opt/archie/beeri/logs/jobs-consumer.pid
    if test -f "$PID_FILE"; then
        echo "killing running jobs-consumer process..."        
        kill `cat $PID_FILE`
    fi
    mvn clean install -DskipTests
    rm /opt/hilel14/archie/beeri/lib/archie-beeri-core*.jar
    mv target/archie-beeri-core*.jar /opt/hilel14/archie/beeri/lib
    /opt/hilel14/archie/beeri/bin/jobs-consumer.sh
}

case "$1" in
    setup)
        setup
    ;;
    update)
        update
    ;;
    *)
        echo $"Usage: $0 {setup|update}"
        exit 1
esac