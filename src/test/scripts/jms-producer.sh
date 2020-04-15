#!/bin/bash

#curl -u admin:admin -d "body=abc" -d "key=123" http://localhost:8161/api/message/my.test?type=queue

DATA_FOLDER="`dirname $0`/../data"
MESSAGE_BODY=$(<$DATA_FOLDER/import-attributes.json)
curl -u admin:admin -d "body=$MESSAGE_BODY" -d "archieJobName=import-folder" http://localhost:8161/api/message/archie.beeri.jobs?type=queue
