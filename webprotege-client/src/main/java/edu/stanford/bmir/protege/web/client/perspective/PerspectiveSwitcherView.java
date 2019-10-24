package edu.stanford.bmir.protege.web.client.perspective;


import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.shared.perspective.PerspectiveId;

import java.util.List;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 23/06/2014
 */
public interface PerspectiveSwitcherView extends IsWidget {

    /**
     * Handles notifications for when a perspective link has been activated.
     */
    interface PerspectiveLinkActivatedHandler {
        void handlePerspectiveLinkActivated(PerspectiveId perspectiveId);
    }

    /**
     * Handles request to add a new perspective link.
     */
    interface AddPerspectiveLinkRequestHandler {
        void handleAddNewPerspectiveLinkRequest();
    }

    interface AddBookmarkedPerspectiveLinkHandler {
        void handleAddBookmarkedPerspective(PerspectiveId perspectiveId);
    }

    interface RemovePerspectiveLinkRequestHandler {
        void handleRemovePerspectiveLinkRequest(PerspectiveId perspectiveId);
    }

    interface ResetPerspectiveToDefaultStateHandler {
        void handleResetPerspectiveToDefaultState(PerspectiveId perspectiveId);
    }

    interface AddViewHandler {
        void handleAddViewToPerspective(PerspectiveId perspectiveId);
    }

    /**
     * Sets the selected perspective link.  This link will be visually distinguished from the other perspective
     * links in the view.
     * @param perspectiveId The {@link PerspectiveId}.  Not {@code null}.
     * @throws NullPointerException if {@code perspectiveId} is {@code null}.
     */
    void setHighlightedPerspective(PerspectiveId perspectiveId);

    /**
     * Sets the perspective links that are displayed by this switcher.
     * @param perspectives The list of perspective links that are displayed.  Not {@code null}.
     */
    void setPerspectiveLinks(List<PerspectiveId> perspectives);

    void addPerspectiveLink(PerspectiveId perspectiveId);

    void removePerspectiveLink(PerspectiveId perspectiveId);

    /**
     * Gets the perspectives that are linked to by this switcher.
     * @return The perspective links.  Modifying the collection will not change the underlying displayed links in the
     * view.
     */
    List<PerspectiveId> getPerspectiveLinks();

    /**
     * Sets the handler that will be called when a perspective link is activated.
     * @param handler The handler.  Not {@code null}.
     */
    void setPerspectiveLinkActivatedHandler(PerspectiveLinkActivatedHandler handler);

    /**
     * Sets the handler that will be called when a request to add a new perspective link is made by the user.
     * @param handler The handler.  Not {@code null}.
     */
    void setAddPerspectiveLinkRequestHandler(AddPerspectiveLinkRequestHandler handler);

    void setAddBookMarkedPerspectiveLinkHandler(AddBookmarkedPerspectiveLinkHandler handler);

    /**
     * Sets the handler that will be called when a request to remove a perspective link is made by the user.
     * @param handler  The handler.  Not {@code null}.
     */
    void setRemovePerspectiveLinkHandler(RemovePerspectiveLinkRequestHandler handler);

    void setResetPerspectiveToDefaultStateHandler(ResetPerspectiveToDefaultStateHandler handler);

    void setAddViewHandler(AddViewHandler handler);

    void setBookmarkedPerspectives(List<PerspectiveId> perspectives);

    void setAddPerspectiveAllowed(boolean addPerspectiveAllowed);

    void setClosePerspectiveAllowed(boolean closePerspectiveAllowed);

    void setAddViewAllowed(boolean addViewAllowed);
}
