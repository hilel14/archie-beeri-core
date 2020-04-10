#!/bin/sh

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`

java \
-cp $APP_HOME/webapp/WEB-INF/classes:$APP_HOME/webapp/WEB-INF/lib/*:$APP_HOME/lib/* \
org.hilel14.archie.beeri.jobs.ImportYomonimJob