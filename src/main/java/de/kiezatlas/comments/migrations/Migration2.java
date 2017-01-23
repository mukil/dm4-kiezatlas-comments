package de.kiezatlas.comments.migrations;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.core.AssociationType;
import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.core.service.accesscontrol.SharingMode;
import de.deepamehta.workspaces.WorkspacesService;
import de.kiezatlas.comments.CommentsService;

import java.util.logging.Logger;


/**
 * Revise the concept/implementation of the district editorial aspects.
 * @author malted
 */
public class Migration2 extends Migration {

    private Logger log = Logger.getLogger(getClass().getName());

    static final String KIEZATLAS_WORKSPACE_URI = "de.kiezatlas.workspace";

    @Inject private WorkspacesService workspaceService;
    @Inject private AccessControlService acService;

    @Override
    public void run() {
        log.info("### Migration 2: Installing Kiezatlas Comments Workspace and Types in Database ###");
        // 1) Create new "Comments" workspace
        Topic workspace = workspaceService.createWorkspace(CommentsService.COMMENTS_WORKSPACE_NAME,
                CommentsService.COMMENTS_WORKSPACE_URI, SharingMode.COLLABORATIVE);
        acService.setWorkspaceOwner(workspace, AccessControlService.ADMIN_USERNAME);
        // 2) Assign all our types from migration1 to the "Kiezatlas" workspace so "admin" can edit these definitions
        Topic kiezatlas = workspaceService.getWorkspace(KIEZATLAS_WORKSPACE_URI);
        TopicType comment = dm4.getTopicType(CommentsService.COMMENT);
        TopicType commentMessage = dm4.getTopicType(CommentsService.COMMENT_MESSAGE);
        TopicType commentContact = dm4.getTopicType(CommentsService.COMMENT_CONTACT);
        AssociationType assignmentEdge = dm4.getAssociationType(CommentsService.COMMENT_ASSIGNMENT);
        workspaceService.assignTypeToWorkspace(comment, kiezatlas.getId());
        workspaceService.assignTypeToWorkspace(commentMessage, kiezatlas.getId());
        workspaceService.assignTypeToWorkspace(commentContact, kiezatlas.getId());
        workspaceService.assignTypeToWorkspace(assignmentEdge, kiezatlas.getId());
        log.info("### Migration 2: Successfully installed Kiezatlas Comments Plugin ### ");
    }
}