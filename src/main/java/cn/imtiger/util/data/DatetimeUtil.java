package cn.imtiger.util.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 * @author ShenHongtai
 * @date 2020-4-20
 */
public class DatetimeUtil {
	
	/**
	 * 获取指定距离日期时间
	 * @param pastday 距今多少天
	 * @return
	 */
	public static String getPastDateTime(int pastday) {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(date.getTime() - (pastday - 1) * 24 * 60 * 60 * 1000)) + " 00:00:00";
	}

	/**
	 * 格式化Calendar类型的日期时间
	 */
	public static String formatDateTime(Calendar dateTime, String dateFormat) {
		if (dateTime == null) {
			return getNowDateTime(dateFormat);
		} else {
			return new SimpleDateFormat(dateFormat).format(dateTime.getTime());
		}
	}

	/**
	 * 格式化Date类型的日期时间
	 */
	public static String formatDateTime(Date dateTime, String dateFormat) {
		if (dateTime == null) {
			return getNowDateTime(dateFormat);
		} else {
			return new SimpleDateFormat(dateFormat).format(dateTime);
		}
	}

	/**
	 * 获取指定格式的当前日期时间
	 */
	public static String getNowDateTime(String dateFormat) {
		return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取默认格式的当前日期时间
	 */
	public static String getNow() {
		return getNowDateTime("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 获取默认格式时间
	 */
	public static String getNowTime() {
		return getNowDateTime("HH:mm:ss");
	}
	
	/**
	 * 获取默认格式日期
	 */
	public static String getNowDate() {
		return getNowDateTime("yyyy-MM-dd");
	}
}
