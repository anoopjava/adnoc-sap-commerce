package com.adnoc.backoffice.editor;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.SalesGroup;
import com.adnoc.service.enums.SalesOffice;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.editor.defaultenum.EnumValueResolver;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.impl.AbstractCockpitEditorRenderer;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;
import de.hybris.platform.enumeration.EnumerationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdnocSalesGroupEnumEditorImpl extends AbstractCockpitEditorRenderer<Object>
{
    private static final Logger LOG = LogManager.getLogger(AdnocSalesGroupEnumEditorImpl.class);

    public static final int ENTER_CODE_KEY = 13;
    public static final String PARAM_L10N_KEY_NULL = "nullValueLabel";
    public static final String PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL = "enum.editor.null";
    protected static final Pattern PATTERN_ENUM = Pattern.compile("java\\.lang\\.Enum(?:\\((.*)\\))?");
    private static final Object OPTIONAL_OBJECT = new Object();
    private static final String EDITOR_PARAM_RESOLVER = "valueResolver";
    private static final String EDITOR_PARAM_OPTIONAL = "isOptional";
    @Resource
    protected EnumValueResolver enumValueResolver;
    @Resource
    protected LabelService labelService;
    @Resource
    protected CockpitLocaleService cockpitLocaleService;
    @Resource
    protected CockpitProperties cockpitProperties;
    @Resource
    private EnumerationService enumerationService;
    @Resource
    private AdnocConfigService adnocConfigService;


    @Override
    public void render(final Component parent, final EditorContext<Object> context, final EditorListener<Object> listener)
    {
        LOG.info("appEvent=AdnocSalesGroupEnumEditorImpl.{} - Entered method", "render");
        LOG.info("Rendering AdnocSalesGroupEnumEditorImpl with context: {}", context);
        Validate.notNull("All parameters are mandatory", new Object[]{parent, context, listener});
        final Combobox box = new Combobox();
        parent.appendChild(box);
        final Object initialValue = context.getInitialValue();
        List<Object> initialValues = new ArrayList();
        final List<Object> allValues = getAllValues(context, initialValue);
        if (initialValue != null)
        {
            initialValues.add(initialValue);
        }

        if (isOptional(context))
        {
            initialValues = ListUtils.union(List.of(OPTIONAL_OBJECT), initialValues);
        }

        final AdnocSalesGroupEnumEditorImpl.FilteredListModelList<Object> model = new AdnocSalesGroupEnumEditorImpl.FilteredListModelList<>(initialValues, context);
        if (initialValue != null)
        {
            if (initialValue instanceof Collection)
            {
                model.setSelection((Collection) initialValue);
            }
            else
            {
                model.setSelection(Collections.singletonList(initialValue));
            }
        }

        box.setModel(model);
        box.setAutodrop(true);
        box.setItemRenderer((item, data, index) -> {
            if (data == OPTIONAL_OBJECT)
            {
                item.setSclass("ye-enum-editor-null-element");
                item.setValue((Object) null);
            }
            else
            {
                item.setValue(data);
            }

            item.setLabel(mapEnumToString(data, context));
        });
        final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext = new AdnocSalesGroupEnumEditorImpl.RestoreContext();
        box.addEventListener("onOpen", new AdnocSalesGroupEnumEditorImpl.ComboboxOpenEventListener(listener, box, restoreContext, context, this, allValues));
        box.addEventListener("onBlur", new AdnocSalesGroupEnumEditorImpl.ComboboxBlurEventListener(listener, box, restoreContext, context, this));
        box.addEventListener("onOK", new AdnocSalesGroupEnumEditorImpl.ComboboxOkEventListener(listener, box, restoreContext, context, this));
        box.addEventListener("onChange", new AdnocSalesGroupEnumEditorImpl.ComboboxChangeEventListener(listener, box, restoreContext, context, this));
        box.setDisabled(!context.isEditable());
    }

    protected String mapEnumToString(final Object value, final EditorContext<Object> context)
    {
        if (value == OPTIONAL_OBJECT)
        {
            return (String) StringUtils.defaultIfBlank(getL10nDecorator(context, PARAM_L10N_KEY_NULL, PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL), "-");
        }
        else
        {
            String label = labelService.getObjectLabel(value);
            if (StringUtils.isBlank(label))
            {
                label = String.valueOf(value);
            }

            return label;
        }
    }

    protected List<Object> getAllValues(final String valueType, final Object initialValue)
    {
        return Collections.emptyList();
    }

    protected List<Object> getAllValues(final EditorContext<Object> context, final Object initialValue)
    {
        LOG.info("Retrieving all values for context: {}, initialValue: {}", context, initialValue);
        final String valueType = context.getValueType();
        final EnumValueResolver resolver = getEnumValueResolver(context);
        if (resolver != null)
        {
            final List<Object> allValues = resolver.getAllValues(valueType, initialValue);
            if (CollectionUtils.isNotEmpty(allValues))
            {
                return allValues;
            }
        }

        final List<Object> values;
        if (initialValue instanceof Enum)
        {
            values = Arrays.asList(((Enum) initialValue).getDeclaringClass().getEnumConstants());
        }
        else if (valueType != null)
        {
            values = getEnumValues(valueType);
        }
        else
        {
            values = Collections.emptyList();
        }

        return (List) Stream.concat(values.stream(), getAllValues(valueType, initialValue).stream()).distinct().collect(Collectors.toList());
    }

    private List<Object> getEnumValues(final String valueType)
    {
        try
        {
            final Matcher enumMatcher = PATTERN_ENUM.matcher(valueType);
            if (enumMatcher.matches() && enumMatcher.groupCount() == 1 && enumMatcher.group(1) != null)
            {
                final String enumType = enumMatcher.group(1);
                final Class<?> enumClass = Class.forName(enumType, true, getClass().getClassLoader());
                if (!enumClass.isEnum())
                {
                    return Collections.emptyList();
                }

                return Arrays.asList(enumClass.getEnumConstants());
            }
        }
        catch (final ClassNotFoundException e)
        {
            LOG.debug(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    protected EnumValueResolver getEnumValueResolver(final EditorContext<Object> context)
    {
        return context.containsParameter(EDITOR_PARAM_RESOLVER) ? (EnumValueResolver) SpringUtil.getBean((String) context.getParameterAs(EDITOR_PARAM_RESOLVER), EnumValueResolver.class) : enumValueResolver;
    }

    protected boolean isOptional(final EditorContext<Object> context)
    {
        return context.getParameterAsBoolean(EDITOR_PARAM_OPTIONAL, context.isOptional());
    }

    protected Object getSelectedItemValue(final Combobox box)
    {
        final Comboitem selectedItem = box.getSelectedItem();
        if (selectedItem != null)
        {
            return selectedItem.getValue();
        }
        else
        {
            final int size = box.getItems().size();

            for (int i = 0; i < size; ++i)
            {
                if (StringUtils.equals(box.getValue(), ((Comboitem) box.getItems().get(i)).getLabel()))
                {
                    return ((Comboitem) box.getItems().get(i)).getValue();
                }
            }

            return null;
        }
    }

    private static class RestoreContext
    {
        private Object valueOnOpen = new Object();
        private String previousSelectedValue = "";

        public RestoreContext()
        {
            // Empty constructor - just initializes a new context with default field values
        }

        public Object getValueOnOpen()
        {
            return valueOnOpen;
        }

        public void setValueOnOpen(final Object valueOnOpen)
        {
            this.valueOnOpen = valueOnOpen;
        }

        public String getPreviousSelectedValue()
        {
            return previousSelectedValue;
        }

        public void setPreviousSelectedValue(final String previousSelectedValue)
        {
            this.previousSelectedValue = previousSelectedValue;
        }
    }

    private class ComboboxListener
    {
        private final EditorListener<Object> listener;
        private AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext;
        private final Combobox box;
        private final EditorContext<Object> context;
        private final AdnocSalesGroupEnumEditorImpl editor;
        private List<Object> allValues;

        public ComboboxListener(final EditorListener<Object> listener, final Combobox box, final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext, final EditorContext<Object> context, final AdnocSalesGroupEnumEditorImpl editor, final List<Object> allValues)
        {
            this.listener = listener;
            this.box = box;
            this.restoreContext = restoreContext;
            this.context = context;
            this.editor = editor;
            this.allValues = allValues;
        }

        public EditorListener<Object> getListener()
        {
            return listener;
        }

        public Combobox getBox()
        {
            return box;
        }

        public AdnocSalesGroupEnumEditorImpl getEditor()
        {
            return editor;
        }

        public AdnocSalesGroupEnumEditorImpl.RestoreContext getRestoreContext()
        {
            return restoreContext;
        }

        public void setRestoreContext(final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext)
        {
            this.restoreContext = restoreContext;
        }

        public EditorContext<Object> getContext()
        {
            return context;
        }

        public List<Object> getAllValues()
        {
            return allValues;
        }

        public void setAllValues(final List<Object> allValues)
        {
            this.allValues = allValues;
        }

        protected void renderComboboxItems(final AdnocSalesGroupEnumEditorImpl.FilteredListModelList<Object> model)
        {
            box.setValue("");
            box.setModel(model);
            box.invalidate();
        }

        protected boolean isValidEnumInput()
        {
            if (editor.getSelectedItemValue(getBox()) != null)
            {
                return true;
            }
            else
            {
                final String value = getBox().getValue();
                final String enumEditorNullValue = (String) StringUtils.defaultIfBlank(editor.getL10nDecorator(getContext(), AdnocSalesGroupEnumEditorImpl.PARAM_L10N_KEY_NULL, AdnocSalesGroupEnumEditorImpl.PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL), "-");
                return editor.isOptional(context) && (StringUtils.isBlank(value) || StringUtils.equals(value, enumEditorNullValue));
            }
        }
    }

    private class ComboboxOpenEventListener extends AdnocSalesGroupEnumEditorImpl.ComboboxListener implements EventListener<OpenEvent>
    {
        private boolean alreadyOpened = false;

        public ComboboxOpenEventListener(final EditorListener<Object> listener, final Combobox box, final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext, final EditorContext<Object> context, final AdnocSalesGroupEnumEditorImpl editor, final List<Object> allValues)
        {
            super(listener, box, restoreContext, context, editor, allValues);
        }

        @Override
        public void onEvent(final OpenEvent event)
        {
            if (!alreadyOpened)
            {
                AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocSalesGroupEnumEditorImpl.{} - Starting combobox open event handling with context: {}", "ComboboxOpenEventListener.onEvent", getContext());
                final AdnocRegistrationModel parentObject = (AdnocRegistrationModel) getContext().getParameter("parentObject");
                final String salesOffice;
                if (ObjectUtils.allNotNull(parentObject, parentObject.getSalesOffice()))
                {
                    salesOffice = parentObject.getSalesOffice().getCode();
                }
                else
                {
                    salesOffice = null;
                }

                AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocEditorOpen, handling open event for context: {}, sales group : {}", getContext(), salesOffice);
                List<Object> allValues = new ArrayList<>();
                if (isAutoSort(getContext()))
                {
                    final Collator localeAwareStringComparator = Collator.getInstance(cockpitLocaleService.getCurrentLocale());

                    if (StringUtils.isNotEmpty(salesOffice))
                    {
                        allValues = adnocConfigService.getTargetEnumValues(SalesOffice.class, salesOffice, SalesGroup.class);
                    }
                    else
                    {
                        allValues = super.getAllValues();
                    }
                    allValues.sort(Comparator.comparing((f) -> mapEnumToString(f, getContext()), localeAwareStringComparator));
                }

                if (isOptional(getContext()))
                {
                    setAllValues(ListUtils.union(List.of(AdnocSalesGroupEnumEditorImpl.OPTIONAL_OBJECT), allValues));
                }

                final FilteredListModelList<Object> model = AdnocSalesGroupEnumEditorImpl.this.new FilteredListModelList<>(allValues, getContext());
                final Object initialValue = getContext().getInitialValue();
                final RestoreContext restoreContext = getRestoreContext();
                restoreContext.setValueOnOpen(initialValue);
                restoreContext.setPreviousSelectedValue(getBox().getValue());
                setRestoreContext(restoreContext);
                if (initialValue instanceof Collection)
                {
                    model.setSelection((Collection) initialValue);
                }
                else if (initialValue != null)
                {
                    model.setSelection(Collections.singletonList(initialValue));
                }

                renderComboboxItems(model);
                getBox().setOpen(true);
                alreadyOpened = true;
            }
            else if (event.isOpen())
            {
                List<Object> allValues = new ArrayList<>();
                final AdnocRegistrationModel parentObject = (AdnocRegistrationModel) getContext().getParameter("parentObject");
                final String salesOffice;
                if (ObjectUtils.allNotNull(parentObject, parentObject.getSalesOffice()))
                {
                    salesOffice = parentObject.getSalesOffice().getCode();
                }
                else
                {
                    salesOffice = null;
                }
                final Collator localeAwareStringComparator = Collator.getInstance(cockpitLocaleService.getCurrentLocale());

                if (StringUtils.isNotEmpty(salesOffice))
                {
                    allValues = adnocConfigService.getTargetEnumValues(SalesOffice.class, salesOffice, SalesGroup.class);
                }
                else
                {
                    allValues = super.getAllValues();
                }
                allValues.sort(Comparator.comparing((f) -> mapEnumToString(f, getContext()), localeAwareStringComparator));
                if (isOptional(getContext()))
                {
                    setAllValues(ListUtils.union(List.of(AdnocSalesGroupEnumEditorImpl.OPTIONAL_OBJECT), allValues));
                }
                final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext = getRestoreContext();
                restoreContext.setValueOnOpen("");
                restoreContext.setPreviousSelectedValue("");
                setRestoreContext(restoreContext);
                final FilteredListModelList<Object> model = AdnocSalesGroupEnumEditorImpl.this.new FilteredListModelList<>(allValues, getContext());
                getBox().setValue("");
                getBox().setModel(model);
            }
            else

            {
                final AdnocSalesGroupEnumEditorImpl.FilteredListModelList<Object> model = (AdnocSalesGroupEnumEditorImpl.FilteredListModelList) getBox().getModel();
                final Object selectedValue = getEditor().getSelectedItemValue(getBox());
                if (selectedValue != null)
                {
                    model.setSelection(Collections.singletonList(selectedValue));
                }

                renderComboboxItems(model);
            }
        }

        private Object getSelectedItemValue()
        {
            Object selectedValue = null;
            final Comboitem selectedItem = getBox().getSelectedItem();
            if (selectedItem != null)
            {
                selectedValue = selectedItem.getValue();
            }

            return selectedValue;
        }

        private boolean isAutoSort(final EditorContext<Object> context)
        {
            final boolean isAutoSortGlobalEnabled = cockpitProperties.getBoolean("cockpitng.defaultenumeditor.autosort", true);
            final boolean isAutoSortEditorEnabled = context.getParameterAsBoolean("autoSort", true);
            return context.getParameter("autoSort") == null ? isAutoSortGlobalEnabled : isAutoSortEditorEnabled;
        }
    }

    private class ComboboxOkEventListener extends AdnocSalesGroupEnumEditorImpl.ComboboxListener implements EventListener<KeyEvent>
    {
        public ComboboxOkEventListener(final EditorListener<Object> listener, final Combobox box, final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext, final EditorContext<Object> context, final AdnocSalesGroupEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final KeyEvent event)
        {
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocSalesGroupEnumEditorImpl.{} - Handling OK pressed, keyCode: {}", "ComboboxOkEventListener.onEvent", event.getKeyCode());
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocEditorOk, handling OK event with key code: {}", event.getKeyCode());
            if (event.getKeyCode() == 13)
            {
                final Object selectedVal = getEditor().getSelectedItemValue(getBox());
                if (ObjectUtils.notEqual(selectedVal, getRestoreContext().getValueOnOpen()))
                {
                    getListener().onValueChanged(selectedVal);
                    getListener().onEditorEvent("enter_pressed");
                }
            }

        }
    }

    private class ComboboxChangeEventListener extends AdnocSalesGroupEnumEditorImpl.ComboboxListener implements EventListener<InputEvent>
    {
        public ComboboxChangeEventListener(final EditorListener<Object> listener, final Combobox box, final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext, final EditorContext<Object> context, final AdnocSalesGroupEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final InputEvent event)
        {
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocEditorChange, handling change event, new value: {}, previous value: {}", event.getValue(), event.getPreviousValue());
            final Object selectedVal = getEditor().getSelectedItemValue(getBox());
            final boolean isSelectedObjChanged = ObjectUtils.notEqual(selectedVal, getRestoreContext().getValueOnOpen()) && !StringUtils.equals(String.valueOf(event.getValue()), String.valueOf(event.getPreviousValue()));
            if (isValidEnumInput() && isSelectedObjChanged)
            {
                getListener().onValueChanged(selectedVal);
            }

        }
    }

    private class ComboboxBlurEventListener extends AdnocSalesGroupEnumEditorImpl.ComboboxListener implements EventListener<Event>
    {
        public ComboboxBlurEventListener(final EditorListener<Object> listener, final Combobox box, final AdnocSalesGroupEnumEditorImpl.RestoreContext restoreContext, final EditorContext<Object> context, final AdnocSalesGroupEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final Event event)
        {
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocSalesGroupEnumEditorImpl.{} - Handling Blur event, eventName: {}", "ComboboxBlurEventListener.onEvent", event.getName());
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocEditorBlur, handling blur event: {}", event.getName());
            if (StringUtils.equals(event.getName(), "onBlur"))
            {
                revertToPreviousValidEnumValueIfNecessary();
            }

        }

        private void revertToPreviousValidEnumValueIfNecessary()
        {
            if (!isValidEnumInput())
            {
                final AdnocSalesGroupEnumEditorImpl.FilteredListModelList<Object> model = (AdnocSalesGroupEnumEditorImpl.FilteredListModelList) getBox().getModel();
                final Object valueOnOpen = getRestoreContext().getValueOnOpen();
                final String enumEditorNullValue = (String) StringUtils.defaultIfBlank(getEditor().getL10nDecorator(getContext(), AdnocSalesGroupEnumEditorImpl.PARAM_L10N_KEY_NULL, AdnocSalesGroupEnumEditorImpl.PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL), "-");
                if (valueOnOpen == null && StringUtils.equals(getRestoreContext().getPreviousSelectedValue(), enumEditorNullValue))
                {
                    getBox().setValue(enumEditorNullValue);
                }
                else
                {
                    model.setSelection(Collections.singletonList(valueOnOpen));
                    renderComboboxItems(model);
                }
            }

        }
    }

    protected class FilteredListModelList<E> extends ListModelList<E> implements ListSubModel
    {
        private static final long serialVersionUID = -7363443468394544632L;
        private final SimpleListModel simpleList;
        private final transient Map<Object, String> labelsMap;

        public FilteredListModelList(final Collection<? extends E> c, final EditorContext<Object> context)
        {
            super(c);
            AdnocSalesGroupEnumEditorImpl.LOG.info("appEvent=AdnocEditorFilter, creating filtered list model with collection size: {}", c.size());
            labelsMap = (Map) c.stream().collect(Collectors.toMap(Function.identity(), (val) -> mapEnumToString(val, context)));
            simpleList = new SimpleListModel(new ArrayList(c))
            {
                @Override
                protected boolean inSubModel(final Object key, final Object value)
                {
                    final String idx = String.valueOf(key);
                    if (idx.isEmpty())
                    {
                        return false;
                    }
                    else
                    {
                        final String valueString = (String) labelsMap.get(value);
                        return !valueString.isEmpty() && valueString.startsWith(idx);
                    }
                }
            };
        }

        @Override
        public ListModel getSubModel(final Object value, final int nRows)
        {
            return (ListModel) (value instanceof String && StringUtils.isEmpty((String) value) ? this : simpleList.getSubModel(value, nRows));
        }

        @Override
        public boolean equals(final Object o)
        {
            return super.equals(o);
        }

        @Override
        public int hashCode()
        {
            return super.hashCode();
        }
    }

}