/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Velocity context for a forgotten password email.
 */
public class ForgottenPasswordEmailContext extends CustomerEmailContext
{
	private int expiresInMinutes = 30;
	private String token;
	private String frontendUrl;

	public int getExpiresInMinutes()
	{
		return expiresInMinutes;
	}

	public void setExpiresInMinutes(final int expiresInMinutes)
	{
		this.expiresInMinutes = expiresInMinutes;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(final String token)
	{
		this.token = token;
	}

	public String getURLEncodedToken() throws UnsupportedEncodingException
	{
		return URLEncoder.encode(token, "UTF-8");
	}

	public String getRequestResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/login/pw/request/external";
	}

	public String getSecureRequestResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/login/pw/request/external";
	}

	public String getResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/login/pw/change?token=" + getURLEncodedToken();
	}

	public String getSecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/login/pw/change?token=" + getURLEncodedToken();
	}

	public String getDisplayResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/my-account/update-password";
	}

	public String getDisplaySecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getFrontendUrl() + "/my-account/update-password";
	}

	@Override
	public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcessModel, emailPageModel);
		if (storeFrontCustomerProcessModel instanceof ForgottenPasswordProcessModel)
		{
			setToken(((ForgottenPasswordProcessModel) storeFrontCustomerProcessModel).getToken());
		}
	}

	protected String getFrontendUrl()
	{
		return frontendUrl;
	}

	public void setFrontendUrl(final String frontendUrl)
	{
		this.frontendUrl = frontendUrl;
	}
}
