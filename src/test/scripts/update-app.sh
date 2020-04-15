#!/bin/sh
set -e
# cd /path/to/api/project/root
mvn clean install -DskipTests
rm -rf /opt/hilel14/archie/beeri/webapp/WEB-INF/classes/org/
mv target/archie-beeri-api-1.0-SNAPSHOT/WEB-INF/classes/org/ /opt/hilel14/archie/beeri/webapp/WEB-INF/classes/
sudo systemctl restart tomcat8
