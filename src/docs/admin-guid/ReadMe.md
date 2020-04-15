# Archie Installation and configuration

## Initial setup

Install the latest version of [CentOS](https://www.centos.org/) Linux.

Install some usefull packages

```bash
#!/bin/bash
sudo yum install epel-release
sudo yum install java-1.8.0-openjdk-devel git maven lsof curl wget ImageMagick ghostscript tesseract-langpack-heb bash-completion

```

Set host name and time zone

```bash
#!/bin/bash
sudo hostnamectl set-hostname archie.beeri.org.il
sudo timedatectl set-timezone Asia/Jerusalem
```

Clone `archie beeri` repository from GitHub:

```bash
#!/bin/bash
git clone https://github.com/hilel14/archie-beeri.git
```

Create dedicated user:

```bash
#!/bin/bash
sudo useradd archie
sudo passwd archie
```

Create application folders (and optionally mount AWS S3 buckets):

```bash
#!/bin/bash
sudo mkdir -p /var/opt/archie/beeri/{assetstore,backup,import,logs,mail}
  echo aws_access_key_id:aws_secret_access_key > /etc/passwd-s3fs
  echo "s3fs#archie-beeri-asset-store /var/opt/archie/beeri/assetstore fuse url=https://s3-eu-west-1.amazonaws.com,use_cache=/tmp,allow_other,uid=archie,gid=apache 0 0" >> /etc/fstab
  echo "s3fs#archie-import /var/opt/archie/beeri/import fuse url=https://s3-eu-west-1.amazonaws.com,use_cache=/tmp,allow_other,uid=archie,gid=apache 0 0" >> /etc/fstab
  echo "s3fs#archie-beeri-yomonim /var/opt/archie/beeri/mail/inbox fuse url=https://s3-eu-west-1.amazonaws.com,use_cache=/tmp,allow_other,uid=archie,gid=apache 0 0" >> /etc/fstab
sudo mkdir -p /var/opt/archie/beeri/assetstore/{public,private,secret,import}
sudo mkdir -p /var/opt/archie/beeri/assetstore/public/{assets,preview}
sudo mkdir -p /var/opt/archie/beeri/assetstore/private/{assets,preview}
sudo mkdir -p /var/opt/archie/beeri/assetstore/secret/{assets,preview}
sudo mkdir -p /var/opt/archie/beeri/mail/{attachments,done,inbox}
sudo chown -R archie.apache /var/opt/archie/beeri
sudo chcon -R -t httpd_sys_content_t /var/opt/archie/beeri/assetstore/public
sudo chcon -R -t httpd_sys_content_t /var/opt/archie/beeri/assetstore/private
sudo mkdir -p /opt/hilel14/archie/beeri/{bin,config,webapp}
sudo chown -R archie /opt/hilel14/archie/beeri
```
## Install and configure MariaDB

Iinital setup:

```bash
#!/bin/bash
sudo yum install mariadb-server
sudo systemctl enable mariadb
sudo systemctl start mariadb
mysql_secure_installation
```

Create application database and user:

```bash
#!/bin/bash
mysqladmin -u root -p create archie_beeri
mysql -u root -p -e "GRANT ALL ON archie_beeri.* TO 'archie'@'localhost' IDENTIFIED BY '12345678'";
cat ~/archie-beeri/api/src/setup/mariadb/create-archie-beeri-tables.sql | mysql -u archie -p archie_beeri
```

## Install and configure Apache Solr

Download [Apache Solr](http://lucene.apache.org/solr) version 7.7.1 and extract to `/opt/apache/solr`

Make the dedicated system user archie the owner of solr folder:

```bash
#!/bin/bash
sudo chown -R archie /opt/apache/solr
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/solr/solr.service /etc/systemd/system
sudo systemctl enable solr
sudo systemctl start solr
```

Test Solr installation:

```bash
#!/bin/bash
systemctl status solr
sudo jps -l
```

* Open browser and go to [administration console](http://localhost:8983/solr)

Create and configure `archie_beeri` collection

```bash
#!/bin/bash
sh ~/archie-beeri/api/src/setup/solr/create-beeri-collection.sh
/opt/apache/solr/bin/post -c archie_beeri ~/archie-beeri/api/src/test/resources/default-collection.xml
```

Test Solr configuration:

* Open terminal and execute:
`curl "http://localhost:8983/solr/archie_beeri/select?wt=json&indent=on&q=*:*"`

## Install and configure Apache ActiveMQ

Download the latest version of [Apache ActiveMQ](https://activemq.apache.org) and extract to `/opt/apache/activemq`

Make the dedicated system user archie the owner of activemq folder:

```bash
#!/bin/bash
sudo chown -R archie /opt/apache/activemq
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/activemq/activemq.service /etc/systemd/system
sudo systemctl enable activemq
sudo systemctl start activemq
```

Test ActiveMQ installation:

```bash
#!/bin/bash
systemctl status activemq
sudo jps -l
```

* Open browser and go to [administration console](http://127.0.0.1:8161/admin/)

## Install and configure Apache Tomcat

Download the latest 8.5 version of [Apache Tomcat](https://tomcat.apache.org) and extract to `/opt/apache/tomcat`

Remove unused mudules:

```bash
#!/bin/bash
sudo rm -rf /opt/apache/tomcat/webapps/{docs,examples,host-manager}
```

Make the dedicated system user archie the owner of tomcat folder:

```bash
#!/bin/bash
sudo chown -R archie /opt/apache/tomcat
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/tomcat/tomcat8.service /etc/systemd/system
sudo cp ~/archie-beeri/api/src/setup/tomcat/setenv.sh /opt/apache/tomcat/bin
sudo systemctl enable tomcat8
sudo systemctl start tomcat8
```

Test Tomcat installation

```bash
#!/bin/bash
systemctl status tomcat8
sudo jps -l
```

* Open browser and go to [administration console](http://localhost:8080)

## Install and configure Apache HTTPd

Install HTTPd package:

```bash
#!/bin/bash
sudo yum install httpd
```

Copy virtual host file to configuration folder:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/httpd/archie.beeri.virtual-host.conf /etc/httpd/conf.d
```

Set the correct ServerName in /etc/httpd/conf.d/archie.beeri.virtual-host.conf

Start HTTPd service:

```bash
#!/bin/bash
sudo systemctl enable httpd
sudo systemctl start httpd
```

Setup [Let’s Encrypt](https://letsencrypt.org) certificate

## Build and deploy Archie API

Build and deploy classes:

```bash
#!/bin/bash
cd ~/archie-beeri/api
mvn clean install
sudo mv target/archie-beeri-api-${version}/* /opt/hilel14/archie/beeri/webapp/
```

Copy configuration files to application folder:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/archie/archie-beeri-api.xml /opt/apache/tomcat/conf/Catalina/localhost
sudo cp ~/archie-beeri/api/src/setup/archie/jobs-consumer.sh /opt/hilel14/archie/beeri/bin
sudo cp ~/archie-beeri/api/src/setup/archie/users-admin.sh /opt/hilel14/archie/beeri/bin
sudo cp ~/archie-beeri/api/src/setup/archie/logging.properties /opt/hilel14/archie/beeri/config
sudo chown -R archie /opt/hilel14/archie
```

Replace 12345678 with real password in:
```
/opt/hilel14/archie/beeri/webapp/WEB-INF/classes/archie.beeri.properties
/opt/apache/tomcat/conf/Catalina/localhost/archie-beeri-api.xml
```

Test Archie API installation:

```bash
#!/bin/bash
su -c "/opt/hilel14/archie/beeri/bin/jobs-consumer.sh" archie
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp ~/archie-beeri/api/src/setup/archie/archie-beeri.service  /etc/systemd/system
sudo systemctl enable archie-beeri
sudo systemctl start archie-beeri
```

## Build and deploy Archie GUI

Create root site directory and copy htaccess file to it:

```bash
#!/bin/bash
sudo mkdir -p /var/www/archie/beeri
sudo chown -R root:root /var/www/archie/beeri
sudo chcon -R -t httpd_sys_content_t /var/www/archie/beeri
```

Build and deploy Angular app

```bash
#!/bin/bash
cd ~/archie-beeri/gui
npm update
ng build --prod --base-href / --i18n-file src/locale/messages.he.xlf --i18n-format xlf --i18n-locale he
sudo mv dist/archie-beeri-ui/* /var/www/archie/beeri
```
