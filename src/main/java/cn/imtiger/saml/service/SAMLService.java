package cn.imtiger.saml.service;

import cn.imtiger.saml.entity.SAMLRequestBean;
import cn.imtiger.saml.entity.SAMLResponseBean;

/**
 * SAML2.0服务
 * @author shen_hongtai
 * @date 2019-11-29
 */
public interface SAMLService {
	
	/**
	 * 构建SAML请求报文
	 * @param localDomain 客户端地址
	 * @param destination 服务端登录地址
	 * @param assertionConsumerServiceURL 客户端回调地址
	 * @return
	 */
	public String buildSAMLRequest(String localDomain, String destination, String assertionConsumerServiceURL);
	
	/**
	 * 构建SAML响应报文
	 * @param requestId SAML请求ID authnRequest.getID()
	 * @param userId 登录用户ID
	 * @param destination 客户端回调地址 authnRequest.getAssertionConsumerServiceURL()
	 * @param audience authnRequest.getIssuer().getValue();
	 * @param localDomain 服务端地址
	 * @return
	 */
	public String buildSAMLResponse(String requestId, String userId, String destination, String audience, String localDomain);

	/**
	 * 解析SAML请求报文
	 * @param encodedRequest SAML请求报文
	 * @return
	 */
	public SAMLRequestBean resolveSAMLRequest(String encodedRequest);
	
	/**
	 * 解析SAML返回报文
	 * @param encodedResponse SAML返回报文
	 * @return
	 */
	public SAMLResponseBean resolveSAMLResponse(String encodedResponse) throws Exception;
}
