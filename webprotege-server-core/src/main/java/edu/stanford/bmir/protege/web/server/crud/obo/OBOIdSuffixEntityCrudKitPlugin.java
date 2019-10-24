package edu.stanford.bmir.protege.web.server.crud.obo;

import edu.stanford.bmir.protege.web.server.crud.EntityCrudKitHandler;
import edu.stanford.bmir.protege.web.server.crud.EntityCrudKitPlugin;
import edu.stanford.bmir.protege.web.shared.crud.EntityCrudKit;
import edu.stanford.bmir.protege.web.shared.crud.EntityCrudKitPrefixSettings;
import edu.stanford.bmir.protege.web.shared.crud.EntityCrudKitSettings;
import edu.stanford.bmir.protege.web.shared.crud.oboid.OBOIdSuffixKit;
import edu.stanford.bmir.protege.web.shared.crud.oboid.OBOIdSuffixSettings;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 8/19/13
 */
public class OBOIdSuffixEntityCrudKitPlugin implements EntityCrudKitPlugin<OBOIdSuffixEntityCrudKitHandler, OBOIdSuffixSettings, OBOIdSession> {

    @Nonnull
    private final OBOIdSuffixKit kit;

    @Inject
    public OBOIdSuffixEntityCrudKitPlugin(@Nonnull OBOIdSuffixKit kit) {
        this.kit = checkNotNull(kit);
    }

    @Override
    public EntityCrudKit<OBOIdSuffixSettings> getEntityCrudKit() {
        return kit;
    }

    @Override
    public EntityCrudKitHandler<OBOIdSuffixSettings, OBOIdSession> getEntityCrudKitHandler() {
        return new OBOIdSuffixEntityCrudKitHandler(new EntityCrudKitPrefixSettings(), new OBOIdSuffixSettings());
    }

    @Override
    public EntityCrudKitHandler<OBOIdSuffixSettings, OBOIdSession> getEntityCrudKitHandler(EntityCrudKitSettings<OBOIdSuffixSettings> settings) {
        return new OBOIdSuffixEntityCrudKitHandler(settings.getPrefixSettings(), settings.getSuffixSettings());
    }

    @Override
    public OBOIdSuffixSettings getDefaultSettings() {
        return new OBOIdSuffixSettings();
    }
}
