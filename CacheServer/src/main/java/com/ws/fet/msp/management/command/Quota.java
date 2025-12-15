package com.ws.fet.msp.management.command;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.ws.msp.service.QuotaManager;

@ShellCommandGroup("MSP Quota Management")
@SshShellComponent
public class Quota {
    
	@Autowired
	private SshShellHelper helper;

	@Autowired
	QuotaManager quotaAgent;

	enum ResetSmsQuotaCmd {
		all, global
	}

	enum resetQueryQuotaCmd {
		all, hr, min
	}

	enum showQuotaCmd {
		sms, query
	}

	private StringBuffer printAllSmsQuota(Map<String, Long> map){
		StringBuffer sbout = new StringBuffer();
		sbout.append("ID                   Quota\n");
		for (var entry : map.entrySet()) {
			sbout.append(String.format("%-20s %-10d \n", entry.getKey(), entry.getValue()));
		}
		return sbout;
	}

	private StringBuffer printAllQueryQuota(Map<String, List<Long>> map){
		StringBuffer sbout = new StringBuffer();
		sbout.append("ID                   HourlyQuota          MinutelyQuota");
		for (var entry : map.entrySet()) {
			sbout.append(String.format("%-20s %-20d %-20d  \n", entry.getKey(), entry.getValue().get(0),entry.getValue().get(1)));
		}
		return sbout;
	}

	@ShellMethod(key = "quota resetAllQuota", value = "Reset All Quota.")
	public String resetAllQuota() {
		StringBuffer sb = new StringBuffer();

		quotaAgent.resetAll();
		// @formatter:off
		sb.append("=============================================================\n");
		sb.append("Reset All Quota.\n");
		sb.append("=============================================================\n");

		return sb.toString();
		// @formatter:on
	}

	@ShellMethod(key = "quota resetSmsQuota", value = "Reset All/Global Sms Quota.")
	public void resetAllSmsQuota(
        @ShellOption(value = "global/all", defaultValue = "all", valueProvider = EnumValueProvider.class) ResetSmsQuotaCmd cmd
	) {
		StringBuffer sb = new StringBuffer();

        switch (cmd) {
			case all:
				quotaAgent.resetSubmitLimit();
				sb.append("=============================================================\n");
				sb.append("Reset Global SMS Quota and All CP SMS Quota.\n");
				sb.append("=============================================================\n");
				break;
			case global:
				quotaAgent.resetGlobalSmsLimitCount();
				sb.append("=============================================================\n");
				sb.append("Reset Global SMS Quota.\n");
				sb.append("=============================================================\n");
				break;
			default:
                helper.print("Invalid show command");
				break;
		}
		helper.print(sb.toString());
		// @formatter:on
	}

	@ShellMethod(key = "quota resetSmsQuota cp", value = "Reset Cp Sms Quota.")
	public void resetAllSmsQuotaCp(String cpname) {
		StringBuffer sb = new StringBuffer();

		if(cpname != null){
			quotaAgent.resetCpSubmitLimitCount(cpname);
			sb.append("=============================================================\n");
			sb.append("Reset "+cpname+" SMS Quota.\n");
			sb.append("=============================================================\n");
		}else{
			sb.append("=============================================================\n");
			sb.append("Reset cp Sms Quota.Usage: quota resetSmsQuota cp <cpname>\n");
			sb.append("=============================================================\n");
		}

		helper.print(sb.toString());
		// @formatter:on
	}


	@ShellMethod(key = "quota resetQueryQuota", value = "Reset all/hr/min Query Dr Quota.")
	public void resetQueryQuota(
        @ShellOption(value = "all/hr/min", defaultValue = "all", valueProvider = EnumValueProvider.class) resetQueryQuotaCmd  cmd
	) {
		StringBuffer sb = new StringBuffer();

        switch (cmd) {
			case all:
				quotaAgent.resetQueryLimit();
				sb.append("=============================================================\n");
				sb.append("Reset All Query Dr Quota.\n");
				sb.append("=============================================================\n");
				break;
			case hr:
				quotaAgent.resetQueryDrHourlyCount();
				sb.append("=============================================================\n");
				sb.append("Reset Hourly Query Dr Quota.\n");
				sb.append("=============================================================\n");
				break;
			case min:
				quotaAgent.resetQueryDrMinutelyCount();
				sb.append("=============================================================\n");
				sb.append("Reset Minutely Query Dr Quota.\n");
				sb.append("=============================================================\n");
				break;	
			default:
                helper.print("Invalid show command");
				break;
		}
		helper.print(sb.toString());
	}

	@ShellMethod(key = "quota resetQueryQuota hr cp", value = "Reset CP Hourly Query Dr Quota. Usage: quota resetQueryQuota hr cp <cpname>")
	public String resetCPHrQueryQuota(String cpname) {
		StringBuffer sb = new StringBuffer();

		if(cpname != null){
			quotaAgent.resetQueryDrHourlyCount(cpname);

			// @formatter:off
			sb.append("=============================================================\n");
			sb.append("Reset "+cpname+" Hourly Query Dr Quota.\n");
			sb.append("=============================================================\n");
		}else{
			sb.append("=============================================================\n");
			sb.append("Reset CP Hourly Query Dr Quota. Usage: quota resetQueryQuota hr cp <cpname>\n");
			sb.append("=============================================================\n");
		}

		return sb.toString();
		// @formatter:on
	}

