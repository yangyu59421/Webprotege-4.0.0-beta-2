package edu.stanford.bmir.protege.web.shared.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataList;
import edu.stanford.bmir.protege.web.shared.form.data.FormDataValue;
import edu.stanford.bmir.protege.web.shared.form.field.FormElementId;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12/04/16
 */
public class FormData implements Serializable, IsSerializable {

    @JsonUnwrapped
    private Map<String, FormDataValue> data = new HashMap<>();

    private FormData() {
    }

    public static FormData empty() {
        return new FormData(Collections.emptyMap());
    }

    public FormData(@Nonnull Map<FormElementId, FormDataValue> data) {
        checkNotNull(data);
        data.forEach((id, val) -> {
            checkNotNull(id);
            checkNotNull(val);
            this.data.put(id.getId(), val);
        });
    }

    public Map<FormElementId, FormDataValue> getData() {
        Map<FormElementId, FormDataValue> result = new HashMap<>();
        data.forEach((id, val) -> result.put(FormElementId.get(id), val));
        return result;
    }

    public boolean isEmpty() {
        if(data.isEmpty()) {
            return true;
        }
        for(FormDataValue value : data.values()) {
            if(!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    public Optional<FormDataValue> getFormElementData(FormElementId formElementId) {
        return Optional.ofNullable(data.get(formElementId.getId()));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FormData)) {
            return false;
        }
        FormData other = (FormData) obj;
        return this.data.equals(other.data);
    }

    public static class Builder {

        private final ListMultimap<FormElementId, FormDataValue> builder_data = ArrayListMultimap.create();

        public Builder() {
        }

        public Builder addData(FormElementId elementId, FormDataValue dataValue) {
            builder_data.put(elementId, dataValue);
            return this;
        }

        public FormData build() {
            Map<FormElementId, FormDataValue> map = new HashMap<>();
            for(FormElementId elementId : builder_data.keys()) {
                map.put(elementId, new FormDataList(builder_data.get(elementId)));
            }
            return new FormData(map);
        }
    }


    @Override
    public String toString() {
        return toStringHelper("FormData")
                .addValue(data)
                .toString();
    }

}
