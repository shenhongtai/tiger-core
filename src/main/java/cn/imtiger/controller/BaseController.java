package cn.imtiger.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.imtiger.constant.MessageConst;
import cn.imtiger.constant.StatusConst;
import cn.imtiger.util.data.ValidateUtil;
import cn.imtiger.util.net.TokenUtil;
import nl.bitwalker.useragentutils.UserAgent;

/**
 * 公共控制器
 * @author shen_hongtai
 * @date 2020-4-20
 */
public abstract class BaseController {
	/**
	 * 日志打印对象
	 */
	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

	/**
	 * 以注入方式获取HTTP请求对象
	 */
    @Autowired
    private HttpServletRequest request;
    
    /**
     * 以注入方式获取HTTP响应对象
     */
    @Autowired
    private HttpServletResponse response;
    
    /**
     * 读取application配置文件里的应用名称
     */
    @Value("${app.name:}")
    private String appName;
    
    /**
     * 读取application配置文件里的应用描述
     */
    @Value("${app.desc:}")
    private String appDesc;
    
    /**
     * 读取application配置文件里的应用版本号，默认值为1.0.0
     */
    @Value("${app.version:1.0.0}")
    private String version; 

    /**
     * 读取application配置文件里的session名
     */
    @Value("${app.sessionKey:}")
    private String sessionKey;
    
    /**
     * 读取application配置文件里的ICP备案号
     */
    @Value("${app.icp:}")
    private String icp;

    /**
     * 读取application配置文件里的ICP查询网址
     */
    @Value("${app.icpUrl:}")
    private String icpUrl;
    
    /**
     * 获取用户设备类型
     * @return
     */
    public String getUserDevice() {
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getOperatingSystem().getDeviceType().getName();
	}
    
    /**
     * 获取用户操作系统类型
     * @return
     */
    public String getUserOperatingSystem() {
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getOperatingSystem().getName();
	}
    
    /**
     * 获取用户浏览器类型
     * @return
     */
    public String getUserBrowser() {
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser().getName();
	}
    
    /**
     * 获取用户的IP地址
     * @return
     */
	public String getIPAddress() {
    	String ip = request.getHeader("x-real-ip");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("x-forwarded-for");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		return ip;
	}
    
    /**
     * 获取用户的端口号
     * @return
     */
    public String getRequestPort() {
		return request.getLocalPort() + "";
	}

