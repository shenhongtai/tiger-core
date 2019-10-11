package cn.imtiger.util.net;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import cn.imtiger.util.data.ValidateUtil;

/**
 * 电子邮件工具类
 * @author ShenHongtai
 * @date 2019-7-13
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
			Email email = new SimpleEmail();
			email.setHostName((String) provider[0]);
			email.setSmtpPort((Integer) provider[1]);
			email.setAuthenticator(new DefaultAuthenticator(senderAddress, senderPassword));
			email.setSSLOnConnect(true);
			email.setFrom(senderAddress, senderName);
			email.setSubject(title);
			email.setMsg(message);
			if (acceptorAddress != null) {
				for (String string : acceptorAddress) {
					email.addTo(string, string);
				}
			}
			if (copyAddress != null) {
				for (String string : copyAddress) {
					email.addCc(string);
				}
			}
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
			throw new Exception("邮件发送失败，" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		String title = "A simple mail";
		String message = "Welcome to my company, and I wish you have a good time.";
		try {
			send(PROVIDER_163, "TigerSoftSystem", "443122163@163.com", "shen9211025",
					new String[] { "15853107903@163.com" }, null, title, message);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
