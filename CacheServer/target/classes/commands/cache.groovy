package commands

import java.util.concurrent.TimeUnit

import org.apache.commons.lang3.time.StopWatch
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.springframework.beans.factory.BeanFactory

import com.hazelcast.config.MapConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.monitor.LocalMapStats
import com.ws.msp.dao.CacheStatusDao
import com.ws.msp.pojo.*
import com.ws.msp.service.*
import com.ws.msp.service.CacheAgent.MspCache
import com.ws.util.StringUtil

@Usage("Show system information")
class cache {
	enum GetCmd {
		sysparam
	}

	enum ShowCmd {
		config, size, stats
	}

	private void printStats(PrintWriter out, String cache, LocalMapStats stats) {
		out.println("=============================================================");
		out.println(cache);
		out.println("=============================================================");
		out.printf("%-30s %s\n","Owned Entry Count", stats.getOwnedEntryCount());
		out.printf("%-30s %s\n","Owned Entry Cost(Bytes)", stats.getOwnedEntryMemoryCost());
		out.printf("%-30s %s\n","Cache Hit Count", stats.getHits());
		out.printf("%-30s %s\n","Backup Copy", stats.getBackupCount());
		out.printf("%-30s %s\n","Backup Entry Count", stats.getBackupEntryCount());
		out.printf("%-30s %s\n","Backup Entry Cost(Bytes)", stats.getBackupEntryMemoryCost());
		out.printf("%-30s %s\n","Locked Entry Count", stats.getLockedEntryCount());
		out.printf("%-30s %s\n","Dirty Entry Count", stats.getDirtyEntryCount());
		out.printf("%-30s %s\n","Get Operation Count", stats.getGetOperationCount());
		out.printf("%-30s %s\n","Get Operation Time", stats.getTotalGetLatency());
		out.printf("%-30s %s\n","Max Get Operation Time", stats.getMaxGetLatency());
		out.printf("%-30s %s\n","Put Operation Count", stats.getPutOperationCount());
		out.printf("%-30s %s\n","Put Operation Time", stats.getTotalPutLatency());
		out.printf("%-30s %s\n","Max Put Operation Time", stats.getMaxPutLatency());
		out.printf("%-30s %s\n","Delete Operation Count", stats.getRemoveOperationCount());
		out.printf("%-30s %s\n","Delete Operation Time", stats.getTotalRemoveLatency());
		out.printf("%-30s %s\n","Max Delete Operation Time", stats.getMaxRemoveLatency());
		out.printf("%-30s %s\n","Create Date", new Date(stats.getCreationTime()));
		out.printf("%-30s %s\n","Last Update Date", new Date(stats.getLastUpdateTime()));
		out.printf("%-30s %s\n","Last Access Date", new Date(stats.getLastAccessTime()));
	}

	private void printConfig(PrintWriter out, MapConfig cfg) {
		out.println("=============================================================");
		out.println(cfg.getName());
		out.println("=============================================================");
		out.printf("%-30s %s\n","Backup Count", cfg.getBackupCount());
		out.printf("%-30s %s\n","Async Backup Count", cfg.getAsyncBackupCount());
		out.printf("%-30s %s\n","Time To Live (Seconds)", cfg.getTimeToLiveSeconds());
		out.printf("%-30s %s\n","Max Idle (Seconds)", cfg.getMaxIdleSeconds());
		out.printf("%-30s %s\n","Eviction Policy", cfg.getEvictionPolicy());
		out.printf("%-30s %s\n","Merge Policy", cfg.getMergePolicy());
		out.printf("%-30s\n","Max Size Config :");
		out.printf("%-30s %s\n","Policy", cfg.getMaxSizeConfig().getMaxSizePolicy());
		out.printf("%-30s %s\n","Size ", cfg.getMaxSizeConfig().getSize());
	}
	@Usage("Show Cache Entry")
	@Command
	public void show(InvocationContext context, @Usage("Information type") @Argument ShowCmd cmd, @Usage("Cache name") @Argument MspCache name) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);
		HazelcastInstance hz = beanFactory.getBean(HazelcastInstance.class);

		String mkey = null;
		switch (name) {
			case MspCache.blacklist :
				mkey = BlackList.class.getName();
				break;
			case MspCache.mnp :
				mkey = MnpApiPhoneroutinginfo.class.getName();
				break;
			case MspCache.cp :
				mkey = ContentProvider.class.getName();
				break;
			case MspCache.prefix :
				mkey = FetPrefix.class.getName();
				break;
			case MspCache.spam :
				mkey = SpamKeyWord.class.getName();
				break;
			default:
				break;
		}

		switch (cmd) {
			case ShowCmd.stats :
				if (name == null || name == MspCache.all) {
					List<IMap> caches = agent.getCache();
					for (IMap m : caches) {
						printStats(out, m.getName(), m.getLocalMapStats());
					}
				} else {
					IMap cache = agent.getCache(mkey);
					LocalMapStats stats = cache.getLocalMapStats();
					printStats(out, mkey, stats);
				}
				break;
			case ShowCmd.config :
				if (name == null || name == MspCache.all) {
					Map<String,MapConfig> cfg = agent.getConfigs();
					for (String cache : cfg.keySet()) {
						printConfig(out, cfg.get(cache));
					}
				} else {
					out.println(agent.getConfig(mkey));
				}
				break;
			case ShowCmd.size :
				if (name == null || name == MspCache.all) {
					List<IMap> caches = agent.getCache();
					out.println("Cache                                              Size       ");
					out.println("================================================== ===========");

					for (IMap m : caches) {
						out.printf("%-50s %d\n",m.getName(),m.size());
					}
				} else {
					IMap cache = agent.getCache(mkey);
					out.println("Cache                                              Size       ");
					out.println("================================================== ===========");
					out.printf("%-50s %d\n",cache.getName(),cache.size());
				}
				break;
			default :
				out.println("Missing argument Information Type");
				break;
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

	@Usage("Lookup Cache Entry")
	@Command
	public void lookup(InvocationContext context, @Usage("Cache object") @Argument MspCache cache, @Argument String id) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheAgent agent = beanFactory.getBean(CacheAgent.class);

		List<IMap> caches = agent.getCache();
		String mkey = null;
		switch (cache) {
			case MspCache.all :
				out.println("Operation not support");
				break;
			case MspCache.blacklist :
				mkey = BlackList.class.getName();
				break;
			case MspCache.mnp :
				mkey = MnpApiPhoneroutinginfo.class.getName();
				break;
			case MspCache.cp :
				mkey = ContentProvider.class.getName();
				break;
			case MspCache.prefix :
				mkey = FetPrefix.class.getName();
				break;
			case MspCache.spam :
				mkey = SpamKeyWord.class.getName();
				break;
			default:
				out.println("Operation not support");
				break;
		}

		IMap cacheMap = null;
		for (IMap map : caches) {
			if (map.getName().equals(mkey)) {
				cacheMap = map;
			}
		}

		if (cacheMap == null) {
			out.printf("Cache for [%s] not found",mkey);
		} else {
			Object o = cacheMap.get(id);
			out.println(o == null?"null":o);
		}
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

	@Usage("Show Cache Status")
	@Command
	public void status(InvocationContext context) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		CacheStatusDao agent = beanFactory.getBean(CacheStatusDao.class);

		Map<String,Boolean> status = agent.getStatus();
		out.println("Cache                                              Ready?");
		out.println("================================================== ======");
		for (String key : status.keySet()) {
			out.printf("%-50s %s\n", key, status.get(key));
		}
	}
}