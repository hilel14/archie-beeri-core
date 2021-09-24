#!/bin/sh

# Extract docs from export file and save in a new file
jq .response.docs export.json > import.json