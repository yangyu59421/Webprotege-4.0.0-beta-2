package edu.stanford.bmir.protege.web.shared.form.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.List;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/16
 */
public abstract class FormDataValue implements IsSerializable {

    /**
     * Obtain this value as a literal.
     * @return The value as a literal, or absent if this value is not a primitive value that wraps
     * a literal.
     */
    public Optional<OWLLiteral> asLiteral() {
        return Optional.empty();
    }

    /**
     * Obtain this value as a list of values.
     * @return This value as a list of values.  If this value is a {@link FormDataList}
     * then the values contained in the list will be returned, otherwise this value as a one element list.
     */
    public abstract List<FormDataValue> asList();

    public abstract Optional<IRI> asIRI();

    @JsonIgnore
    public abstract boolean isObject();

    public abstract Optional<OWLEntity> asOWLEntity();

    /**
     * Determines if this form data value is empty.
     * @return true if this form data value is empty.  For {@link FormDataPrimitive} values this
     * is false.  For {@link FormDataList} values this is true if the list is empty, or if all
     * values of the list are empty, otherwise it is false.  For {@link FormDataObject} values this
     * is true if the object does not have any fields, or if the fields are empty, otherwise it is
     * false.
     */
    public abstract boolean isEmpty();
}
