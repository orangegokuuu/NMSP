package com.ws.fet.msp.management.command;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.ws.fet.msp.server.ClusterDaemon;

@ShellCommandGroup("MSP Cluster information")
@SshShellComponent
public class ClusterCmd {

    @Autowired
    private SshShellHelper helper;

    @Autowired
	private ClusterDaemon daemon;

    enum ClusterShowCmd {
		status, member
	}

    @ShellMethod(key = "cluster show", value = "Show cluster detail.")
    public void showCluster(
        @ShellOption(value = "cmd", defaultValue = "status", valueProvider = EnumValueProvider.class) ClusterShowCmd showCmd
    ) {
        StringBuffer sb = new StringBuffer();
        switch (showCmd) {
			case status:
                HazelcastInstance hz = daemon.getHz();
                Cluster cluster = (Cluster) hz.getCluster();
                Member local = cluster.getLocalMember();
                sb.append("==========================Status===================================\n");
                sb.append(String.format("Cluster Name             : %s\n", hz.getName()));
                sb.append(String.format("Cluster Status           : %s\n", cluster.getClusterState()));
                sb.append(String.format("Cluster Protocol Version : %s\n", cluster.getClusterVersion()));
                sb.append(String.format("Local Node Protocol      : %s\n", local.getVersion()));
                sb.append(String.format("Local Address            : %s\n", local.getAddress()));
                sb.append(String.format("Master Node              : %s\n", daemon.isMasterNode()));
				break;
			case member:
                List<String> members = daemon.getClusterMembers();
                sb.append("==========================Members===================================\n");
                for (String s : members) {
                    sb.append(s);
                }
				break;
			default:
                helper.print("Invalid show command");
				break;
		}

        helper.print(sb.toString());
    }


    @ShellMethod(key = "cluster help", value = "Print cluster ommand help")
	public String quotaHelp() {
		StringBuffer sb = new StringBuffer();

		// @formatter:off
		sb.append("=============================================================\n");
		sb.append("cluster command\n");
		sb.append("=============================================================\n");
		sb.append(String.format("%-15s %s\n", "show status", "Show cluster status."));
		sb.append(String.format("%-15s %s\n", "show ", "Show cluster member."));
		return sb.toString();
		// @formatter:on
	}
}
