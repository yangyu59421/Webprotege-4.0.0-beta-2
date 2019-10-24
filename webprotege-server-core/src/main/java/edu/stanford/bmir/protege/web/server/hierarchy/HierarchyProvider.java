package edu.stanford.bmir.protege.web.server.hierarchy;

import java.util.List;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/03/15
 */
public interface HierarchyProvider<N> extends HasGetAncestors<N> {

    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    Set<N> getRoots();

    Set<N> getChildren(N object);


    Set<N> getDescendants(N object);


    Set<N> getParents(N object);


    Set<N> getAncestors(N object);


    Set<N> getEquivalents(N object);


    Set<List<N>> getPathsToRoot(N object);


    boolean containsReference(N object);


    void addListener(OWLObjectHierarchyProviderListener<N> listener);


    void removeListener(OWLObjectHierarchyProviderListener<N> listener);

}
