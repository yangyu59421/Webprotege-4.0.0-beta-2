package edu.stanford.bmir.protege.web.server.perspective;

import edu.stanford.bmir.protege.web.server.inject.project.ProjectDirectoryFactory;
import edu.stanford.bmir.protege.web.shared.auth.Md5MessageDigestAlgorithm;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/02/16
 */
public class PerspectiveFileManager {

    public static final String EXTENSION = ".json";

    public static final String PERSPECTIVE_DATA_DIRECTORY_NAME = "perspective-data";

    public static final String PERSPECTIVE_LIST_FILE_NAME = "perspective.list.json";

    private final File defaultPerspectivesDirectory;

    private final Provider<Md5MessageDigestAlgorithm> algorithmProvider;

    private final ProjectDirectoryFactory projectDirectoryFactory;

    private final Logger logger = LoggerFactory.getLogger(PerspectiveFileManager.class);

    @Inject
    public PerspectiveFileManager(@DefaultPerspectiveDataDirectory File defaultPerspectivesDirectory,
                                  ProjectDirectoryFactory projectDirectoryFactory,
                                  Provider<Md5MessageDigestAlgorithm> algorithmProvider) {
        this.defaultPerspectivesDirectory = defaultPerspectivesDirectory;
        this.projectDirectoryFactory = projectDirectoryFactory;
        this.algorithmProvider = algorithmProvider;
    }

    public File getDefaultPerspectiveLayout(PerspectiveId perspectiveId) {
        copyDefaultPerspectiveDataIfNecessary();
        // Location: default-perspectives/{{PerspectiveIdDigest}}.json
        return new File(defaultPerspectivesDirectory, getPerspectiveIdFileName(perspectiveId));
    }

    private synchronized void copyDefaultPerspectiveDataIfNecessary() {
        if(!defaultPerspectivesDirectory.exists() || !getDefaultPerspectiveList().exists()) {
            PerspectiveDataCopier copier = new PerspectiveDataCopier(defaultPerspectivesDirectory);
            copier.copyDefaultPerspectiveData();
        }
    }

    public File getDefaultPerspectiveLayoutForProject(ProjectId projectId, PerspectiveId perspectiveId) {
        //  Location:  {{ProjectId}}/perspective-data/default/{{PerspectiveIdDigest}}.json
        File defaultDirectory = new File(getPerspectiveDataDirectory(projectId), "default");
        return new File(defaultDirectory, getPerspectiveIdFileName(perspectiveId));
    }

    public File getPerspectiveLayoutForUser(ProjectId projectId, PerspectiveId perspectiveId, UserId userId) {
        //  Location:  {{ProjectId}}/perspective-data/{{UserIdDigest}}/{{PerspectiveIdDigest}}.json
        File userDirectory = getUserDirectory(projectId, userId);
        return new File(userDirectory, getPerspectiveIdFileName(perspectiveId));
    }

    public File getDefaultPerspectiveList() {
        return new File(defaultPerspectivesDirectory, PERSPECTIVE_LIST_FILE_NAME);
    }

    public File getDefaultPerspectiveListForProject(ProjectId projectId) {
        return new File(getPerspectiveDataDirectory(projectId), PERSPECTIVE_LIST_FILE_NAME);
    }

    public File getPerspectiveListForUser(ProjectId projectId, UserId userId) {
        File userDirectory = getUserDirectory(projectId, userId);
        return new File(userDirectory, PERSPECTIVE_LIST_FILE_NAME);
    }




    private File getUserDirectory(ProjectId projectId, UserId userId) {
        String userIdDigest = getUserIdDigest(userId);
        return new File(getPerspectiveDataDirectory(projectId), userIdDigest);
    }

    private File getPerspectiveDataDirectory(ProjectId projectId) {
        return new File(projectDirectoryFactory.getProjectDirectory(projectId), PERSPECTIVE_DATA_DIRECTORY_NAME);
    }

    private String getPerspectiveIdFileName(PerspectiveId perspectiveId) {
        return getPerspectiveIdDigest(perspectiveId) + EXTENSION;
    }

    private String getPerspectiveIdDigest(PerspectiveId perspectiveId) {
        String id = perspectiveId.getId();
        return getDigest(id);
    }

    private String getUserIdDigest(UserId userId) {
        String userName = userId.getUserName();
        return getDigest(userName);
    }

    private String getDigest(String value) {
        Md5MessageDigestAlgorithm algorithm = algorithmProvider.get();
        algorithm.updateWithBytesFromUtf8String(value);
        return algorithm.computeDigestAsBase16Encoding();
    }

}
