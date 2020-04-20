package cn.imtiger.saml.service.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.imtiger.saml.entity.SAMLRequestBean;
import cn.imtiger.saml.entity.SAMLResponseBean;
import cn.imtiger.saml.service.SAMLService;
import cn.imtiger.saml.util.SAMLUtil;
import cn.imtiger.util.data.StringUtil;

/**
 * SAML2.0服务
 * @author shen_hongtai
 * @date 2019-11-29
 */
@Service("samlService")
public class SAMLServiceImpl implements SAMLService {
	
	private Logger logger = LoggerFactory.getLogger(SAMLServiceImpl.class);
	
	protected static final XMLObjectBuilderFactory builderFactory;
	
	private static final String AUTHN_REQUEST_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:obtained";

	private static final String PROVIDER_NAME = "DSP-IDP-Provider";

	private static final String NAMEID_FORMAT_ENTITY = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";

	static {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Security.addProvider(new BouncyCastleProvider());
		builderFactory = Configuration.getBuilderFactory();
	}
	
	@Override
	public String buildSAMLRequest(String localDomain, String destination, String assertionConsumerServiceURL) {
		NameIDPolicy nameIDPolicy = (NameIDPolicy) buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
		nameIDPolicy.setAllowCreate(true);
		nameIDPolicy.setFormat(NameID.PERSISTENT);
		
		AuthnContextClassRef classRef = (AuthnContextClassRef) buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		classRef.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);
		RequestedAuthnContext rac = (RequestedAuthnContext) buildXMLObject(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
		rac.getAuthnContextClassRefs().add(classRef);

		Issuer rIssuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
		rIssuer.setFormat(NAMEID_FORMAT_ENTITY);
		rIssuer.setValue(localDomain);
		
		AuthnRequest authnRequest = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
		authnRequest.setID("_" + StringUtil.createUUID());
		authnRequest.setVersion(SAMLVersion.VERSION_20);
		authnRequest.setProviderName(PROVIDER_NAME);
		authnRequest.setAssertionConsumerServiceURL(assertionConsumerServiceURL);
		authnRequest.setAttributeConsumingServiceIndex(0);
		authnRequest.setNameIDPolicy(nameIDPolicy);
		authnRequest.setRequestedAuthnContext(rac);
		authnRequest.setForceAuthn(false);
		authnRequest.setIssueInstant(SAMLUtil.getIssueInstant());
		authnRequest.setDestination(destination);
		authnRequest.setConsent(AUTHN_REQUEST_CONSENT);
		authnRequest.setIssuer(rIssuer);
		authnRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		
		String samlRequest = buildXMLObjectToString(authnRequest);
		String encodedRequest = buildXMLObjectToBase64String(authnRequest);
		if (logger.isDebugEnabled()) {
			logger.debug("SAMLRequest=" + samlRequest);
			logger.debug("SAMLRequestBase64=" + encodedRequest);
		}
		return encodedRequest;
	}
	
	@Override
	public SAMLRequestBean resolveSAMLRequest(String encodedRequest) {
		SAMLRequestBean samlRequestInfo = null;
		if (logger.isDebugEnabled()) {
			logger.debug("resolveSAMLRequestBase64=" + encodedRequest);
		}
		if (StringUtils.isNotBlank(encodedRequest)) {
			AuthnRequest authnRequest = (AuthnRequest) buildBase64StringToXMLObject(encodedRequest);
			samlRequestInfo = new SAMLRequestBean(authnRequest);
			if (logger.isDebugEnabled()) {
				logger.debug("samlRequestInfo=" + samlRequestInfo.toString());
			}
		}
		return samlRequestInfo;
	}
	
	@Override
	public String buildSAMLResponse(String requestId, String userId, String destination, String audience, String localDomain) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");
		Calendar c = Calendar.getInstance();
		String issueInstant = sdf.format(c.getTime()).replace("#", "T").concat("Z");
		c.add(Calendar.MINUTE, 5);
		String notOnOrAfter = sdf.format(c.getTime()).replace("#", "T").concat("Z");
		String assertionId = StringUtil.createUUID();
		String sessionIndex = StringUtil.createUUID();
		
