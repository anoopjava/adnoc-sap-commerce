/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.integration.constants;

/**
 * Global class for all Adnocintegration constants. You can add global constants for your extension into this class.
 */
public final class AdnocintegrationConstants extends GeneratedAdnocintegrationConstants
{
	public static final String EXTENSIONNAME = "adnocintegration";
	public static final String AUTHORIZATION_SCOPE_PROPERTY = EXTENSIONNAME + ".oauth.scope";
	public static final String LICENSE_URL_PROPERTY = EXTENSIONNAME + ".license.url";
	public static final String TERMS_OF_SERVICE_URL_PROPERTY = EXTENSIONNAME + ".terms.of.service.url";
	public static final String LICENSE_PROPERTY = EXTENSIONNAME + ".licence";
	public static final String DOCUMENTATION_DESC_PROPERTY = EXTENSIONNAME + ".documentation.desc";
	public static final String DOCUMENTATION_TITLE_PROPERTY = EXTENSIONNAME + ".documentation.title";
	public static final String API_VERSION = "1.0.0";

	public static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";

	public static final String SAMPLE_MAP_STRING_KEY = "StringKey";
	public static final String SAMPLE_MAP_STRING_VALUE = "StringValue";
	public static final String SAMPLE_MAP_INTEGER_KEY = "integerKey";
	public static final int SAMPLE_MAP_INTEGER_VALUE = 10001;

	public static final String SAMPLE_LIST_STRING_VALUE = "new String";
	public static final double SAMPLE_LIST_DOUBLE_VALUE = 0.123d;

	public static final String HOST = "adnocintegration.host";
	public static final String HOST_DEFAULT = "hostname";

	private AdnocintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
