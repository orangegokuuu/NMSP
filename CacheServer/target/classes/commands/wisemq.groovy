package commands

import java.text.BreakIterator

import javax.management.openmbean.CompositeData

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.springframework.beans.factory.BeanFactory

import com.ws.fet.msp.management.BrokerManager
import com.ws.fet.msp.management.BrokerStatus
import com.ws.fet.msp.management.ClientConnection
import com.ws.msp.pojo.*
import com.ws.msp.service.*
import com.ws.util.DateUtil
import com.ws.util.StringUtil

@Usage("Show system information")
class wisemq {
	public static final String ALL = "*";

	enum ShowSubCmd {
		status, version, client, queue
	}

	enum QueueSubCmd {
		count, browse, flush, block, unblock, reset
	}

	@Usage("Show available nodes")
	@Command
	public void nodes(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		BrokerManager broker = beanFactory.getBean(BrokerManager.class);
		PrintWriter out = context.getWriter();

		if (broker.getNodes() != null && broker.getNodes().size() > 0) {
			out.println("Availble Node");
			out.println("==============================");
			for (String node : broker.getNodes()) {
				out.println(node);
			}
		}
	}

	@Usage("Connect to node")
	@Command
	public void connect(InvocationContext context, @Usage("node name") @Argument String node) {
		PrintWriter out = context.getWriter();
		if (StringUtil.isEmpty(node)) {
			out.println("Missing argument ", Color.red);
			return;
		}
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		BrokerManager broker = beanFactory.getBean(BrokerManager.class);
		boolean found = false;
		for (String n : broker.getNodes()) {
			if (n.equals(node)) {
				found = true;
			}
		}

		if (!found) {
			out.println("Invalid node name ["+node+"]", Color.red);
			return;
		}

		broker.connectNode(node);
		context.getAttributes().put("jms.node", node);
		out.println("Connected to node "+node);
	}

	private void printClient(PrintWriter out, BrokerManager broker, String node) {
		out.println("ID                   Address                        Create Time                    Session");
		out.println("==================== ============================== ============================== ==========");
		for (ClientConnection client : broker.getClients()) {
			out.printf("%-20s %-30s %-30s %d\n",client.getConnectionID(), client.getClientAddress(), DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss",client.getCreationTime()), client.getSessionCount());
		}
	}

	private void printVersion(PrintWriter out, BrokerManager broker, String node) {
		out.println("Version : "+broker.getVersion());
	}

	private void printStatus(PrintWriter out, BrokerManager broker, String node) {
		BrokerStatus status = broker.getStatus();
		out.printf("UUID                    : %s\n",status.getNodeID());
		out.printf("Started                 : %s\n",status.isStarted());
		out.printf("Clustered               : %s\n",status.isClustered());
		out.printf("Master Node             : %s\n",!(status.isBackup()));
		out.printf("Backup Synchronized     : %s\n",status.isReplicaSync());
		out.printf("Data Persistence        : %s\n",status.isPersistenceEnabled());
		out.printf("Uptime                  : %s\n",status.getUptime());
		out.printf("Connections             : %d\n",status.getConnectionCount());
		out.printf("Max Thread              : %d\n",status.getThreadPoolMaxSize());
		out.printf("Bindings Directory      : %s\n",status.getBindingsDirectory());
		out.printf("Journal Type            : %s\n",status.getJournalType());
		out.printf("Journal Directory       : %s\n",status.getJournalDirectory());
	}

	private void printQueue(PrintWriter out, BrokerManager broker, String node) {
		String[] queues =  broker.getQueueNames();
		Arrays.sort(queues);
		for (String q : queues) {
			out.println(q);
		}
	}

	@Usage("Show system info")
	@Command
	public void show(InvocationContext context, @Usage("operation") @Argument ShowSubCmd cmd) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		BrokerManager broker = beanFactory.getBean(BrokerManager.class);

		if (cmd == null) {
			out.println("Invalid command", Color.red);
			return;
		}

		String node = context.getAttributes().get("jms.node");
		if (StringUtil.isEmpty(node)) {
			out.println("No server connected", Color.red);
			return;
		}

		out.println("Current Node : "+node);
		out.println("===================================================");

		switch (cmd) {
			case ShowSubCmd.status :
				printStatus(out, broker, node);
				break;

			case ShowSubCmd.queue :
				printQueue(out, broker, node);
				break;

			case ShowSubCmd.client :
				printClient(out, broker, node);
				break;

			case ShowSubCmd.version :
				printVersion(out, broker, node);
				break;

			default :
				out.println("Invalid command", Color.red);
				return;
		}
	}

