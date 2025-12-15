package com.ws.ibm.imq.config;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQSimpleConnectionManager;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class MQConnectionFactory {
  private static MQSimpleConnectionManager myConnMan;
  private static MQPoolToken token;
  static {
    //setup a default connection manager
    myConnMan = new MQSimpleConnectionManager();
    myConnMan.setActive(MQSimpleConnectionManager.MODE_AUTO);
    myConnMan.setTimeout(1200000);
    myConnMan.setHighThreshold(50);
    MQEnvironment.setDefaultConnectionManager(myConnMan);
    MQException.log = null;
    //force the default connection pool to remain active by keeping a dummy token
    token = MQEnvironment.addConnectionPoolToken();
  }

  private MQConnectionFactory() {
  }

  public static MQQueueManager createConnection(String qmgrName) throws
      MQException {
    return new MQQueueManager(qmgrName, myConnMan);
  }

  public static synchronized MQQueueManager createConnection(String hostname, int port,
                                                String channel, String qmgrName) throws MQException {
    MQEnvironment.hostname = hostname;
    MQEnvironment.port = port;
    MQEnvironment.channel = channel;
    return new MQQueueManager(qmgrName, myConnMan);
  }
}