#!/bin/sh

set -e

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`

java \
-Darchie.home=$APP_HOME \
-cp $APP_HOME/resources:$APP_HOME/lib/* \
org.hilel14.archie.beeri.core.cli.JobsConsumer &

echo $! > /var/opt/archie/beeri/logs/jobs-consumer.pid
