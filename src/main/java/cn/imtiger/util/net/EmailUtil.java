package cn.imtiger.util.net;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import cn.imtiger.util.data.ValidateUtil;

/**
 * 电子邮件工具类
 * @author ShenHongtai
 * @date 2020-4-20
 */
public class EmailUtil {
	/**
	 * 网易邮箱
	 */
	public static final Object[] PROVIDER_163 = { "smtp.163.com", 465 };
	
	/**
	 * 发送文字消息邮件
	 * @param provider
	 * @param senderName
	 * @param senderAddress
	 * @param senderPassword
	 * @param acceptorAddress
	 * @param copyAddress
	 * @param title
	 * @param message
	 * @throws Exception
	 */
	public static void send(Object[] provider, String senderName, String senderAddress, String senderPassword,
			String[] acceptorAddress, String[] copyAddress, String title, String message) throws Exception {
		
		/**
		 * 校验所需的参数是否填写完整 
		 */
		if (provider == null || ValidateUtil.isNull((String) provider[0]) || ValidateUtil.isNull(provider[1] + "")) {
			throw new Exception("电子邮件服务商不能为空");
		}

		if (ValidateUtil.isNull(senderAddress) || ValidateUtil.isNull(senderPassword)) {
			throw new Exception("发件人信息不能为空");
		}
		
		if (ValidateUtil.isNull(acceptorAddress)) {
			throw new Exception("收件人不能为空");
		}
		
		if (ValidateUtil.isNull(title)) {
			throw new Exception("主题不能为空");
		}
		
		if (ValidateUtil.isNull(message)) {
			throw new Exception("邮件内容不能为空");
		}

		try {
			/**
			 * 创建一封邮件
			 */
			HtmlEmail email = new HtmlEmail();
			/**
			 * 设置字符集
			 */
			email.setCharset("UTF-8");
			/**
			 * 设置发件服务器地址、端口
			 */
			email.setHostName((String) provider[0]);
			email.setSmtpPort((Integer) provider[1]);
			/**
			 * 设置发送方邮箱账号、密码
			 */
			email.setAuthenticator(new DefaultAuthenticator(senderAddress, senderPassword));
			/**
			 * 设置使用SSL安全连接
			 */
			email.setSSLOnConnect(true);
			/**
			 * 设置发件人信息
			 */
			email.setFrom(senderAddress, senderName);
			/**
			 * 设置邮件主题
			 */
			email.setSubject(title);
			/**
			 * 设置邮件内容
			 */
			email.setHtmlMsg(message);
			/**
			 * 设置收件人
			 */
			if (acceptorAddress != null) {
				for (String string : acceptorAddress) {
					email.addTo(string, string);
				}
			}
			/**
			 * 设置抄送（可以没有）
			 */
			if (copyAddress != null) {
				for (String string : copyAddress) {
					email.addCc(string);
				}
			}
			/**
			 * 发送邮件
			 */
			email.send();
		} catch (EmailException e) {
			/**
			 * 发送失败打印日志，返回失败原因
			 */
			e.printStackTrace();
			throw new Exception("邮件发送失败，" + e.getMessage());
		}
	}
}
