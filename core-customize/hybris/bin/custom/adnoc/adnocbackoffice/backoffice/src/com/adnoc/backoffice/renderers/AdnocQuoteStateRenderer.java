package com.adnoc.backoffice.renderers;

import com.hybris.cockpitng.common.EditorBuilder;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Attribute;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Parameter;
import com.hybris.cockpitng.core.model.ModelObserver;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.YTestTools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaPanelRenderer;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class AdnocQuoteStateRenderer extends DefaultEditorAreaPanelRenderer
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteStateRenderer.class);

    protected static final String QUOTE_STATE_OBSERVER_ID = "quoteStateObserver";
    protected static final String QUOTE_ENTRY = "Quote";
    protected static final String QUALIFIER = "state";
    protected static final String CURRENT_OBJECT = "currentObject";
    private Editor editor;
    private TypeFacade typeFacade;
    private QuoteState quoteState;
    final List<QuoteState> quoteStateList = List.of(QuoteState.BUYER_SUBMITTED, QuoteState.SELLER_REQUEST);

    @Override
    public void render(final Component component, final AbstractPanel abstractPanelConfiguration, final Object object, final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
    {
        if (abstractPanelConfiguration instanceof CustomPanel && object instanceof QuoteModel)
        {
            quoteState = ((QuoteModel) object).getState();

            try
            {
                final Attribute attribute = new Attribute();
                attribute.setDescription("field.quote.state.description");
                attribute.setPosition(BigInteger.valueOf(3));
                attribute.setQualifier(QUALIFIER);
                if (!quoteStateList.contains(quoteState))
                {
                    attribute.setReadonly(Boolean.TRUE);
                }
                final DataType quote = getTypeFacade().load(QUOTE_ENTRY);
                final boolean canReadObject = getPermissionFacade().canReadInstanceProperty(quote.getClazz(), "state");
                if (!canReadObject)
                {
                    final Div attributeContainer = new Div();
                    attributeContainer.setSclass("yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed");
                    renderNotReadableLabel(attributeContainer, attribute, dataType, getLabelService().getAccessDeniedLabel(attribute));
                    attributeContainer.setParent(component);
                    return;
                }

                createAttributeRenderer().render(component, attribute, object, quote, widgetInstanceManager);
                final WidgetModel widgetInstanceModel = widgetInstanceManager.getModel();
                widgetInstanceModel.addObserver(CURRENT_OBJECT, new ModelObserver()
                {
                    @Override
                    public void modelChanged()
                    {
                        if (QuoteModel.class.equals(widgetInstanceModel.getValueType(AdnocQuoteStateRenderer.CURRENT_OBJECT)))
                        {
                            final QuoteModel currentQuote = (QuoteModel) widgetInstanceModel.getValue(AdnocQuoteStateRenderer.CURRENT_OBJECT, QuoteModel.class);
                            if (Objects.nonNull(currentQuote))
                            {
                                final QuoteState state = widgetInstanceModel.getValue("Quote.state", QuoteState.class);
                                quoteState = ObjectUtils.isNotEmpty(state) ? state : currentQuote.getState();
                                editor = createEditor(widgetInstanceManager);
                                editor.setInitialValue(quoteState);
                                currentQuote.setState(quoteState);
                            }
                        }
                    }
                });
            }
            catch (final TypeNotFoundException e)
            {
                if (LOG.isWarnEnabled())
                {
                    LOG.warn(e.getMessage(), e);
                }
            }

        }
    }

    @Override
    protected Editor createEditor(final DataType genericType, final WidgetInstanceManager widgetInstanceManager, final Attribute attribute, final Object object)
    {
        final DataAttribute genericAttribute = genericType.getAttribute(attribute.getQualifier());
        if (genericAttribute == null)
        {
            return null;
        }
        else
        {
            final String qualifier = genericAttribute.getQualifier();
            final boolean editable = !attribute.isReadonly() && canChangeProperty(genericAttribute, object);
            final String editorSClass = getEditorSClass(editable);
            final boolean editorValueDetached = isEditorValueDetached(attribute);
            setPasswordEditorAsDefaultForEncryptedStrings(attribute, genericAttribute);
            final EditorBuilder editorBuilder = getEditorBuilder(widgetInstanceManager).addParameters(attribute.getEditorParameter().stream(), this::extractParameterName, this::extractParameterValue).configure(CURRENT_OBJECT, genericAttribute, editorValueDetached).setReadOnly(!editable).setLabel(resolveAttributeLabel(attribute, genericType)).setDescription(getAttributeDescription(genericType, attribute)).useEditor(attribute.getEditor()).setValueType(resolveEditorType(genericAttribute)).apply((editorx) -> editorx.setSclass(editorSClass)).apply((editorx) -> processEditorBeforeComposition(editorx, genericType, widgetInstanceManager, attribute, object));
            final Editor editor = buildEditor(editorBuilder, widgetInstanceManager);
            YTestTools.modifyYTestId(editor, "editor_" + qualifier);
            editor.setWidth("350px;");
            return editor;
        }
    }

    private String getEditorSClass(final boolean editable)
    {
        return editable ? "yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-ed-editor" : "ye-default-editor-readonly";
    }

    private boolean isEditorValueDetached(final Attribute attribute)
    {
        return (Boolean) attribute.getEditorParameter().stream().filter((parameter) -> parameter.getName().equals("attributeValueDetached")).map(Parameter::getValue).map(Boolean::valueOf).findAny().orElse(Boolean.FALSE);
    }

    protected Editor createEditor(final WidgetInstanceManager widgetInstanceManager)
    {
        final Editor editor = new Editor();
        editor.setWidgetInstanceManager(widgetInstanceManager);
        editor.setType(QUOTE_ENTRY);
        return editor;
    }


    protected TypeFacade getTypeFacade()
    {
        return typeFacade;
    }

    public void setTypeFacade(final TypeFacade typeFacade)
    {
        this.typeFacade = typeFacade;
    }

}
