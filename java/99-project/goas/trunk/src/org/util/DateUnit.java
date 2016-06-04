package org.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 日期处理
 */

public class DateUnit {

	public static String getCurrentYM() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(new Date());
	}
	
	public static String getCurrentYMD() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		return dateFormat.format(new Date());
	}
	
	public static String getCurrentDateAndTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(new Date());
	}
	
	public static String dateToStr(Date date) {
		return dateToStr(date, "MEDIUM");
	}
	
    public static String dateToStr(Date date, String type) { 
        String str = null; 
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        if (type.equals("SHORT")) { 
            // 07-1-18 
            format = DateFormat.getDateInstance(DateFormat.SHORT); 
            str = format.format(date); 
        } else if (type.equals("MEDIUM")) { 
            // 2007-1-18 
            format = DateFormat.getDateInstance(DateFormat.MEDIUM); 
            str = format.format(date); 
        } else if (type.equals("FULL")) { 
            // 2007年1月18日 星期四 
            format = DateFormat.getDateInstance(DateFormat.FULL); 
            str = format.format(date); 
        } 
        return str; 
    } 
    
    public static Date strToDate(String str) { 
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        Date date = null; 
        try { 
            // Fri Feb 24 00:00:00 CST 2012 
            date = format.parse(str);  
        } catch (ParseException e) { 
            e.printStackTrace(); 
        } 
//        // 2012-02-24 
//        date = java.sql.Date.valueOf(str);                      
        return date; 
    } 
    
	/*
	 * null: Error
	 * 0: date1 == date2
	 * >0: date1 > date2
	 * <0: date1 < date2
	 */
	public static Integer cmpDate(String date1, String date2) {
		
		if(date1==null || date2==null) {
			return null;
		}
		
		try {
			String[] dateArray1 = date1.split("[-. :]");
			String[] dateArray2 = date2.split("[-. :]");
			
			int length = 0;
			if (dateArray1.length > dateArray2.length) {
				length = dateArray2.length;
			} else {
				length = dateArray1.length;
			}
			
			for (int i=0; i<length; i++) {//比较共有的地方
				
				if (Integer.parseInt(dateArray1[i])-Integer.parseInt(dateArray2[i]) != 0)
					return Integer.parseInt(dateArray1[i])-Integer.parseInt(dateArray2[i]);
			}
			
			return dateArray1.length-dateArray2.length;//共有的地方相同就比较长度
			
		} catch (Exception e) {
			return null;
		}
	}
	
	//dateFormat: yyyy-MM-dd
	public static boolean isEndOfMonth(String date) {
		
		if(date==null || date.length()!=10) {
			return false;
		}
		
		try {
			
			String[] dateStrArray = date.split("-");
			int year = Integer.parseInt(dateStrArray[0]);
			int month = Integer.parseInt(dateStrArray[1]);
			int day = Integer.parseInt(dateStrArray[2]);
			
			boolean isLeap = false;
			if((year%4==0 && year%100!=0) || year%400==0) {
				isLeap = true;
			}
			
			if(month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12) {
				if(day == 31) {
					return true;
				}
			}
			else if(month!=2 && day==30) {
				return true;
			}
			else {
				if(isLeap==true && day==29) {
					return true;
				}
				else if(isLeap==false && day==28) {
					return true;
				}
			}
			
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
