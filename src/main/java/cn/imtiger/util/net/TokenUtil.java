package cn.imtiger.util.net;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tokenπ§æﬂ¿‡
 * @author shen_hongtai
 * @date 2019-9-18
 */
public final class TokenUtil {
	private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

	static final String CSRF_PARAM_NAME = "CSRFToken";

	public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = TokenUtil.class.getName() + ".tokenval";

	/**
	 * Create Token from Request
	 * 
	 * @param request
	 * @return
	 */
	public static String getTokenForRequest(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return getTokenForSession(session);
	}

	/**
	 * Create Token from Session
	 * 
	 * @param session
	 * @return
	 */
	public static String getTokenForSession(HttpSession session) {
		String token = null;

		synchronized (session) {
			token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
			if (null == token) {
				token = UUID.randomUUID().toString();
				session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
			}
		}
		return token;
	}

	/**
	 * Extracts the token value from the session
	 * 
	 * @param request
	 * @return
	 */
	public static String getTokenFromRequest(HttpServletRequest request) {
		return request.getParameter(CSRF_PARAM_NAME);
	}

	/**
	 * Check Crsf Token Validate
	 * 
	 * @param request
	 * @return
	 */
	public static boolean checkCrsfTokenValidate(HttpServletRequest request) {
		String _csrf = request.getParameter("csrf");
		HttpSession session = request.getSession();
		String token = getTokenForSession(session);
		if (_csrf == null || !_csrf.equals(token)) {
			if (logger.isDebugEnabled()) {
				logger.debug("CSRF attack detected. URL:" + request.getRequestURI());
			}
			return false;
		}
		return true;
	}
}