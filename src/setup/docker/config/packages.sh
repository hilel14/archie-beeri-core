#!/bin/sh
# Install core packages
yum -y install epel-release
yum -y install java-11-openjdk mariadb-server httpd ImageMagick ghostscript tesseract tesseract-langpack-heb lsof curl wget
# Install optional packages
yum -y install vim bash-completion iproute
