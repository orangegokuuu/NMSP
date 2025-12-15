package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.springframework.beans.factory.BeanFactory

import com.ws.msp.pojo.*
import com.ws.msp.service.*
import com.ws.util.StringUtil

@Usage("Quota Management")
class quota {
	public static final String ALL = "*";
	
	enum subCmd {
		global, sms, query 
	}
	
	enum SmsCmd {
		all, global, cp
	}

	enum QryCmd {
		all, hr, min
	}
	
	private void printAllSmsQuota(PrintWriter out, Map<String, Long> map){
		out.println("ID                   Quota");
		for (cp in map) {
			out.printf("%-20s %-10d \n", cp.getKey(), cp.getValue());
		}
	}
	
	private void printAllQueryQuota(PrintWriter out, Map<String, List<Long>> map){
		//hourly
		out.println("Hourly Quota");
		out.println("ID                   HourlyQuota          MinutelyQuota");
		for (cp in map) {
			out.printf("%-20s %-20d %-20d \n", cp.getKey(), cp.getValue().get(0), cp.getValue().get(1));
		}
	}
	
	@Usage("Show Quota")
	@Command
	public void show(InvocationContext context, @Usage("Type") @Argument subCmd cmd, @Usage("CPID") @Argument String cpId) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		QuotaManager agent = beanFactory.getBean(QuotaManager.class);

		if (cmd == null) {
			out.println("Invalid command, missing Type", Color.red);
			return;
		}
		
		if (subCmd.global.equals(cmd)){
			//print global
			out.printf("Global SMS Quota = %d \n", agent.getGlobalSmsLimitCount());
			return;
		}else {
			if (StringUtil.isEmpty(cpId)) {
				out.println("Invalid command", Color.red);
				return;
			}
		}
		
		if (subCmd.sms.equals(cmd)){
			//print sms
			if(ALL.equals(cpId)){
				// print all cp
				printAllSmsQuota(out, agent.getCpSubmitLimitCount());
			}else{
				// print cp
				out.printf("SMS Quota = %d \n", agent.getCpSubmitLimitCount(cpId));
			}
			return;
		}
		
		if (subCmd.query.equals(cmd)){
			//print query
			if(ALL.equals(cpId)){
				// print all cp
				printAllQueryQuota(out, agent.getQueryDrCount());
			}else{
				// print cp
				out.printf("Hourly Query Dr Quota = %d \n", agent.getQueryDrHourlyCount(cpId));
				out.printf("Minutely Query Dr Quota = %d \n", agent.getQueryDrMinutelyCount(cpId));
			}
			return;
		}
	}
		
	@Usage("Reset All Quota")
	@Command
	public void resetAllQuota(InvocationContext context) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		QuotaManager agent = beanFactory.getBean(QuotaManager.class);

		agent.resetAll();
		out.println("Reset All Quota");
	}
	
	@Usage("Reset Sms Quota")
	@Command
	public void resetSmsQuota(InvocationContext context, @Usage("Type") @Argument SmsCmd cmd, @Usage("CPID") @Argument String cpId) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		QuotaManager agent = beanFactory.getBean(QuotaManager.class);

		if (cmd == null) {
			out.println("Invalid command, missing Type", Color.red);
			return;
		}
		

		if (SmsCmd.all.equals(cmd)){
			agent.resetSubmitLimit()
			out.println("Reset Global SMS Quota and All CP SMS Quota");
			return;
		} else {
			if (StringUtil.isEmpty(cpId)) {
				out.println("Invalid command", Color.red);
				return;
			}
		}
		
		if (SmsCmd.global.equals(cmd)){
			agent.resetGlobalSmsLimitCount();
			out.println("Reset Global SMS Quota");
			return;
		} 
		
		if (SmsCmd.cp.equals(cmd)){
			if (ALL.equals(cpId)) {
				agent.resetCpSubmitLimitCount();
				out.println("Reset All CP SMS Quota");
				return;
			} else {
				agent.resetCpSubmitLimitCount(cpId);
				out.println("Reset CP SMS Quota");
				return;
			}
		}
	}
	
	@Usage("Reset Query Quota")
	@Command
	public void resetQueryQuota(InvocationContext context, @Usage("Type") @Argument QryCmd cmd, @Usage("CPID") @Argument String cpId) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		QuotaManager agent = beanFactory.getBean(QuotaManager.class);

		if (cmd == null) {
			out.println("Invalid command, missing Type", Color.red);
			return;
		}
		

		if (QryCmd.all.equals(cmd)){
			agent.resetQueryLimit()
			out.println("Reset All Query Dr Quota");
			return;
		} else {
			if (StringUtil.isEmpty(cpId)) {
				out.println("Invalid command", Color.red);
				return;
			}
		}
		
		if (QryCmd.hr.equals(cmd)){
			if (StringUtil.isEmpty(cpId)) {
				agent.resetQueryDrHourlyCount()
				out.println("Reset Hourly Query Dr Quota");
				return;
			} else {
				agent.resetQueryDrHourlyCount(cpId)
				out.println("Reset %s Hourly Query Dr Quota", cpId);
				return;
			}
		}
		
		if (QryCmd.min.equals(cmd)){
			if (StringUtil.isEmpty(cpId)) {
				agent.resetQueryDrMinutelyCount()
				out.println("Reset Minutely Query Dr Quota");
				return;
			} else {
				agent.resetQueryDrMinutelyCount(cpId)
				out.println("Reset %s Minutely Query Dr Quota", cpId);
				return;
			}
		}
	}
}