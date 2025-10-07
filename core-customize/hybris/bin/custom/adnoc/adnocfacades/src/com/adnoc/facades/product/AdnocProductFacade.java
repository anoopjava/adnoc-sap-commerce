package com.adnoc.facades.product;

import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import de.hybris.platform.commercefacades.product.ProductFacade;

/**
 * The interface Adnoc product facade.
 */
public interface AdnocProductFacade extends ProductFacade
{
    /**
     * Gets attachment for product.
     *
     * @param productCode the product code
     * @return the attachment for product
     */
    AttachmentData getAttachmentForProduct(String productCode);
}
