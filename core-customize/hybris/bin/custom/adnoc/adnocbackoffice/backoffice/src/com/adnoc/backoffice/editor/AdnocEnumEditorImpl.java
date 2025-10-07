package com.adnoc.backoffice.editor;

import com.hybris.backoffice.user.BackofficeRoleService;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.editor.defaultenum.EnumValueResolver;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.impl.AbstractCockpitEditorRenderer;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;
import de.hybris.platform.core.enums.QuoteState;
import org.apache.commons.collections4.CollectionUtils;
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

public class AdnocEnumEditorImpl extends AbstractCockpitEditorRenderer<Object>
{
    private static final Logger LOG = LogManager.getLogger(AdnocEnumEditorImpl.class);

    public static final int ENTER_CODE_KEY = 13;
    public static final String PARAM_L10N_KEY_NULL = "nullValueLabel";
    public static final String PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL = "enum.editor.null";
    protected static final Pattern PATTERN_ENUM = Pattern.compile("java\\.lang\\.Enum(?:\\((.*)\\))?");
    private static final Object OPTIONAL_OBJECT = new Object();
    private static final String EDITOR_PARAM_RESOLVER = "valueResolver";
    private static final String EDITOR_PARAM_OPTIONAL = "isOptional";
    private static final String MANAGER_ROLE = "csquoteapproverbackofficerole";
    private static final List<String> AGENT_ROLE = List.of("customersupportagentrole", "customersupportmanagerrole", "customersupportadministratorrole");
    final List<QuoteState> ALLOWED_CHANGE_STATES_FOR_AGENT = List.of(QuoteState.BUYER_SUBMITTED, QuoteState.SELLER_REQUEST);
    final List<QuoteState> ALLOWED_CHANGE_STATES_FOR_MANAGER = List.of(QuoteState.SELLER_SUBMITTED);
    final List<QuoteState> SELLER_APPROVER_QUOTE_STATES = List.of(QuoteState.SELLERAPPROVER_APPROVED, QuoteState.SELLERAPPROVER_REJECTED);
    final List<QuoteState> SELLER_REQUEST_QUOTE_STATES = List.of(QuoteState.SELLER_SUBMITTED, QuoteState.SELLERAPPROVER_REJECTED);

    @Resource
    protected EnumValueResolver enumValueResolver;
    @Resource
    protected LabelService labelService;
    @Resource
    protected CockpitLocaleService cockpitLocaleService;
    @Resource
    protected CockpitProperties cockpitProperties;
    @Resource
    protected BackofficeRoleService backofficeRoleService;

    private static void logWarning(final String enumType)
    {
        if (LOG.isWarnEnabled())
        {
            LOG.warn(String.format("%s is not an enumeratation! Cannot retrieve values.", enumType));
        }

    }

