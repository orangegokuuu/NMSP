package com.ws.fet.msp.management.command;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import com.ws.msp.dao.CacheStatusDao;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.FetPrefix;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.msp.service.CacheAgent;
import com.ws.msp.service.CacheAgent.MspCache;

@ShellCommandGroup("MSP Cache Management")
@SshShellComponent
public class Cache {

	@Autowired
	private SshShellHelper helper;

	@Autowired
	private CacheAgent agent;

	@Autowired
	private CacheStatusDao cacheStatusDao;

	enum GetCmd {
		sysparam
	}

	enum ShowCmd {
		config, size, stats
	}


	private void printStats(String cache, LocalMapStats stats) {
		// @formatter:off
		helper.print("=============================================================");
		helper.print(cache);
		helper.print("=============================================================");
		helper.print(String.format("%-30s %s", "Owned Entry Count", stats.getOwnedEntryCount()));
		helper.print(String.format("%-30s %s", "Owned Entry Cost(Bytes)",stats.getOwnedEntryMemoryCost()));
		helper.print(String.format("%-30s %s", "Cache Hit Count", stats.getHits()));
		helper.print(String.format("%-30s %s", "Backup Copy", stats.getBackupCount()));
		helper.print(String.format("%-30s %s", "Backup Entry Count", stats.getBackupEntryCount()));
		helper.print(String.format("%-30s %s", "Backup Entry Cost(Bytes)",stats.getBackupEntryMemoryCost()));
		helper.print(String.format("%-30s %s", "Locked Entry Count", stats.getLockedEntryCount()));
		helper.print(String.format("%-30s %s", "Dirty Entry Count", stats.getDirtyEntryCount()));
		helper.print(String.format("%-30s %s", "Get Operation Count", stats.getGetOperationCount()));
		helper.print(String.format("%-30s %s", "Get Operation Time", stats.getTotalGetLatency()));
		helper.print(String.format("%-30s %s", "Max Get Operation Time", stats.getMaxGetLatency()));
		helper.print(String.format("%-30s %s", "Put Operation Count", stats.getPutOperationCount()));
		helper.print(String.format("%-30s %s", "Put Operation Time", stats.getTotalPutLatency()));
		helper.print(String.format("%-30s %s", "Max Put Operation Time", stats.getMaxPutLatency()));
		helper.print(String.format("%-30s %s", "Delete Operation Count", stats.getRemoveOperationCount()));
		helper.print(String.format("%-30s %s", "Delete Operation Time", stats.getTotalRemoveLatency()));
		helper.print(String.format("%-30s %s", "Max Delete Operation Time", stats.getMaxRemoveLatency()));
		helper.print(String.format("%-30s %s", "Create Date", new Date(stats.getCreationTime())));
		helper.print(String.format("%-30s %s", "Last Update Date", new Date(stats.getLastUpdateTime())));
		helper.print(String.format("%-30s %s", "Last Access Date", new Date(stats.getLastAccessTime())));
		// @formatter:on
	}

	private void printConfig(MapConfig cfg) {
		// @formatter:off
		helper.print("=============================================================");
		helper.print(cfg.getName());
		helper.print("=============================================================");
		helper.print(String.format("%-30s %s\n", "Backup Count", cfg.getBackupCount()));
		helper.print(String.format("%-30s %s\n", "Async Backup Count", cfg.getAsyncBackupCount()));
		helper.print(String.format("%-30s %s\n", "Time To Live (Seconds)", cfg.getTimeToLiveSeconds()));
		helper.print(String.format("%-30s %s\n", "Max Idle (Seconds)", cfg.getMaxIdleSeconds()));
		// helper.print(String.format("%-30s %s\n", "Eviction Policy", cfg.getEvictionPolicy()));
		// helper.print(String.format("%-30s %s\n", "Merge Policy", cfg.getMergePolicy()));
		// helper.print(String.format("%-30s\n", "Max Size Config :"));
		// helper.print(String.format("%-30s %s\n", "Policy", cfg.getMaxSizeConfig().getMaxSizePolicy()));
		// helper.print(String.format("%-30s %s\n", "Size ", cfg.getMaxSizeConfig().getSize()));
		// @formatter:on
	}

