package com.adnoc.controllers;

import com.adnoc.facades.downloadstatement.AdnocDownloadStatementFacade;
import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.integrationservices.util.timeout.IntegrationExecutionException;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@Tag(name = "Adnoc Download Statement")
public class AdnocDownloadStatementController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocDownloadStatementController.class);

    protected static final String MESSAGE = "No PDF data received.";
    @Resource(name = "adnocDownloadStatementFacade")
    private AdnocDownloadStatementFacade adnocDownloadStatementFacade;

    @Operation(operationId = "getDownloadStatement", summary = "Get the Download Statement.", description = "Download Statement.")
    @PostMapping(value = "/getDownloadStatement", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<byte[]> getDownloadStatement(
            @RequestBody final AdnocDownloadStatementRequestData adnocDownloadStatementRequestData,
            @ApiFieldsParam @RequestParam(defaultValue = AdnocBaseController.DEFAULT_FIELD_SET) final String fields)
    {
        try
        {
            final byte[] statementContent = adnocDownloadStatementFacade.processDownloadStatement(adnocDownloadStatementRequestData);
            LOG.info("processing download statement for payer={}", adnocDownloadStatementRequestData.getB2bUnitUid());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Statement")
                    .header("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION)
                    .body(statementContent);

        }
        catch (final IntegrationExecutionException integrationExecutionException)
        {
            final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
            errorWsDTO.setMessage(integrationExecutionException.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorWsDTO.getMessage().getBytes(StandardCharsets.UTF_8));
        }
        catch (final Exception exception)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(String.format("Error processing in download statement request, error=%s", exception.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
