package edu.stanford.bmir.protege.web.client.inject;

import dagger.Subcomponent;
import edu.stanford.bmir.protege.web.client.collection.CollectionPresenter;
import edu.stanford.bmir.protege.web.client.lang.DefaultDisplayNameSettingsPresenter;
import edu.stanford.bmir.protege.web.client.primitive.PrimitiveDataEditorClientModule;
import edu.stanford.bmir.protege.web.client.project.ProjectPrefixDeclarationsPresenter;
import edu.stanford.bmir.protege.web.client.project.ProjectPresenter;
import edu.stanford.bmir.protege.web.client.sharing.SharingSettingsPresenter;
import edu.stanford.bmir.protege.web.client.tag.ProjectTagsPresenter;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.client.projectsettings.ProjectSettingsPresenter;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Oct 2016
 */
@Subcomponent(
        modules = {
                ClientProjectModule.class,
                ClientIssuesModule.class,
                PrimitiveDataEditorClientModule.class
        }
)
@ProjectSingleton
public interface ClientProjectComponent {

    ProjectPresenter getProjectPresenter();

    SharingSettingsPresenter getSharingSettingsPresenter();

    ProjectSettingsPresenter getProjectSettingsPresenter();

    ProjectPrefixDeclarationsPresenter getProjectPrefixesPresenter();

    CollectionPresenter getCollectionPresenter();

    ProjectTagsPresenter getProjectTagsPresenter();

    DefaultDisplayNameSettingsPresenter getLanguageSettingsPresenter();
}
