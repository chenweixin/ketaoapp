package com.android.ketaoapp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/17 0017.
 */
public class CommonUtil {

    public static String getFormatedDateTime(long dateTime) {
        int today = getTodayBegin();
        long current = System.currentTimeMillis() / 1000;

        Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        currentDate.setTimeInMillis(current * 1000);
        date.setTimeInMillis(dateTime * 1000);
        //几秒前
        if(dateTime >= today && (current - dateTime) < 60){
            return (current - dateTime)+"秒前";
        }
        //几分钟前
        else if(dateTime >= today && (current - dateTime) < 3600){
            return ((current - dateTime)/60)+"分钟前";
        }
        //几小时前
        else if(dateTime >= today && (current - dateTime) < 86400){
            return ((current - dateTime)/3600)+"小时前";
        }
        //昨天
        else if(dateTime < today && (today - dateTime) < 86400){
            return "昨天 "+getTime(dateTime);
        }
        //今年
        else if(currentDate.get(Calendar.YEAR) == date.get(Calendar.YEAR)){
            return date.get(Calendar.MONTH)+"月"+date.get(Calendar.DAY_OF_MONTH)+"日 "+getTime(dateTime);
        }
        //其他
        else{
            return date.get(Calendar.YEAR)+"年" + date.get(Calendar.MONTH)+"月"+date.get(Calendar.DAY_OF_MONTH)+"日 "+getTime(dateTime);
        }
    }

    public static int getTodayBegin(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static String getTime(long dateTime){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateTime*1000);
        return sDateFormat.format(cal.getTime());
    }

    public static String getDate(long dateTime){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(dateTime*1000);
        return date.get(Calendar.YEAR)+"年" + date.get(Calendar.MONTH)+"月"+date.get(Calendar.DAY_OF_MONTH)+"日";
    }
}
