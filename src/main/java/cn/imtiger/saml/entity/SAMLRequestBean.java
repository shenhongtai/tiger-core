package cn.imtiger.saml.entity;

import org.opensaml.saml2.core.AuthnRequest;

import com.alibaba.fastjson.JSON;

/**
 * SAML2.0请求解析结果对象
 * @author shen_hongtai
 * @date 2019-12-5
 */
public class SAMLRequestBean {
	String id;
	String version;
	String provider;
	String destination;
	String audience;

	public SAMLRequestBean(AuthnRequest authnRequest) {
		this.id = authnRequest.getID();
		this.version = authnRequest.getVersion().toString();
		this.provider = authnRequest.getProviderName();
		this.destination = authnRequest.getAssertionConsumerServiceURL();
		this.audience = authnRequest.getIssuer().getValue();
	}

	/**
	 * 获取SAML请求ID
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取SAML请求版本
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 获取登录服务提供方名称
	 * @return
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * 获取客户端回调地址
	 * @return
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * 获取客户端域名
	 * @return
	 */
	public String getAudience() {
		return audience;
	}

	@Override
	public String toString() {
		return "[SAMLRequestInfo]" + JSON.toJSONString(this);
	}

}
