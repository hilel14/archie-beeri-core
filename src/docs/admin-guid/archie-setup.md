# Archie application setup

## Initial setup

0. `cp src/config/service.sh /opt/hilel14/archie/beeri/bin`
0. `cp src/config/logging.properties /opt/hilel14/archie/beeri/config`
0. `mvn dependency:copy-dependencies && mv target/dependency/* /opt/hilel14/archie/beeri/lib`
0. `cp src/test/resources/archie.beeri.properties /opt/hilel14/archie/beeri/resources`
0. Edit */opt/hilel14/archie/beeri/resources/archie.beeri.properties* - adjust values to prodcution environment

## Application updates

0. See `update-app.sh`
