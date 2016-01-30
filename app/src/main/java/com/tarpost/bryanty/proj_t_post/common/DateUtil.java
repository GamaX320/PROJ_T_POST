package com.tarpost.bryanty.proj_t_post.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try{
            date = sdf.parse(dateStr);
        }catch(ParseException e){
            e.printStackTrace();
        }

        return date;
    }
}
