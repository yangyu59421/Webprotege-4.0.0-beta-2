package edu.stanford.bmir.protege.web.server.perspective;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.ProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.shared.perspective.GetPerspectivesAction;
import edu.stanford.bmir.protege.web.shared.perspective.GetPerspectivesResult;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18/02/16
 */
public class GetPerspectivesActionHandler implements ProjectActionHandler<GetPerspectivesAction, GetPerspectivesResult> {

    private final PerspectivesManager perspectivesManager;

    @Inject
    public GetPerspectivesActionHandler(PerspectivesManager perspectivesManager) {
        this.perspectivesManager = perspectivesManager;
    }

    @Nonnull
    @Override
    public Class<GetPerspectivesAction> getActionClass() {
        return GetPerspectivesAction.class;
    }

    @Nonnull
    @Override
    public RequestValidator getRequestValidator(@Nonnull GetPerspectivesAction action, @Nonnull RequestContext requestContext) {
        return NullValidator.get();
    }

    @Nonnull
    @Override
    public GetPerspectivesResult execute(@Nonnull GetPerspectivesAction action, @Nonnull ExecutionContext executionContext) {
        ProjectId projectId = action.getProjectId();
        UserId userId = action.getUserId();
        ImmutableList<PerspectiveId> perspectives = perspectivesManager.getPerspectives(projectId, userId);
        return new GetPerspectivesResult(perspectives);
    }
}