    @Override
    public void render(final Component parent, final EditorContext<Object> context, final EditorListener<Object> listener)
    {
        Validate.notNull("All parameters are mandatory", new Object[]{parent, context, listener});
        final Combobox box = new Combobox();
        parent.appendChild(box);
        final Object initialValue = context.getInitialValue();
        List<Object> initialValues = new ArrayList();
        final List<Object> allValues = getAllValues(context, initialValue);
        if (ObjectUtils.isNotEmpty(initialValue))
        {
            initialValues.add(initialValue);
        }

        if (isOptional(context))
        {
            initialValues = ListUtils.union(List.of(OPTIONAL_OBJECT), initialValues);
        }

        final FilteredListModelList<Object> model = new FilteredListModelList<>(initialValues, context);
        if (ObjectUtils.isNotEmpty(initialValue))
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
        final RestoreContext restoreContext = new RestoreContext();
        box.addEventListener("onOpen", new ComboboxOpenEventListener(listener, box, restoreContext, context, this, allValues));
        box.addEventListener("onBlur", new ComboboxBlurEventListener(listener, box, restoreContext, context, this));
        box.addEventListener("onOK", new ComboboxOkEventListener(listener, box, restoreContext, context, this));
        box.addEventListener("onChange", new ComboboxChangeEventListener(listener, box, restoreContext, context, this));
        box.setDisabled(!getAllowedChangeStatesForCurrentRole().contains(initialValue) ? Boolean.TRUE : !context.isEditable());
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
        final String valueType = context.getValueType();
        final EnumValueResolver resolver = getEnumValueResolver(context);
        if (resolver != null)
        {
            final List<Object> resolvedValues = resolver.getAllValues(valueType, initialValue);
            final List<Object> allValues;
            List<QuoteState> allowedChangeStates = Collections.emptyList();

            final Optional<String> activeRole = backofficeRoleService.getActiveRole();

            if (activeRole.isPresent() && MANAGER_ROLE.equalsIgnoreCase(activeRole.get()))
            {
                allValues = resolvedValues.stream().filter(SELLER_APPROVER_QUOTE_STATES::contains).collect(Collectors.toList());
                allowedChangeStates = ALLOWED_CHANGE_STATES_FOR_MANAGER;
            }
            else if (activeRole.isPresent() && AGENT_ROLE.contains(activeRole.get().toLowerCase()))
            {
                allValues = resolvedValues.stream().filter(SELLER_REQUEST_QUOTE_STATES::contains).collect(Collectors.toList());
                allowedChangeStates = ALLOWED_CHANGE_STATES_FOR_AGENT;
            }
            else
            {
                allValues = new ArrayList<>(resolvedValues);
            }

            if (allowedChangeStates.contains(initialValue))
            {
                allValues.add(initialValue);
            }

            return CollectionUtils.isNotEmpty(allValues) ? allValues : null;
        }

        final List<Object> values;
        if (initialValue instanceof Enum)
        {
            values = Arrays.asList(((Enum) initialValue).getDeclaringClass().getEnumConstants());
        }
        else if (StringUtils.isNotEmpty(valueType))
        {
            values = getEnumValues(valueType);
        }
        else
        {
            values = Collections.emptyList();
        }

        return (List) Stream.concat(values.stream(), getAllValues(valueType, initialValue).stream()).distinct().collect(Collectors.toList());
    }

