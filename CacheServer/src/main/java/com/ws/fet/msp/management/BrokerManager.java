package com.ws.fet.msp.management;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl;
import org.apache.activemq.artemis.api.core.management.BroadcastGroupControl;
import org.apache.activemq.artemis.api.core.management.QueueControl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.fet.msp.config.CacheServerProperties;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class BrokerManager {
	private String ON_PREFIX = null;
	private String BROKER_NAME = null;

	@Autowired
	private CacheServerProperties cfg = null;

	@PostConstruct
	public void startManager() throws Exception {
		if (!cfg.getJms().getJmxPrefix().endsWith(":")) {
			ON_PREFIX = cfg.getJms().getJmxPrefix() + ":";
		} else {
			ON_PREFIX = cfg.getJms().getJmxPrefix();
		}

		BROKER_NAME = ON_PREFIX + "broker=\"%B\"".replaceAll("%B", cfg.getJms().getBrokerName());
	}

	private JMXConnector connector = null;
	private MBeanServerConnection mbsc = null;

	private ObjectName getBrokerName() throws MalformedObjectNameException {
		ObjectName on = new ObjectName(BROKER_NAME);
		return on;
	}

	private ObjectName getAddressName(String queue) throws MalformedObjectNameException {
		ObjectName bn = getBrokerName();
		ObjectName an = new ObjectName(
		        bn.getCanonicalName() + ",component=addresses,address=\"%A\"".replaceAll("%A", queue));

		return an;
	}

	private ObjectName getQueueName(String queue) throws MalformedObjectNameException {
		ObjectName an = getAddressName(queue);
		ObjectName qn = new ObjectName(an.getCanonicalName()
		        + ",subcomponent=queues,routing-type=\"anycast\",queue=\"%Q\"".replaceAll("%Q", queue));

		return qn;
	}

	private ObjectName getBroadcastName() throws MalformedObjectNameException {
		ObjectName bn = getBrokerName();
		ObjectName on = new ObjectName(
		        bn.getCanonicalName() + ",component=broadcast-groups,name=\"%B\"".replaceAll("%B", "bg-group1"));

		return on;
	}

	private ActiveMQServerControl getServerControl() throws MalformedObjectNameException {
		ObjectName on = getBrokerName();
		ActiveMQServerControl serverControl = MBeanServerInvocationHandler.newProxyInstance(mbsc, on,
		        ActiveMQServerControl.class, false);

		return serverControl;
	}

	private QueueControl getQueueControl(String queue) throws MalformedObjectNameException {
		ObjectName on = getQueueName(queue);
		QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(mbsc, on, QueueControl.class, false);
		return queueControl;
	}

	private BroadcastGroupControl getBroadcastControl() throws MalformedObjectNameException {
		ObjectName on = getBroadcastName();
		BroadcastGroupControl control = MBeanServerInvocationHandler.newProxyInstance(mbsc, on,
		        BroadcastGroupControl.class, false);

		return control;
	}

	public void connectNode(String node) throws MalformedURLException, IOException {
		if (connector != null) {
			try {
				connector.close();
			} catch (IOException e) {
				log.debug("Fail to close old JMX connection");
			}
		}

		String nodeUrl = cfg.getJms().getJmxUrl().get(node);

		log.debug("Connnecting to node [{}] using URL [{}]", node, nodeUrl);
		this.connector = JMXConnectorFactory
		        .connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://%S/jmxrmi".replaceAll("%S", nodeUrl)), null);

		this.mbsc = connector.getMBeanServerConnection();
	}

	public List<String> getNodes() {
		List<String> nodes = cfg.getJms().getJmxUrl().keySet().stream().collect(Collectors.toList());

		return nodes;

	}

	public String[] getQueueNames() throws MalformedObjectNameException {
		String[] names = null;
		ActiveMQServerControl serverControl = getServerControl();
		names = serverControl.getAddressNames();

		return names;
	}

	public List<ClientConnection> getClients() throws Exception {
		ActiveMQServerControl serverControl = getServerControl();
		ObjectMapper mapper = new ObjectMapper();
		List<ClientConnection> conn = mapper.readValue(serverControl.listConnectionsAsJSON(),
		        new TypeReference<List<ClientConnection>>() {
		        });
		return conn;
	}

	public String getVersion() throws MalformedObjectNameException {
		ActiveMQServerControl mq = getServerControl();
		return mq.getVersion();
	}

	public BrokerStatus getStatus() throws MalformedObjectNameException {
		BrokerStatus status = new BrokerStatus();
		ActiveMQServerControl mq = getServerControl();
		BeanUtils.copyProperties(mq, status);
		
		return status;
	}

	public boolean killClient(String ip) throws Exception {
		ActiveMQServerControl mq = getServerControl();
		return mq.closeConnectionsForAddress(ip);
	}
	
	public long getMessageCount(String queue) throws MalformedObjectNameException {
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.getMessageCount();
	}
	
	public long getMessageAcknowledged(String queue) throws MalformedObjectNameException{
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.getMessagesAcknowledged();
	}
	
	public long getMessagesExpired(String queue) throws MalformedObjectNameException{
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.getMessagesExpired();
	}
	
	public long getMessagesKilled(String queue) throws MalformedObjectNameException{
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.getMessagesKilled();
	}
	
	public long getMessagesAdded(String queue) throws MalformedObjectNameException{
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.getMessagesAdded();
	}
	

	public CompositeData[] getMessage(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.browse();
	}
	
	public int flushQueue(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.removeMessages(null);
	}
	
	public void pauseQueue(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		if (!queueControl.isPaused()) {
			queueControl.pause();
		}
	}
	public boolean isPausedQueue(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		return queueControl.isPaused();
	}
	
	public void resumeQueue(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		if (queueControl.isPaused()) {
			queueControl.resume();
		}
	}
	
	public void resetStats(String queue) throws Exception {
		QueueControl queueControl = getQueueControl(queue);
		queueControl.resetMessageCounter();
		queueControl.resetMessagesAcknowledged();
		queueControl.resetMessagesAdded();
		queueControl.resetMessagesExpired();
		queueControl.resetMessagesKilled();
	}

	
	public String getBackupGroup() throws Exception {
		BroadcastGroupControl bgControl = getBroadcastControl();
		bgControl.getName();
		return bgControl.getConnectorPairsAsJSON();
	}
	// public static void main(final String[] args) throws Exception {
	// ObjectName on = new ObjectName("org.apache.activemq.artemis:broker=\"broker0\"");
	// JMXConnector connector = JMXConnectorFactory
	// .connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://192.168.1.51:10099/jmxrmi"), null);
	// MBeanServerConnection mbsc = connector.getMBeanServerConnection();
	// ActiveMQServerControl serverControl = MBeanServerInvocationHandler.newProxyInstance(mbsc, on,
	// ActiveMQServerControl.class, false);
	//
	// ObjectMapper mapper = new ObjectMapper();
	// List<ClientConnection> conn = mapper.readValue(serverControl.listConnectionsAsJSON(),
	// new TypeReference<List<ClientConnection>>() {
	// });
	// System.out.println(conn);
	//
	// connector.close();
	// }
}
