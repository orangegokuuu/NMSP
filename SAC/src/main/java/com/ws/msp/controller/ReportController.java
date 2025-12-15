package com.ws.msp.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.util.DateUtil;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Log4j2
@Controller
@RequestMapping("/sms/report")
public class ReportController {

	@Autowired
	private DataSource dataSource;

	// @Permission(id = "SERVICE_06", level = PrivilegeLevel.READ)
	// // @LogAction(type = SacConstant.EXP_RPT, message = "Generate Report[{5}]")
	// // @LogEvent(type = SacConstant.EXP_RPT, message = "Generate Report[{5}]")
	// @RequestMapping(value =
	// "/getReport/{sysId}/{sourceId}/{reportType}/{datePeriod}/{rptFormat}/{start}/{end}",
	// method = RequestMethod.GET)
	// public ModelAndView getReport(ModelMap modelMap, ModelAndView modelAndView,
	// @PathVariable(value="sysId") String sysId,
	// @PathVariable(value="sourceId") String sourceId,
	// @PathVariable(value="reportType") String reportType,
	// @PathVariable(value="datePeriod") String datePeriod,
	// @PathVariable(value="rptFormat") String rptFormat,
	// @PathVariable(value="start") @DateTimeFormat(pattern="yyyy-MM-dd") Date
	// start,
	// @PathVariable(value="end") @DateTimeFormat(pattern="yyyy-MM-dd") Date end) {

	// String reportTitle = null;
	// String period = null;
	// LocalDateTime startLocalDateTime = null;

	// startLocalDateTime =
	// start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

	// // bound date range into 1 month when datePeriod not equal to "Month"
	// if(!datePeriod.equals("M") && end.after(DateUtils.addMonths(start, 1))){
	// end = DateUtils.addMonths(start, 1);
	// }

	// String selectedCriteria = "1 SysId and 1 SourceID";
	// String selectionType = "121"; // default, 1 sysId and 1 source Id
	// if (sysId.equals("ALL")){ // All sysId and All source Id
	// selectionType = "A2N";
	// selectedCriteria = "All SysId and All SourceID";
	// }else if(sourceId.equals("ALL")){ // 1 sysId and All source Id
	// selectionType = "12A";
	// selectedCriteria = "1 SysId and All SourceID";
	// }

	// switch (datePeriod){
	// case "H":
	// period = "Houryly";
	// // start = DateUtils.truncate(start, Calendar.DATE);
	// start =
	// java.sql.Timestamp.valueOf(startLocalDateTime.truncatedTo(ChronoUnit.DAYS));
	// end = DateUtil.dayEnd(end);
	// break;
	// case "M":
	// period = "Monthy";
	// start = DateUtil.monthStart(start);
	// end = DateUtil.monthEnd(end);
	// break;
	// default:
	// period = "Daily";
	// // start = DateUtils.truncate(start, Calendar.DATE);
	// start =
	// java.sql.Timestamp.valueOf(startLocalDateTime.truncatedTo(ChronoUnit.DAYS));
	// end = DateUtil.dayEnd(end);
	// break;
	// }

	// switch (reportType) {
	// case "clientMtSummary":
	// reportTitle = "Client Traffic MT-SMS "+ period +" Summary";
	// break;
	// case "clientMtDetail":
	// reportTitle = "Client Traffic MT-SMS "+ period +" Detail";
	// if(sysId.toUpperCase().equals("ALL")){
	// reportType = reportType + "_all";
	// }
	// break;
	// case "clientMtSmsFailure":
	// reportTitle = "Client Traffic MT-SMS Failure Analysis "+ period +" Summary";
	// break;
	// case "clientMoSummary":
	// reportTitle = "Client Traffic MO-SMS "+ period +" Summary";
	// break;
	// case "clientDrSummary":
	// reportTitle = "Client Traffic DR "+ period +" Summary";
	// break;
	// default:
	// log.debug("Invaild reportType[{}]", reportType);
	// break;
	// }

	// if(!datePeriod.equals("D")){
	// reportType = reportType + datePeriod;
	// }

	// modelMap.put("datasource", dataSource);
	// modelMap.put("sysId", sysId);
	// modelMap.put("sourceId", sourceId);
	// modelMap.put("selectionType", selectionType);
	// modelMap.put("selectedCriteria", selectedCriteria);
	// modelMap.put("reportType", reportTitle);
	// modelMap.put("format", rptFormat);
	// modelMap.put("sDate", start);
	// modelMap.put("eDate", end);

	// return new ModelAndView(reportType, modelMap);
	// }

