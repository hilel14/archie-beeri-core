## Build

docker build --rm --tag local/archie.beeri:2 .

## Run

docker run -idt --mount type=bind, source="$(pwd)", target=/home/archie/archie-soft --name=archie.beeri.2 -p 80:80 -p 8983:8983 -p 8161:8161 local/archie.beeri:2
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
* Set Java 11 as default: `update-alternatives --config java`
* Set utf-8 as default locale: `export LC_ALL=en_US.UTF-8`
* Create admin user: `/opt/hilel14/archie/beeri/bin/users-admin.sh`
* Add some collections: `/opt/hilel14/archie/beeri/bin/create-dc-collections.sh core/src/test/resources/collections.txt`

## Docker production post-installation tasks

On production, move HTTPd from docker container to the host?

* Remove development related directives from HTTPd config file
* Install TLS certificate
* Run mysql_secure_installation
* SET PASSWORD FOR 'archie'@'localhost' = PASSWORD('changeme');
* Make sure relevent scripts are executable
* Update archie.beeri.properties
* Add appllication users

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

# Angular Live Development Server

Change directory to root of Angular project
* ng serve --disableHostCheck
* ng serve --base-href=/gui/
* ng serve --host 0.0.0.0
