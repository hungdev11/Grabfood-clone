package com.grabdriver.myapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_TIME_DISPLAY_FORMAT = "dd/MM/yyyy HH:mm";
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private static final Locale LOCALE_VN = new Locale("vi", "VN");
    
    /**
     * Chuyển đổi Date thành chuỗi theo định dạng
     * 
     * @param date Đối tượng Date cần chuyển đổi
     * @param format Định dạng (nếu null sẽ dùng DATE_TIME_FORMAT)
     * @return Chuỗi ngày tháng định dạng
     */
    public static String dateToString(Date date, String format) {
        if (date == null) return null;
        
        if (format == null) format = DATE_TIME_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(format, LOCALE_VN);
        sdf.setTimeZone(DEFAULT_TIME_ZONE);
        
        return sdf.format(date);
    }
    
    /**
     * Chuyển đổi Date thành chuỗi theo định dạng mặc định
     */
    public static String dateToString(Date date) {
        return dateToString(date, null);
    }
    
    /**
     * Chuyển đổi chuỗi ngày tháng thành đối tượng Date
     * 
     * @param dateStr Chuỗi ngày tháng
     * @param format Định dạng (nếu null sẽ dùng DATE_TIME_FORMAT)
     * @return Đối tượng Date hoặc null nếu không parse được
     */
    public static Date stringToDate(String dateStr, String format) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        
        if (format == null) format = DATE_TIME_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(format, LOCALE_VN);
        sdf.setTimeZone(DEFAULT_TIME_ZONE);
        
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Chuyển đổi chuỗi ngày tháng thành đối tượng Date theo định dạng mặc định
     */
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, null);
    }
    
    /**
     * Chuyển đổi chuỗi ngày tháng từ định dạng này sang định dạng khác
     * 
     * @param dateStr Chuỗi ngày tháng đầu vào
     * @param inputFormat Định dạng đầu vào
     * @param outputFormat Định dạng đầu ra
     * @return Chuỗi ngày tháng định dạng đầu ra
     */
    public static String convertDateFormat(String dateStr, String inputFormat, String outputFormat) {
        Date date = stringToDate(dateStr, inputFormat);
        if (date == null) return null;
        
        return dateToString(date, outputFormat);
    }
    
    /**
     * Định dạng đầu vào chung về định dạng hiển thị
     */
    public static String formatToDisplayDateTime(String dateTimeStr) {
        return convertDateFormat(dateTimeStr, DATE_TIME_FORMAT, DATE_TIME_DISPLAY_FORMAT);
    }
    
    /**
     * Tính thời gian ước tính đến thời điểm hiện tại
     * 
     * @param targetTime Thời điểm cần tính
     * @return Chuỗi thời gian ước tính theo dạng "x phút" hoặc "x giờ"
     */
    public static String getEstimatedTimeFromNow(Date targetTime) {
        if (targetTime == null) return "";
        
        long diffInMillis = targetTime.getTime() - System.currentTimeMillis();
        if (diffInMillis <= 0) return "0 phút";
        
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        if (diffInMinutes < 60) {
            return diffInMinutes + " phút";
        } else {
            long hours = diffInMinutes / 60;
            long remainingMinutes = diffInMinutes % 60;
            if (remainingMinutes == 0) {
                return hours + " giờ";
            } else {
                return hours + " giờ " + remainingMinutes + " phút";
            }
        }
    }
    
    /**
     * Lấy thời điểm hiện tại định dạng theo yêu cầu
     * 
     * @param format Định dạng đầu ra
     * @return Chuỗi thời điểm hiện tại
     */
    public static String getCurrentDateTime(String format) {
        return dateToString(new Date(), format);
    }
    
    /**
     * Lấy thời điểm hiện tại theo định dạng mặc định
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime(DATE_TIME_FORMAT);
    }
    
    /**
     * Lấy thời điểm trong tương lai từ hiện tại
     * 
     * @param minutes Số phút trong tương lai
     * @return Đối tượng Date
     */
    public static Date getFutureTime(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(DEFAULT_TIME_ZONE);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
    
    /**
     * Lấy thời điểm trong tương lai từ hiện tại định dạng theo chuẩn API
     * 
     * @param minutes Số phút trong tương lai
     * @return Chuỗi thời gian định dạng DATE_TIME_FORMAT
     */
    public static String getFutureTimeString(int minutes) {
        return dateToString(getFutureTime(minutes), DATE_TIME_FORMAT);
    }
    
    /**
     * Tính thời gian trôi qua từ một thời điểm
     * 
     * @param pastTime Thời điểm trong quá khứ
     * @return Chuỗi thời gian trôi qua
     */
    public static String getTimeAgo(Date pastTime) {
        if (pastTime == null) return "";
        
        long diffInMillis = System.currentTimeMillis() - pastTime.getTime();
        if (diffInMillis < 0) return "vừa xong";
        
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        if (diffInMinutes < 1) {
            return "vừa xong";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + " phút trước";
        } else if (diffInMinutes < 24 * 60) {
            long hours = diffInMinutes / 60;
            return hours + " giờ trước";
        } else if (diffInMinutes < 48 * 60) {
            return "hôm qua";
        } else {
            long days = diffInMinutes / (24 * 60);
            if (days < 30) {
                return days + " ngày trước";
            } else {
                return dateToString(pastTime, "dd/MM/yyyy");
            }
        }
    }
    
    /**
     * Tính thời gian trôi qua từ một chuỗi thời gian
     * 
     * @param pastTimeStr Chuỗi thời gian trong quá khứ
     * @return Chuỗi thời gian trôi qua
     */
    public static String getTimeAgo(String pastTimeStr) {
        Date pastTime = stringToDate(pastTimeStr);
        return getTimeAgo(pastTime);
    }
} 