    private List<QuoteState> getAllowedChangeStatesForCurrentRole()
    {
        final String role = backofficeRoleService.getActiveRole().orElse("").toLowerCase();
        if (role.equalsIgnoreCase(MANAGER_ROLE))
        {
            return ALLOWED_CHANGE_STATES_FOR_MANAGER;
        }
        else if (AGENT_ROLE.contains(role))
        {
            return ALLOWED_CHANGE_STATES_FOR_AGENT;
        }
        return Collections.emptyList();
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
                    logWarning(enumType);
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
            // Empty constructor - instance variables are initialized with default values // valueOnOpen = new Object() is initialized at declaration // previousSelectedValue = "" is initialized at declaration
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
        private RestoreContext restoreContext;
        private final Combobox box;
        private final EditorContext<Object> context;
        private final AdnocEnumEditorImpl editor;
        private List<Object> allValues;

        public ComboboxListener(final EditorListener<Object> listener, final Combobox box, final RestoreContext restoreContext, final EditorContext<Object> context, final AdnocEnumEditorImpl editor, final List<Object> allValues)
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

        public AdnocEnumEditorImpl getEditor()
        {
            return editor;
        }

        public RestoreContext getRestoreContext()
        {
            return restoreContext;
        }

        public void setRestoreContext(final RestoreContext restoreContext)
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

        protected void renderComboboxItems(final FilteredListModelList<Object> model)
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
                final String enumEditorNullValue = (String) StringUtils.defaultIfBlank(editor.getL10nDecorator(getContext(), PARAM_L10N_KEY_NULL, PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL), "-");
                return editor.isOptional(context) && (StringUtils.isBlank(value) || StringUtils.equals(value, enumEditorNullValue));
            }
        }
    }

    private class ComboboxOpenEventListener extends ComboboxListener implements EventListener<OpenEvent>
    {
        private boolean alreadyOpened = false;

        public ComboboxOpenEventListener(final EditorListener<Object> listener, final Combobox box, final RestoreContext restoreContext, final EditorContext<Object> context, final AdnocEnumEditorImpl editor, final List<Object> allValues)
        {
            super(listener, box, restoreContext, context, editor, allValues);
        }

        @Override
        public void onEvent(final OpenEvent event)
        {
            if (!alreadyOpened)
            {
                if (isAutoSort(getContext()))
                {
                    final Collator localeAwareStringComparator = Collator.getInstance(cockpitLocaleService.getCurrentLocale());
                    super.getAllValues().sort(Comparator.comparing((f) -> mapEnumToString(f, getContext()), localeAwareStringComparator));
                }

                if (isOptional(getContext()))
                {
                    setAllValues(ListUtils.union(List.of(AdnocEnumEditorImpl.OPTIONAL_OBJECT), super.getAllValues()));
                }

                final FilteredListModelList<Object> model = AdnocEnumEditorImpl.this.new FilteredListModelList<>(super.getAllValues(), getContext());
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
                final RestoreContext restoreContext = getRestoreContext();
                restoreContext.setValueOnOpen(getEditor().getSelectedItemValue(getBox()));
                restoreContext.setPreviousSelectedValue(getBox().getValue());
                setRestoreContext(restoreContext);
            }
            else
            {
                final FilteredListModelList<Object> model = (FilteredListModelList) getBox().getModel();
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

    private class ComboboxOkEventListener extends ComboboxListener implements EventListener<KeyEvent>
    {
        public ComboboxOkEventListener(final EditorListener<Object> listener, final Combobox box, final RestoreContext restoreContext, final EditorContext<Object> context, final AdnocEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final KeyEvent event)
        {
            if (event.getKeyCode() == AdnocEnumEditorImpl.ENTER_CODE_KEY)
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

    private class ComboboxChangeEventListener extends ComboboxListener implements EventListener<InputEvent>
    {
        public ComboboxChangeEventListener(final EditorListener<Object> listener, final Combobox box, final RestoreContext restoreContext, final EditorContext<Object> context, final AdnocEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final InputEvent event)
        {
            final Object selectedVal = getEditor().getSelectedItemValue(getBox());
            final boolean isSelectedObjChanged = ObjectUtils.notEqual(selectedVal, getRestoreContext().getValueOnOpen()) && !StringUtils.equals(String.valueOf(event.getValue()), String.valueOf(event.getPreviousValue()));
            if (isValidEnumInput() && isSelectedObjChanged)
            {
                getListener().onValueChanged(selectedVal);
            }

        }
    }

    private class ComboboxBlurEventListener extends ComboboxListener implements EventListener<Event>
    {
        public ComboboxBlurEventListener(final EditorListener<Object> listener, final Combobox box, final RestoreContext restoreContext, final EditorContext<Object> context, final AdnocEnumEditorImpl editor)
        {
            super(listener, box, restoreContext, context, editor, Collections.emptyList());
        }

        @Override
        public void onEvent(final Event event)
        {
            if (StringUtils.equals(event.getName(), "onBlur"))
            {
                revertToPreviousValidEnumValueIfNecessary();
            }

        }

        private void revertToPreviousValidEnumValueIfNecessary()
        {
            if (!isValidEnumInput())
            {
                final FilteredListModelList<Object> model = (FilteredListModelList) getBox().getModel();
                final Object valueOnOpen = getRestoreContext().getValueOnOpen();
                final String enumEditorNullValue = (String) StringUtils.defaultIfBlank(getEditor().getL10nDecorator(getContext(), PARAM_L10N_KEY_NULL, PARAM_L10N_KEY_DEFAULT_FALLBACK_NULL), "-");
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
