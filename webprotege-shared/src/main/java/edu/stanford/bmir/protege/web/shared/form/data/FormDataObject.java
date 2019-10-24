package edu.stanford.bmir.protege.web.shared.form.data;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.*;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.singletonList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/03/16
 */
public class FormDataObject extends FormDataValue {

    private Map<String, FormDataValue> map = new HashMap<>();

    public FormDataObject() {
    }

    public FormDataObject(Map<String, FormDataValue> map) {
        this.map.putAll(map);
    }

    public Optional<FormDataValue> get(String key) {
        return Optional.ofNullable(map.get(key));
    }

    public Map<String, FormDataValue> getMap() {
        return map;
    }

    @Override
    public boolean isEmpty() {
        if(map.isEmpty()) {
            return true;
        }
        for(FormDataValue value : map.values()) {
            if(!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<IRI> asIRI() {
        return Optional.empty();
    }

    @Override
    public Optional<OWLEntity> asOWLEntity() {
        return Optional.empty();
    }

    @Override
    public List<FormDataValue> asList() {
        return singletonList(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FormDataObject)) {
            return false;
        }
        FormDataObject other = (FormDataObject) obj;
        return this.map.equals(other.map);
    }


    @Override
    public String toString() {
        return toStringHelper("FormDataObject")
                .addValue(map)
                .toString();
    }

    @Override
    public boolean isObject() {
        return true;
    }

}
