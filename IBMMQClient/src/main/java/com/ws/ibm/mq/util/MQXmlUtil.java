package com.ws.ibm.mq.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.common.collect.ImmutableMap;
import com.ws.api.util.HttpApiUtils;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MessageHeader;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.legacyPojo.SMS;
import com.ws.msp.legacyPojo.SMS.Message;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MQXmlUtil {
	private static String retryOption = "4";

	private static final ImmutableMap<String, String> langMap = new ImmutableMap.Builder<String, String>().put("V", "C") // UniCode
			.put("U", "E") // ASCII code
			.put("W", "B") // Big5
			.build();

	private static String mapLangauge(char lang) {
		return langMap.get(String.valueOf(lang));
	}

	public static String getFormatOneXmlData(MspMessage msg) throws SMSException {
		MessageHeader header = msg.getHeader();
		StringBuilder xml = new StringBuilder();
		xml.append("<SMS>");
		xml.append(genXmlBody("SysId", header.getSysID()));
		xml.append("<Message>");
		if (header.getType() == 'M') {

			// check if target duplicate
			List<String> targets = msg.getTargetList();
			Set<String> set = new HashSet<String>(targets);
			if (set.size() < targets.size()) {
				// There are duplicate targets
				throw new SMSException(LegacyConstant.FORMAT_ONE, 1008);
			}
			for (String target : targets) {
				xml.append(genXmlBody("Target", target));
			}

		} else {
			xml.append(genXmlBody("Target", header.getTarget()));
		}

		xml.append(genXmlBody("Source", header.getSource()));
		String lang = mapLangauge(header.getLanguage());
		String la = lang == null ? String.valueOf(header.getLanguage()) : lang;
		
		// 2090719 modify by YC, change Unicode encoding to UTF-8 for match httpapi spec wrong type.
		String encoding = LegacyConstant.LANG.get(la);
		if(StringUtils.isNoneBlank(la)) {
			if(la.equals("C") || la.equals("V")) {
				encoding = "UTF-8";
			}
		}
		xml.append(genXmlBody("Text", HttpApiUtils.base64Encoded(msg.getBody(), encoding)));
		// xml.append(genXmlBody("Text", HttpApiUtils.base64Encoded(msg.getBody(),
		// "UTF-8")));
		// String encodedText = HttpApiUtils.base64Encoded(msg.getBody(),
		// LegacyConstant.LANG.get(la));

		// xml.append(genXmlBody("Text", msg.getBody()));
		xml.append(genXmlBody("Language", la));
		xml.append(genXmlBody("DrFlag", Boolean.toString(header.isDrFlag())));
		xml.append(genXmlBody("ValidType", MQXmlUtil.retryOption));
		xml.append(genXmlBody("IsMQ", "true"));
		xml.append("</Message>");
		xml.append("</SMS>");

		return xml.toString();
	}

	public static List<String> getFormatTwoXmlData(SMS msg) throws SMSException {
		List<String> xmlList = new ArrayList<>();

		List<Message> msgList = msg.getMessage();
		for (Message message : msgList) {
			StringBuilder xml = new StringBuilder();
			xml.append("<SMS>");
			xml.append(genXmlBody("SysId", msg.getSysId()));
			xml.append("<Message>");

			// check if target duplicate
			List<SMS.Message.Target> targets = message.getTarget();
			Set<SMS.Message.Target> set = new HashSet<SMS.Message.Target>(targets);
			if (set.size() < targets.size()) {
				// There are duplicate targets
				throw new SMSException(LegacyConstant.FORMAT_TWO, 1008);
			}

			message.getTarget().forEach(t -> {
				// add target(s)
				xml.append(genXmlBody("Target", t.getValue()));
			});

			xml.append(genXmlBody("Source", message.getSource()));

			// String encodedText = HttpApiUtils.base64Encoded(message.getText(),
			// LegacyConstant.LANG.get(message.getLanguage()));

			xml.append(genXmlBody("Text", message.getText()));
			// xml.append(genXmlBody("Text", HttpApiUtils.base64Encoded(message.getText(),
			// "UTF-8")));

			xml.append(genXmlBody("Language", message.getLanguage()));
			xml.append(genXmlBody("DrFlag", Boolean.toString(message.isDrFlag())));
			xml.append(genXmlBody("ValidType", message.getValidPeriod().toString()));

			// see whether validPeriod need to implement
			// xml.append(genXmlBody("ValidPeriod", message.getValidPeriod().toString()));

			xml.append(genXmlBody("IsMQ", "true"));
			xml.append("</Message>");
			xml.append("</SMS>");

			log.debug("xml = {}", xml);
			xmlList.add(xml.toString());
		}

		return xmlList;
	}

	// public static String getFormatTwoXmlMo(MessageObject msg) {
	//
	// StringBuilder xml = new StringBuilder();
	// xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	// xml.append("<SmsMO>");
	// xml.append("<Message>");
	// xml.append(genXmlBody("Target", msg.));
	//
	//
	// xml.append("</Message>");
	// xml.append("</SmsMO>");
	//
	//
	// log.debug("xml = {}", xml);
	// return ;
	// }

	private static String genXmlBody(String tag, String value) {
		String result = "";
		result = "<" + tag + ">" + value + "</" + tag + ">";
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T XmlToObject(String xml, Class<T> clazz)
			throws JAXBException, SAXException, ParserConfigurationException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		T o = null;
		try {
			// ignore DTD section
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			spf.setFeature("http://xml.org/sax/features/validation", false);
			//XXX add by matthew 2019-06-27 Secure SAXParserFactory that prevents XXE 
			/*
			//Using the SAXParserFactory's setFeature
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			// Xerces 2 only - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			*/
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			InputSource inputSource = new InputSource(new StringReader(xml));
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			SAXSource source = new SAXSource(xmlReader, inputSource);
			o = (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			log.error("==== XmlToObject JAXBException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (SAXException e) {
			log.error("==== XmlToObject SAXException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (ParserConfigurationException e) {
			log.error("==== XmlToObject ParserConfigurationException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (Exception e) {
			log.error("==== XmlToObject Exception: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		}
		return o;
	}

	public static <T> String ObjectToXml(Object obj, Class<T> clazz) throws JAXBException {
		String result = null;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(obj, sw);
			result = sw.toString();
			log.debug("Object to xml:\r\n{}", result);
		} catch (JAXBException e) {
			log.error("==== ObjectToXml JAXBException: [{}]", e.getMessage());
			throw e;
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return result;
	}

	public static <T> String ObjectToXmlWithDTD(Object obj, Class<T> clazz) throws JAXBException {
		String result = null;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", "\n<!DOCTYPE SMS SYSTEM  \"sms.dtd\">");
			marshaller.marshal(obj, sw);
			result = sw.toString();
			log.debug("Object to xml:[{}]", result);
		} catch (JAXBException e) {
			log.error("==== ObjectToXml JAXBException: [{}]", e.getMessage());
			throw e;
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return result;
	}

}
