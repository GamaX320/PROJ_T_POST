package com.tarpost.bryanty.proj_t_post.common;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by BRYANTY on 26-Jan-2016.
 */
public class DateUtil {

    public static String getTimeRangeStr(Date date){
        String timeRangeStr = "";
        Date currentDate = new Date();

        if(date != null){
            if(date.before(currentDate)){

                int differenceYear = currentDate.getYear() - date.getYear();
                int differenceMonth = currentDate.getMonth() - date.getMonth();
                int differenceDate = currentDate.getDate() - date.getDate();
                int differenceHour = currentDate.getHours() - date.getHours();
                int differenceMinute = currentDate.getMinutes() - date.getMinutes();
                int differenceSecond = currentDate.getSeconds() - date.getSeconds();

                if(differenceSecond > 0){
                    timeRangeStr= "just now";
                }

                if(differenceMinute > 0){
                    timeRangeStr= differenceMinute+" mins ago";
                }

                if(differenceHour > 0){
                    timeRangeStr= differenceHour+" hours ago";
                }

                if(differenceDate > 0){
                    timeRangeStr= differenceDate+" days ago";
                }

                if(differenceMonth > 0){
                    timeRangeStr= differenceMonth+" months ago";
                }

                if(differenceYear > 0){
                    timeRangeStr= differenceYear+" years ago";
                }
            }
        }

        return timeRangeStr;
    }

    public static Date convertStringToDate(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        Date date = null;

        try{
            date = sdf.parse(dateStr);
        }catch(ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    public static Date convertStringToDateSQLite(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy",Locale.US);
        Date date = null;

        try{
            date = sdf.parse(dateStr);
        }catch(ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    public static String convertDateToString(Date date){
        String dateStr = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, date.getYear()+1900);
        cal.set(Calendar.MONTH, date.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, date.getDate());

        cal.set(Calendar.HOUR, date.getHours());
        cal.set(Calendar.MINUTE, date.getMinutes());
        cal.set(Calendar.SECOND, date.getSeconds());
        dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

        return dateStr;
    }

    public static String convertDateToStringWithout1900(Date date){
        String dateStr = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.MONTH, date.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, date.getDate());

        cal.set(Calendar.HOUR, date.getHours());
        cal.set(Calendar.MINUTE, date.getMinutes());
        cal.set(Calendar.SECOND, date.getSeconds());
        dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

        return dateStr;
    }

    public static String convertYearMonthDayToString(int year, int month, int day){
        String dateStr = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        dateStr = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());

        return dateStr;
    }

    public static String convertYearMonthDayToStringWith1900(int year, int month, int day){
        String dateStr = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year+1900);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        dateStr = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());

        return dateStr;
    }

    public static String convertHourMinuteToString(int hour, int minute){
        String timeStr = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        timeStr = new SimpleDateFormat("HH:mm aa").format(cal.getTime());

        return timeStr;
    }
}
