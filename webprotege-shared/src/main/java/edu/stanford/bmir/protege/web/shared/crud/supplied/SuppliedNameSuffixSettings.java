package edu.stanford.bmir.protege.web.shared.crud.supplied;

import com.google.common.base.MoreObjects;
import edu.stanford.bmir.protege.web.shared.crud.EntityCrudKitId;
import edu.stanford.bmir.protege.web.shared.crud.EntityCrudKitSuffixSettings;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/08/2013
 */
public class SuppliedNameSuffixSettings extends EntityCrudKitSuffixSettings {

    private WhiteSpaceTreatment whiteSpaceTreatment;

    public SuppliedNameSuffixSettings() {
        this(WhiteSpaceTreatment.TRANSFORM_TO_CAMEL_CASE);
    }

    public SuppliedNameSuffixSettings(WhiteSpaceTreatment whiteSpaceTreatment) {
        this.whiteSpaceTreatment = whiteSpaceTreatment;
    }

    @Override
    public EntityCrudKitId getKitId() {
        return SuppliedNameSuffixKit.getId();
    }

    public WhiteSpaceTreatment getWhiteSpaceTreatment() {
        return whiteSpaceTreatment;
    }

    @Override
    public int hashCode() {
        return "ShortFormSuffixKitSettings".hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        return obj instanceof SuppliedNameSuffixSettings;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("SuppliedNameSuffixSettings")
                          .add("whiteSpaceTreatment", whiteSpaceTreatment)
                          .toString();
    }
}
