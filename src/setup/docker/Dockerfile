FROM centos:7.8.2003
WORKDIR /root/
ADD . /root
RUN ./setup.sh
CMD ["/root/start.sh"]
EXPOSE 80 3306 8080 8983 8161
