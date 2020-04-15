#!/bin/sh

set -e

DEV_HOME=`dirname "$0"`          # api/src/test/scripts/
DEV_HOME=`dirname $DEV_HOME`     # api/src/test/
DEV_HOME=`dirname $DEV_HOME`     # api/src/
DEV_HOME=`dirname $DEV_HOME`     # api/
DEV_HOME=`dirname $DEV_HOME`

export JAVA_HOME=/usr/lib/jvm/java-11

echo "DEV_HOME=$DEV_HOME"

api () {
	cd $DEV_HOME/api
	mvn clean install
	/opt/apache/tomcat/bin/shutdown.sh	
	rm -rf /opt/hilel14/archie/beeri/webapp/WEB-INF/classes/org/
	mv target/classes/org/ /opt/hilel14/archie/beeri/webapp/WEB-INF/classes/
	/opt/apache/tomcat/bin/startup.sh
	echo "API deployment completed successfully"	
}

gui () {
	cd $DEV_HOME/gui
	ng build --prod --base-href / --i18n-file src/locale/messages.he.xlf --i18n-format xlf --i18n-locale he
	sudo rm -rf /var/www/archie/beeri
	sudo mv dist/archie-beeri-ui /var/www/archie/beeri
	sudo systemctl restart httpd
	echo "GUI deployment completed successfully"	
}


case "$1" in
	api)
		api
		;;
	gui)
		gui
		;;
	*)
		echo $"Usage: $0 {api|gui}"
		exit 1 
esac
