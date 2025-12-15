package com.ws.fet.msp.management.command;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import javax.management.MalformedObjectNameException;
import javax.management.openmbean.CompositeData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.ws.fet.msp.management.BrokerManager;
import com.ws.fet.msp.management.BrokerStatus;
import com.ws.fet.msp.management.ClientConnection;
import com.ws.util.DateUtil;

@ShellCommandGroup("MSP wisemq information")
@SshShellComponent
public class wiswmq {

    @Autowired
    BrokerManager broker;

    @Autowired
    private SshShellHelper helper;

    private String connectionNode = new String();

    public static final String ALL = "*";

	enum ShowSubCmd {
		status, version, client, queue
	}

	enum QueueSubCmd {
		count, browse, flush, block, unblock, reset
	}

    private void checkConnection(){
        StringBuffer sb = new StringBuffer();
        
        if (connectionNode.length() == 0 || connectionNode.isEmpty()) {
            sb.append("No server connected\n");
            sb.append("==============================\n");
		}else{
            sb.append("Current Node : ");
            sb.append(connectionNode);
            sb.append("\n");
            sb.append("==============================\n");
        }

        helper.print(sb.toString());
    }

    private void wisemqShowStatus() throws MalformedObjectNameException{
        BrokerStatus status = broker.getStatus();
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("UUID                    : %s\n",status.getNodeID()));
        sb.append(String.format("Started                 : %s\n",status.isStarted()));
        sb.append(String.format("Clustered               : %s\n",status.isClustered()));
        sb.append(String.format("Master Node             : %s\n",!(status.isBackup())));
        sb.append(String.format("Backup Synchronized     : %s\n",status.isReplicaSync()));
        sb.append(String.format("Data Persistence        : %s\n",status.isPersistenceEnabled()));
        sb.append(String.format("Uptime                  : %s\n",status.getUptime()));
        sb.append(String.format("Connections             : %d\n",status.getConnectionCount()));
        sb.append(String.format("Max Thread              : %d\n",status.getThreadPoolMaxSize()));
        sb.append(String.format("Bindings Directory      : %s\n",status.getBindingsDirectory()));
        sb.append(String.format("Journal Type            : %s\n",status.getJournalType()));
        sb.append(String.format("Journal Directory       : %s\n",status.getJournalDirectory()));

