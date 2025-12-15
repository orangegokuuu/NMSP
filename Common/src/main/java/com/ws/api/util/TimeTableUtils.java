package com.ws.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeTableUtils {
	
	private static Logger logger = LogManager.getLogger(TimeTableUtils.class);
	
//	private static final String startTime[] = {"00:01","00:31","01:01","01:31","02:01","02:31","03:01","03:31","04:01","04:31"
//			                                  ,"05:01","05:31","06:01","06:31","07:01","07:31","08:01","08:31","09:01","09:31"
//			                                  ,"10:01","10:31","11:01","11:31","12:01","12:31","13:01","13:31","14:01","14:31"
//			                                  ,"15:01","15:31","16:01","16:31","17:01","17:31","18:01","18:31","19:01","19:31"
//			                                  ,"20:01","20:31","21:01","21:31","22:01","22:31","23:01","23:31"};
//	
//	private static final String endTime[] = {"00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00"
//			                                ,"05:30","06:00","06:30","07:00","07:30","08:00","08:30","09:00","09:30","10:00"
//			                                ,"10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00"
//			                                ,"15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00"
//			                                ,"20:30","21:00","21:30","22:00","22:30","23:00","23:30","00:00"};
	
	private static final String startTime[] = {"23:5959","00:2959","00:5959","01:2959","01:5959","02:2959","02:5959","03:2959","03:5959","04:2959"
                                              ,"04:5959","05:2959","05:5959","06:2959","06:5959","07:2959","07:5959","08:2959","08:5959","09:2959"
                                              ,"09:5959","10:2959","10:5959","11:2959","11:5959","12:2959","12:5959","13:2959","13:5959","14:2959"
                                              ,"14:5959","15:2959","15:5959","16:2959","16:5959","17:2959","17:5959","18:2959","18:5959","19:2959"
                                              ,"19:5959","20:2959","20:5959","21:2959","21:5959","22:2959","22:5959","23:2959"};

	private static final String endTime[] = {"00:2959","00:5959","01:2959","01:5959","02:2959","02:5959","03:2959","03:5959","04:2959","04:5959"
                                            ,"05:2959","05:5959","06:2959","06:5959","07:2959","07:5959","08:2959","08:5959","09:2959","09:5959"
                                            ,"10:2959","10:5959","11:2959","11:5959","12:2959","12:5959","13:2959","13:5959","14:2959","14:5959"
                                            ,"15:2959","15:5959","16:2959","16:5959","17:2959","17:5959","18:2959","18:5959","19:2959","19:5959"
                                            ,"20:2959","20:5959","21:2959","21:5959","22:2959","22:5959","23:2959","23:5959"};
	
	
	/**
	 * @param sendTimeData
	 * @param now
	 * @return
	 */
	public static boolean CheckSendTime(String sendTimeData,Date now){
		boolean sw = false;
		try{
			String array[] = sendTimeData.split(",");
			if(array.length!=48){
				logger.debug("Check send SMS Time error ,sendTimeData length: [{}]",array.length);
				return false;
			}
			//int arrayId = getSendTimeRange(now);
			Calendar today = Calendar.getInstance();
			today.setTime(now);
			int arrayId = getSendTimeRange2(today);
			if(array[arrayId].equals("1")){
				sw = true;
			}
			if(arrayId == 999){
				logger.warn("get send time range id=999,sendTimeData:[{}]",sendTimeData);
				logger.warn("get send time range id=999,startTime:[{}]",Arrays.toString(startTime));
				logger.warn("get send time range id=999,endTime:[{}]",Arrays.toString(endTime));
			}
		}catch(Exception e){
			logger.warn("Check send SMS time error : [{}]", e.getMessage());
		}
		return sw;
	}
	
	/**
	 * @param now
	 * @return
	 */
	public static int getSendTimeRange(Date now){
		int id = 999;
		Date start,end;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			now = sdf.parse(sdf.format(now));
			logger.debug("=== now time:[{}]",sdf.format(now));
			for(int i=0;i<48;i++){
				start = sdf.parse(startTime[i]);
				end = sdf.parse(endTime[i]);
				if(now.after(start) && now.before(end) || now.equals(start) || now.equals(end)){
					id = i;
					logger.debug("=== now time:[{}], start time :[{}], end time :[{}]",sdf.format(now),startTime[i],endTime[i]);
					break;
				}
			}
		} catch (ParseException e) {
			logger.warn("get send time range : [{}]", e.getMessage());
		}
		return id;
	}
	
	public static int getSendTimeRange2(Calendar now){
		int id = 999;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mmss");
		String ymd = getYmd(now,0);
//		System.out.println("ymd :"+ymd);
		try {
			for(int i=0;i<48;i++){
				if(i==0){
					// day - 1
					String ymdAddOne = getYmd(now,-1);
					start.setTime(sdf.parse(ymdAddOne+startTime[i]));
				}
				else{
					start.setTime(sdf.parse(ymd+startTime[i]));
				}
				end.setTime(sdf.parse(ymd+endTime[i]));
				if((now.compareTo(start) > 0) && (now.compareTo(end) < 0) || (now.compareTo(start) == 0) || (now.compareTo(end) == 0)){
					id = i;
					logger.debug("=== now time:[{}], start time :[{}], end time :[{}]",sdf.format(now.getTime()),sdf.format(start.getTime()),sdf.format(end.getTime()));
					break;
				}
			}
		} catch (ParseException e) {
			logger.warn("get send time range : [{}]", e.getMessage());
		}
		if(id == 999){
			logger.warn("get send time range id=999,now time:[{}]",sdf.format(now.getTime()));
		}
		return id;
	}
	
	private static String getYmd(Calendar now,int addDay){
		Calendar temp = Calendar.getInstance();
		temp.setTime(now.getTime());
		if(addDay != 0){
			temp.add(Calendar.DAY_OF_MONTH, addDay);
		}
		int yyyy = temp.get(Calendar.YEAR);
		int mm = temp.get(Calendar.MONTH);
		int dd =temp.get(Calendar.DAY_OF_MONTH);
		StringBuffer ymd = new StringBuffer("");
		ymd.append(yyyy);
		if((mm+1) >= 10){
			ymd.append((mm+1));
		}
		else{
			ymd.append("0");
			ymd.append((mm+1));
		}
		if(dd >= 10){
			ymd.append(dd);
		}
		else{
			ymd.append("0");
			ymd.append(dd);
		}
		
		
		return ymd.toString();
	}
	
//	public static void main(String a[]) throws ParseException{
//		String sendTimeData = "1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,"
//				            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mmss");
//		Date now = sdf.parse("2018100100:2900");//new Date();
//		System.out.println("Send Time :"+sdf.format(now));
//		Calendar today = Calendar.getInstance();
//		today.setTime(now);
//		//System.out.println("getSendTimeRange2 :"+getSendTimeRange2(today));
//		System.out.println("CheckSendTime :"+CheckSendTime(sendTimeData,now));
//	}
	
}