	@ShellMethod(key = "cache help", value = "Print command help")
	public String cacheHelp() {
		StringBuffer sb = new StringBuffer();

		// @formatter:off
		sb.append("=============================================================\n");
		sb.append("cache command\n");
		sb.append("=============================================================\n");
		sb.append(String.format("%-10s %s\n", "show", "Show Cache stats. Usage: cache show <stats|config|size> <all|blacklist|mnp|cp|prefix|spam>"));
		sb.append(String.format("%-10s %s\n", "clear", "Clear Cache. Usage: cache clear <all|blacklist|mnp|cp|prefix|spam>"));
		sb.append(String.format("%-10s %s\n", "load", "Load Cache data. Usage: cache load <all|blacklist|mnp|cp|prefix|spam>"));
		sb.append(String.format("%-10s %s\n", "lookup", "Lookup Cache Entry. Usage: cache lookup <blacklist|mnp|cp|prefix|spam>"));
		sb.append(String.format("%-10s %s\n", "status", "Show Cache status."));
		sb.append(String.format("%-10s %s\n", "help", "Show this help"));

		return sb.toString();
		// @formatter:on
	}

	@ShellMethod(key = "cache show", value = "Show Cache stats. Usage: cache show <stats|config|size>")
	public String show(@ShellOption(value = "oper", valueProvider = EnumValueProvider.class) ShowCmd cmd,
			@ShellOption(value = "type", defaultValue = "all", valueProvider = EnumValueProvider.class) MspCache name) {
		StringBuffer sb = new StringBuffer();
		String mkey = null;
		switch (name) {
			case blacklist:
				mkey = BlackList.class.getName();
				break;
			case mnp:
				mkey = MnpApiPhoneroutinginfo.class.getName();
				break;
			case cp:
				mkey = ContentProvider.class.getName();
				break;
			case prefix:
				mkey = FetPrefix.class.getName();
				break;
			case spam:
				mkey = SpamKeyWord.class.getName();
				break;
			default:
				break;
		}

		switch (cmd) {
			case stats:
				if (name == null || name == MspCache.all) {
					List<IMap> caches = agent.getCache();
					for (IMap m : caches) {
						printStats(m.getName(), m.getLocalMapStats());
					}
				} else {
					IMap cache = agent.getCache(mkey);
					LocalMapStats stats = cache.getLocalMapStats();
					printStats(mkey, stats);
				}
				break;
			case config:
				if (name == null || name == MspCache.all) {
					Map<String, MapConfig> cfg = agent.getConfigs();
					for (String cache : cfg.keySet()) {
						printConfig(cfg.get(cache));
					}
				} else {
					sb.append(agent.getConfig(mkey) == null ? "null"
							: agent.getConfig(mkey).toString());
				}
				break;
			case size:

				if (name == null || name == MspCache.all) {
					List<IMap> caches = agent.getCache();
					// @formatter:off
					sb.append("Cache                                              Size       \n");
					sb.append("================================================== ===========\n");

					for (IMap m : caches) {
						sb.append(String.format("%-50s %d\n", m.getName(), m.size()));
					}
					// @formatter:on
				} else {
					// @formatter:off
					IMap cache = agent.getCache(mkey);
					sb.append("Cache                                              Size       \n");
					sb.append("================================================== ===========\n");
					sb.append(String.format("%-50s %d\n", cache.getName(), cache.size()));
					// @formatter:on
				}
				break;
			default:
				sb.append("Missing argument\n");
				break;
		}

		return sb.toString();
	}

