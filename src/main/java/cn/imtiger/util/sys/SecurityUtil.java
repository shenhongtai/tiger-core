package cn.imtiger.util.sys;

import cn.imtiger.util.data.ValidateUtil;

/**
 * 安全管理工具类
 * @author ShenHongtai
 * @date 2019-7-13
 */
public class SecurityUtil {
	/**
	 * 数据库关键字
	 */
	static String[] defaultInjectWords = {"SELECT", "INSERT", "DELETE", "UPDATE", "AND", "DROP", "EXEC", "EXECUTE", "COUNT", "CHR", "MID", "MASTER", "TRUNCATE", "CHAR", "DECLARE", "SITENAME", "NET USER", "XP_CMDSHELL", "OR", "LIKE'", "LIKE", "CREATE", "UNION", "WHERE", "ORDER", "BY", "TABLE", "FROM", "GRANT", "USE", "GROUP_CONCAT", "COLUMN_NAME", "INFORMATION_SCHEMA.COLUMNS", "TABLE_SCHEMA", "*", "%", ";", "--", "//", "%", "#", "||"};
	
	/**
	 * 字符串关键字检测
	 * @param injectStr
	 * @return
	 */
	public static boolean isSqlInject(String injectString) {
		if (ValidateUtil.isNull(injectString)) {
			return false;
		}
		injectString = injectString.toLowerCase(); 
		for (int i = 0; i < defaultInjectWords.length; i++) {
			if (injectString.indexOf(defaultInjectWords[i]) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSqlInject(String injectWords, String injectString) {
		if (ValidateUtil.isNull(injectWords)) {
			return isSqlInject(injectString);
		} else {
			String[] injStrArr = injectWords.split("\\|");
			if (ValidateUtil.isNull(injectString)) {
				return false;
			}
			injectString = injectString.toLowerCase(); 
			for (int i = 0; i < injStrArr.length; i++) {
				if (injectString.indexOf(injStrArr[i]) >= 0) {
					return true;
				}
			}
			return false;
		}
	}
}
