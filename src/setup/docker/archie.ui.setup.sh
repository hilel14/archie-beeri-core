#!/bin/bash

function findHome {
    DEV_HOME=`dirname "$0"`      #~/archie-soft/core/src/setup/docker
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core/src/setup
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core/src
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft/core
    DEV_HOME=`dirname $DEV_HOME` #~/archie-soft
    # one liner:
    #DEV_HOME=$(dirname $(dirname $(dirname $(dirname $(dirname $0)))))
}

findHome
cd $DEV_HOME/gui
npm install
ng build --prod --base-href / --i18n-file src/locale/messages.he.xlf --i18n-format xlf --i18n-locale he
rm -rf /var/www/archie/beeri/*
mv dist/archie-beeri-ui/* /var/www/archie/beeri