	@ShellMethod(key = "cache load", value = "Load Cache stats. Usage: cache load <all|blacklist|mnp|cp|prefix|spam>")
	public void cacheLoad(
			@ShellOption(value = "type", defaultValue = "all", valueProvider = EnumValueProvider.class) MspCache name) {
		if (name == null) {
			helper.print("Invalid command, missing Cache Object");
			return;
		}

		agent.load(name);
		helper.print("Executed");
	}

	@ShellMethod(key = "cache status", value = "Show Cache stats.")
	public void cacheStatus() {
		StringBuffer sb = new StringBuffer();
		Map<String,Boolean> status =cacheStatusDao.getStatus();
		sb.append("Cache                                              Ready?\n");
		sb.append("================================================== ======\n");
		for (String key : status.keySet()) {
			sb.append(String.format("%-50s %s\n", key, status.get(key)));
		}
		helper.print(sb.toString());
	}

	@ShellMethod(key = "cache clear", value = "Clear <name> cache .")
	public void cacheClear(@ShellOption(value = "type", defaultValue = "all", valueProvider = EnumValueProvider.class) MspCache name) {
		StringBuffer sb = new StringBuffer();

		String mkey = null;
		switch (name) {
			case blacklist:
				mkey = BlackList.class.getName();
				break;
			case mnp:
				mkey = MnpApiPhoneroutinginfo.class.getName();
				break;
			case cp:
				mkey = ContentProvider.class.getName();
				break;
			case prefix:
				mkey = FetPrefix.class.getName();
				break;
			case spam:
				mkey = SpamKeyWord.class.getName();
				break;
			default:
				break;
		}
		
		agent.clear(mkey);

		sb.append(String.format("Clear %s cache\n", mkey));
		helper.print(sb.toString());
	}

	@ShellMethod(key = "cache lookup", value = "Lookup Cache Entry")
	public void cacheLookup(@ShellOption(value = "type", defaultValue = "all", valueProvider = EnumValueProvider.class) MspCache name) {
		StringBuffer sb = new StringBuffer();

		String mkey = null;
		switch (name) {
			case blacklist:
				mkey = BlackList.class.getName();
				break;
			case mnp:
				mkey = MnpApiPhoneroutinginfo.class.getName();
				break;
			case cp:
				mkey = ContentProvider.class.getName();
				break;
			case prefix:
				mkey = FetPrefix.class.getName();
				break;
			case spam:
				mkey = SpamKeyWord.class.getName();
				break;
			default:
				break;
		}
		
		if (mkey == null) {
			sb.append(String.format("Cache not found"));
		} else {
			sb.append(mkey);
			sb.append("\n");
		}
		
		helper.print(sb.toString());
	}

