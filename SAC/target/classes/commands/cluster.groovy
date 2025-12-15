package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory

import com.hazelcast.core.HazelcastInstance

@Usage("Show system information")
class cluster {
	enum ShowCmd {
		connection, status
	}


	@Usage("Show Cluster Information")
	@Command
	public void show(InvocationContext context, @Argument ShowCmd cmd) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		HazelcastInstance hz = beanFactory.getBean(HazelcastInstance.class);
		switch (cmd) {
			case ShowCmd.connection :
				out.println(hz.config);
			default :
				//hz.getClientService();
				break;
		}
	}
}