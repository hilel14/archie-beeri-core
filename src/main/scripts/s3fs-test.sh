#!/bin/sh

# echo aws_access_key_id:aws_secret_access_key > /etc/passwd-s3fs

s3fs archie-asset-store-test /var/opt/test/ -o url=https://s3-eu-west-1.amazonaws.com -o use_cache=/tmp,allow_other,uid=1000,gid=1000
# -o dbglevel=info -f -o curldbg

# add line to /etc/fstab
# s3fs#archie-asset-store-test /var/opt/test fuse url=https://s3-eu-west-1.amazonaws.com,use_cache=/tmp,allow_other,uid=1000,gid=1000 0 0
