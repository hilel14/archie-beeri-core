#!/bin/sh

for d in assetstore import logs work; do
    mkdir -p /var/opt/archie/beeri/$d
done
for d1 in public private secret; do
    mkdir /var/opt/archie/beeri/assetstore/$d1
    for d2 in originals thumbnails text; do
        mkdir /var/opt/archie/beeri/assetstore/$d1/$d2
    done
done

for d in bin config webapp; do
    mkdir -p /opt/hilel14/archie/beeri/$d
done