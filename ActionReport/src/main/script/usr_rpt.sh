#!/bin/bash

# config home
# export UACT_HOME=/home/msp
# MSP_HOME

# installation path
UACT_PATH="/home/msp/fet/ActionReport.jar"

# Hostname
HOSTS=( fet-apo1 fettde )
MYHOST=`hostname`

# csv path
ACTIVITY_PATH="/data/msp/logs/sac/activity/"
ACCESS_PATH="/data/msp/logs/sac/access/"

ACTIVITY_FILE_NAME="MSP_`hostname`_ACTIVITY_`date -d "1 hour ago" '+%Y%m%d_%H'`.log"
ACCESS_FILE_NAME="MSP_`hostname`_ACCESS_`date -d "1 hour ago" '+%Y%m%d_%H'`.log"

# peer host 
# REMOTE_HOST="msp@192.168.1.51"
# REMOTE_HOST="msp@192.168.1.245"

# Set your java runtime option here
JAVA_OPTS="-Xms256m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"

for HOST in ${HOSTS[@]}
do
  if [ $HOST != $MYHOST ]; then     
    # check report type
    if [ "$2" = "activity" ]; then
        SRC_PATH="$ACTIVITY_PATH$ACTIVITY_FILE_NAME";
        DEST_PATH="$ACTIVITY_PATH${ACTIVITY_FILE_NAME/$MYHOST/$HOST}";
    else
        SRC_PATH="$ACCESS_PATH$ACCESS_FILE_NAME";
        DEST_PATH="$ACCESS_PATH${ACCESS_FILE_NAME/$MYHOST/$HOST}";
    fi

    # check file exist or not in localhost
    if [ ! -f "$SRC_PATH" ];
    then
        echo 'File does not exist. Generating Report...';
        java $JAVA_OPTS -jar $UACT_PATH $@;
    fi

    # check file exist or not in other host
    if ssh $HOST stat $DEST_PATH \> /dev/null 2\>\&1
    then
        echo "Report exists on destination host [$HOST].";
    else
        echo 'File does not exist. Copying Report...';
        # copy report to other host(s)    
        echo "scp $SRC_PATH $HOST:$DEST_PATH";
        scp $SRC_PATH $HOST:$DEST_PATH;
    fi
  fi;
done