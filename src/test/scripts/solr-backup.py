#!/usr/bin/env python2

import os

data_path = "/opt/apache/solr/server/solr/archie_beeri/data/"
history_file = "upload-history.txt"

os.chdir(data_path)

f = open(history_file, "r")
history = f.readlines()
print(history)

for folder in os.listdir(data_path):
    if folder.startswith("snapshot."):
        if folder in history:
            print(folder + " already uploaded")
        else:
            print(folder)
