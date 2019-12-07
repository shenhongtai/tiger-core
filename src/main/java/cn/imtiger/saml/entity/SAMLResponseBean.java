package cn.imtiger.saml.entity;

import java.util.Date;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.SubjectConfirmation;

import com.alibaba.fastjson.JSON;

/**
 * SAML2.0响应解析结果对象
 * @author shen_hongtai
 * @date 2019-12-5
 */
public class SAMLResponseBean {
	String id;
	String inResponseTo;
	String assertionId;
	String userId;
	Date issueInstant;
	Date notOnOrAfter;

	public SAMLResponseBean(Response response) {
		Assertion assertion = response.getAssertions().get(0);
		AuthnStatement authnStatement = assertion.getAuthnStatements().get(0);
		SubjectConfirmation subjectConfirmation = assertion.getSubject()
				.getSubjectConfirmations().get(0);

		this.id = response.getID();
		this.inResponseTo = response.getInResponseTo();
		this.assertionId = assertion.getID();
		this.userId = assertion.getSubject().getNameID().getValue();
		this.issueInstant = new Date(authnStatement.getAuthnInstant()
				.getMillis());
		this.notOnOrAfter = new Date(subjectConfirmation
				.getSubjectConfirmationData().getNotOnOrAfter().getMillis());
	}

	/**
	 * 获取SAML请求ID
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取返回地址
	 * @return
	 */
	public String getInResponseTo() {
		return inResponseTo;
	}

	/**
	 * 获取断言ID
	 * @return
	 */
	public String getAssertionId() {
		return assertionId;
	}

	/**
	 * 获取登录用户ID
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取SAML响应时间
	 * @return
	 */
	public Date getIssueInstant() {
		return issueInstant;
	}

	/**
	 * 获取SAML响应过期时间
	 * @return
	 */
	public Date getNotOnOrAfter() {
		return notOnOrAfter;
	}

	@Override
	public String toString() {
		return "[SAMLResponseInfo]" + JSON.toJSONString(this);
	}

}