	/**
	 * 校验用户访问权限
	 * @param viewPath
	 */
	public void checkUserAuthority(String viewPath) {
		// 权限标识
		boolean flag = false;
		// 获取当前登录用户信息
		JSONObject user = this.getCurrentUser();
		if (user != null) {
			if (user.get("TYPE") != null) {
				if ("1".equals(user.get("ID"))) {
					// 用户ID是1，为超级管理员，所有页面都可访问
					flag = true;
				} else if ("1".equals(user.get("TYPE"))) {
					// 用户类型是1（社会用户），访问的页面是client开头的，也就是手机端页面，标识为true（可以访问）
					if (viewPath.startsWith("client")) {
						flag = true;
					}
				} else if ("2".equals(user.get("TYPE")) || "3".equals(user.get("TYPE"))) {
					// 用户类型是2或3（平台管理和停车场管理），访问的是manage开头的，也就是管理端页面，标识为true
					if (viewPath.startsWith("manage")) {
						flag = true;
					}
				}
			}
		}
		
		// 如果flag没有改成true，表示没有权限，返回401错误
		if (flag == false) {
			try {
				response.sendError(401);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
    
	/**
	 * 根据页面地址获取页面视图的公共方法
	 * @param viewPath
	 * @param model
	 * @return
	 */
	public String getView(String viewPath, LinkedHashMap<String, Object> model) {
		// 如果访问的页面不是login（登录）或reg（注册），判断一下是否有权限访问
		if (!"login".equals(viewPath) && !"reg".equals(viewPath)) {
			this.checkUserAuthority(viewPath);
		}
		
		// 如果可以访问，向页面返回系统路径、token、应用名称、描述、版本等信息，用于页面显示和请求
		model.put("base", request.getContextPath());
		model.put("appName", appName);
		model.put("appDesc", appDesc);
		model.put("version", version);
		model.put("icp", icp);
		model.put("icpUrl", icpUrl);
		return viewPath;
	}

	/**
	 * 获取当前登录用户的公共方法
	 * @return
	 */
	public JSONObject getCurrentUser() {
		// 获取请求里的session
		HttpSession session = this.request.getSession();
		JSONObject jsonUser = null;
		if (ValidateUtil.isNotBlank(sessionKey) && session != null) {
			// 如果session不为空，说明是登录状态，根据配置文件里的session名取值
			Object object = session.getAttribute(sessionKey);
			// 如果值不为空，取出值转换为JSON格式对象，返回给调用方
			if (object != null) {
				jsonUser = JSONObject.parseObject((String) object);	
			}
		}
		return jsonUser;
	}
	
	/**
	 * 获取所有请求参数的Map集合
	 * @param req
	 * @return
	 */
	public Map<String, Object> getParameters(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<>();
		Enumeration<String> parameterNames = req.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String key = (String) parameterNames.nextElement();
			String value = req.getParameter(key);
			map.put(key, value);
		}
		return map;
	}
	
	/**
	 * 获取字符串参数
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return request.getParameter(name);
	}
	
	/**
	 * 获取参数并转换为Long类型
	 * @param name
	 * @return
	 */
	public Long getLong(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Long.parseLong(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 获取参数并转换为Integer类型
	 * @param name
	 * @return
	 */
	public Integer getInteger(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Integer.parseInt(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 获取参数并转换为Short类型
	 * @param name
	 * @return
	 */
	public Short getShort(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Short.parseShort(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 获取参数并转换为Byte类型
	 * @param name
	 * @return
	 */
	public Byte getByte(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Byte.parseByte(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 获取参数并转换为Double类型
	 * @param name
	 * @return
	 */
	public Double getDouble(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Double.parseDouble(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 获取参数并转换为Float类型
	 * @param name
	 * @return
	 */
	public Float getFloat(String name) {
		if (ValidateUtil.isNotBlank(name) && ValidateUtil.isNotBlank(getString(name))) {
			return Float.parseFloat(getString(name));
		} else {
			return null;
		}
	}
	
	/**
	 * 封装响应值（可以自定义错误信息，且返回数据）
	 * @param data
	 * @param success
	 * @param errorMessage
	 * @return
	 */
	public String getResponse(Object data, boolean success, String errorMessage) {
		JSONObject json = new JSONObject();
		int count = this.getDataCount(data);
		if (success) {
			// 请求成功，往响应内容里添加数据、行数、成功的代码和信息
			json.put("data", data);
			json.put("total", count);
			json.put("code", StatusConst.SUCCESS);
			json.put("message", MessageConst.SUCCESS);
		} else {
			// 请求失败，添加失败代码
			json.put("code", StatusConst.FAILURE);
			if (ValidateUtil.isNotBlank(errorMessage)) {
				// 如果给出了自定义的错误提示，加进去
				json.put("message", errorMessage);
			} else {
				// 如果没传，用默认的错误信息
				json.put("message", MessageConst.FAILURE);
			}
		}
		return json.toJSONString();
	}
	
	/**
	 * 封装响应值（使用默认错误信息，并返回数据）
	 * @param data
	 * @param success
	 * @return
	 */
	public String getResponse(Object data, boolean success) {
		return getResponse(data, success, null);
	}
	
	/**
	 * 封装响应值（可以自定义错误信息，不返回数据）
	 * @param success
	 * @param errorMessage
	 * @return
	 */
	public String getResponse(boolean success, String errorMessage) {
		return getResponse(null, success, errorMessage);
	}
	
	/**
	 * 封装响应值（使用默认错误信息，且不返回数据）
	 * @param success
	 * @return
	 */
	public String getResponse(boolean success) {
		return getResponse(null, success, null);
	}
	
	/**
	 *  封装响应值（分页，有返回数据，有错误信息）
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @param total
	 * @param success
	 * @param errorMessage
	 * @return
	 */
	public String getPaginateResponse(Object data, int pageNum, int pageSize, int total, boolean success, String errorMessage) {
		JSONObject json = new JSONObject();
		if (success) {
			json.put("total", total);
			json.put("data", data);
			json.put("pageNum", pageNum);
			json.put("pageSize", pageSize);
			json.put("code", StatusConst.SUCCESS);
			json.put("message", MessageConst.SUCCESS);
		} else {
			json.put("code", StatusConst.FAILURE);
			json.put("message", MessageConst.FAILURE + "，" + errorMessage);
		}
		return json.toJSONString();
	}
	
	/**
	 * 计算数据的条数
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Integer getDataCount(Object data) {
		if (data != null) {
			if (data instanceof List) {
				// 如果是list类型，获取list的大小作为条数
				return ((List) data).size();
			} else if (data instanceof JSONArray) {
				// 如果是JSONArray，获取数组的大小作为条数
				return ((JSONArray) data).size();
			} else if (data instanceof Map || data instanceof JSONObject) {
				// 如果是Map或者JSONObject，直接返回1条
				return 1;
			}
		}
		// 如果数据为空，返回0条
		return 0;
	}
}
