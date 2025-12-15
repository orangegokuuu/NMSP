package com.ws.fet.msp.usr.reporting;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@Component
public class ReportGenerator {
	private static Logger logger = LogManager.getLogger(ReportGenerator.class);

	@Value("${usr.access.log.path}")
	private String accessPath = null;

	@Value("${usr.activity.log.path}")
	private String activityPath = null;

	@Autowired
	private DataSource dataSource = null;

	public void fillReport(String rptName, String rptFormat, Map<String, Object> param, String reportFile)
			throws JRException, SQLException, ReportException {
		
		logger.debug("Loading Classpath Jasper[{}] ", rptName);
		URL jasperResURL = this.getClass().getResource("/jasper/" + rptName + ".jasper");
		JasperReport jasperReport = null;
		try {
			jasperReport = (JasperReport) JRLoader.loadObject(jasperResURL);
		} catch (JRException e) {
			e.printStackTrace();
		}

		JasperPrint jasperPrint = null;
		try {
			jasperPrint = JasperFillManager.fillReport(jasperReport, param, dataSource.getConnection());
		} catch (JRException | SQLException e) {
			e.printStackTrace();
		}

		JRCsvExporter exporter = new JRCsvExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(new File(reportFile)));
		SimpleCsvExporterConfiguration csvConfiguration = new SimpleCsvExporterConfiguration();
		exporter.setConfiguration(csvConfiguration);
		try {
			exporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	public String getDefaultPath(String rptType, Date date) {
		
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			logger.debug("Fail to get hostname");
		}
		
		String filePath = null;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat hf = new SimpleDateFormat("HH");
		String rptDate = df.format(date);
		String rptHr = hf.format(date);
		
		if (rptType.equals("access")) {
			filePath = accessPath+"MSP_"+hostName+"_ACCESS_"+rptDate+"_"+rptHr+".log";
		} else if (rptType.equals("activity")) {
			filePath = activityPath+"MSP_"+hostName+"_ACTIVITY_"+rptDate+"_"+rptHr+".log";
		}
		return filePath;
	}

}