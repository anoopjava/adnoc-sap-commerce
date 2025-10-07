/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCartFacadeTest
{
	@InjectMocks
	private AdnocCartFacadeImpl adnocCartFacadeImpl;

	@Mock
	private AbstractPopulatingConverter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
	@Mock
	private ConfigurationService configurationService;

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		final AddToCartParams addToCartParams = new AddToCartParams();
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		Mockito.when(commerceCartParameterConverter.convert(addToCartParams)).thenReturn(commerceCartParameter);

		final CommerceCartModification commerceCartModification = new CommerceCartModification();
		Mockito.when(commerceCartService.addToCart(commerceCartParameter)).thenReturn(commerceCartModification);

		final CartModificationData cartModificationData = new CartModificationData();
		Mockito.when(cartModificationConverter.convert(commerceCartModification)).thenReturn(cartModificationData);

		final Configuration configuration = Mockito.mock(Configuration.class);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);

		final CartModificationData cartModificationDataRes = adnocCartFacadeImpl.addToCart(addToCartParams);
		Assertions.assertThat(cartModificationDataRes).isEqualTo(cartModificationData);
		Assertions.assertThat(commerceCartParameter.isCreateNewEntry()).isTrue();
	}
}
