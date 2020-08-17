#!/bin/sh
# RPM repositories
yum -y install epel-release
# Core packages
LIST="java-11-openjdk mariadb-server httpd ImageMagick ghostscript tesseract tesseract-langpack-heb lsof curl wget"
# Optional packages
LIST="$LIST vim bash-completion iproute"
# Install
yum -y install $LIST
# NodeJS
curl -sL https://rpm.nodesource.com/setup_10.x | sudo bash -
yum -y install nodejs
