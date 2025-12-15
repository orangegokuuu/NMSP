package com.ws.api.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.ws.emg.constant.ApiConstant;
import com.ws.httpapi.pojo.BatchRetrieveDR;
import com.ws.httpapi.pojo.QueryDR;
import com.ws.httpapi.pojo.RetrieveDR;
import com.ws.httpapi.pojo.SMS;
import com.ws.msp.config.MspProperties;

import ie.omk.smpp.util.DefaultAlphabetEncoding;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class XmlUtils {
	@Autowired
	private MspProperties properties;

	private String prefix[];
	private String daLength[];

	DefaultAlphabetEncoding defaultEnc = new DefaultAlphabetEncoding();

	// private static final String DA_PREFIX =

	@PostConstruct
	public void init() {
		log.debug("Invoke init");
		prefix = properties.getApi().getDa().getFormat().getPrefix().split(",");
		daLength = properties.getApi().getDa().getFormat().getLength().split(",");
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
			// XXX add by matthew 2019-06-27 Secure SAXParserFactory that prevents XXE
			// Using the SAXParserFactory's setFeature
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			// Xerces 2 only -
			// http://xerces.apache.org/xerces-j/features.html#external-general-entities
			spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();

			// Using the XMLReader's setFeature
			xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);

			InputSource inputSource = new InputSource(new StringReader(xml));
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			SAXSource source = new SAXSource(xmlReader, inputSource);
			o = (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			log.warn("XmlToObject JAXBException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (SAXException e) {
			log.warn("XmlToObject SAXException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (ParserConfigurationException e) {
			log.warn("XmlToObject ParserConfigurationException: [{}] , xml:[{}]", e.getMessage(), xml);
			throw e;
		} catch (Exception e) {
			log.warn("XmlToObject Exception: [{}] , xml:[{}]", e.getMessage(), xml);
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
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			// marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
			marshaller.marshal(obj, sw);
			result = sw.toString();
		} catch (JAXBException e) {
			log.warn("==== ObjectToXml JAXBException: [{}]", e.getMessage());
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

	public String checkSms(SMS sms) {
		String result = "";
		if (sms == null) {
			return ApiConstant.RC_INVALID_XML;
		}
		if (StringUtils.isBlank(sms.getSysId())) {
			return ApiConstant.RC_MISS_SYSID;
		}
		// XXX fixed 2017-10-30 start
		if (StringUtils.isBlank(sms.getMessage().getValidType())) {
			return ApiConstant.RC_INVALID_XML;
		}
		if (sms.getMessage().getTarget().size() < 1) {
			return ApiConstant.RC_INVALID_DA;
		}
		if (StringUtils.isBlank(sms.getMessage().getSource())) {
			return ApiConstant.RC_INVALID_OA;
		}
		if (StringUtils.isBlank(sms.getMessage().getLanguage())) {
			return ApiConstant.RC_INVALID_LANG;
		}
		if (StringUtils.isBlank(sms.getMessage().getDrFlag())) {
			return ApiConstant.RC_INVALID_DR;
		}

		// XXX fixed 2017-10-30 end
		// if (StringUtils.isBlank(sms.getMessage().getText())){
		//
		// }
		// if(sms.getMessage().getTarget().size() > 100){
		// return ApiConstant.RC_INVALID_DA;
		// }
		if (sms.getMessage().getTarget().size() > 0) {
			for (String da : sms.getMessage().getTarget()) {
				if (StringUtils.isBlank(da)) {
					return ApiConstant.RC_INVALID_DA;
				} else if (!checkNumberPrefix(da)) {
					return ApiConstant.RC_INVALID_DA;
				}
			}
		}
		if (checkLanguage(sms.getMessage().getLanguage())) {
			return ApiConstant.RC_INVALID_LANG;
		}
		if (!sms.getMessage().getDrFlag().equals("true") && !sms.getMessage().getDrFlag().equals("false")) {
			return ApiConstant.RC_INVALID_DR;
		}
		if (checkValidType(sms.getMessage().getValidType())) {
			return ApiConstant.RC_INVALID_VALIDTYPE;
		}
		return result;
	}

	public String checkSmsRetrieve(RetrieveDR dr) {
		String result = "";
		if (dr == null) {
			return ApiConstant.RC_INVALID_XML;
		}
		if (StringUtils.isBlank(dr.getSysId())) {
			return ApiConstant.RC_MISS_SYSID;
		}
		return result;
	}

	public String checkSmsBatchRetrieve(BatchRetrieveDR dr) {
		String result = "";
		if (dr == null) {
			return ApiConstant.RC_INVALID_XML;
		}
		if (StringUtils.isBlank(dr.getSysId())) {
			return ApiConstant.RC_MISS_SYSID;
		}
		if (StringUtils.isBlank(dr.getMessageId())) {
			return ApiConstant.RC_PARAM_EMPTY;
		}
		return result;
	}

	public String checkSmsQueryDR(QueryDR dr) {
		String result = "";
		if (dr == null) {
			return ApiConstant.RC_INVALID_XML;
		}
		if (StringUtils.isBlank(dr.getSysId())) {
			return ApiConstant.RC_MISS_SYSID;
		}
		if (StringUtils.isBlank(dr.getType())) {
			return ApiConstant.RC_INVALID_TYPE;
		}
		// if (StringUtils.isBlank(dr.getMessageId()) ||
		// StringUtils.isBlank(dr.getType())) {
		// return ApiConstant.RC_PARAM_EMPTY;
		// }
		return result;
	}

	public boolean checkNumberPrefix(String number) {
		boolean sw = false;

		for (int i = 0; i < prefix.length; i++) {
			if (daLength[i].length() > 2) {
				String range[] = daLength[i].split("-");
				if (range.length == 2) {
					String tempNum = number;
					if (tempNum.trim().startsWith("+")) {
						tempNum = tempNum.substring(1, tempNum.trim().length());
						log.debug("==== checkNumberPrefix ,da trim prefix '+' [{}]", tempNum);
					}
					if (tempNum.trim().length() >= Integer.valueOf(range[0])
							&& tempNum.trim().length() <= Integer.valueOf(range[1])) {
						log.debug("==== checkNumberPrefix da:[{}],prefix:[{}],length:[{}],sub length:[{}]", number,
								prefix[i], daLength[i], prefix[i].length());
						if (prefix[i].equals(number.trim().substring(0, prefix[i].length()))) {
							sw = true;
							break;
						}
					}
				} else {
					log.info("checkNumberPrefix() length range is too much, range:[{}]", daLength[i]);
				}
			} else {
				if (number.trim().length() == Integer.valueOf(daLength[i])) {
					log.debug("==== checkNumberPrefix da:[{}],prefix:[{}],length:[{}],sub length:[{}]", number,
							prefix[i], daLength[i], prefix[i].length());
					if (prefix[i].equals(number.trim().substring(0, prefix[i].length()))) {
						sw = true;
						break;
					}
				}
			}

		}

		// if(!sw && number.trim().startsWith("+") && !number.trim().startsWith("+886")
		// && number.trim().length() >= 6 && number.trim().length() <= 20){
		// log.debug("==== checkNumberPrefix da:[{}]", number);
		// sw = true;
		// }

		return sw;
	}

	public boolean checkLanguage(String language) {
		String langArray[] = { "C", "E", "B", "U" };
		boolean sw = true;
		for (String lang : langArray) {
			if (lang.equals(language)) {
				sw = false;
				break;
			}
		}
		return sw;
	}

	public boolean checkValidType(String validType) {
		String array[] = { "0", "1", "2", "3", "4" };
		boolean sw = true;
		for (String val : array) {
			if (val.equals(validType)) {
				sw = false;
				break;
			}
		}
		return sw;
	}

	// public String validateXmlFormat(String xml, String xsdName) {
	// String result = "";
	// SAXParserFactory spf = SAXParserFactory.newInstance();
	// XmlUtils utils = new XmlUtils();
	// try {
	// SchemaFactory factory =
	// SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	// File file = utils.getXsdFile(xsdName);
	// StreamSource ss = new StreamSource(file);
	// Schema schema = factory.newSchema(ss);
	//
	// spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
	// true);
	// spf.setFeature("http://xml.org/sax/features/validation", true);
	// spf.setValidating(true);
	// spf.setNamespaceAware(true);
	// spf.setSchema(schema);
	// Validator validator = schema.newValidator();
	// validator.validate(new StreamSource(new StringReader(xml)));
	//
	// } catch (SAXException e) {
	// log.error("==== checkXmlFormat error SAXException: [{}]", e.getMessage());
	// if (e.getMessage().indexOf("未預期子項元素") > 0) {
	// result = ApiConstant.RC_UNSUPPORT_PARAM;
	// } else {
	// result = ApiConstant.RC_INVALID_XML;
	// }
	// } catch (ParserConfigurationException e) {
	// log.error("==== checkXmlFormat error ParserConfigurationException: [{}]",
	// e.getMessage());
	// result = ApiConstant.RC_INVALID_XML;
	// } catch (Exception e) {
	// log.error("==== checkXmlFormat error Exception: [{}]", e.getMessage());
	// result = ApiConstant.RC_INVALID_XML;
	// }
	// return result;
	// }

	public boolean checkTextLength(String text, String language, String apiVersion) {
		boolean isTooLong = false;
		int cht = "2".equals(apiVersion) ? getMaxChinsesLength() : getSingleChineseLength();
		int eng = "2".equals(apiVersion) ? getMaxEnglishLength() : getSingleEnglishLength();

		try {

			byte[] message = null;

			if (("C".equals(language) || "B".equals(language) || "U".equals(language)) && text.length() > cht) {
				isTooLong = true;
			} else if ("E".equals(language)) {

				message = defaultEnc.encodeString(new String(text.getBytes("UTF-8")));

				if (message.length > eng) {
					isTooLong = true;
				}
			}

			log.debug(
					"==== checkTextLength language:[{}], canLongMsg:[{}], cht max size:[{}], eng max size:[{}], now text length:[{}]",
					language, "2".equals(apiVersion) ? "YES" : "NO", cht, eng,
					"E".equals(language) ? message.length : text.length());
		} catch (Exception e) {
			log.warn("==== checkTextLength error Exception: [{}]", e.getMessage());
			log.warn(e, e);
		}
		return isTooLong;
	}

	private File getXsdFile(String xsdName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(xsdName + ".xsd").getFile());
		return file;
	}

	private int getSingleChineseLength() {
		return properties.getApi().getContent().getChinese().getMinLength();
	}

	private int getSingleEnglishLength() {
		return properties.getApi().getContent().getEnglish().getMinLength();
	}

	private int getMaxChinsesLength() {
		return properties.getApi().getContent().getChinese().getMaxLength();
	}

	private int getMaxEnglishLength() {
		return properties.getApi().getContent().getEnglish().getMaxLength();
	}

	// public static void main(String a[]) {
	// String xml = "<?xml version=\"1.0\"
	// encoding=\"UTF-8\"?><SMS><SysId></SysId><Message><Target>0909000001</Target><Source>01999991201703400000</Source><Text>SGksIHlvdS7ll6jvvIzlprM=</Text><Language>E</Language><DrFlag>true</DrFlag><ValidType>0</ValidType></Message><GG></SMS>";
	// System.out.println("xml:"+xml);
	// String sms = validateXmlFormat(xml,"SMS");
	// System.out.println("XXXXXXXXXX:"+sms);
	// System.out.println("XXXXXXXXXX2:"+checkNumberPrefix("0906123123"));
	// System.out.println("XXXXXXXXXX2:"+"09".equals("0906123123".substring(0,
	// 2)));
	// System.out.println("XXXXXXXXXX2:"+checkTextLength("5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz","C",true));

	// String regexp1="312345600000[0-9]{8}";
	// String regexp2="312345600000[0-9]{5}808";
	// String regexp3="^31234[0-9]{3}0000[0-9]{5}808";
	//
	// List<String> list = new ArrayList<String>();
	// list.add(regexp1);
	// list.add(regexp2);
	// list.add(regexp3);
	// String allEx =
	// "\\(\\w+\\)|\\[[\\w|-]+\\]|\\{[\\w|-|,]\\}|\\[b|B|d|D|s|S|w|W]|[*|+|?|^]";
	// for(String regexp:list){
	// System.out.println("regexp:"+regexp.replaceAll(allEx, ""));
	// System.out.println("length:"+regexp.replaceAll(allEx, "").length());
	// }
	//
	// System.out.println("XXX:"+RegexUtil.longestMatch(list,
	// "31234560000000000808"));
	// }
}
