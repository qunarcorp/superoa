package com.qunar.superoa.utils;

import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.AgentException;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_上午10:44
 * @Despriction: 用于实现，日期、字符串、long之间的相互转换
 */

public class DateTimeUtil {

    /**
     * main
     */
    public static void main(String[] args) {
        String currentTime = currentTime();                    // 获取当前时间的字符串形式
        long currentLong = currentTimeLong();                // 获取当前时间的long型

        System.out.println("示例输出：");

        System.out.println("currentTime: " + currentTime);
        System.out.println("currentTimeAsSecond:" + currentTimeAsSecond());
        System.out.println("currentLong: " + currentLong);

        Date stringToDate = toDate(currentTime);            // 日期字符串转化为日期
        Date longToDate = toDate(currentLong);                // long转化为日期

        System.out.println("stringToDate: " + toString(stringToDate));    // 显示为日期串
        System.out.println("longToDate: " + toString(longToDate));        // 显示为日期串
        System.out.println(getTime());
        System.out.println(getDate());
        System.out.println(getDate("/"));
    }

    // 获取当前时间，long型
    public static long currentTimeLong() {
        return new Date().getTime();
    }

    // 获取当前时间，字符串形式
    public static String currentTime() { return toString( new Date()); }

    // 从字符串, 获取日期, 如time = "2016-3-16 4:12:16"
    public static Date toDate(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            throw new AgentException(ResultEnum.DATE_FORMART_ERROR);
        }
    }

    // 从long, 获取日期
    public static Date toDate(long millSec) {
        return new Date(millSec);
    }

    /**
     * 日期转化为字符串形式
     * @param date
     * @return
     */
    public static String toString(Date date) { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date); }


    /**
     * 获取当前的系统时间，以秒为单位, java.util.Date.Date()
     */
    private static long currentTimeAsSecond() {
        return new Date().getTime() / 1000;    //获取当前时间的秒数值
    }


    /**
     *  获取当前时间字符串
     * @return
     */
    public static String getTime(){
        return new DateTime().toString("HH:mm:ss");
    }

    /**
     *  获取当前时间字符串
     * @return
     */
    public static String getDate(){
        return new DateTime().toString("yyyy-MM-dd");
    }

    /**
     *  获取当前时间字符串
     * @return
     */
    public static String getDateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     *  获取当前时间字符串
     * @return
     */
    public static String getDate(String str){
        return new SimpleDateFormat(new StringBuilder().append("yyyy").append(str).append("MM").append(str).append("dd").toString()).format(new Date());
    }

}