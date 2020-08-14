## Build
sudo docker build --rm --tag local/archie.beeri:2 .

## Run
sudo docker run -idt --name=archie.beeri.2 -p 80:80 -p 8080:8080 -p 8983:8983 -p 8161:8161 local/archie.beeri:2
-i, --interactive 
-d, --detach 
-t, --tty 
-p, --publish

## Connect
sudo docker exec -it archie.beeri.2 /bin/bash

## Optional configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address to 0.0.0.0
* Edit /opt/apache/activemq/conf/jetty.xml > bean id="jettyPort" >  property name="host" : set value to 0.0.0.0
* Edit /opt/apache/tomcat/conf/tomcat-users.xml > Add user to manager-gui role

## Test

Docker published ports
* http://localhost
* http://localhost:8983/solr
* http://localhost:8161/admin/ (user: admin, password: admin)

Proxy
* http://localhost/api
* http://localhost/docs

Direct access
Replace 172.17.0.2 with real container ip
sudo docker inspect archie.beeri.2 | grep IPAddress
* http://172.17.0.2
* http://172.17.0.2:8983/solr
* http://172.17.0.2:8161/admin/
* mysql -h 172.17.0.2 -u archie -p archie_beeri