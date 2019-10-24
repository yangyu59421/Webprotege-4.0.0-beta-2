package edu.stanford.bmir.protege.web.server.shortform;

import com.google.common.base.Optional;
import edu.stanford.bmir.protege.web.server.inject.project.RootOntology;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
* @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 19/03/2014
*/
public class WebProtegeOntologyIRIShortFormProvider extends OntologyIRIShortFormProvider {

    public static final String ROOT_ONTOLOGY_SHORT_FORM = "root-ontology";

    private OWLOntology rootOntology;

    @Inject
    public WebProtegeOntologyIRIShortFormProvider(@RootOntology OWLOntology rootOntology) {
        this.rootOntology = rootOntology;
    }

    @Override
    public String getShortForm(OWLOntology ont) {
        if(ont.equals(rootOntology)) {
            return ROOT_ONTOLOGY_SHORT_FORM;
        }
        return super.getShortForm(ont);
    }

    @Nonnull
    @Override
    public String getShortForm(IRI iri) {
        if (Optional.of(iri).equals(rootOntology.getOntologyID().getOntologyIRI())) {
            return ROOT_ONTOLOGY_SHORT_FORM;
        }
        return super.getShortForm(iri);
    }
}
