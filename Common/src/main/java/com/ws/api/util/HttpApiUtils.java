package com.ws.api.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ws.util.CommonFileUtil;

public class HttpApiUtils {

	private static Logger logger = LogManager.getLogger(HttpApiUtils.class);
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getTimestampForXml(Date now){
		if(now!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HHmmss");
			return sdf.format(now);
		}
		else return "";
	}

	public static LocalDateTime date2LocalDateTime(Date date){
		if(date == null){
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static Date getDateForAddOrSub(int days){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, days);
		logger.debug("date:[{}]", c.getTime());
		return c.getTime();
	}
	
	public static int getDayOfWeek(){
		int dayFfWeek = 0;
		Calendar c = Calendar.getInstance();
		dayFfWeek = c.get(Calendar.DAY_OF_WEEK);
		dayFfWeek--;
//		if(dayFfWeek == 1){
//			dayFfWeek = 7;
//		}
//		else{
//			dayFfWeek--;
//		}

		return dayFfWeek;
	}
	
	/**
	 * format ex: yyyy/MM/dd HH:mm:ss  or yyMMddHHmm
	 * @param format
	 * @param date
	 * @return
	 */
	public static String formatDate(String format,Date date){
		if(date != null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}
		else return "";
	}
	
	/**
	 * 
	 * @param encoded
	 * @param decoding UTF8 / iso8859-1 etc...
	 * @param changCoding UTF8 / iso8859-1 etc...
	 * @return
	 */
	public static String base64Decoded(String encoded,String decoding ,String changCoding){
		String text = "";
		try{
			byte[] decoded = Base64.getDecoder().decode(encoded);
			text = new String(decoded,decoding);
			if(changCoding!=null && !changCoding.equals("")){
				text = new String(text.getBytes(changCoding),changCoding);
			}
		}catch(Exception e){
			logger.warn("base64Decoded error:[{}]",e.getMessage());
		}
		return text;
	}
		
	public static String base64Encoded(String decoded,String encodeing){
		String text= "";
		try{
			byte[] encoded =Base64.getEncoder().encode(decoded.getBytes(encodeing));
			text = new String(encoded);
		}catch(Exception e){
			logger.warn("base64Encoded error:[{}]",e);
		}
		return text;
	}
	
	/**
	 * 
	 * @param encoded
	 * @param language C / E / B / U
	 * @param changCoding
	 * @return
	 */
	public static String getBase64DecodedText(String encoded,String language ,String changCoding){
		String text = "";
		try{
			if(encoded!=null && !encoded.equals("")){
				String coding = "UTF-8";
				if("B".equals(language)) coding = "big5";
				// else if("E".equals(language)) coding = "ISO-8859-1";
				// unicode use UTF-16BE
				// disable by YC 20190716 because old spec was wrong
//				else if("C".equals(language)) coding = "UTF-16BE"; 
				logger.debug("==== getBase64DecodedText encoded text:[{}], coding:[{}]", encoded, coding);
				byte[] decoded = Base64.getDecoder().decode(encoded);
				text = new String(decoded,coding);
				logger.debug("==== getBase64DecodedText decoded text:[{}], coding:[{}]", text, coding);
				if(changCoding!=null && !changCoding.equals("")){
					text = new String(text.getBytes(changCoding),changCoding);
					logger.debug("==== getBase64DecodedText change code text:[{}], coding:[{}]", text, changCoding);
				}
			}
			
		}catch(Exception e){
			logger.warn("getBase64DecodedText error:[{}]",e.getMessage());
			text = encoded;
		}
		return CommonFileUtil.removeLinefeedInTheEnd(text);
	}
	
	public static String toHexString(String text) {
	    StringBuilder str = new StringBuilder();
	    try{
	    	byte[] ba = text.getBytes("UTF-8");
	    	for(int i = 0; i < ba.length; i++)
	    		str.append(String.format("%x", ba[i]));
	    	
	    }catch(UnsupportedEncodingException e){
	    	logger.warn("toHexString error:[{}]",e.getMessage());
			return text ;
	    }
	    return str.toString();
	}

	public static String fromHexString(String hex) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < hex.length(); i+=2) {
	        str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
	    }
	    return str.toString();
	}
}
