#!/bin/bash

REQUIRED=1.8

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

if [ `type -p java` != "" ]; then
    JAVA=`type -p java`
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    JAVA="$JAVA_HOME/bin/java"
else
    echo "No Java installed"
    exit 1;
fi

if [[ "$JAVA" ]]; then
    JAVA_VER=$("$JAVA" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$JAVA_VER" < "$REQUIRED"  ]]; then
        echo "Application required Java $REQUIRED but $JAVA_VER installed"
        exit 1;
    fi
fi

#set PROV_HOME
PROV_HOME=`cd "$PRGDIR/.." ; pwd`

PROV_LIB_HOME=$PROV_HOME/lib
PROV_BIN_HOME=$PROV_HOME/bin
PROV_RES_HOME=$PROV_HOME/config

TMPLIB=`find $PROV_LIB_HOME -name "*.jar"`

for c in $TMPLIB;
do
  CLASSPATH=$CLASSPATH:$c
done;

export CLASSPATH JAVA
