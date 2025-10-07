package com.adnoc.validators.helper;

import com.adnoc.b2bocc.ordermanagement.data.ReturnReasonWsDTO;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservices.core.returns.data.ReturnRequestsData;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestWsDTO;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class AdnocOrderReturnsHelper extends AbstractHelper
{

    private static final Logger LOG = LogManager.getLogger(AdnocOrderReturnsHelper.class);

    @Resource(name = "omsReturnFacade")
    private OmsReturnFacade omsReturnFacade;

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @Resource(name = "productFacade")
    private ProductFacade productFacade;

    @Resource(name = "enumerationService")
    private EnumerationService enumerationService;

    /**
     * Returns the list of order return requests for the current user
     *
     * @param currentPage The current result page requested.
     * @param pageSize    he number of results returned per page.
     * @param sort        Sorting method applied to the return results.
     * @param fields      configuration for fields that will be returned in {@link ReturnRequestListWsDTO}
     * @return The {@link ReturnRequestListWsDTO} for the current user.
     */
    public ReturnRequestListWsDTO searchOrderReturnRequests(final int currentPage, final int pageSize, final String sort,
                                                            final String fields)
    {
        LOG.info("appEvent=AdnocOrderReturn, searchOrderReturnRequests called with currentPage:{},pageSize:{},sort:{},fields:{}",currentPage,pageSize,sort,fields);
        final PageableData pageableData = createPageableData(currentPage, pageSize, sort);

        final ReturnRequestsData returnRequestsData = createReturnRequestsData(
                omsReturnFacade.getPagedReturnRequestsByCurrentUser(pageableData));

        return getDataMapper().map(returnRequestsData, ReturnRequestListWsDTO.class, fields);
    }

    /**
     * Returns a return request by its code
     *
     * @param orderReturnRequestCode Code of the order return request
     * @param fields                 configuration for fields that will be returned in {@link ReturnRequestWsDTO}
     * @return THe {@link ReturnRequestWsDTO} which the code belongs to
     */
    public ReturnRequestWsDTO getOrderReturnRequest(final String orderReturnRequestCode, final String fields)
    {
        LOG.info("appEvent=AdnocOrderReturn, getOrderReturnRequest called with orderReturnRequestCode:{},fields:{}",orderReturnRequestCode,fields);

        final ReturnRequestData returnRequestDetails = omsReturnFacade.getReturnForReturnCode(orderReturnRequestCode);
        validateUserForOrder(returnRequestDetails.getOrder().getCode());

        populateReturnEntriesWithProductData(returnRequestDetails);

        return getDataMapper().map(returnRequestDetails, ReturnRequestWsDTO.class, fields);
    }

    /**
     * Cancels an order return request
     *
     * @param orderReturnRequestCode The code of the order return request which is requested to be cancelled
     */
    public void cancelOrderReturnRequest(final String orderReturnRequestCode)
    {
        LOG.info("appEvent=AdnocOrderReturn, cancelOrderReturnRequest called with orderReturnRequestCode:{}",orderReturnRequestCode);

        final ReturnRequestData returnRequestDetails = omsReturnFacade.getReturnForReturnCode(orderReturnRequestCode);
        validateUserForOrder(returnRequestDetails.getOrder().getCode());

        final CancelReturnRequestData cancelReturnRequestData = new CancelReturnRequestData();
        cancelReturnRequestData.setCode(orderReturnRequestCode);
        cancelReturnRequestData.setCancelReason(CancelReason.CUSTOMERREQUEST);

        omsReturnFacade.cancelReturnRequest(cancelReturnRequestData);
    }

    /**
     * Creates an order return request
     *
     * @param returnRequestEntryInputListWsDTO The return request entry input list for an order
     * @param fields                           configuration for fields that will be returned in {@link ReturnRequestWsDTO}
     * @return The {@link ReturnRequestWsDTO} converted from the newly created return request
     */
    public ReturnRequestWsDTO createOrderReturnRequest(final ReturnRequestEntryInputListWsDTO returnRequestEntryInputListWsDTO,
                                                       final String fields)
    {
        LOG.info("appEvent=AdnocOrderReturn, createOrderReturnRequest called with orderCode:{},fields:{}",returnRequestEntryInputListWsDTO.getOrderCode(),fields);

        final String orderCode = returnRequestEntryInputListWsDTO.getOrderCode();

        LOG.debug("appEvent=AdnocOrderReturn, Validating user for orderCode: {}", orderCode);
        validateUserForOrder(orderCode);

        LOG.info("appEvent=AdnocOrderReturn,Fetching order details for orderCode: {}", orderCode);
        final OrderData order = orderFacade.getOrderDetailsForCode(orderCode);
        final ReturnRequestData returnRequestData = prepareReturnRequestData(order, returnRequestEntryInputListWsDTO);

        final ReturnRequestData createdReturnRequestData = omsReturnFacade.createReturnRequest(returnRequestData);

        populateReturnEntriesWithProductData(createdReturnRequestData);

        return getDataMapper().map(createdReturnRequestData, ReturnRequestWsDTO.class, fields);
    }

    /**
     * Populates return entries of {@code ReturnRequestData} with {@code ProductData}
     *
     * @param returnRequestData The return request data whose return entries are populated
     */
    protected void populateReturnEntriesWithProductData(final ReturnRequestData returnRequestData)
    {
        LOG.info("appEvent=AdnocOrderReturn, populateReturnEntriesWithProductData called with returnRequestData:{}",returnRequestData.getCode());

        returnRequestData.getReturnEntries().forEach(entry -> {
            final ProductData productData = productFacade
                    .getProductForCodeAndOptions(entry.getOrderEntry().getProduct().getCode(), null);
            entry.getOrderEntry().setProduct(productData);
        });
    }

    /**
     * Creates a new {@link ReturnRequestsData} from {@link SearchPageData}
     *
     * @param result Search results
     * @return {@link ReturnRequestsData} which contains the information provided by {@link SearchPageData}
     */

    protected ReturnRequestsData createReturnRequestsData(final SearchPageData<ReturnRequestData> result)
    {
        LOG.info("appEvent=AdnocOrderReturn, createReturnRequestsData called with result:{}",result);

        final ReturnRequestsData returnRequestsData = new ReturnRequestsData();

        returnRequestsData.setReturnRequests(result.getResults());
        returnRequestsData.setSorts(result.getSorts());
        returnRequestsData.setPagination(result.getPagination());

        return returnRequestsData;
    }

    /**
     * Validates if the current user has access to the order
     *
     * @param orderCode the order code
     * @throws NotFoundException if current user has no access to the order
     */
    protected void validateUserForOrder(final String orderCode)
    {
        try
        {
            orderFacade.getOrderDetailsForCode(orderCode);
        }
        catch (final UnknownIdentifierException ex)
        {
            LOG.warn("Order not found for current user in current BaseStore", ex);
            throw new NotFoundException("Resource not found");
        }
    }

    /**
     * It prepares the {@link ReturnRequestData} object by taking the order and the map of orderentries {@link OrderEntryData} number and returned quantities
     *
     * @param order                       order {@link OrderData} which we want to return
     * @param returnRequestEntryInputList a {@link ReturnRequestEntryInputListWsDTO} array of order entries number and the returned quantities
     * @return returnRequest {@link ReturnRequestData}
     */
    protected ReturnRequestData prepareReturnRequestData(final OrderData order,
                                                         final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList)
    {
        LOG.info("appEvent=AdnocOrderReturn, prepareReturnRequestData called with order:{}", order);

        final ReturnRequestData returnRequest = new ReturnRequestData();

        final List<ReturnEntryData> returnEntries = returnRequestEntryInputList.getReturnRequestEntryInputs().stream()
                .map(this::mapToReturnEntryData).collect(toList());

        returnRequest.setOrder(order);
        returnRequest.setReturnRequestDocument(returnRequestEntryInputList.getReturnRequestDocument());
        returnRequest.setReturnEntries(returnEntries);
        returnRequest.setRefundDeliveryCost(false);
        Optional.ofNullable(mapToReturnReasonData(returnRequestEntryInputList.getReturnReason()))
                .ifPresent(returnRequest::setRefundReason);

        return returnRequest;
    }

    /**
     * Maps {@link ReturnRequestEntryInputWsDTO} to {@link ReturnEntryData} along with default values necessary for a return request
     *
     * @param entryInput Return request entry input which will be mapped
     * @return The mapped {@link ReturnRequestEntryInputWsDTO}
     */
    protected ReturnEntryData mapToReturnEntryData(final ReturnRequestEntryInputWsDTO entryInput)
    {
        LOG.info("appEvent=AdnocOrderReturn, mapToReturnEntryData method called with entryInput:{}",entryInput.getOrderEntryNumber());
        final ReturnEntryData returnEntry = new ReturnEntryData();

        final OrderEntryData oed = new OrderEntryData();
        oed.setEntryNumber(entryInput.getOrderEntryNumber());

        returnEntry.setOrderEntry(oed);
        returnEntry.setExpectedQuantity(entryInput.getQuantity());
        returnEntry.setAction(ReturnAction.HOLD);

        return returnEntry;
    }

    private RefundReason mapToReturnReasonData(final ReturnReasonWsDTO returnReasonWsDTO)
    {
        return Objects.nonNull(returnReasonWsDTO) ? enumerationService.getEnumerationValue(RefundReason.class, returnReasonWsDTO.getCode()) : null;
    }
}