		StringBuffer samlResponse = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
				.append("<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"").append(destination).append("\" IssueInstant=\"").append(issueInstant).append("\" ID=\"").append(requestId).append("\" Version=\"2.0\">")
				.append("<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"").append(assertionId).append("\" IssueInstant=\"").append(issueInstant).append("\" Version=\"2.0\">")
				.append("<saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\"></saml:Issuer>")
				.append("<saml:Subject>")
				.append("<saml:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">").append(userId).append("</saml:NameID>")
				.append("<saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">")
				.append("<saml:SubjectConfirmationData Address=\"").append(localDomain).append("\" NotOnOrAfter=\"").append(notOnOrAfter).append("\" Recipient=\"").append(destination).append("\"/>")
				.append("</saml:SubjectConfirmation>")
				.append("</saml:Subject>")
				.append("<saml:Conditions NotBefore=\"").append(issueInstant).append("\" NotOnOrAfter=\"").append(notOnOrAfter).append("\">")
				.append("<samlp:AudienceRestriction> ")
				.append("<samlp:Audience>").append(audience).append("</samlp:Audience>")
				.append("</samlp:AudienceRestriction>")
				.append("</saml:Conditions>")
				.append("<saml:AuthnStatement AuthnInstant=\"").append(issueInstant).append("\" SessionIndex=\"").append(sessionIndex).append("\" SessionNotOnOrAfter=\"").append(notOnOrAfter).append("\">")
				.append("<saml:SubjectLocality Address=\"").append(localDomain).append("\"/>")
				.append("<saml:AuthnContext>")
				.append("<saml:AuthnContextDeclRef/>")
				.append("</saml:AuthnContext>")
				.append("</saml:AuthnStatement>")
				.append("</saml:Assertion>")
				.append("<samlp:Status>")
				.append("<samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"></samlp:StatusCode>")
				.append("</samlp:Status>")
				.append("</samlp:Response>");
		
		String encodedResponse = Base64.encodeBytes(samlResponse.toString().getBytes(), Base64.DONT_BREAK_LINES);
		if (logger.isDebugEnabled()) {
			logger.debug("SAMLResponse=" + samlResponse.toString());
			logger.debug("SAMLResponseBase64=" + encodedResponse);
		}
		return encodedResponse;
	}

	@Override
	public SAMLResponseBean resolveSAMLResponse(String encodedResponse) throws Exception {
		SAMLResponseBean samlResponseInfo = null;
		if (logger.isDebugEnabled()) {
			logger.debug("resolveSAMLResponseBase64=" + encodedResponse);
		}
		if (StringUtils.isNotBlank(encodedResponse)) {
			Response response = (Response) buildBase64StringToXMLObject(encodedResponse);
			samlResponseInfo = new SAMLResponseBean(response);
			if (logger.isDebugEnabled()) {
				logger.debug("samlResponseInfo=" + samlResponseInfo.toString());
			}
			Long now = new Date().getTime();
			Long endTime = samlResponseInfo.getNotOnOrAfter().getTime();
			Long startTime = samlResponseInfo.getIssueInstant().getTime();
			if (now < startTime || now >= endTime) {
				throw new Exception("不在断言消费有效期内，请确认系统时间是否正确");
			}
		}
		return samlResponseInfo;
	}

	/**
	 * 生成XMLObject
	 * @param objectQName
	 * @return
	 */
	private XMLObject buildXMLObject(QName objectQName) {
		XMLObjectBuilder<?> builder = builderFactory.getBuilder(objectQName);
		return builder.buildObject(objectQName.getNamespaceURI(),
				objectQName.getLocalPart(), objectQName.getPrefix());
	}
	
	/**
	 * XMLObject转为字符串
	 * @param xmlObject
	 * @return
	 */
	private String buildXMLObjectToString(XMLObject xmlObject) {
		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
		Element authDOM;
		try {
			authDOM = marshaller.marshall(xmlObject);
			StringWriter rspWrt = new StringWriter();
			XMLHelper.writeNode(authDOM, rspWrt);
			String messageXML = rspWrt.toString();
			return messageXML;
		} catch (MarshallingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * XMLObject转为Base64字符串
	 * @param xmlObject
	 * @return
	 */
	private String buildXMLObjectToBase64String(XMLObject xmlObject) {
		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
		Element authDOM;
		try {
			authDOM = marshaller.marshall(xmlObject);
			StringWriter rspWrt = new StringWriter();
			XMLHelper.writeNode(authDOM, rspWrt);
			String messageXML = rspWrt.toString();
			return Base64.encodeBytes(messageXML.getBytes(), Base64.DONT_BREAK_LINES);
		} catch (MarshallingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Base64字符串转为XMLObject
	 * @param xmlObjectString
	 * @return
	 */
	private XMLObject buildBase64StringToXMLObject(String xmlObjectString) {
		try {
			BasicParserPool parser = new BasicParserPool();
			parser.setNamespaceAware(true);
			byte[] decryptArray = Base64.decode(xmlObjectString);
			String xmlString = new String(decryptArray);
			Document doc = (Document) parser.parse(new ByteArrayInputStream(
					xmlString.getBytes()));
			Element samlElement = (Element) doc.getDocumentElement();
			Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory()
					.getUnmarshaller(samlElement);
			return unmarshaller.unmarshall(samlElement);
		} catch (XMLParserException e) {
			throw new RuntimeException(e);
		} catch (UnmarshallingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
