package com.adnoc.service.integration.hooks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

class AdnocB2BCustomerPrePersistTest {

    @InjectMocks
    private AdnocB2BCustomerPrePersist prePersistHook;

    @Mock
    private UserService userService;

    @Mock
    private PersistenceContext persistenceContext;

    @Mock
    private UserGroupModel adminGroup;

    @Mock
    private UserGroupModel customerGroup;

    @Test
    void testExecute_NewSoldToCustomer_UpdatesGroupsCorrectly() {
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        when(customer.getUid()).thenReturn("soldToCustomer");
        when(customer.getPk()).thenReturn(null); // simulate new customer
        B2BUnitModel unit = mock(B2BUnitModel.class);
        when(unit.getPartnerFunction()).thenReturn(PartnerFunction.SP);
        Set<PrincipalGroupModel> initialGroups = new HashSet<>();
        initialGroups.add(unit);
        initialGroups.add(customerGroup);
        when(customer.getGroups()).thenReturn(initialGroups);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(adminGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BCUSTOMERGROUP)).thenReturn(customerGroup);
        Optional<ItemModel> result = prePersistHook.execute(customer, persistenceContext);
        assertTrue(result.isPresent());
        verify(customer).setGroups(argThat(groups ->
                groups.contains(adminGroup) && !groups.contains(customerGroup)
        ));
    }

    @Test
    void testExecute_NewNonSoldToCustomer_NoGroupChange() {
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        when(customer.getUid()).thenReturn("nonSoldToCustomer");
        when(customer.getPk()).thenReturn(null); // simulate new customer
        B2BUnitModel unit = mock(B2BUnitModel.class);
        when(unit.getPartnerFunction()).thenReturn(PartnerFunction.SH);
        Set<PrincipalGroupModel> initialGroups = new HashSet<>();
        initialGroups.add(unit);
        initialGroups.add(customerGroup);
        when(customer.getGroups()).thenReturn(initialGroups);
        Optional<ItemModel> result = prePersistHook.execute(customer, persistenceContext);
        assertTrue(result.isPresent());
        verify(customer).setGroups(initialGroups);
    }

    @Test
    void testExecute_ExistingCustomer_NoChanges() {
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        when(customer.getUid()).thenReturn("existingCustomer");
        when(customer.getPk()).thenReturn(mock(de.hybris.platform.core.PK.class)); // simulate existing
        Set<PrincipalGroupModel> groups = new HashSet<>();
        when(customer.getGroups()).thenReturn(groups);
        Optional<ItemModel> result = prePersistHook.execute(customer, persistenceContext);
        assertTrue(result.isPresent());
        verify(customer, never()).setGroups(any());
    }

    @Test
    void testExecute_NotACustomerModel() {
        ItemModel item = mock(ItemModel.class);
        Optional<ItemModel> result = prePersistHook.execute(item, persistenceContext);

        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }
}
