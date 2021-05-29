FROM centos:7.8.2003
WORKDIR /var/opt/init
ADD . /var/opt/init
ENV LC_ALL en_US.UTF-8
RUN ./setup.sh
CMD ["/var/opt/init/start.sh"]
EXPOSE 80
