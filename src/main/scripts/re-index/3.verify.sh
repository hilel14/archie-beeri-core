#!/bin/sh

head import.json -n 50

echo "\n\n\n"

echo "id filed count (Num Docs):"
grep \"id\": import.json | wc

echo "\n"

echo "dcTitle filed count"
grep \"dcTitle\": import.json | wc

echo "\n"

echo "Size of export and import files:"
ls -lh export.json
ls -lh import.json

echo "\n"
