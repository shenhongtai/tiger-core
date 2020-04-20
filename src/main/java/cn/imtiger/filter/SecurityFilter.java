package cn.imtiger.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.imtiger.util.data.ValidateUtil;

/**
 * 安全过滤器
 * @author shen_hongtai
 * @date 2019-12-11
 */
@Configuration
@WebFilter(filterName = "securityFilter", urlPatterns = {"/*"}, description = "安全过滤器")
@PropertySource(value = "classpath:app.properties", encoding = "UTF-8")
public class SecurityFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	@Value("${imtiger.security.enabled:1}")
	private String enabled;

	@Value("${imtiger.security.xssEnabled:1}")
	private String xssEnabled;
	
	@Value("${imtiger.security.refererEnabled:1}")
	private String refererEnabled;
	
	@Value("${imtiger.security.sqlInjectEnabled:1}")
	private String sqlInjectEnabled;

	@Value("${imtiger.security.httpMethodEnabled:1}")
	private String 	httpMethodEnabled;
	
	@Value("${imtiger.security.referer:}")
	private String trustedReferers;
	
	@Value("${imtiger.security.xss:}")
	private String xss;
	
	@Value("${imtiger.security.sql:}")
	private String sql;
	
	@Value("${imtiger.security.whiteListURLs:}")
	private String whiteListURLs;

	private List<Pattern> patterns = null;

	private List<Object[]> getXssPatternList() {
		List<Object[]> ret = new ArrayList<>();
		ret.add(new Object[] {
				"(((<|%3c)/?(script|iframe|frame|body|img|STYLE))|expression|javascript|vbscript|eval|window.execScript|window.setInterval|window.setTimeout|document.write|document.writeln|document.body.innerHTML|document.body.outerHTML|document.forms\\[0\\].action|document.attachEvent|document.create|document.execCommand|window.attachEvent|document.referrer|document.location.href|document.location.host|document.location.hostname|document.location.replace|document.location.assign|document.URL|document.URLUnencoded|window.navigate|document.open|window.open|window.location.href|window.location.host|window.location.hotname|document.cookie|document.location.hash|onabort|onactivate|onafterprint|onafterupdate|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditfocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|ontextmenu|ontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror|onerrorupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmouseout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizeend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)",
				2});
		if (ValidateUtil.isNotBlank(this.xss)) {
			ret.add(new Object[]{this.xss, 42});
		}

		return ret;
	}

	private List<Pattern> getPatterns() {
		if (this.patterns == null) {
			List<Pattern> list = new ArrayList<>();
			String regex = null;
			Integer flag = null;
			int arrLength = 0;
			Iterator<Object[]> var5 = this.getXssPatternList().iterator();

			while (var5.hasNext()) {
				Object[] arr = (Object[]) var5.next();
				arrLength = arr.length;

				for (int i = 0; i < arrLength; ++i) {
					regex = (String) arr[0];
					flag = (Integer) arr[1];
					list.add(Pattern.compile(regex, flag));
				}
			}

			this.patterns = list;
		}

		return this.patterns;
	}

	private boolean isPatternExists(String value) {
		if (ValidateUtil.isNotBlank(value)) {
			Matcher matcher = null;
			Iterator<Pattern> var3 = this.getPatterns().iterator();

			while (var3.hasNext()) {
				Pattern pattern = (Pattern) var3.next();
				matcher = pattern.matcher(value);
				if (matcher.find()) {
					return true;
				}
			}
		}

		return false;
	}

	public void init(FilterConfig config) throws ServletException {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			StringBuffer currentURL = httpRequest.getRequestURL();
			if (logger.isDebugEnabled()) {
				logger.debug("请求URL：" + currentURL);
			}

			if (!"1".equals(enabled) || this.isWhiteURL(currentURL.toString())) {
				if (logger.isDebugEnabled()) {
					logger.debug("请求未进行安全过滤：" + currentURL);
				}

				chain.doFilter(httpRequest, httpResponse);
			} else {
				/**
				 * 信任域检查
				 */
				if ("1".equals(refererEnabled)) {
					if (!this.isRequestFefererTrusted(httpRequest)) {
						httpResponse.sendError(403);
						return;
					}
				}

				/**
				 * XSS注入检查
				 */
				if ("1".equals(xssEnabled)) {
					if (this.isSpecialCharactersInRequest(httpRequest)) {
						httpResponse.sendError(403);
						return;
					}
				}
				
				/**
				 * SQL注入检查
				 */
				if ("1".equals(sqlInjectEnabled)) {
					if (this.isSqlCharactersInRequest(httpRequest)) {
						httpResponse.sendError(403);
						return;
					}
				}

				/**
				 * HTTP请求动词检查
				 */
				if ("1".equals(httpMethodEnabled)) {
					if (!this.isHttpMethodAllowed(httpRequest)) {
						httpResponse.sendError(403);
						return;
					}
				}

				chain.doFilter(httpRequest, httpResponse);
			}
		}
	}

	private boolean isSpecialCharactersInRequest(HttpServletRequest request) {
		Map<String, String[]> paramsMap = request.getParameterMap();
		StringBuffer val = new StringBuffer();
		if (null != paramsMap && paramsMap.size() > 0) {
			Iterator<Entry<String, String[]>> var4 = paramsMap.entrySet().iterator();

			label38 : while (true) {
				Entry<String, String[]> paramEntry;
				do {
					if (!var4.hasNext()) {
						break label38;
					}

					paramEntry = (Entry<String, String[]>) var4.next();
				} while (null == paramEntry.getValue());

				String[] var6 = (String[]) paramEntry.getValue();
				int var7 = var6.length;

				for (int var8 = 0; var8 < var7; ++var8) {
					String paramValue = var6[var8];
					if (this.isPatternExists(paramValue)) {
						val.append(",").append(paramValue);
					}
				}
			}
		}

		if (ValidateUtil.isNotBlank(val.toString())) {
			if (logger.isInfoEnabled()) {
				logger.info("请求中含有非法字符：" + val);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean isRequestFefererTrusted(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		if (ValidateUtil.isNotBlank(this.trustedReferers)
				&& ValidateUtil.isNotBlank(referer)) {
			String[] refererArray = this.trustedReferers.split("\\|");
			String[] var4 = refererArray;
			int var5 = refererArray.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				String trustedReferer = var4[var6];
				if (referer.startsWith("http://" + trustedReferer)
						|| referer.startsWith("https://" + trustedReferer)) {
					return true;
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("请求来源域不被信任： " + referer);
			}

			return false;
		} else {
			return true;
		}
	}

	private boolean isHttpMethodAllowed(HttpServletRequest request) {
		String method = request.getMethod();
		return "GET".equals(method) || "POST".equals(method)
				|| "HEAD".equals(method);
	}

	private boolean isSqlCharactersInRequest(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		Enumeration<?> params = req.getParameterNames();
		StringBuffer sql = new StringBuffer();

		while (params.hasMoreElements()) {
			String name = params.nextElement().toString();
			String[] value = req.getParameterValues(name);

			for (int i = 0; i < value.length; ++i) {
				sql.append(value[i]);
			}
		}

		if (this.sqlValidate(sql.toString())) {
			if (logger.isInfoEnabled()) {
				logger.info("请求中含有非法sql字符：" + sql);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean sqlValidate(String str) {
		str = str.toLowerCase();
		if (ValidateUtil.isNotBlank(this.sql)) {
			String[] badStrs = this.sql.split("\\|");

			for (int i = 0; i < badStrs.length; ++i) {
				if (str.indexOf(badStrs[i]) >= 0) {
					return true;
				}
			}
		}

		return false;
	}

	public void destroy() {
		
	}

	private boolean isWhiteURL(String currentURL) {
		String[] whiteURLs = this.whiteListURLs.split(";");
		if (ValidateUtil.isBlank(this.whiteListURLs) || whiteURLs.length <= 0) {
			return false;
		} else {
			String[] var3 = whiteURLs;
			int var4 = whiteURLs.length;

			for (int var5 = 0; var5 < var4; ++var5) {
				String s = var3[var5];
				if (currentURL.indexOf(s) > -1) {
					return true;
				}
			}

			return false;
		}
	}
}