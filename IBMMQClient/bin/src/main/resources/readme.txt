Before running this program, you need to...

1. make sure qManager started
if not, login mqm and start Queue Manager
strmqm qManagerName
(In lab(51), qManagerName would be DMZ.QM3)

2. make sure queue and user have been created.
if not, run script in ~/bin/mq/ or create cp using MQSAC (http://192.168.1.51:8084/mqsac/)
Script:
login msp
usage: sudo	./q_create (qManager) (cpId)
q_create.sh: sudo ./q_create DMZ.QM3 MQTEST
(cpId should be Uppercase)

sudo useradd mqtest
(username should be lowercase)

3. config /tmp/trigger.sh
edit param targetHost and point to your MQ Client host
*in production env, this sh is located at /var/mqm/bin/trigger.sh

4. run init queue monitor
login mqm
runmqtrm -m DMZ.QM3 -q SMS.INIT.Q

5. config properties
including ibm.submitAPIURL, ibm.jms.host, ibm.jms.port, ibm.jms.queueManagerName

6. triggerTask.sh
*need to config the path of trigger.sh
A bash that could trigger all queues where depth > 0 for all queue managers.
better to setup a conjob in order to enhance system reliability.
