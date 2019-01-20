package com.smart.library.center.common.util;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期格式化类
 *
 * @author franklin.li
 */
public class DateUtil {

    /**
     * 获取utc零时区的时间戳（10位 second）
     *
     * @return
     */
    public static long getUTC() {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 获取utc零时区的时间戳（13位 second）
     *
     * @return
     */
    public static long getUTCms() {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        return calendar.getTimeInMillis();
    }

    public static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone zone = TimeZone.getDefault();
        df.setTimeZone(zone);
        String date = df.format(new Date());
        return date;
    }

    /**
     * @param标准utc时间 （10位）s
     * @param "GMT+8"
     * @return
     */
    public static String dateFormat(long time, String ID) {
        if (time / 1000000000000l == 0) {
            time *= 1000;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone zone = TimeZone.getTimeZone(ID);
        format.setTimeZone(zone);
        return format.format(time);
    }

    /**
     * 本地默认时区下的时间格式化
     *
     * @param标准utc时间 （10位）s
     * @return
     */
    public static String dateFormat(long time) {
        if (time / 1000000000000l == 0) {
            time *= 1000;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone zone = TimeZone.getDefault();
        format.setTimeZone(zone);
        return format.format(time);
    }

    /**
     * @param time 标准utc时间（10位）s
     * @paramthe  ID for a TimeZone, either an abbreviation such as "PST", a
     *             full name such as "America/Los_Angeles", or a custom ID such
     *             as "GMT-8:00". Note that the support of abbreviations is for
     *             JDK 1.1.x compatibility only and full names should be used.
     * @paramlike :"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String dateFormat(long time, String ID, String format) {
        if (time / 1000000000000l == 0) {
            time *= 1000;
        }
        DateFormat df = new SimpleDateFormat(format);
        TimeZone zone = TimeZone.getTimeZone(ID);
        df.setTimeZone(zone);
        return df.format(time);
    }

    public static int getDateByUTC(long utc) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Object []getDatesInStrFormat(long from,long to){
        List<String> dates=new ArrayList<String>();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        while(from<=to){
            dates.add(format.format(new Date(from*1000)));
            from=from+60*60*24;
        }
        return dates.toArray();
    }

    public static Object []getDatesInStrFormatInteger(long from,long to){
        List<Integer> dates=new ArrayList<Integer>();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        while(from<=to){
            dates.add(Integer.parseInt(format.format(new Date(from*1000))));
            from=from+60*60*24;
        }
        return dates.toArray();
    }


    public static boolean checkMonth(String inputMonth){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        //年份和月份都相等
        if(year==Integer.parseInt(inputMonth.substring(0,4))
                &&month==Integer.parseInt(inputMonth.substring(4,6))){
            return true;
        }
        return false;
    }

    /*
    * 通过月份如200101和Dateformat得到一个月的最后一天的时间戳,DateFormat的格式必须这样yyyyMMdd，例如20010101
    * */
    public static Long getLastDayByMonth(String inputMonth,DateFormat format) throws ParseException {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(format.parse(inputMonth+"01"));
        StringBuilder str=new StringBuilder();
        int m=calendar.get(Calendar.MONTH)+1;
        str.append(calendar.get(Calendar.YEAR));
        if(m==12){
            str.append(calendar.get(Calendar.YEAR)+1);
            str.append("0101");
        }else {
            str.append((m+1)>=10?"":"0");
            str.append((m+1)+"01");
        }
        System.out.println("date="+str.toString());
        return format.parse(str.toString()).getTime()/1000-24*60*60;
    }

    /*
    * compatible with epoch time and millseconds.
    * */
    public static Date compatibilityDate(long time) {
        if (time < 1e10 && time > 1e9) {
            return new Date(time * 1000);
        } else {
            return new Date(time);
        }
    }

    /*
    * from date object to epoch time, if date is less than 1971y just return
    * it's millseconds, maybe it's from old historical data.
    *
    * */
    public static long toEpochSecond(Date date) {
        long time = date.getTime();
        if (time < 1e10 && time > 1e9) {
            return time;
        } else {
            return date.toInstant().getEpochSecond();
        }
    }


}
