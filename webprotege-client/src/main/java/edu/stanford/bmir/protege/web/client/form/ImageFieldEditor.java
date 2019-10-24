package edu.stanford.bmir.protege.web.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.editor.ValueEditor;
import edu.stanford.bmir.protege.web.client.library.msgbox.InputBox;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataPrimitive;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataValue;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/04/16
 */
public class ImageFieldEditor extends Composite implements ValueEditor<FormDataValue> {

    @Nonnull
    private final InputBox inputBox;

    interface ImageFieldEditorUiBinder extends UiBinder<HTMLPanel, ImageFieldEditor> {

    }

    private static ImageFieldEditorUiBinder ourUiBinder = GWT.create(ImageFieldEditorUiBinder.class);

    @UiField
    Image imageField;

    @UiField
    Label placeHolder;

    @UiField
    HTMLPanel container;

    @UiField
    FocusPanel focusPanel;

    private Optional<IRI> theIRI = Optional.empty();

    private boolean dirty = false;

    @Inject
    public ImageFieldEditor(@Nonnull InputBox inputBox) {
        this.inputBox = checkNotNull(inputBox);
        initWidget(ourUiBinder.createAndBindUi(this));
        updateUi();
        // The drag over handler is required in order to enable drop support
        placeHolder.addDragOverHandler(dragOverEvent -> {});
        placeHolder.addDropHandler(event -> {
            String data = event.getDataTransfer().getData("text/plain");
            if(data.startsWith("http:") || data.startsWith("https:")) {
                event.preventDefault();
                setEditedValue(data);
            }
        });
    }

    @Override
    public void setValue(FormDataValue object) {
        theIRI = object.asIRI();
        updateUi();
    }

    private void updateUi() {
        if (theIRI.isPresent()) {
            imageField.setUrl(theIRI.get().toString());
            imageField.getElement().getStyle().clearDisplay();
            placeHolder.setVisible(false);
            setTitle("Source: " + theIRI.get().toString());
        }
        else {
            imageField.getElement().getStyle().setDisplay(Style.Display.NONE);
            placeHolder.setVisible(true);
            setTitle("");
        }
    }

    @UiHandler("imageField")
    protected void handleImageClicked(ClickEvent event) {
        showEditingDialog();
    }


    @UiHandler("placeHolder")
    protected void handlePlaceHolderClicked(ClickEvent event) {
        showEditingDialog();
    }

    @UiHandler("focusPanel")
    public void handleFocusPanelKeyUp(KeyUpEvent event) {
        if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            showEditingDialog();
        }
    }

    private void showEditingDialog() {
        inputBox.showDialog(
                "Image URL",
                false,
                theIRI.map(IRI::toString).orElse(""),
                this::setEditedValue);
    }

    private void setEditedValue(String editedValue) {
        Optional<IRI> newValue;
        if(editedValue.trim().isEmpty()) {
            newValue = Optional.empty();
        }
        else {
            newValue = Optional.of(IRI.create(editedValue));
        }
        if (!theIRI.equals(newValue)) {
            theIRI = newValue;
            imageField.setUrl(editedValue);
            updateUi();
            ValueChangeEvent.fire(ImageFieldEditor.this, getValue());
        }
    }

    @Override
    public void clearValue() {
        theIRI = Optional.empty();
        dirty = false;
        updateUi();
    }

    @Override
    public Optional<FormDataValue> getValue() {
        return theIRI.map(FormDataPrimitive::get);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<FormDataValue>> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isWellFormed() {
        return getValue().isPresent();
    }
}