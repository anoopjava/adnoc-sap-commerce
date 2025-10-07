package com.adnoc.controllers;

import com.adnoc.facades.product.AdnocProductFacade;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Tag(name = "Products")
@RequestMapping(value = "/{baseSiteId}/products")
public class AdnocProductsController extends AdnocBaseController
{
    @Resource(name = "adnocProductFacade")
    private AdnocProductFacade adnocProductFacade;

    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP})
    @Operation(operationId = "getProductDocumentAttachment", summary = "Retrieves the attachment of a Product document.", description = "Retrieves the attachment associated with a given product document for the given attachment identifier.")
    @GetMapping(value = "/{productCode}/documentAttachment", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<byte[]> getProductDocumentAttachment(
            @Parameter(description = "Product code.", example = "PROD1234", required = true)
            @PathVariable final String productCode) throws NotFoundException
    {

        final AttachmentData attachmentData = adnocProductFacade.getAttachmentForProduct(productCode);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, attachmentData.getFileType());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(attachmentData.getFileName()));
        headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION);
        return ResponseEntity.ok().headers(headers).body(attachmentData.getFileContent().getByteArray());
    }
}
