package cn.imtiger.entity;

import com.alibaba.fastjson.JSONObject;

public class CommonResponse {
	private Integer code;
	private String message;
	private Object content;

	public CommonResponse(Integer code, String message, Object content) {
		this.code = code;
		this.message = message;
		this.content = content;
	}
	
	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		object.put("code", this.code);
		object.put("message", this.message);
		object.put("content", this.content);
		return object.toJSONString();
	}

	protected Integer getCode() {
		return code;
	}

	protected void setCode(Integer code) {
		this.code = code;
	}

	protected String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
}
