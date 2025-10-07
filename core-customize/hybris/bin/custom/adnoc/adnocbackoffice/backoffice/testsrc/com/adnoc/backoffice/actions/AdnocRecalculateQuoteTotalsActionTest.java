package com.adnoc.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocRecalculateQuoteTotalsActionTest
{

    @InjectMocks
    private AdnocRecalculateQuoteTotalsAction adnocRecalculateQuoteTotalsAction;
    @Mock
    private CalculationService calculationService;

    @Test
    public void testPerform() throws CalculationException
    {
        final ActionContext<QuoteModel> actionContext = Mockito.mock(ActionContext.class);

        final QuoteModel quoteModel = new QuoteModel();
        Mockito.when(actionContext.getData()).thenReturn(quoteModel);

        Mockito.doThrow(new CalculationException("Test calculation exception")).when(calculationService).calculateTotals(quoteModel, true);
        adnocRecalculateQuoteTotalsAction.perform(actionContext);

        final DefaultWidgetModel widget = Mockito.mock(DefaultWidgetModel.class);
        Mockito.when(actionContext.getParameter("parentWidgetModel")).thenReturn(widget);
        Mockito.doNothing().when(calculationService).calculateTotals(quoteModel, true);

        final ActionResult actionResult = adnocRecalculateQuoteTotalsAction.perform(actionContext);
        Assertions.assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.SUCCESS);
    }

    @Test
    public void textCanPerform()
    {
        final ActionContext<QuoteModel> actionContext = Mockito.mock(ActionContext.class);
        Assertions.assertThat(adnocRecalculateQuoteTotalsAction.canPerform(actionContext)).isTrue();
    }

    @Test
    public void textNeedsConfirmation()
    {
        final ActionContext<QuoteModel> actionContext = Mockito.mock(ActionContext.class);
        Assertions.assertThat(adnocRecalculateQuoteTotalsAction.needsConfirmation(actionContext)).isTrue();
    }

    @Test
    public void textGetConfirmationMessage()
    {
        final ActionContext<QuoteModel> actionContext = Mockito.mock(ActionContext.class);
        Mockito.when(actionContext.getLabel("perform.recalculate")).thenReturn("Test Label");
        Assertions.assertThat(adnocRecalculateQuoteTotalsAction.getConfirmationMessage(actionContext)).isNotEmpty();
    }
}