	@Permission(id = "SERVICE_06", level = PrivilegeLevel.READ)
	// @LogAction(type = SacConstant.EXP_RPT, message = "Generate Report[{5}]")
	// @LogEvent(type = SacConstant.EXP_RPT, message = "Generate Report[{5}]")
	@RequestMapping(value = "/getReport/{sysId}/{sourceId}/{reportType}/{datePeriod}/{rptFormat}/{start}/{end}", method = RequestMethod.GET)
	public void getReport(HttpServletResponse response,
			@PathVariable(value = "sysId") String sysId,
			@PathVariable(value = "sourceId") String sourceId,
			@PathVariable(value = "reportType") String reportType,
			@PathVariable(value = "datePeriod") String datePeriod,
			@PathVariable(value = "rptFormat") String rptFormat,
			@PathVariable(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
			@PathVariable(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end)
			throws Exception {

		String reportTitle = null;
		String jsReportFilePath = "jasper/";
		String jsReportFileName = null;
		String period = null;
		LocalDateTime startLocalDateTime = null;

		startLocalDateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// bound date range into 1 month when datePeriod not equal to "Month"
		if (!datePeriod.equals("M") && end.after(DateUtils.addMonths(start, 1))) {
			end = DateUtils.addMonths(start, 1);
		}

		String selectedCriteria = "1 SysId and 1 SourceID";
		String selectionType = "121"; // default, 1 sysId and 1 source Id
		if (sysId.equals("ALL")) { // All sysId and All source Id
			selectionType = "A2N";
			selectedCriteria = "All SysId and All SourceID";
		} else if (sourceId.equals("ALL")) { // 1 sysId and All source Id
			selectionType = "12A";
			selectedCriteria = "1 SysId and All SourceID";
		}

		switch (datePeriod) {
			case "H":
				period = "Houryly";
				// start = DateUtils.truncate(start, Calendar.DATE);
				start = java.sql.Timestamp.valueOf(startLocalDateTime.truncatedTo(ChronoUnit.DAYS));
				end = DateUtil.dayEnd(end);
				break;
			case "M":
				period = "Monthy";
				start = DateUtil.monthStart(start);
				end = DateUtil.monthEnd(end);
				break;
			default:
				period = "Daily";
				// start = DateUtils.truncate(start, Calendar.DATE);
				start = java.sql.Timestamp.valueOf(startLocalDateTime.truncatedTo(ChronoUnit.DAYS));
				end = DateUtil.dayEnd(end);
				break;
		}

		switch (reportType) {
			case "clientMtSummary":
				reportTitle = "Client Traffic MT-SMS " + period + " Summary";
				if (period.equals("Houryly")) {
					jsReportFileName = "clientMtSummaryH.jasper";
				} else {
					jsReportFileName = "clientMtSummary.jasper";
				}
				break;
			case "clientMtDetail":
				jsReportFileName = "clientMtDetail.jasper";
				reportTitle = "Client Traffic MT-SMS " + period + " Detail";
				if (sysId.toUpperCase().equals("ALL")) {
					reportType = reportType + "_all";
					jsReportFileName = "clientMtDetail_all.jasper";
				} else {
					jsReportFileName = "clientMtDetail.jasper";
				}
				break;
			case "clientMtSmsFailure":
				reportTitle = "Client Traffic MT-SMS Failure Analysis " + period + " Summary";
				if (period.equals("Houryly")) {
					jsReportFileName = "clientMtSmsFailureH.jasper";
				} else {
					jsReportFileName = "clientMtSmsFailure.jasper";
				}
				break;
			case "clientMoSummary":
				reportTitle = "Client Traffic MO-SMS " + period + " Summary";
				if (period.equals("Houryly")) {
					jsReportFileName = "clientMoSummaryH.jasper";
				} else {
					jsReportFileName = "clientMoSummary.jasper";
				}
				break;
			case "clientDrSummary":
				reportTitle = "Client Traffic DR " + period + " Summary";
				if (period.equals("Houryly")) {
					jsReportFileName = "clientDrSummaryH.jasper";
				} else {
					jsReportFileName = "clientDrSummary.jasper";
				}
				break;
			default:
				log.debug("Invaild reportType[{}]", reportType);
				break;
		}

		if (!datePeriod.equals("D")) {
			reportType = reportType + datePeriod;
		}

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("datasource", dataSource);
		parameters.put("sysId", sysId);
		parameters.put("sourceId", sourceId);
		parameters.put("selectionType", selectionType);
		parameters.put("selectedCriteria", selectedCriteria);
		parameters.put("reportType", reportTitle);
		parameters.put("format", rptFormat);
		parameters.put("sDate", start);
		parameters.put("eDate", end);

		genJasperReport(response, parameters, jsReportFilePath + jsReportFileName);

	}

	// for Monthly Detail MT Usage Report only
	@Permission(id = "SERVICE_06", level = PrivilegeLevel.READ)
	// @LogAction(type = SacConstant.EXP_RPT, message = "Generate Report[{4}]")
	// @LogEvent(type = SacConstant.EXP_RPT, message = "Generate Report[{4}]")
	@RequestMapping(value = "/getReportMTDetailM/{criteria}/{reportType}/{rptFormat}/{start}/{end}", method = RequestMethod.GET)
	public void getReportMTDetailM(HttpServletResponse response,
			@PathVariable(value = "criteria") String criteria,
			@PathVariable(value = "reportType") String reportType,
			@PathVariable(value = "rptFormat") String rptFormat,
			@PathVariable(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
			@PathVariable(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end)
			throws Exception {

		String reportTitle = null;
		String period = null;
		String jsReportFilePath = "jasper/";
		String jsReportFileName = null;

		log.debug("Request for Report [{}]  @[{}] - [{}]", reportType, start, end);

		String selectedCriteria = "All Interface"; // interface or sysId
		if (criteria.equals("sysId")) {
			selectedCriteria = "All SysId";
		}

		period = "Monthy";
		start = DateUtil.monthStart(start);
		end = DateUtil.monthEnd(end);
		reportTitle = "Client Traffic MT-SMS " + period + " Detail";
		reportType = reportType + "M";
		if (criteria.equals("sysId")) {
			reportType += "2";
			jsReportFileName = "clientMtDetailM2.jasper";
		} else {
			jsReportFileName = "clientMtDetailM.jasper";
		}

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("datasource", dataSource);
		parameters.put("selectionType", criteria);
		parameters.put("selectedCriteria", selectedCriteria);
		parameters.put("reportType", reportTitle);
		parameters.put("format", rptFormat);
		parameters.put("sDate", start);
		parameters.put("eDate", end);

		genJasperReport(response, parameters, jsReportFilePath + jsReportFileName);

	}

	// /* Example for JasperReport in Spring5 */
	// @GetMapping(value = "/report1")
	// public void getPdf(HttpServletResponse response) throws JRException,
	// IOException, SQLException {
	// // InputStream jasperStream =
	// this.getClass().getResourceAsStream("/jasperreports/HelloWorld1.jasper");
	// InputStream jasperStream =
	// this.getClass().getResourceAsStream("/jasper/clientMtDetail.jrxml");

	// // adjust alignment in html report
	// StringBuffer sb = new StringBuffer();
	// sb.append("<html>");
	// sb.append("<head>");
	// sb.append(" <title></title>");
	// sb.append(" <meta http-equiv=\"Content-Type\" content=\"text/html;
	// charset=UTF-8\"/>");
	// sb.append(" <style type=\"text/css\">");
	// sb.append(" a {text-decoration: none}");
	// sb.append(" </style>");
	// sb.append("</head>");
	// sb.append("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\"
	// vlink=\"#000000\"");
	// sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"
	// border=\"0\">");
	// sb.append("<tr><td align=\"left\">");

	// Map<String, Object> exporterParameters = new HashMap<String, Object>();
	// exporterParameters.put("net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN",
	// Boolean.FALSE);
	// exporterParameters.put("net.sf.jasperreports.engine.export.JRHtmlExporterParameter.HTML_HEADER",
	// sb.toString());

	// JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// exporterParameters, dataSource.getConnection());

	// response.setContentType("text/html");
	// // response.setHeader("Content-disposition", "inline;
	// filename=helloWorldReport.pdf");

	// final OutputStream outStream = response.getOutputStream();
	// // JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, null);
	// }

	private void genJasperReport(HttpServletResponse response, Map<String, Object> jsParameters, String jsReportFile)
			throws Exception {

		String rtpFormat = null;
		Connection connection = null;
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(jsReportFile);

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
		// JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

		try {
			connection = dataSource.getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jsParameters, connection);
			OutputStream outputStream = null;
			rtpFormat = (String) jsParameters.get("format");
			if (rtpFormat.equals("pdf")) {

				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment;");
				outputStream = response.getOutputStream();

				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				response.getOutputStream().flush();
				response.getOutputStream().close();

			} else if (rtpFormat.equals("html")) {

				response.setContentType("text/html");

				HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
				exporter.exportReport();
			} else if (rtpFormat.equals("csv")) {

				// response.setContentType("text/html");

				JRCsvExporter exporter = new JRCsvExporter(DefaultJasperReportsContext.getInstance());
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
				exporter.exportReport();
			} else if (rtpFormat.equals("csv")) {

				response.setContentType("text/html");

				JRCsvExporter exporter = new JRCsvExporter(DefaultJasperReportsContext.getInstance());
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.exportReport();
			} else if (rtpFormat.equals("xlsx")) {

				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment;");
				outputStream = response.getOutputStream();

				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				configuration.setOnePagePerSheet(true);
				configuration.setIgnoreGraphics(false);
				exporter.setConfiguration(configuration);

				exporter.exportReport();
			}

		} finally {

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
