package cn.imtiger.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.imtiger.constant.HttpConst;
import cn.imtiger.constant.MessageConst;
import cn.imtiger.constant.StatusConst;
import cn.imtiger.util.data.ValidateUtil;
import cn.imtiger.util.net.TokenUtil;

/**
 * 基本视图控制器
 * @author shen_hongtai
 * @date 2019-9-18
 */
public abstract class BaseController {
	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private HttpServletRequest request;
    
    @Value("${imtiger.app.name:'TigerWebApplication'}")
    private String appName;
    
    @Value("${imtiger.app.desc:'Powered by IMTIGER.CN'}")
    private String appDesc;
    
    @Value("${imtiger.app.keywords:''}")
    private String keywords;
    
    @Value("${imtiger.app.version:'1.0.0'}")
    private String version; 
    
    @Value("${imtiger.access.sessionKey:''}")
    private String sessionKey;

	@Value("${imtiger.security.csrfEnabled:1}")
	private String csrfEnabled;

	public void checkCSRFToken(HttpServletRequest request, HttpServletResponse response) {
		boolean flag = TokenUtil.checkCrsfTokenValidate(request);
		if (!flag) {
			try {
				response.sendError(403);
			} catch (Exception var5) {
				logger.error(var5.getMessage());
			}
		}
	}
    
	public String getView(String viewPath, LinkedHashMap<String, Object> model) {
		model.put("base", request.getContextPath());
		model.put("csrf", TokenUtil.getTokenForRequest(request));
		model.put("appName", appName);
		model.put("appDesc", appDesc);
		model.put("keywords", keywords);
		model.put("version", version);
		return viewPath;
	}

	public JSONObject getCurrentUser() {
		HttpSession session = this.request.getSession();
		JSONObject jsonUser = null;
		if (ValidateUtil.isNotBlank(sessionKey) && session != null) {
			Object object = session.getAttribute(sessionKey);
			if (object != null) {
				jsonUser = (JSONObject) JSONObject.toJSON(object);	
			}
		}
		return jsonUser;
	}
	
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
	
	public String getString(String name) {
		return request.getParameter(name);
	}
	
	public Long getLong(String name) {
		return Long.parseLong(getString(name));
	}
	
	public Integer getInteger(String name) {
		return Integer.parseInt(getString(name));
	}
	
	public Short getShort(String name) {
		return Short.parseShort(getString(name));
	}
	
	public Byte getByte(String name) {
		return Byte.parseByte(getString(name));
	}
	
	public Double getDouble(String name) {
		return Double.parseDouble(getString(name));
	}
	
	public Float getFloat(String name) {
		return Float.parseFloat(getString(name));
	}
	
	public String getResponse(Object data, boolean success) {
		JSONObject json = new JSONObject();
		int count = this.getDataCount(data);
		if (success) {
			if (count != 0) {
				json.put("data", data);
				json.put("total", count);
			}
			json.put("code", StatusConst.SUCCESS);
			json.put("message", MessageConst.SUCCESS);
		} else {
			json.put("code", StatusConst.FAILURE);
			json.put("message", MessageConst.FAILURE);
		}
		return json.toJSONString();
	}
	
	public String getPaginateResponse(Object data, Long pageNum, Long pageSize, boolean success) {
		JSONObject json = new JSONObject();
		int count = this.getDataCount(data);
		if (success) {
			if (count != 0) {
				json.put("data", data);
				json.put("total", count);
			}
			json.put("pageNum", pageNum);
			json.put("pageSize", pageSize);
			json.put("code", StatusConst.SUCCESS);
			json.put("message", MessageConst.SUCCESS);
		} else {
			json.put("code", StatusConst.FAILURE);
			json.put("message", MessageConst.FAILURE);
		}
		return json.toJSONString();
	}
	
	@SuppressWarnings("rawtypes")
	public Integer getDataCount(Object data) {
		if (data != null) {
			if (data instanceof List) {
				return ((List) data).size();
			} else if (data instanceof JSONArray) {
				return ((JSONArray) data).size();
			} else if (data instanceof Map || data instanceof JSONObject) {
				return 1;
			}
		}
		return 0;
	}
	
	public void writeFile(HttpServletResponse response, File file, String fileName) {
		if (file != null && file.isFile() && file.length() <= 2147483647L) {
			try {
				response.addHeader("Content-disposition", "attachment; filename=" 
						+ new String(file.getName().getBytes("GBK"), "ISO8859-1"));
			} catch (UnsupportedEncodingException var25) {
				response.addHeader("Content-disposition", "attachment; filename=" + file.getName());
			}

			String contentType = this.request.getServletContext().getMimeType(
					file.getName());
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			response.setContentType(contentType);
			response.setContentLength((int) file.length());
			InputStream inputStream = null;
			ServletOutputStream outputStream = null;

			try {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				outputStream = response.getOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, n);
				}

				outputStream.flush();
			} catch (FileNotFoundException var26) {
				var26.printStackTrace();
			} catch (IOException var27) {
				var27.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException var24) {
						var24.printStackTrace();
					}
				}

				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException var23) {
						var23.printStackTrace();
					}
				}
			}
		}
	}

	public void writeText(HttpServletResponse response, String text) {
		this.write(response, HttpConst.TEXT, text);
	}

	public void writeJSON(HttpServletResponse response, String text) {
		this.write(response, HttpConst.JSON, text);
	}

	public void writeXML(HttpServletResponse response, String text) {
		this.write(response, HttpConst.XML, text);
	}

	public void writeHTML(HttpServletResponse response, String text) {
		this.write(response, HttpConst.HTML, text);
	}

	private void write(HttpServletResponse response, String contentType, String text) {
		response.setContentType(contentType);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);

		try {
			response.getWriter().write(text);
		} catch (IOException var5) {
			logger.error(var5.getMessage(), var5);
		}
	}
}
