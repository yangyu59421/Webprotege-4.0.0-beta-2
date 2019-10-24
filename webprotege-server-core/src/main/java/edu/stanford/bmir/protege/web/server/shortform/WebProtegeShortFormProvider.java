package edu.stanford.bmir.protege.web.server.shortform;

import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import org.semanticweb.owlapi.model.HasPrefixedName;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;

import javax.inject.Inject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/04/2012
 */
@Deprecated
@ProjectSingleton
public class WebProtegeShortFormProvider implements ShortFormProvider {

    private final IRIShortFormProvider iriShortFormProvider;

    @Inject
    public WebProtegeShortFormProvider(IRIShortFormProvider iriShortFormProvider) {
        this.iriShortFormProvider = iriShortFormProvider;
    }

    public synchronized String getShortForm(OWLEntity owlEntity) {
        if(owlEntity instanceof HasPrefixedName) {
            return ((HasPrefixedName) owlEntity).getPrefixedName();
        }
        else {
            return iriShortFormProvider.getShortForm(owlEntity.getIRI());
        }
    }

    @Override
    public void dispose() {

    }
}