	private void printQueueCount(PrintWriter out, BrokerManager broker, String name) {
		if (ALL.equals(name)) {
			String[] queues =  broker.getQueueNames();
			Arrays.sort(queues);
			for (String q : queues) {
				out.printf("%-30s : %d messages\n",q,broker.getMessageCount(q));
			}
		} else {
			out.printf("%-30s : %d messages\n",name,broker.getMessageCount(name));
			out.printf("%-30s : %d messages acknowledged\n",name,broker.getMessageAcknowledged(name));
			out.printf("%-30s : %d messages expired\n",name,broker.getMessagesExpired(name));
			out.printf("%-30s : %d messages killed\n",name,broker.getMessagesKilled(name));
			out.printf("%-30s : %d messages added\n",name,broker.getMessagesAdded(name));
		}
	}

	private void printQueueData(PrintWriter out, BrokerManager broker, String name) {
		CompositeData[] data = broker.getMessage(name);
		for (CompositeData d : data) {
			out.println(d);
		}
	}

	@Usage("Queue operations")
	@Command
	public void kill(InvocationContext context, @Usage("Queue name") @Argument String ip) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		BrokerManager broker = beanFactory.getBean(BrokerManager.class);

		if (ip == null) {
			out.println("Invalid command", Color.red);
			return;
		}

		String node = context.getAttributes().get("jms.node");
		if (StringUtil.isEmpty(node)) {
			out.println("No server connected", Color.red);
			return;
		}
		if (broker.killClient(ip)){
			out.println("Client has been removed");
		} else{
			out.println("Fail to remove client");
		}
	}
	
	@Usage("Queue operations")
	@Command
	public void queue(InvocationContext context, @Usage("operation") @Argument QueueSubCmd cmd, @Usage("Queue name") @Argument String name) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		BrokerManager broker = beanFactory.getBean(BrokerManager.class);

		if (cmd == null) {
			out.println("Invalid command", Color.red);
			return;
		}

		if (StringUtil.isEmpty(name)) {
			out.println("Invalid command, missing queue name", Color.red);
			return;
		}

		String node = context.getAttributes().get("jms.node");
		if (StringUtil.isEmpty(node)) {
			out.println("No server connected", Color.red);
			return;
		}

		boolean qExists = false;
		String[] queues =  broker.getQueueNames();
		if (!ALL.equals(name)) {
			for (String q : queues) {
				if (q.equals(name)) {
					qExists = true;
					break;
				}
			}
		} else {
			qExists = true;
		}

		if (!qExists) {
			out.println("Invalid Queue name", Color.red);
			return;
		}

		out.println("Current Queue : "+name);
		out.println("===================================================");

		switch (cmd) {
			case QueueSubCmd.count :
				printQueueCount(out, broker, name);
				break;
			case QueueSubCmd.browse :
				if (ALL.equals(name)) {
					out.println("browse all queue operation not allowed", Color.red);
				} else {
					printQueueData(out, broker, name);
				}
				break;
			case QueueSubCmd.flush :
				if (ALL.equals(name)) {
					out.println("flush all queue operation not allowed", Color.red);
				} else {
					int numOfFlushedMsg =  broker.flushQueue(name);
					if(numOfFlushedMsg > 0){
						out.printf("%d message(s) have been removed\n", numOfFlushedMsg);
					}else {
						out.println("Queue is empty!", Color.red);
					}
				}
				break;
			case QueueSubCmd.block :
				if (ALL.equals(name)) {
					out.println("block all queue operation not allowed", Color.red);
				} else {
					broker.pauseQueue(name);
					out.printf("Queue %s has been paused\n", name);
				}
				break;
			case QueueSubCmd.unblock :
				if (ALL.equals(name)) {
					out.println("unblock all queue operation not allowed", Color.red);
				} else {
					broker.resumeQueue(name);
					out.printf("Queue %s has been resumed\n", name);
				}
				break;
			case QueueSubCmd.reset :
				if (ALL.equals(name)) {
					out.println("reset all queue operation not allowed", Color.red);
				} else {
					broker.resetStats(name);
					out.printf("Queue %s statistic has been reset\n", name);
				}
				break;
			default :
				out.println("Invalid command", Color.red);
				return;
		}
	}
}