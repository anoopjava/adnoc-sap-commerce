package com.adnoc.facades.quote;

import com.adnoc.facades.AdnocCartFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocQuoteFacadeImplTest
{
    @InjectMocks
    private AdnocQuoteFacadeImpl adnocQuoteFacadeImpl;
    @Mock
    private AdnocCartFacade adnocCartFacade;
    @Mock
    private UserService userService;
    @Mock
    private BaseStoreService baseStoreService;
    @Mock
    private BaseStoreModel baseStoreModel;
    @Mock
    private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
    @Mock
    private QuoteModel quoteModel;
    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Test
    public void testUpdateOrderEntryList_CartReferenceNotNull() throws Exception
    {
        final String quoteCode = "Q123";
        final List<OrderEntryData> cartEntriesData = new ArrayList<>();
        cartEntriesData.add(new OrderEntryData());

        final CustomerModel currentUser = mock(CustomerModel.class);
        final UserModel userModel = mock(UserModel.class);
        quoteModel = mock(QuoteModel.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        Mockito.when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(userModel);

        Mockito.when(commerceQuoteService.getQuoteByCodeAndCustomerAndStore(
                        any(CustomerModel.class), any(UserModel.class), any(BaseStoreModel.class), eq("Q123")))
                .thenReturn(quoteModel);

        final CartModel cartModel = mock(CartModel.class);
        given(quoteModel.getCartReference()).willReturn(cartModel);
        Mockito.when(cartModel.getCode()).thenReturn("C123");
        Mockito.when(adnocCartFacade.updateCartEntries(cartEntriesData, cartModel)).thenReturn(new ArrayList<>());
        final List<CartModificationData> result = adnocQuoteFacadeImpl.updateOrderEntryList(cartEntriesData, quoteCode);
        assertNotNull(result);
        verify(adnocCartFacade).updateCartEntries(cartEntriesData, cartModel);
    }

}
