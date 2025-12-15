#!/bin/bash -x
# create queue. return output stearm 
# $1=target queue manager
# $2=target cp name

# mqm home

# queue manager ( DMZ.QM3 )
qmanager=$1
cpname=$2
cpnameL=`echo $cpname | tr '/A-Z/' '/a-z/'`
reqQName="SMS.$cpname.REQ.Q"
plyQName="SMS.$cpname.PLY.Q"
chlName="SMS.$cpname.CHL"
psName="SMS.$cpname.REQ.PS"
today=`date '+%Y-%m-%d'`
timeNow=`date '+%T'`

# login
#if [ "$(whoami)" != "mqm" ]; then
#  echo "This script should run as mqm" 
#fi

# not necessary
su mqm -s /bin/sh -c "setmqaut -m $qmanager -n $reqQName -t queue -g $cpnameL -remove"
su mqm -s /bin/sh -c "setmqaut -m $qmanager -n $plyQName -t queue -g $cpnameL -remove"

del_chl="Delete Channel($chlName)"
su mqm -s /bin/sh -c "echo \"$del_chl\" | runmqsc $qmanager"

del_req="Delete QL($reqQName)"
su mqm -s /bin/sh -c "echo \"$del_req\" | runmqsc $qmanager"

del_ply="Delete QL($plyQName)"
su mqm -s /bin/sh -c "echo \"$del_ply\" | runmqsc $qmanager"

del_ps="Delete process($psName)"
su mqm -s /bin/sh -c "echo \"$del_ps\" | runmqsc $qmanager"
