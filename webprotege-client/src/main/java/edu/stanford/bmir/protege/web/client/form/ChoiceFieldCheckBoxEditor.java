package edu.stanford.bmir.protege.web.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import edu.stanford.bmir.protege.web.resources.WebProtegeClientBundle;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataList;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataValue;
import edu.stanford.bmir.protege.web.shared.form.field.ChoiceDescriptor;

import javax.inject.Inject;
import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/16
 */
public class ChoiceFieldCheckBoxEditor extends Composite implements ChoiceFieldEditor {

    private ValueChangeHandler<Boolean> checkBoxValueChangedHandler;

    interface ChoiceFieldCheckBoxEditorUiBinder extends UiBinder<HTMLPanel, ChoiceFieldCheckBoxEditor> {

    }

    private static ChoiceFieldCheckBoxEditorUiBinder ourUiBinder = GWT.create(ChoiceFieldCheckBoxEditorUiBinder.class);

    @UiField
    HTMLPanel container;

    private final Map<CheckBox, ChoiceDescriptor> checkBoxes = new HashMap<>();

    private final List<FormDataValue> defaultChoices = new ArrayList<>();

    @Inject
    public ChoiceFieldCheckBoxEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        checkBoxValueChangedHandler = event -> ValueChangeEvent.fire(ChoiceFieldCheckBoxEditor.this, getValue());
    }

    @Override
    public void setChoices(List<ChoiceDescriptor> choices) {
        container.clear();
        checkBoxes.clear();
        for(ChoiceDescriptor choiceDescriptor : choices) {
            CheckBox checkBox = new CheckBox(new SafeHtmlBuilder().appendHtmlConstant(choiceDescriptor.getLabel()).toSafeHtml());
            checkBoxes.put(checkBox, choiceDescriptor);
            container.add(checkBox);
            checkBox.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            checkBox.addValueChangeHandler(checkBoxValueChangedHandler);
            checkBox.addStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
            checkBox.addFocusHandler(event -> {
                checkBox.addStyleName(WebProtegeClientBundle.BUNDLE.style().focusBorder());
                checkBox.removeStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
            });
            checkBox.addBlurHandler(event -> {
                checkBox.addStyleName(WebProtegeClientBundle.BUNDLE.style().noFocusBorder());
                checkBox.removeStyleName(WebProtegeClientBundle.BUNDLE.style().focusBorder());
            });
        }
    }

    @Override
    public void setDefaultChoices(List<FormDataValue> defaultChoices) {
        this.defaultChoices.clear();
        this.defaultChoices.addAll(defaultChoices);
        selectDefaultChoices();
    }

    @Override
    public void setValue(FormDataValue value) {
        clearValue();
        for(FormDataValue data : value.asList()) {
            for(CheckBox checkBox : checkBoxes.keySet()) {
                if(checkBoxes.get(checkBox).getValue().equals(data)) {
                    checkBox.setValue(true);
                }
            }
        }
        selectDefaultChoices();
    }

    private void selectDefaultChoices() {
        if (!defaultChoices.isEmpty()) {
            setValue(new FormDataList(defaultChoices));
        }
    }

    @Override
    public void clearValue() {
        for(CheckBox checkBox : checkBoxes.keySet()) {
            checkBox.setValue(false);
        }
    }

    @Override
    public Optional<FormDataValue> getValue() {
        List<FormDataValue> selected = new ArrayList<>();
        for(CheckBox checkBox : checkBoxes.keySet()) {
            if(checkBox.getValue()) {
                selected.add(checkBoxes.get(checkBox).getValue());
            }
        }

        return Optional.of(new FormDataList(selected));
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<FormDataValue>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isWellFormed() {
        return true;
    }
}