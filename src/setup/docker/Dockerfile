FROM centos:7.8.2003
WORKDIR /root/
ADD . /root
ENV LC_ALL en_US.UTF-8
RUN ./setup.sh
CMD ["/root/start.sh"]
EXPOSE 80
