package cn.imtiger.saml.util;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

/**
 * SAML2.0工具类
 * @author shen_hongtai
 * @date 2019-11-29
 */
public abstract class SAMLUtil {
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static DateTime getIssueInstant() {
		Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        return new DateTime(year, month, day, hour, minute, second, 0, ISOChronology.getInstanceUTC());
	}

	/**
	 * 获取消费截止时间（默认为当前时间后5分钟）
	 * @return
	 */
	public static DateTime getNotOnOrAfter() {
		return getNotOnOrAfter(5);
	}

	/**
	 * 获取消费截止时间
	 * @param expiredMinutes 有效期（单位：分钟）
	 * @return
	 */
	public static DateTime getNotOnOrAfter(Integer expiredMinutes) {
		Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE) + expiredMinutes;
        int second = cal.get(Calendar.SECOND);
        return new DateTime(year, month, day, hour, minute, second, 0, ISOChronology.getInstanceUTC());
	}
	
}
