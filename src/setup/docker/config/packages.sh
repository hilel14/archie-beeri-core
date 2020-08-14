#!/bin/sh
# Install core packages
yum -y install epel-release
yum -y install java-1.8.0-openjdk-devel git maven lsof curl wget ImageMagick ghostscript tesseract-langpack-heb mariadb-server httpd
# Install optional packages
yum -y install vim bash-completion iproute
