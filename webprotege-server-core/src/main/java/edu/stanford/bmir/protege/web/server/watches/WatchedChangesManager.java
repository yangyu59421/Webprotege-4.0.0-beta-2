package edu.stanford.bmir.protege.web.server.watches;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.hierarchy.HierarchyProvider;
import edu.stanford.bmir.protege.web.server.revision.EntitiesByRevisionCache;
import edu.stanford.bmir.protege.web.server.revision.ProjectChangesManager;
import edu.stanford.bmir.protege.web.server.revision.Revision;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.change.ProjectChange;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.watches.Watch;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.watches.WatchType.BRANCH;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/05/15
 */
@ProjectSingleton
public class WatchedChangesManager {

    private final HierarchyProvider<OWLClass> classHierarchyProvider;

    private final HierarchyProvider<OWLObjectProperty> objectPropertyHierarchyProvider;

    private final HierarchyProvider<OWLDataProperty> dataPropertyHierarchyProvider;

    private final HierarchyProvider<OWLAnnotationProperty> annotationPropertyHierarchyProvider;

    private final HasImportsClosure rootOntologyImportsClosureProvider;

    private final RevisionManager changeManager;

    private final ProjectChangesManager projectChangesManager;

    private final EntitiesByRevisionCache entitiesByRevisionCache;

    @Inject
    public WatchedChangesManager(ProjectChangesManager projectChangesManager,
                                 HierarchyProvider<OWLClass> classHierarchyProvider,
                                 HierarchyProvider<OWLObjectProperty> objectPropertyHierarchyProvider,
                                 HierarchyProvider<OWLDataProperty> dataPropertyHierarchyProvider,
                                 HierarchyProvider<OWLAnnotationProperty> annotationPropertyHierarchyProvider,
                                 HasImportsClosure rootOntologyImportsClosureProvider,
                                 RevisionManager changeManager,
                                 EntitiesByRevisionCache entitiesByRevisionCache) {
        this.projectChangesManager = checkNotNull(projectChangesManager);
        this.classHierarchyProvider = checkNotNull(classHierarchyProvider);
        this.objectPropertyHierarchyProvider = checkNotNull(objectPropertyHierarchyProvider);
        this.dataPropertyHierarchyProvider = checkNotNull(dataPropertyHierarchyProvider);
        this.annotationPropertyHierarchyProvider = checkNotNull(annotationPropertyHierarchyProvider);
        this.rootOntologyImportsClosureProvider = checkNotNull(rootOntologyImportsClosureProvider);
        this.changeManager = checkNotNull(changeManager);
        this.entitiesByRevisionCache = checkNotNull(entitiesByRevisionCache);
    }

    public ImmutableList<ProjectChange> getProjectChangesForWatches(Set<Watch> watches) {
        Set<OWLEntity> superEntities = new HashSet<>();
        Set<OWLEntity> directWatches = new HashSet<>();
        for (Watch watch : watches) {
            if (watch.getType() == BRANCH) {
                OWLEntity entity = watch.getEntity();
                superEntities.add(entity);
                directWatches.add(entity);
            }
            else {
                directWatches.add(watch.getEntity());
            }
        }
        if (superEntities.isEmpty() && directWatches.isEmpty()) {
            return ImmutableList.of();
        }
        ImmutableList.Builder<ProjectChange> result = ImmutableList.builder();
        List<Revision> revisionsCopy = changeManager.getRevisions();
        for (Revision revision : revisionsCopy) {
            for (OWLEntity watchedEntity : getWatchedEntities(superEntities, directWatches, revision)) {
                ImmutableList<ProjectChange> changes = projectChangesManager.getProjectChangesForSubjectInRevision(watchedEntity, revision);
                result.addAll(changes);
            }
        }
        return result.build();
    }

    private Set<OWLEntity> getWatchedEntities(Set<OWLEntity> superEntities, Set<OWLEntity> directWatches, Revision revision) {
        Set<OWLEntity> watchedEntities = new HashSet<>();
        Set<OWLEntity> entities = entitiesByRevisionCache.getEntities(revision);
        for (OWLEntity entity : entities) {
            if (directWatches.contains(entity)) {
                watchedEntities.add(entity);
            }
            else {
                boolean watchedByAncestor = isWatchedByAncestor(superEntities, entity);
                if (watchedByAncestor) {
                    watchedEntities.add(entity);
                }
            }
        }
        return watchedEntities;
    }


    private Boolean isWatchedByAncestor(final Set<OWLEntity> watchedAncestors, OWLEntity entity) {
        return entity.accept(new OWLEntityVisitorEx<Boolean>() {
            @Override
            public Boolean visit(OWLClass cls) {
                final Set<? extends OWLEntity> ancestors = classHierarchyProvider.getAncestors(cls);
                return isWatchedByAncestor(ancestors);
            }


            @Override
            public Boolean visit(OWLObjectProperty property) {
                return isWatchedByAncestor(objectPropertyHierarchyProvider.getAncestors(property));
            }

            @Override
            public Boolean visit(OWLDataProperty property) {
                return isWatchedByAncestor(dataPropertyHierarchyProvider.getAncestors(property));
            }

            @Override
            public Boolean visit(OWLNamedIndividual individual) {
                final Collection<OWLClassExpression> types = EntitySearcher.getTypes(individual, rootOntologyImportsClosureProvider.getImportsClosure());
                for (OWLClassExpression ce : types) {
                    if (!ce.isAnonymous()) {
                        if (isWatchedByAncestor(classHierarchyProvider.getAncestors(ce.asOWLClass()))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Boolean visit(OWLDatatype datatype) {
                return false;
            }

            @Override
            public Boolean visit(OWLAnnotationProperty property) {
                return isWatchedByAncestor(annotationPropertyHierarchyProvider.getAncestors(property));
            }

            private Boolean isWatchedByAncestor(Set<? extends OWLEntity> ancestors) {
                for (OWLEntity anc : ancestors) {
                    if (watchedAncestors.contains(anc)) {
                        return true;
                    }
                }
                return false;
            }

        });
    }

}
