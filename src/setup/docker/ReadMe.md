## Build
sudo docker build --rm --tag local/archie.beeri:2 .

## Run
sudo docker run -idt --name=archie.beeri.2 -p 80:80 -p 3306:3306 -p 8080:8080 -p 8983:8983 -p 8161:8161 local/archie.beeri:2
-i, --interactive 
-d, --detach 
-t, --tty 
-p, --publish

## Connect
sudo docker exec -it archie.beeri.2 /bin/bash

## Optional configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address = 0.0.0.0
* Edit /opt/apache/activemq/conf/jetty.xml > bean id="jettyPort" >  property name="host" : set value to 0.0.0.0
* Edit /opt/apache/tomcat/conf/tomcat-users.xml > Add user to manager-gui role

## Test

Docker published ports. For direct access, replace localhost with internal ip, obtained with docker inspect (example: 172.17.0.2)
* http://localhost
* http://localhost:8983/solr
* http://localhost:8161/admin/ (user: admin, password: admin)
* http://localhost:8080/manager/html
* mysql -h 127.0.0.1 -u archie -p archie_beeri

HTTPd reverse Proxy
* http://localhost/api
* http://localhost/docs

## Deploy WS
* cd archie-beeri-org/core
* mvn clean install
* cd archie-beeri-org/ws
* mvn clean install
* sudo docker cp target/archie-beeri-ws.war archie.beeri.2:/opt/apache/tomcat/webapps/