	/*
	 * @Usage("Load Cache Entry")
	 * 
	 * @Command public void load(InvocationContext
	 * context, @Usage("Cache object") @Argument
	 * MspCache cache) { PrintWriter out = context.getWriter(); BeanFactory
	 * beanFactory =
	 * (BeanFactory) context.getAttributes().get("spring.beanfactory"); CacheAgent
	 * agent =
	 * beanFactory.getBean(CacheAgent.class);
	 * 
	 * if (cache == null) { helper.print("Invalid command, missing Cache Object");
	 * return; }
	 * 
	 * agent.load(cache); helper.print("Executed"); }
	 * 
	 * @Usage("Lookup Cache Entry")
	 * 
	 * @Command public void lookup(InvocationContext
	 * context, @Usage("Cache object") @Argument
	 * MspCache cache, @Argument String id) { PrintWriter out = context.getWriter();
	 * BeanFactory
	 * beanFactory = (BeanFactory)
	 * context.getAttributes().get("spring.beanfactory"); CacheAgent
	 * agent = beanFactory.getBean(CacheAgent.class);
	 * 
	 * List<IMap> caches = agent.getCache(); String mkey = null; switch (cache) {
	 * case MspCache.all
	 * : helper.print("Operation not support"); break; case MspCache.blacklist :
	 * mkey =
	 * BlackList.class.getName(); break; case MspCache.mnp : mkey =
	 * MnpApiPhoneroutinginfo.class.getName(); break; case MspCache.cp : mkey =
	 * ContentProvider.class.getName(); break; case MspCache.prefix : mkey =
	 * FetPrefix.class.getName(); break; case MspCache.spam : mkey =
	 * SpamKeyWord.class.getName();
	 * break; default: helper.print("Operation not support"); break; }
	 * 
	 * IMap cacheMap = null; for (IMap map : caches) { if
	 * (map.getName().equals(mkey)) { cacheMap =
	 * map; } }
	 * 
	 * if (cacheMap == null) { String.format("Cache for [%s] not found",mkey); }
	 * else { Object o =
	 * cacheMap.get(id); helper.print(o == null?"null":o); } }
	 * 
	 * @Usage("Clear Cache Entry")
	 * 
	 * @Command public void clear(InvocationContext
	 * context, @Usage("Cache object") @Argument String
	 * name) { PrintWriter out = context.getWriter(); BeanFactory beanFactory =
	 * (BeanFactory)
	 * context.getAttributes().get("spring.beanfactory"); CacheAgent agent =
	 * beanFactory.getBean(CacheAgent.class);
	 * 
	 * agent.clear(name); helper.print("Executed"); }
	 * 
	 * @Usage("Test cache performance")
	 * 
	 * @Command public void test(InvocationContext context, @Usage("CPID") @Argument
	 * String cpID) {
	 * PrintWriter out = context.getWriter(); BeanFactory beanFactory =
	 * (BeanFactory)
	 * context.getAttributes().get("spring.beanfactory"); ContentProviderManager
	 * repo =
	 * beanFactory.getBean(ContentProviderManager.class); ContentProvider cp = null;
	 * StopWatch timer
	 * = new StopWatch();
	 * 
	 * timer.start(); cp = repo.get(ContentProvider.class, cpID); timer.stop();
	 * String.format("First Attempt elapsed time[%-10d]ms CP = [%s]\n",
	 * timer.getTime(TimeUnit.MILLISECONDS), cp.getCpId()); timer.reset();
	 * 
	 * timer.start(); for (int i=0; i<10; i++) { cp =
	 * repo.get(ContentProvider.class, cpID); }
	 * timer.stop();
	 * 
	 * String.format("Ten   Attempt elapsed time[%-10d]ms CP = [%s]\n",
	 * timer.getTime(TimeUnit.MILLISECONDS), cp.getCpId()); }
	 * 
	 * @Usage("Get System param")
	 * 
	 * @Command public void get(InvocationContext context, @Usage("Type") @Argument
	 * GetCmd
	 * cmd, @Usage("ID") @Argument String id) { PrintWriter out =
	 * context.getWriter(); BeanFactory
	 * beanFactory = (BeanFactory)
	 * context.getAttributes().get("spring.beanfactory"); CacheAgent
	 * agent = beanFactory.getBean(CacheAgent.class);
	 * 
	 * if (cmd == null) { helper.print("Invalid command, missing Type", Color.red);
	 * return; }
	 * 
	 * if (StringUtil.isEmpty(id)) { helper.print("Invalid command, missing ID",
	 * Color.red); return;
	 * }
	 * 
	 * helper.print(agent.getSystemParam(id)); }
	 * 
	 * @Usage("Show Cache Status")
	 * 
	 * @Command public void status(InvocationContext context) { PrintWriter out =
	 * context.getWriter(); BeanFactory beanFactory = (BeanFactory)
	 * context.getAttributes().get("spring.beanfactory"); CacheStatusDao agent =
	 * beanFactory.getBean(CacheStatusDao.class);
	 * 
	 * Map<String,Boolean> status = agent.getStatus();
	 * helper.print("Cache                                              Ready?");
	 * helper.print("================================================== ======");
	 * for (String key :
	 * status.keySet()) { String.format("%-50s %s\n", key, status.get(key)); } }
	 */
}
