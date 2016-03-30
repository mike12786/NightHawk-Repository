package com.zapporoo.nighthawk.quickblox.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UtilDate {
//	public static CharSequence timeToDay(long time) {
//		Date date_sms = new Date(time);
//		Date date_now = new Date();
//		
//		if (isSameDay(date_sms, date_now)){
//			return DateFormat.format("kk:mm", time);
//		}
//		
//		if (isSameYear(date_sms, date_now)){
//			return DateFormat.format("dd MMM", time );
//		}
//		
//		return DateFormat.format("dd MMM yyyy", time );
//	}
	
	
	public static CharSequence timeSocialNetwork(long time){
        time = time * 1000;
		return timeToDay(time).toString().replace(" 0:", " 12:").replace("AM", "am").replace("PM","pm");
	}
	
	private static CharSequence timeToDay(long time) {
		Date date_sms = new Date(time);
		Date date_now = new Date();
		
		//KK <= 12
		//kk <= 24
		//a AM/PM
		
		if (isSameDay(date_sms, date_now)){
			return timelocalTIme("'Today at' K:mma", time);
			//return DateFormat.format("kk:mm", time);
		}
		
		if (isYesterday(date_sms, date_now)){
			return timelocalTIme("'Yesterday at' K:mma", time);
			//return DateFormat.format("kk:mm", time);
		}
		
		if (isSameWeek(date_sms, date_now)){
			return timelocalTIme("cccc 'at' K:mma", time);
			//return DateFormat.format("kk:mm", time);
		}
		
		if (isSameYear(date_sms, date_now)){
			return timelocalTIme("MMMM dd 'at' K:mma", time );
			//return DateFormat.format("dd MMM", time );
		}
		
		return timelocalTIme("MMMM dd yyyy 'at' K:mma", time );
		//return DateFormat.format("dd MMM yyyy", time );
	}
	
	public static String timelocalTIme(String format, long time){
			//SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			//Date parsed = sourceFormat.parse("2011-03-01 15:10:37");
			Date date = new Date(time);
			
			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat destFormat = new SimpleDateFormat(format);//"yyyy-MM-dd HH:mm:ss");
			destFormat.setTimeZone(tz);

			return destFormat.format(date);
	}
	
	/**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
        
    }
    
    public static boolean isYesterday(Date date1, Date now) {
        if (date1 == null || now == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(now);
        
        if (cal1 == null || calNow == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        
        
        return (cal1.get(Calendar.ERA) == calNow.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == calNow.get(Calendar.WEEK_OF_YEAR) &&
                cal1.get(Calendar.DAY_OF_WEEK) == (calNow.get(Calendar.DAY_OF_WEEK) - 1));
        
    }
    
    public static boolean isSameWeek(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR));
        
    }
    
    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
        
    }
    
    
    public static boolean isSameYear(Date date1, Date date2){
    	Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    
    static long secondsInMilli = 1000;
	static long minutesInMilli = secondsInMilli * 60;
	static long hoursInMilli = minutesInMilli * 60;
	static long daysInMilli = hoursInMilli * 24;
	
	/**	Returns difference in current time and expiryTime, formatted in XXhr YYmin
	 * @param expiryTime
	 * @return
	 */
	public static String calculateTimeToString(long expiryTime) {
		long different = expiryTime - (new Date()).getTime() + 60000;
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
 		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		if (elapsedHours == 0)
			return elapsedMinutes + " min";
		return elapsedHours + "hr " + elapsedMinutes + " min";
	}
	
	public static long calculateTimeToLong(long expiryTimeTimes15Min) {
		int min_per_selection = 15;
		long summ = min_per_selection * (expiryTimeTimes15Min+1) * minutesInMilli + (new Date()).getTime();
		return summ;
	}
	
	public static String getTimeInHHMMSS(long time_milliseconds){
		return (String) DateFormat.format("(mm:ss)", time_milliseconds);
	}
}
