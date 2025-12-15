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
today=`date '+%Y-%m-%d'`
timeNow=`date '+%T'`
trigger_path=/tmp/trigger_entry.sh

# login
#if [ "$(whoami)" != "mqm" ]; then
#  echo "This script should run as mqm" 
#fi

req_cmd="DEFINE QLOCAL($reqQName) PROCESS(SMS.$cpname.REQ.PS) DESCR('Input queue for $cpname') INITQ(SMS.INIT.Q) DEFPSIST(YES) MAXMSGL(200000) TRIGGER TRIGTYPE(FIRST) REPLACE"

# run create  req_cmd as mqm
su mqm -s /bin/sh -c "echo \"$req_cmd\" | runmqsc $qmanager"
#su mqm -s /bin/sh -c "whoami"

ply_cmd="DEFINE QLOCAL($plyQName) DESCR ('Output queue for $cpname') MAXMSGL(2000) DEFPSIST(YES) REPLACE"

# run create ply_cmd as mqm
su mqm -s /bin/sh -c "echo \"$ply_cmd\" | runmqsc $qmanager"

#ps_cmd="DEFINE PROCESS(SMS.$cpname.REQ.PS) DESCR ('MT client process for $cpname') APPLTYPE(UNIX) APPLICID('curl -X POST $mqclientPath/triggerQueue/$cpname/ -d') REPLACE"
ps_cmd="DEFINE PROCESS(SMS.$cpname.REQ.PS) DESCR ('MT client process for $cpname') APPLTYPE(UNIX) APPLICID('$trigger_path') USERDATA(' ') ENVRDATA('$cpname') REPLACE" 

# run create ps_cmd
su mqm -s /bin/sh -c "echo \"$ps_cmd\" | runmqsc $qmanager"

chl_cmd="DEFINE CHANNEL ($chlName) CHLTYPE(SVRCONN) TRPTYPE(TCP) DESCR('Channel for app') HBINT(300) MAXMSGL(4194304) MCAUSER('$cpnameL') RCVDATA(' ') RCVEXIT(' ') MAXINST(10) SCYDATA(' ') SCYEXIT(' ') SENDDATA(' ') SENDEXIT(' ') REPLACE"

# run create chl
su mqm -s /bin/sh -c "echo \"$chl_cmd\" | runmqsc $qmanager"

# start chl
su mqm -s /bin/sh -c "runmqchl -c $chlName -m $qmanager"

# Auth
su mqm -s /bin/sh -c "setmqaut -m $qmanager -t qmgr -g $cpnameL +set +connect +inq"
su mqm -s /bin/sh -c "setmqaut -m $qmanager -n $reqQName -t q -p $cpnameL +browse +get +inq +passall +passid +put +set +setall +setid +chg +clr +dlt +dsp"
su mqm -s /bin/sh -c "setmqaut -m $qmanager -n $plyQName -t q -p $cpnameL +browse +get +inq +passall +passid +put +set +setall +setid +chg +clr +dlt +dsp"

