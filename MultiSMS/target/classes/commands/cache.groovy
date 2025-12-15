package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.crsh.text.Color
import org.springframework.beans.factory.BeanFactory

import com.ws.mnp.qp.pojo.MetaTableInfo
import com.ws.mnp.qp.pojo.MnpInfoA
import com.ws.mnp.qp.pojo.MnpInfoB
import com.ws.mnp.qp.pojo.SubscriberInfoA
import com.ws.mnp.qp.pojo.SubscriberInfoB
import com.ws.mnp.service.ReconciliationManager
import com.ws.util.StringUtil

@Usage("Cache server operation")
class cache {

	@Usage("Show active table")
	@Command
	public void metadata(InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ReconciliationManager reconManager = beanFactory.getBean(ReconciliationManager.class);
		List<MetaTableInfo> metadata = reconManager.listAll(MetaTableInfo.class);
		long mnpA = reconManager.rowCount(MnpInfoA.class);
		long mnpB = reconManager.rowCount(MnpInfoB.class);
		long subA = reconManager.rowCount(SubscriberInfoA.class);
		long subB = reconManager.rowCount(SubscriberInfoB.class);
		PrintWriter out = context.getWriter();
		for (MetaTableInfo meta : metadata) {
			out.printf("",meta, Color.red);
			out.println("==================================================");
			out.printf ("%-50s\n",meta.getKeyName());
			out.println("==================================================");
			out.print("Active Table       :");
			out.print(meta.getActiveTable(), Color.red);
			if (meta.getActiveTable().equals("MNP_INFO_A")) {
				out.print("("+mnpA+")", Color.red);
			} else if (meta.getActiveTable().equals("MNP_INFO_B")) {
				out.print("("+mnpB+")", Color.red);
			} else if (meta.getActiveTable().equals("SUBSCRIBER_INFO_A")) {
				out.print("("+subA+")", Color.red);
			} else if (meta.getActiveTable().equals("SUBSCRIBER_INFO_B")) {
				out.print("("+subB+")", Color.red);
			}
			out.println();
			out.print("Standby Table      :");
			out.print(meta.getStandByTable());
			if (meta.getStandByTable().equals("MNP_INFO_A")) {
				out.print("("+mnpA+")");
			} else if (meta.getStandByTable().equals("MNP_INFO_B")) {
				out.print("("+mnpB+")");
			} else if (meta.getStandByTable().equals("SUBSCRIBER_INFO_A")) {
				out.print("("+subA+")");
			} else if (meta.getStandByTable().equals("SUBSCRIBER_INFO_B")) {
				out.print("("+subB+")");
			}
			out.println();
			out.print("Active Cache       :");
			out.println(meta.getActiveMap(), Color.red);
			out.print("Standby Cache      :");
			out.println(meta.getStandByMap());
			out.print("Last Update        :");
			out.println(meta.getUpdateDate());
			out.println();
		}
	}

	@Usage("Compare active/standby MNP record")
	@Command
	public void mnp(InvocationContext context, @Usage("Subscriber id") @Argument String id) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ReconciliationManager reconManager = beanFactory.getBean(ReconciliationManager.class);
		MetaTableInfo mnpMeta = reconManager.get(MetaTableInfo.class, "MNP_INFO");
		if (StringUtil.isEmpty(id)) {
			out.println("Missing argument", Color.red);
		} else if (!id.matches("[9|6|5]\\d{7}")) {
			out.println("Invalid argument", Color.red);
		} else {
			if (mnpMeta == null) {
				out.println("Metadata not found", Color.red);
				return;
			}
			MnpInfoA infoA = reconManager.get(MnpInfoA.class, id);
			MnpInfoB infoB = reconManager.get(MnpInfoB.class, id);
			if (mnpMeta.getActiveTable().equals("MNP_INFO_A")) {
				out.println("==================================================");
				out.println("MNP_INFO_A (Active)                               ");
				out.println("==================================================");
				out.println(infoA, Color.red);
			} else {
				out.println("==================================================");
				out.println("MNP_INFO_A                                        ");
				out.println("==================================================");
				out.println(infoA);
			}
			if (mnpMeta.getActiveTable().equals("MNP_INFO_B")) {
				out.println("==================================================");
				out.println("MNP_INFO_B (Active)                               ");
				out.println("==================================================");
				out.println(infoB, Color.red);
			} else {
				out.println("==================================================");
				out.println("MNP_INFO_B                                        ");
				out.println("==================================================");
				out.println(infoB);
			}
		}
	}

	@Usage("Compare active/standby Subscriber record")
	@Command
	public void subscriber(InvocationContext context, @Usage("Subscriber id") @Argument String id) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ReconciliationManager reconManager = beanFactory.getBean(ReconciliationManager.class);
		MetaTableInfo subMeta = reconManager.get(MetaTableInfo.class, "SUBSCRIBER_INFO");

		if (StringUtil.isEmpty(id)) {
			out.println("Missing argument", Color.red);
		} else if (!id.matches("[9|6|5]\\d{7}")) {
			out.println("Invalid argument", Color.red);
		} else {
			if (subMeta == null) {
				out.println("Metadata not found", Color.red);
				return;
			}
			SubscriberInfoA infoA = reconManager.get(SubscriberInfoA.class, id);
			SubscriberInfoB infoB = reconManager.get(SubscriberInfoB.class, id);
			if (subMeta.getActiveTable().equals("SUBSCRIBER_INFO_A")) {
				out.println("==================================================");
				out.println("SUBSCRIBER_INFO_A (Active)                        ");
				out.println("==================================================");
				out.println(infoA, Color.red);
			} else {
				out.println("==================================================");
				out.println("SUBSCRIBER_INFO_A                                 ");
				out.println("==================================================");
				out.println(infoA);
			}
			if (subMeta.getActiveTable().equals("SUBSCRIBER_INFO_B")) {
				out.println("==================================================");
				out.println("SUBSCRIBER_INFO_B (Active)                        ");
				out.println("==================================================");
				out.println(infoB, Color.red);
			} else {
				out.println("==================================================");
				out.println("SUBSCRIBER_INFO_B                                 ");
				out.println("==================================================");
				out.println(infoB);
			}
		}
	}

	@Usage("Swap active table")
	@Command
	public void swap(InvocationContext context) {
		PrintWriter out = context.getWriter();
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		ReconciliationManager reconManager = beanFactory.getBean(ReconciliationManager.class);
		if (reconManager.swapCache()) {
			out.println("Committed");
		} else {
			out.println("Operation failed");
		}
	}
}