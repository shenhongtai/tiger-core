package cn.imtiger.annotation.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cn.imtiger.annotation.CronSecondOrMinute;
import cn.imtiger.util.data.StringUtil;
import cn.imtiger.util.data.ValidateUtil;

public class CronSecondOrMinuteValidatorImpl implements ConstraintValidator<CronSecondOrMinute, List<CronSecondOrMinute>> {

	@Override
	public void initialize(CronSecondOrMinute constraintAnnotation) {

	}

	@Override
	public boolean isValid(List<CronSecondOrMinute> value, ConstraintValidatorContext context) {
		for (CronSecondOrMinute cron : value) {
			if (ValidateUtil.isValidSecond(cron.value())) {
				return true;
			} else if (StringUtil.containsCount(cron.value(), "/") == 1) {
				String[] arr = cron.value().split("/");
				if (ValidateUtil.isValidSecond(arr[0]) && ValidateUtil.isValidSecond(arr[1])) {
					return true;
				}
			} else if (StringUtil.containsCount(cron.value(), "-") == 1) {
				String[] arr = cron.value().split("-");
				if (ValidateUtil.isValidSecond(arr[0]) && ValidateUtil.isValidSecond(arr[1])) {
					return true;
				}
			} else if ("*".equals(cron.value())) {
				return true;
			}
		}
		return false;
	}
}
