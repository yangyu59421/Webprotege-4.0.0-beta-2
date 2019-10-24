package edu.stanford.bmir.protege.web.server.frame;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.ChangeDescriptionGenerator;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.change.ReverseEngineeredChangeDescriptionGeneratorFactory;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.frame.EntityFrame;
import edu.stanford.bmir.protege.web.shared.frame.UpdateFrameAction;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/02/2013
 */
public abstract class AbstractUpdateFrameHandler<A extends UpdateFrameAction<F, S>, F extends EntityFrame<S>,  S extends OWLEntityData> extends AbstractProjectActionHandler<A, Result> implements ActionHandler<A, Result> {

    @Nonnull
    private final ReverseEngineeredChangeDescriptionGeneratorFactory changeDescriptionGeneratorFactory;

    @Nonnull
    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges applyChanges;

    @Nonnull
    private final OWLOntology rootOntology;

    public AbstractUpdateFrameHandler(@Nonnull AccessManager accessManager,
                                      @Nonnull ReverseEngineeredChangeDescriptionGeneratorFactory changeDescriptionGeneratorFactory,
                                      @Nonnull EventManager<ProjectEvent<?>> eventManager,
                                      @Nonnull HasApplyChanges applyChanges,
                                      @Nonnull OWLOntology rootOntology) {
        super(accessManager);
        this.changeDescriptionGeneratorFactory = changeDescriptionGeneratorFactory;
        this.eventManager = eventManager;
        this.applyChanges = applyChanges;
        this.rootOntology = rootOntology;
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.EDIT_ONTOLOGY;
    }

    /**
     * Executes the specified action, against the specified project in the specified context.
     * @param action The action to be handled/executed
     * @param executionContext The {@link edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext} that should be
     * used to provide details such as the
     * {@link edu.stanford.bmir.protege.web.shared.user.UserId} of the user who requested the action be executed.
     * @return The result of the execution to be returned to the client.
     */
    @Nonnull
    @Override
    public Result execute(@Nonnull A action, @Nonnull ExecutionContext executionContext) {
        F from = action.getFrom();
        F to = action.getTo();
        final EventTag startTag = eventManager.getCurrentTag();
        if(from.equals(to)) {
            return createResponse(action.getTo(), eventManager.getEventsFromTag(startTag));
        }

        UserId userId = executionContext.getUserId();

        FrameTranslator<F, S> translator = createTranslator();

        final FrameChangeGenerator<F, S> changeGenerator = new FrameChangeGenerator<>(from, to,
                                                                                      translator,
                                                                                      rootOntology,
                                                                                      changeDescriptionGeneratorFactory);
        ChangeDescriptionGenerator<S> generator = changeDescriptionGeneratorFactory.get("Edited " + from.getSubject().getBrowserText());
        applyChanges.applyChanges(userId, changeGenerator);
        EventList<ProjectEvent<?>> events = eventManager.getEventsFromTag(startTag);
        return createResponse(action.getTo(), events);
    }

//    private void applyChangesToChangeDisplayName(OWLAPIProject project, ExecutionContext executionContext, F from, F to, UserId userId) {
//        // Set changes
//        EntityCrudKitHandler<?, ChangeSetEntityCrudSession> entityEditorKit = project.getEntityCrudKitHandler();
//        ChangeSetEntityCrudSession session = entityEditorKit.createChangeSetSession();
//        OntologyChangeList.Builder<S> changeListBuilder = new OntologyChangeList.Builder<>();
//        entityEditorKit.update(session, to.getFrame().getSubject(),
//                                 EntityShortForm.get(to.getTitle()),
//                                 project.getEntityCrudContext(executionContext.getUserId()),
//                                 changeListBuilder);
//        FixedChangeListGenerator<S> changeListGenerator = FixedChangeListGenerator.get(changeListBuilder.build().getChanges());
//        String typePrintName = from.getFrame().getSubject().getEntityType().getPrintName().toLowerCase();
//        FixedMessageChangeDescriptionGenerator<S> changeDescriptionGenerator =
//                FixedMessageChangeDescriptionGenerator.get("Renamed the " + typePrintName + " " + from.getTitle() + " to " + to.getTitle());
//        project.applyChanges(userId, changeListGenerator, changeDescriptionGenerator);
//    }

    protected abstract Result createResponse(F to, EventList<ProjectEvent<?>> events);

    protected abstract FrameTranslator<F, S> createTranslator();

    protected abstract String getChangeDescription(F from, F to);
}