        helper.print(sb.toString());
    }

    private void wisemqShowVersion() throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Version : "+broker.getVersion()));
        helper.print(sb.toString());
    }

    private void wisemqShowClient() throws Exception{
        StringBuffer sb = new StringBuffer();
        
        sb.append("ID                   Address                        Create Time                    Session\n");
        sb.append("==================== ============================== ============================== ==========\n");
        
		for (ClientConnection client : broker.getClients()) {
            sb.append(String.format("%-20s %-30s %-30s %d\n",client.getConnectionID(), client.getClientAddress(), DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss",client.getCreationTime()), client.getSessionCount()));
		}

        helper.print(sb.toString());
    }

    private void wisemqShowQueue() throws Exception{
        StringBuffer sb = new StringBuffer();
        String[] queues;
        try {
            queues = broker.getQueueNames();
            Arrays.sort(queues);
            sb.append("Queue Names   \n");
            sb.append("====================\n");
            for (String q : queues) {
                sb.append(q);
                sb.append("\n");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        helper.print(sb.toString());
    }

    private void wisemqQueueCount(String queuename) throws MalformedObjectNameException{
        StringBuffer sb = new StringBuffer();
        String[] queues =  broker.getQueueNames();
        if (queuename.equalsIgnoreCase("all")) {
            try {
                Arrays.sort(queues);
                for (String q : queues) {
                    try {
                        sb.append(String.format("%-30s : %d messages\n",q,broker.getMessageCount(q)));
                    } catch (Exception ex) {
                        
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		} else {
            sb.append(String.format("%-30s : %d messages\n",queuename,broker.getMessageCount(queuename)));
            sb.append(String.format("%-30s : %d messages acknowledged\n",queuename,broker.getMessageAcknowledged(queuename)));
            sb.append(String.format("%-30s : %d messages expired\n",queuename,broker.getMessagesExpired(queuename)));
            sb.append(String.format("%-30s : %d messages killed\n",queuename,broker.getMessagesKilled(queuename)));
            sb.append(String.format("%-30s : %d messages added\n",queuename,broker.getMessagesAdded(queuename)));
		}

        helper.print(sb.toString());

    }

    private void wisemqQueueBrowse(String queuename) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (queuename.equalsIgnoreCase("all")) {
            sb.append("browse all queue operation not allowed.\n");
		} else {
            CompositeData[] data = broker.getMessage(queuename);
            for (CompositeData d : data) {
                sb.append(d);
                sb.append("\n");
                sb.append("==========================\n");
            }
		}
        helper.print(sb.toString());
	}

    private void wisemqQueueFlush(String queuename) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (queuename.equalsIgnoreCase("all")) {
            sb.append("flush all queue operation not allowed.\n");
		} else {
            int numOfFlushedMsg =  broker.flushQueue(queuename);
            if(numOfFlushedMsg > 0){
                sb.append(String.format("%d message(s) have been removed\n", numOfFlushedMsg));
            }else {
                sb.append("Queue is empty!\n");
            }
		}
        helper.print(sb.toString());
	}

    private void wisemqQueueBlock(String queuename) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (queuename.equalsIgnoreCase("all")) {
            sb.append("block all queue operation not allowed.\n");
		} else {
            broker.pauseQueue(queuename);
            sb.append(String.format("Queue %s has been paused\n", queuename));
		}
        helper.print(sb.toString());
	}

    private void wisemqQueueUnblock(String queuename) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (queuename.equalsIgnoreCase("all")) {
            sb.append("unblock all queue operation not allowed.\n");
		} else {
            broker.resumeQueue(queuename);
            sb.append(String.format("Queue %s has been resumed\n", queuename));
		}
        helper.print(sb.toString());
	}

    private void wisemqQueueReset(String queuename) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (queuename.equalsIgnoreCase("all")) {
            sb.append("flush all queue operation not allowed.\n");
		} else {
            broker.resetStats(queuename);
            sb.append(String.format("Queue %s has been reset\n", queuename));
		}
        helper.print(sb.toString());
	}
    

    @ShellMethod(key = "wisemq nodes", value = "Show cluster status.")
    public void showWisemqNodes() {
        checkConnection();

        StringBuffer sb = new StringBuffer();
        if (broker.getNodes() != null && broker.getNodes().size() > 0) {
            sb.append("Availble Node\n");
            sb.append("==============================\n");
			for (String node : broker.getNodes()) {
                sb.append(node);
                sb.append("\n");
			}
		}

        helper.print(sb.toString());
    }

    @ShellMethod(key = "wisemq connect", value = "Connect to wisemq node.")
    public void connectWisemqNode(String wisemqnode) throws MalformedURLException, IOException {
        checkConnection();
        StringBuffer sb = new StringBuffer();
        if (wisemqnode == null) {
            sb.append("Missing argument\n");
		}else{
            boolean found = false;
            for (String n : broker.getNodes()) {
                if (n.equals(wisemqnode)) {
                    found = true;
                }
            }
            if (!found) {
                sb.append("Invalid node name [");
                sb.append(wisemqnode);
                sb.append("]\n");
            }else{
                broker.connectNode(wisemqnode);
                sb.append("Connected to node ");
                sb.append(wisemqnode);
                sb.append("\n");

                connectionNode = wisemqnode;
            }
        }

        helper.print(sb.toString());
    }

    @ShellMethod(key = "wisemq show", value = "Show wisemq detail.")
    public void showWisemq(
        @ShellOption(value = "cmd", defaultValue = "status", valueProvider = EnumValueProvider.class) ShowSubCmd showCmd
    ) throws Exception {
        checkConnection();
        switch (showCmd) {
			case status:
                wisemqShowStatus();
				break;
			case version:
                wisemqShowVersion();
				break;
			case client:
                wisemqShowClient();
				break;
			case queue:
                wisemqShowQueue();
				break;
			default:
                helper.print("Invalid show command");
				break;
		}
    }

    @ShellMethod(key = "wisemq kill", value = "kill <client_ip>  | kill wisemq client.")
    public void killWisemqClient(String clientIp) throws Exception {
        checkConnection();

        StringBuffer sb = new StringBuffer();
        
        if (clientIp == null) {
            sb.append("Invalid command");
			return;
		}

		if (broker.killClient(clientIp)){
            sb.append("Client has been removed");
		} else{
            sb.append("Fail to remove client");
		}

        helper.print(sb.toString());
    }

    @ShellMethod(key = "wisemq queue", value = "Show wisemq queue count.")
    public void showWisemqQueueCount(
        @ShellOption(value = "queueCmd", valueProvider = EnumValueProvider.class) QueueSubCmd queueCmd,
        String queuename
        ) throws Exception {
        checkConnection();

        boolean qExists = false;
		String[] queues =  broker.getQueueNames();
		if (queuename.equalsIgnoreCase("all")) {
            qExists = true;
		} else {
            for (String q : queues) {
				if (q.equals(queuename)) {
					qExists = true;
					break;
				}
			}
		}

		if (!qExists) {
            helper.print("Invalid Queue name");
			return;
		}

        switch (queueCmd) {
			case count:
                wisemqQueueCount(queuename);
				break;
			case browse:
                wisemqQueueBrowse(queuename);
				break;
			case flush:
                wisemqQueueFlush(queuename);
				break;
			case block:
                wisemqQueueBlock(queuename);
				break;
            case unblock:
                wisemqQueueUnblock(queuename);
				break;
			case reset:
                wisemqQueueReset(queuename);
				break;
			default:
                helper.print("Invalid show command");
				break;
		}

    }

    @ShellMethod(key = "wisemq help", value = "Print wisemq ommand help")
	public String quotaHelp() {
		StringBuffer sb = new StringBuffer();

		// @formatter:off
		sb.append("=============================================================\n");
		sb.append("wisemq command\n");
		sb.append("=============================================================\n");
        sb.append(String.format("%-25s %s\n", "connect <node>", "Connect to wisemq node."));
        sb.append(String.format("%-25s %s\n", "kill <client_ip> ", "kill wisemq client."));
        sb.append(String.format("%-25s %s\n", "nodes", "Show wisemq nodes."));
        sb.append(String.format("%-25s %s\n", "show status", "Show wisemq status. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "show version", "Show wisemq version. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "show client", "Show wisemq client. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "show queue", "Show wisemq queue. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue browse <cpname>", "browse wisemq queue content. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue count <all/cpname>", "Show wisemq queue count. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue flush <cpname>", "flush wisemq queue. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue block <cpname>", "block wisemq queue. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue unblock <cpname>", "unblock wisemq queue. Need to connect to a wisemq node."));
        sb.append(String.format("%-25s %s\n", "queue reset <cpname>", "reset wisemq queue. Need to connect to a wisemq node."));

		return sb.toString();
		// @formatter:on
	}
    
    public static void main(String[] args){
        String aaa = "$.artemis.internal.sf.msp-cluster.00bf3cbb-bd8b-11ed-bc71-3c4a92eaacd4";

        System.out.println(aaa);
        System.out.println(aaa.replaceAll("\\$.", ""));
    }
}