	@ShellMethod(key = "quota resetQueryQuota min cp", value = "Reset CP Hourly Query Dr Quota. Usage: quota resetQueryQuota min cp <cpname>")
	public String resetCPMinQueryQuota(String cpname) {
		StringBuffer sb = new StringBuffer();
		if(cpname != null){
			quotaAgent.resetQueryDrMinutelyCount(cpname);
			// @formatter:off
			sb.append("=============================================================\n");
			sb.append("Reset "+cpname+" Minutely Query Dr Quota.\n");
			sb.append("=============================================================\n");
		}else{
			sb.append("=============================================================\n");
			sb.append("Reset CP Hourly Query Dr Quota. Usage: quota resetQueryQuota min cp <cpname>\n");
			sb.append("=============================================================\n");
		}
		return sb.toString();
		// @formatter:on
	}

	@ShellMethod(key = "quota show Global", value = "Show  Global quota. Usage: quota show Global")
	public void showGlobalQuota() {
		StringBuffer sb = new StringBuffer();
		
		
		sb.append("=============================================================\n");
		sb.append("Global Quota : " + quotaAgent.getGlobalSmsLimitCount() + " \n");
		sb.append("=============================================================\n");
		helper.print(sb.toString());

		// helper.print("quota show");
	}

	@ShellMethod(key = "quota show", value = "Show sms quota. Usage: quota show sms < all | cpname >")
	public void showCpSmsQuota(
		@ShellOption(value = "sms/query", defaultValue = "all", valueProvider = EnumValueProvider.class) showQuotaCmd cmd,	
		String cpname
	) {
		StringBuffer sb = new StringBuffer();

		switch (cmd) {
			case sms:
				if(cpname != null){
					sb.append("=============================================================\n");
					if(cpname.equalsIgnoreCase("all")){
						sb.append(printAllSmsQuota(quotaAgent.getCpSubmitLimitCount()));
					}else{
						sb.append(cpname +" SMS Quota : " + quotaAgent.getCpSubmitLimitCount(cpname) + " \n");
					}
					sb.append("=============================================================\n");
				}else{
					sb.append("=============================================================\n");
					sb.append("Show sms quota. Usage: quota show sms < all | cpname >\n");
					sb.append("=============================================================\n");
				}
				break;
			case query:
				if(cpname != null){
					sb.append("=============================================================\n");
					if(cpname.equalsIgnoreCase("all")){
						sb.append(printAllQueryQuota(quotaAgent.getQueryDrCount()));
					}else{
						sb.append(cpname + " Hourly Query Dr Quota : " + quotaAgent.getQueryDrHourlyCount(cpname) + " \n");
						sb.append(cpname + " Minutely Query Dr Quota : " + quotaAgent.getQueryDrHourlyCount(cpname) + " \n");
					}
					sb.append("=============================================================\n");
				}else{
					sb.append("=============================================================\n");
					sb.append("Show sms quota. Usage: quota show query < all | cpname >\n");
					sb.append("=============================================================\n");
				}
				break;
			default:
                helper.print("Invalid show command");
				break;
		}
		helper.print(sb.toString());
	}

	@ShellMethod(key = "quota help", value = "Print quota command help")
	public String quotaHelp() {
		StringBuffer sb = new StringBuffer();

		// @formatter:off
		sb.append("=============================================================\n");
		sb.append("quota command\n");
		sb.append("=============================================================\n");
		sb.append(String.format("%-35s %s\n", "show Global", "Show  Global SMS quota."));
		sb.append(String.format("%-35s %s\n", "show query <all/cpname>", "Show query quota."));
		sb.append(String.format("%-35s %s\n", "show sms <all/cpname>", "Show sms quota."));
		sb.append(String.format("%-35s %s\n", "resetAllQuota", "Reset All Quota."));
        sb.append(String.format("%-35s %s\n", "resetSmsQuota all", "Reset Global SMS Quota and All CP SMS Quota."));
        sb.append(String.format("%-35s %s\n", "resetSmsQuota global", "Reset Global SMS Quota."));
        sb.append(String.format("%-35s %s\n", "resetSmsQuota cp <cpname>", "Reset CP SMS Quota."));
        sb.append(String.format("%-35s %s\n", "resetQueryQuota all", "Reset All Query Dr Quota."));
        sb.append(String.format("%-35s %s\n", "resetQueryQuota hr", "Reset Hourly Query Dr Quota."));
        sb.append(String.format("%-35s %s\n", "resetQueryQuota hr cp <cpname>", "Reset CP Hourly Query Dr Quota."));
        sb.append(String.format("%-35s %s\n", "resetQueryQuota min", "Reset Minutely Query Dr Quota."));
        sb.append(String.format("%-35s %s\n", "resetQueryQuota min cp <cpname>", "Reset CP Minutely Query Dr Quota."));
		return sb.toString();
		// @formatter:on
	}
}
