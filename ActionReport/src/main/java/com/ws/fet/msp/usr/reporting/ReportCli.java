package com.ws.fet.msp.usr.reporting;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ws.fet.msp.usr.reporting.spring.ReportingConfig;
import com.ws.util.DateUtil;
import com.ws.util.StringUtil;

import argparser.ArgParser;
import argparser.StringHolder;
import net.sf.jasperreports.engine.JRException;

public class ReportCli {

	public static void main(String[] args) throws JRException, SQLException, ReportException {
		final String dateTimeFormat = "yyyyMMddHHmm";

		ArgParser parser = new ArgParser("java " + ReportCli.class);
		StringHolder reportType = new StringHolder();
		StringHolder format = new StringHolder("csv");
		StringHolder date = new StringHolder();
		StringHolder output = new StringHolder();
		Date targetDate = null;

		parser.addOption("-type             %s  #Report Type", reportType);
		parser.addOption("-date             %s  #Report Date", date);
		parser.addOption("-output			%s  #Output File", output);

		parser.matchAllArgs(args);
		if (StringUtil.isEmpty(reportType.value)) {
			parser.printErrorAndExit("Please enter the report type");
		}
		if (!reportType.value.equals("access") && !reportType.value.equals("activity")) {
			parser.printErrorAndExit("Report type should be 'access' or 'activity', current = " + reportType.value);
		}
		if (StringUtil.isEmpty(date.value)) {
			date.value = DateUtil.getDateTime(dateTimeFormat, DateUtil.lastHour(new Date(), 1));
		}
		DateFormat df = new SimpleDateFormat(dateTimeFormat);
		try {
			targetDate = df.parse(date.value);
		} catch (ParseException e) {
			parser.printErrorAndExit("Fail to parse date[" + date.value + "]. Format should be "+ dateTimeFormat);
		}

		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(ReportingConfig.class);
		ReportGenerator reportGenerator = ctx.getBean(ReportGenerator.class);

		if (StringUtil.isEmpty(output.value)) {	
			output.value = reportGenerator.getDefaultPath(reportType.value, targetDate);
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("targetDate", date.value);

		reportGenerator.fillReport(reportType.value, format.value, param, output.value);
		ctx.close();
	}
}