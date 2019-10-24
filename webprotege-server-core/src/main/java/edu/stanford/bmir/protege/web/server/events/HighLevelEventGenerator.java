package edu.stanford.bmir.protege.web.server.events;

import edu.stanford.bmir.protege.web.server.change.ChangeApplicationResult;
import edu.stanford.bmir.protege.web.server.change.HasGetRevisionSummary;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.server.revision.Revision;
import edu.stanford.bmir.protege.web.server.shortform.DictionaryManager;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.HasGetEntitiesWithIRI;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.event.*;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionSummary;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AxiomSubjectProvider;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/03/2013
 */
public class HighLevelEventGenerator implements EventTranslator {

    private final ProjectId projectId;

    private final OWLOntology rootOntology;

    private final RenderingManager renderingManager;

    private final HasGetRevisionSummary hasGetRevisionSummary;

    private final HasGetEntitiesWithIRI hasGetEntitiesWithIRI;

    @Inject
    public HighLevelEventGenerator(ProjectId projectId,
                                   OWLOntology rootOntology,
                                   RenderingManager renderingManager,
                                   HasGetEntitiesWithIRI hasGetEntitiesWithIRI,
                                   HasGetRevisionSummary hasGetRevisionSummary) {
        this.projectId = projectId;
        this.rootOntology = rootOntology;
        this.renderingManager = renderingManager;
        this.hasGetEntitiesWithIRI = hasGetEntitiesWithIRI;
        this.hasGetRevisionSummary = hasGetRevisionSummary;
    }

    @Override
    public void prepareForOntologyChanges(List<OWLOntologyChange> submittedChanges) {
    }

    @Override
    public void translateOntologyChanges(Revision revision,
                                         ChangeApplicationResult<?> changes,
                                         final List<ProjectEvent<?>> projectEventList) {
        // TODO: NEED TIDYING AND SPLITTING UP
        final Set<OWLEntity> changedEntities = new HashSet<>();
        for(OWLOntologyChange change : changes.getChangeList()) {
            change.accept(new OWLOntologyChangeVisitor() {
                @Override
                public void visit(@Nonnull AddAxiom change) {
                    final OWLAxiom axiom = change.getAxiom();
                    handleAxiomChange(axiom);
                }

                @Override
                public void visit(@Nonnull RemoveAxiom change) {
                    handleAxiomChange(change.getAxiom());
                }

                @Override
                public void visit(@Nonnull SetOntologyID change) {
                    projectEventList.add(new OntologyFrameChangedEvent(change.getOntology().getOntologyID(), projectId));
                }

                @Override
                public void visit(@Nonnull AddImport change) {
                    projectEventList.add(new OntologyFrameChangedEvent(change.getOntology().getOntologyID(), projectId));
                }

                @Override
                public void visit(@Nonnull RemoveImport change) {
                    projectEventList.add(new OntologyFrameChangedEvent(change.getOntology().getOntologyID(), projectId));
                }

                @Override
                public void visit(@Nonnull AddOntologyAnnotation change) {
                    projectEventList.add(new OntologyFrameChangedEvent(change.getOntology().getOntologyID(), projectId));
                }

                @Override
                public void visit(@Nonnull RemoveOntologyAnnotation change) {
                    projectEventList.add(new OntologyFrameChangedEvent(change.getOntology().getOntologyID(), projectId));
                }

                private void handleAxiomChange(OWLAxiom axiom) {
                    AxiomSubjectProvider p = new AxiomSubjectProvider();
                    OWLObject subject = p.getSubject(axiom);
                    Set<OWLEntity> entities;
                    if(subject instanceof IRI) {
                        entities = hasGetEntitiesWithIRI.getEntitiesWithIRI((IRI) subject);
                    }
                    else if (subject instanceof OWLEntity) {
                        entities = Collections.singleton((OWLEntity) subject);
                    }
                    else {
                        entities = Collections.emptySet();
                    }
                    for (OWLEntity e : entities) {
                        if (!changedEntities.contains(e)) {
                            changedEntities.add(e);
                            ProjectEvent<?> event = e.accept(new OWLEntityVisitorEx<ProjectEvent<?>>() {
                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLClass cls) {
                                    return new ClassFrameChangedEvent(cls, projectId, revision.getUserId());
                                }

                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLObjectProperty property) {
                                    return new ObjectPropertyFrameChangedEvent(property, projectId, revision.getUserId());
                                }

                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLDataProperty property) {
                                    return new DataPropertyFrameChangedEvent(property, projectId, revision.getUserId());
                                }

                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLNamedIndividual individual) {
                                    return new NamedIndividualFrameChangedEvent(individual, projectId, revision.getUserId());
                                }

                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLDatatype datatype) {
                                    return new DatatypeFrameChangedEvent(datatype, projectId, revision.getUserId());
                                }

                                @Nonnull
                                @Override
                                public ProjectEvent<?> visit(@Nonnull OWLAnnotationProperty property) {
                                    return new AnnotationPropertyFrameChangedEvent(property, projectId, revision.getUserId());
                                }
                            });
                            projectEventList.add(event);
                        }
                    }
                }
            });
        }


        Set<OWLEntityData> changedEntitiesData = new HashSet<>();
        Object subject = changes.getSubject();
        if(subject instanceof OWLEntity) {
            OWLEntity entity = (OWLEntity) subject;
            if (rootOntology.containsEntityInSignature(entity)) {
                changedEntitiesData.add(renderingManager.getRendering(entity));
            }
        }
        else if(subject instanceof OWLEntityData) {
            OWLEntityData entityData = (OWLEntityData) subject;
            if (rootOntology.containsEntityInSignature(entityData.getEntity())) {
                changedEntitiesData.add(entityData);
            }
        }
        else if(subject instanceof Collection) {
            Collection<?> collection = (Collection<?>) subject;
            collection.stream()
                      .filter(element -> element instanceof OWLEntity)
                      .map(element -> (OWLEntity) element)
                      .filter(rootOntology::containsEntityInSignature)
                      .forEach(entity -> changedEntitiesData.add(renderingManager.getRendering(entity)));

        }
        Optional<RevisionSummary> revisionSummary = hasGetRevisionSummary.getRevisionSummary(revision.getRevisionNumber());
        if (revisionSummary.isPresent()) {
            ProjectEvent<?> event = new ProjectChangedEvent(projectId, revisionSummary.get(), changedEntitiesData);
            projectEventList.add(event);
        }
    }
}
