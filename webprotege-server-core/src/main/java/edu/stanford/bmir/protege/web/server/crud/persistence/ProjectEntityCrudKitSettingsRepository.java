package edu.stanford.bmir.protege.web.server.crud.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.crud.persistence.ProjectEntityCrudKitSettingsConverter.withProjectId;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 21/08/2013
 * <p>
 * An interface to a repository for storing {@link ProjectEntityCrudKitSettings}.
 * </p>
 */
@ProjectSingleton
public class ProjectEntityCrudKitSettingsRepository {

    public static final String COLLECTION_NAME = "EntityCrudKitSettings";

    @Nonnull
    private final MongoCollection<Document> collection;

    @Nonnull
    private final ProjectEntityCrudKitSettingsConverter converter;

    @Inject
    public ProjectEntityCrudKitSettingsRepository(@Nonnull MongoDatabase database,
                                                  @Nonnull ProjectEntityCrudKitSettingsConverter converter) {
        this.collection = checkNotNull(database).getCollection(COLLECTION_NAME);
        this.converter = checkNotNull(converter);
    }

    @Nonnull
    public Optional<ProjectEntityCrudKitSettings> findOne(@Nonnull ProjectId projectId) {
        return Optional.ofNullable(collection.find(withProjectId(projectId))
                                             .limit(1).first())
                       .map(converter::fromDocument);
    }

    public void save(@Nonnull ProjectEntityCrudKitSettings settings) {
        collection.replaceOne(withProjectId(settings.getProjectId()),
                              converter.toDocument(checkNotNull(settings)),
                              new UpdateOptions().upsert(true));
    }
}
