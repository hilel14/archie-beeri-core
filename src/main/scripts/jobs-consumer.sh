#!/bin/sh

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`

java \
-Djava.util.logging.config.file=$APP_HOME/config/logging.properties \
-cp $APP_HOME/webapp/WEB-INF/classes:$APP_HOME/webapp/WEB-INF/lib/*:$APP_HOME/lib/* \
org.hilel14.archie.beeri.cli.JobsConsumer &

echo $! > /opt/hilel14/archie/beeri/bin/jobs-consumer.pid
