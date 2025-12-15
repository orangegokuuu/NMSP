#!/bin/bash 
qmanager=$1
cpname=$2
user=`echo $cpname | tr '/A-Z/' '/a-z/'`
echo `whoami`

# create user (with lowercase).  
if ! id -u $user > /dev/null 2>&1; then
  useradd $user
fi
