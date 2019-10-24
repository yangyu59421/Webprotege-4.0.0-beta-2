package edu.stanford.bmir.protege.web.server.mansyntax.render;

import com.google.common.collect.Lists;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import java.util.List;
import java.util.Set;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 24/02/2014
 */
public class AnnotationPropertySubPropertyOfSectionRenderer extends AbstractOWLAxiomItemSectionRenderer<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty> {

    @Override
    protected Set<OWLSubAnnotationPropertyOfAxiom> getAxiomsInOntology(OWLAnnotationProperty subject,
                                                                       OWLOntology ontology) {
        return ontology.getSubAnnotationPropertyOfAxioms(subject);
    }

    @Override
    public ManchesterOWLSyntax getSection() {
        return ManchesterOWLSyntax.SUB_PROPERTY_OF;
    }

    @Override
    public List<OWLAnnotationProperty> getRenderablesForItem(OWLAnnotationProperty subject,
                                                             OWLSubAnnotationPropertyOfAxiom item,
                                                             OWLOntology ontology) {
        return Lists.newArrayList(item.getSuperProperty());
    }
}
