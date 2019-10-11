package cn.imtiger.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import cn.imtiger.util.net.TokenUtil;

/**
 * 基本视图控制器
 * @author shen_hongtai
 * @date 2019-9-18
 */
public abstract class BaseController {
	public String getView(HttpServletRequest request, String viewPath, LinkedHashMap<String, Object> model) {
		model.put("base", request.getContextPath());
		model.put("csrf", TokenUtil.getTokenForRequest(request));
		return viewPath;
	}
}
