#!/bin/sh
CFG_FILE=${MSP_HOME}/config/application.properties

function subVar
{
        ORG=$1;
        VAR=$2;
        ARG=`echo $3 | sed -e 's/\//\\\\\//g'`
        VALUE=`echo $ORG | sed -e 's/\${'$VAR'}/'$ARG'/g'`
        echo $VALUE;
}

function getValue
{
        VALUE=`/bin/sed '/^\#/d' $CFG_FILE | /bin/grep "$1" | grep -v "{$1}" | /usr/bin/tail -n 1 | /bin/sed 's/^.*=//'`
        echo $VALUE | /bin/sed -e 's/^\s+//g' | /bin/sed -e 's/s+$//g'
}

function getVarParam
{
        ORG=`getValue $1`;
        VAR=`echo ${ORG%%\}*}`;
        VAR=`echo ${VAR##*\{}`;
        ARG=`getValue $VAR`;

        echo `subVar $ORG $VAR $ARG`;
}

function getParam
{
	PARAM=$1
	VALUE=$( getValue $PARAM )

	if [[ $VALUE = \$* ]]; then
		VALUE=$( getVarParam $VALUE )
	fi

	echo $VALUE;
}

# Read Param Name
export EMG_PID=$( getParam emg.pid )
export EMG_ACTIVE_PROFILE=$( getParam emg.active.profile )
export EMG_ADMIN_USER=$( getParam emg.admin.user )
export EMG_ADMIN_PASS=$( getParam emg.admin.pass )
export EMG_ADMIN_PORT=$( getParam emg.servlet.port )
export EMG_ADMIN_CONTEXT=$( getParam emg.admin.context )
export EMG_SSH_PORT=$( getParam emg.ssh.port )
