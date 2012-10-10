package org.complitex.flexbuh.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.01.2010 0:30:49
 */
public class DateUtil {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_FULL_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_SMALL_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_DATETIME_THREAD_LOCAL = new ThreadLocal<>();

    public static SimpleDateFormat getDateFormatFull(){
        SimpleDateFormat simpleDateFormat = DATE_FORMAT_FULL_THREAD_LOCAL.get();

        if (simpleDateFormat == null){
            simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            DATE_FORMAT_FULL_THREAD_LOCAL.set(simpleDateFormat);
        }

        return simpleDateFormat;
    }

    public static SimpleDateFormat getDateFormatSmall(){
        SimpleDateFormat simpleDateFormat = DATE_FORMAT_SMALL_THREAD_LOCAL.get();

        if (simpleDateFormat == null){
            simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
            DATE_FORMAT_SMALL_THREAD_LOCAL.set(simpleDateFormat);
        }

        return simpleDateFormat;
    }

    public static SimpleDateFormat getDateFormatDateTime(){
        SimpleDateFormat simpleDateFormat = DATE_FORMAT_DATETIME_THREAD_LOCAL.get();

        if (simpleDateFormat == null){
            simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            DATE_FORMAT_DATETIME_THREAD_LOCAL.set(simpleDateFormat);
        }

        return simpleDateFormat;
    }

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getBeginOfDay(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return c.getTime();
    }

    public static String getTimeDiff(long start, long end) {
        long time = end - start;

        long msec = time % 1000;
        time = time / 1000;
        long sec = time % 60;
        time = time / 60;
        long min = time % 60;
        time = time / 60;
        long hour = time;

        return String.format("%d:%02d:%02d", hour, min, sec);
    }

    /**
     * Отображает локализованный месяц
     * @param month месяц, формат 1-январь, 2-февраль
     * @param locale локализация
     * @return месяц
     */
    public static String displayMonth(int month, Locale locale) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        return c.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
    }

    public static boolean isTheSameDay(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 == null || date2 == null) {
            return false;
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static Date getMax(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return date1 == null ? date2 : date1;
        }

        return date1.compareTo(date2) > 0 ? date1 : date2;
    }

    public static boolean isCurrentDay(Date date) {
        return isTheSameDay(date, getCurrentDate());
    }

    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date justBefore(Date current) {
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.SECOND, -1);
        return c.getTime();
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * Creates Date object based on day, month and year.
     * Lenient mode turned off so that exception may be thrown in case of incorrect date information.
     *
     * @param day
     * @param month
     * @param year
     * @return date
     */
    public static Date newDate(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setLenient(false);
        c.set(year, month, day);
        return c.getTime();
    }

    public static Date getDate(String s){
        Date date = null;

        try {
            date = getDateFormatFull().parse(s);
        } catch (ParseException e) {
            //ups
        }

        if (date == null) {
            try {
                date = getDateFormatSmall().parse(s);
            } catch (ParseException e) {
                //ups
            }
        }

        return date;
    }

    public static int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth(){
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.MONTH);
    }

    public static int getCurrentDay(){
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getStringDate(Date date){
        return date != null ? getDateFormatFull().format(date) : "";
    }

    public static String getStringDateTime(Date date){
        return date != null ? getDateFormatDateTime().format(date) : "";
    }
}
