package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory

import com.hazelcast.core.Cluster
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.Member
import com.ws.fet.msp.server.ClusterDaemon
import com.ws.msp.pojo.*
import com.ws.msp.service.*

@Usage("Show system information")
class cluster {
	enum ShowCmd {
		member, status
	}


	@Usage("Show Cluster Information")
	@Command
	public void show(InvocationContext context, @Argument ShowCmd cmd) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ClusterDaemon daemon = beanFactory.getBean(ClusterDaemon.class);
		switch (cmd) {
			case ShowCmd.status :
				HazelcastInstance hz = daemon.getHz();
				Cluster cluster = hz.getCluster();
				Member local = cluster.getLocalMember();
				
				out.printf("Cluster Name             : %s\n", hz.getName());
				out.printf("Cluster Status           : %s\n", cluster.getClusterState());
				out.printf("Cluster Protocol Version : %s\n", cluster.getClusterVersion());
				out.printf("Local Node Protocol      : %s\n", local.getVersion());
				out.printf("Local Address            : %s\n", local.getAddress());
				out.printf("Master Node              : %s\n", daemon.isMasterNode());
				break;
			case ShowCmd.member :
			default :
				HazelcastInstance hz = daemon.getHz();
				Cluster cluster = hz.getCluster();
				Member local = cluster.getLocalMember();
				out.println("Members")
				out.println("==============================")
				List<String> members = daemon.getClusterMembers();
				for (String s : members) {
					out.println(s);
				}
				break;
		}
	}
}