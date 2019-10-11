package cn.imtiger.util.bean;

/**
 * Cron表达式
 * @author shen_hongtai
 * @date 2019-10-11
 */
public class CronExpression {
	String second;
	String minute;
	String hour;
	String date;
	String month;
	String day;
	String year;
	
	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * 获取Cron表达式
	 * @return cronExpression
	 */
	public String getCronExpression() {
		return new StringBuffer(second).append(" ").append(minute).append(" ").append(hour).append(" ").append(date)
				.append(" ").append(month).append(" ").append(day).append(" ").append(year).toString();
	}

	@Override
	public String toString() {
		return "CronExpression[" + this.getCronExpression() + "]";
	}
}
