#!/bin/bash

ROOT_DIRECTORY="/data/msp/ftp/"
#ROOT_DIRECTORY="/Users/qqpapaya/Desktop/delete/fet/msp/ftp/"

## declare an array variable
declare -a ACCOUNTS=("7654321" "TESTLLL")

GEN_FILE_COUNT=10

for ((i=1;i<=$GEN_FILE_COUNT;i++))
do
for SOURCE_ACCOUNT in "${ACCOUNTS[@]}"
do
cat << EOF >> "$ROOT_DIRECTORY""$SOURCE_ACCOUNT"/"$SOURCE_ACCOUNT""$(printf '%014d' $i)"".txt"
FTP TEST from WS generator -  $i !
E
+886938304782
0936105564
EOF
touch "$ROOT_DIRECTORY""$SOURCE_ACCOUNT"/"$SOURCE_ACCOUNT""$(printf '%014d' $i)"".end"
done
done
