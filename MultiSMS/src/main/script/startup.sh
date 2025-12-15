#!/bin/bash

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

. $PRGDIR/ctl.sh
. $PRGDIR/env.sh

# Set your java runtime option here
JAVA_OPTS="-Xms256m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"

if [[ -n $SHS_ACTIVE_PROFILE ]]; then
  JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=$EMG_ACTIVE_PROFILE"
fi;
if [[ -n $EMG_PID ]]; then
  JAVA_OPTS="$JAVA_OPTS -Demg.pid=$EMG_PID"
fi;

START=0
if [[ -f $EMG_PID ]]; then
  CURRENT_PID=`cat $EMG_PID`
  kill -0 $CURRENT_PID >> /dev/null 2>&1
  PID_STATUS=$?
  if [ $PID_STATUS -eq 0 ]; then
    echo "Server running with PID $CURRENT_PID";
    exit 1;
  else
    echo "Old PID file found $CURRENT_PID process not running, Remove PID file"
    rm $DR_PID;
    START=1;
  fi;
else
  START=1;
fi;

if [[ $START -eq 1 ]]; then
  java $JAVA_OPTS com.ws.emg.server.EmgServer $@ &
  exit 0;
else
  exit 1;
fi;