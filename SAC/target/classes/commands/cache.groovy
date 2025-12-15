package commands

import java.util.concurrent.TimeUnit

import org.apache.commons.lang3.time.StopWatch
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.springframework.beans.factory.BeanFactory

import com.hazelcast.core.IMap
import com.ws.msp.pojo.*
import com.ws.msp.service.*
import com.ws.msp.service.CacheAgent.MspCache
import com.ws.util.StringUtil

@Usage("Show system information")
class cache {
	enum LoadSubCmd {
		all, blacklist, cp , mnp, spam
	}

	enum GetCmd {
		sysparam
	}

	@Usage("Show Cache Entry")
	@Command
	public void show(InvocationContext context) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);

		List<IMap> caches = agent.getCache();
		for (IMap m : caches) {
			out.printf("Cache Name[%-40s] Size[%d]\n",m.getName(),m.size());
		}
	}

	@Usage("Load Cache Entry")
	@Command
	public void load(InvocationContext context, @Usage("Cache object") @Argument MspCache cache) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);

		if (cache == null) {
			out.println("Invalid command, missing Cache Object", Color.red);
			return;
		}

		agent.load(cache);
		out.println("Executed");
	}

	@Usage("Clear Cache Entry")
	@Command
	public void clear(InvocationContext context, @Usage("Cache object") @Argument String name) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);

		agent.clear(name);
		out.println("Executed");
	}

	@Usage("Test cache performance")
	@Command
	public void test(InvocationContext context, @Usage("CPID") @Argument String cpID) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ContentProviderManager repo = beanFactory.getBean(ContentProviderManager.class);
		ContentProvider cp = null;
		StopWatch timer = new StopWatch();

		timer.start();
		cp = repo.get(ContentProvider.class, cpID);
		timer.stop();
		out.printf("First Attempt elapsed time[%-10d]ms CP = [%s]\n", timer.getTime(TimeUnit.MILLISECONDS), cp.getCpId());
		timer.reset();

		timer.start();
		for (int i=0; i<10; i++) {
			cp = repo.get(ContentProvider.class, cpID);
		}
		timer.stop();

		out.printf("Ten   Attempt elapsed time[%-10d]ms CP = [%s]\n", timer.getTime(TimeUnit.MILLISECONDS), cp.getCpId());
	}

	@Usage("Get System param")
	@Command
	public void get(InvocationContext context, @Usage("Type") @Argument GetCmd cmd, @Usage("ID") @Argument String id) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);
		
		if (cmd == null) {
			out.println("Invalid command, missing Type", Color.red);
			return;
		}

		if (StringUtil.isEmpty(id)) {
			out.println("Invalid command, missing ID", Color.red);
			return;
		}
		
		out.println(agent.getSystemParam(id));
	}
}