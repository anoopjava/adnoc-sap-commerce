/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades.suggestion;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;
import java.util.Set;


/**
 * Facade to provide simple suggestions for a customer.
 */
public interface SimpleSuggestionFacade
{
	/**
	 * Returns a list of referenced products for a product purchased in a category identified by categoryCode.
	 *
	 * @param categoryCode
	 * @param referenceType    referenceType, can be null
	 * @param excludePurchased if true, only retrieve products that were not yet bought by the user
	 * @param limit            if not null: limit the amount of returned products to the given number
	 * @return a list with referenced products
	 * @deprecated Since 5.0. Use getReferencesForPurchasedInCategory(String categoryCode, List
	 * <ProductReferenceTypeEnum> referenceTypes, boolean excludePurchased, Integer limit) instead.
	 */
	@Deprecated(since = "5.0", forRemoval = true)
	List<ProductData> getReferencesForPurchasedInCategory(String categoryCode, ProductReferenceTypeEnum referenceType,
														  boolean excludePurchased, Integer limit);

	/**
	 * Returns a list of referenced products for a product purchased in a category identified by categoryCode.
	 *
	 * @param categoryCode     the category code
	 * @param referenceTypes   referenceType, can be empty
	 * @param excludePurchased if true, only retrieve products that were not yet bought by the user
	 * @param limit            if not null: limit the amount of returned products to the given number
	 * @return a list with referenced products
	 */
	List<ProductData> getReferencesForPurchasedInCategory(String categoryCode, List<ProductReferenceTypeEnum> referenceTypes,
														  boolean excludePurchased, Integer limit);

	/**
	 * Returns a list of referenced products for a set of products
	 *
	 * @param productCodes     product codes
	 * @param referenceTypes   referenceType, can be empty
	 * @param excludePurchased if true, only retrieve products that were not yet bought by the user
	 * @param limit            if not null: limit the amount of returned products to the given number
	 * @return a list with referenced products
	 */
	List<ProductData> getReferencesForProducts(Set<String> productCodes, List<ProductReferenceTypeEnum> referenceTypes,
											   boolean excludePurchased, Integer limit);

	/**
	 * Returns a list of products that are suggested based on the products in the cart.
	 *
	 * @param referenceTypes   referenceType, can be empty
	 * @param excludePurchased if true, only retrieve products that were not yet bought by the user
	 * @param limit            if not null: limit the amount of returned products to the given number
	 * @return a list with suggested products based on the cart contents
	 */
	List<ProductData> getSuggestionsForProductsInCart(List<ProductReferenceTypeEnum> referenceTypes, boolean excludePurchased,
													  Integer limit);

}
