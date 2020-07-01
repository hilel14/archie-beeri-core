#!/bin/sh

# exit on first error
set -e

# create standard folders in assets bucket
aws s3api put-object --bucket $1 --key originals/
aws s3api put-object --bucket $1 --key thumbnails/
aws s3api put-object --bucket $1 --key text/
