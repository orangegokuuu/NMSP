package commands

import java.util.concurrent.TimeUnit

import javax.sql.DataSource

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.jgroups.Address
import org.springframework.beans.factory.BeanFactory
import org.springframework.data.mongodb.core.MongoTemplate

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.mongodb.MongoClient
import com.ws.jmx.JmxClient
import com.ws.mnp.listener.EnumListener
import com.ws.mnp.listener.PcmppListener
import com.ws.mnp.server.MNPDaemon
import com.ws.mnp.worker.EnumProcessor

@Usage("Show system information")
class show {

	private String getDuration(long millis) {
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		String duration = String.format("%d days, %d hours, %d min, %d sec", days,hours,minutes,seconds);

		return duration;
	}

	@Usage("Show system version")
	@Command
	public void version(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		JmxClient jmxClient = beanFactory.getBean(JmxClient.class);
		context.writer.println("Product                        Version                        ")
		context.writer.println("============================== ===============================");
		Map versions = jmxClient.getVersion();
		for (e in versions) {
			context.writer.printf("%-30s %s\n", e.getKey(), e.getValue());
		}
	}

	@Usage("Show system service")
	@Command
	public void service(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		PcmppListener pcmpp = beanFactory.getBean(PcmppListener.class);
		EnumListener  enums = beanFactory.getBean(EnumListener.class);

		long millis = System.currentTimeMillis() - pcmpp.getUptime().getTime();
		String duration = getDuration(millis);

		context.writer.println("Service                        Status                         ");
		context.writer.println("============================== ===============================");
		context.writer.printf ("PCMPP Uptime                   %s\n",duration);
		context.writer.printf ("PCMPP Socket Listener          %s\n",pcmpp.isRunning());
		context.writer.printf ("PCMPP Connected Client         %d\n",pcmpp.getConnectedClient());
		context.writer.printf ("PCMPP Current Msg Rate         %s\n",pcmpp.getRatePerSecond());
		context.writer.printf ("PCMPP Average Msg Rate         %s\n",pcmpp.getAvgRate());

		millis = System.currentTimeMillis() - enums.getUptime().getTime();
		duration = getDuration(millis);

		context.writer.printf ("ENUM Uptime                    %s\n",duration);
		context.writer.printf ("ENUM Socket Listener           %s\n",enums.isRunning());
		context.writer.printf ("ENUM Current Msg Rate          %s\n",enums.getRatePerSecond());
		context.writer.printf ("ENUM Average Msg Rate          %s\n",enums.getAvgRate());
	}

	@Usage("Show database connection")
	@Command
	public void database(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ComboPooledDataSource ds = beanFactory.getBean(DataSource.class);
		context.writer.println("Properties                     Value                          ");
		context.writer.println("============================== ===============================");
		context.writer.printf ("Driver Class                   %s\n",ds.getDriverClass());
		context.writer.printf ("Connection URL                 %s\n",ds.getJdbcUrl());
		context.writer.printf ("Username                       %s\n",ds.getUser());
		context.writer.printf ("Min Pool Size                  %s\n",ds.getMinPoolSize());
		context.writer.printf ("Max Pool Size                  %s\n",ds.getMaxPoolSize());
		context.writer.printf ("Active Connection              %d\n",ds.getNumConnections());
	}

	@Usage("Show mongodb connection")
	@Command
	public void mongo(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		MongoTemplate template = beanFactory.getBean("mongoTemplate", MongoTemplate.class);
		MongoClient mongo = template.getDb().getMongo();

		context.writer.println("Properties                     Value                          ");
		context.writer.println("============================== ===============================");
		context.writer.printf ("DB Name                        %s\n", template.getDb().getName());
		context.writer.printf ("Read Preference                %s\n", template.getDb().getReadPreference().getName());
		context.writer.printf ("Read Preference(Allow Salve)   %s\n", template.getDb().getReadPreference().isSlaveOk());
		context.writer.printf ("Write Concern(Journaled)       %s\n", template.getDb().getWriteConcern().getJ());
		context.writer.printf ("Write Concern(Fsync)           %s\n", template.getDb().getWriteConcern().getFsync());
		context.writer.printf ("Write Concern(Write strategy)  %s\n", template.getDb().getWriteConcern().getWString());
		context.writer.printf ("Write Concern(Write timeout )  %dms\n", template.getDb().getWriteConcern().getWtimeout());
		int count = 0;
		for (s in mongo.getAllAddress()) {
			if (count == 0) {
				context.writer.printf ("Mongos Connection              %s\n", s);
			} else {
				context.writer.printf ("                               %s\n", s);
			}
			count++;
		}
	}

	@Usage("Show cluster information")
	@Command
	public void cluster(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		MNPDaemon server = beanFactory.getBean(MNPDaemon.class);

		List<Address> members = server.getClusterMembers();
		context.writer.printf ("Cluster : %s\n",server.getClusterName());
		context.writer.println("=============================================================");
		context.writer.println("Member(s)                                                    ");
		context.writer.println("=============================================================");
		for (s in server.getClusterMembers()) {
			context.writer.println(s);
		} 
	}
}