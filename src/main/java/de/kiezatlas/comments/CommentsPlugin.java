package de.kiezatlas.comments;

import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import de.deepamehta.core.service.Inject;
import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.core.Association;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.workspaces.WorkspacesService;
import java.util.List;

/**
 * <a href="http://github.com/mukil/dm4-kiezatlas-comments">Source Code</a>
 *
 * @author Malte Reißig (<a href="mailto:malte@mikromedia.de">Contact</a>)
 * @version 1.0-SNAPSHOT
 */
@Path("/comment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentsPlugin extends PluginActivator implements CommentsService {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject private WorkspacesService workspaces;
    @Inject private AccessControlService accesscl;
    
    Topic workspace = null;

    /**
     *
     * @param topicId
     * @return
     */
    @Override
    public List<RelatedTopic> getComments(long topicId) {
        Topic topic = dm4.getTopic(topicId);
        return topic.getRelatedTopics(COMMENT_ASSIGNMENT);
    }

    @Override
    public Topic createComment(long topicId, String message, String contact) {
        Topic comment = null;
        if (message == null || topicId == 0) {
            log.warning("Could not create comment due to empty comment message or missing topic id");
        } else if (isCommentsWorkspaceMember()) {
            comment = doCreateComment(topicId, message, contact);
        } else {
            log.warning("Could not create comment cause: Requesting user ("
                    + accesscl.getUsername() + ") is not a member of the Comments workspace.");
        }
        return comment;
    }

    private Topic doCreateComment(long topicId, String message, String contact) {
        Topic comment = null;
        // Try to read topic with given id
        Topic topic = dm4.getTopic(topicId);
        // Create comment topic and assign it
        ChildTopicsModel model = mf.newChildTopicsModel();
        model.put(COMMENT_MESSAGE, message);
        // Optionally Add: Comment Contact
        if (contact != null) {
            model.put(COMMENT_CONTACT, contact);
        }
        // Create new Comment Topic and Assign it
        TopicModel topicModel = mf.newTopicModel(COMMENT, model);
        comment = dm4.createTopic(topicModel);
        Association assignment = createCommentAssignment(topic, comment);
        // Do workspace assignment...
        assignCommentToWorkspace(comment, assignment);
        // Do user assignment
        Association userAssoc = createCommentToUsername(comment);
        workspaces.assignToWorkspace(userAssoc, getCommentsWorkspaceId());
        log.info("Comment via \"" + accesscl.getUsername() + "\" succesfully created in Comments workspace");
        return comment;
    }

    private void assignCommentToWorkspace(Topic comment, Association assignment) {
        log.info("Moving new comment into \"Comments\" workspace");
        RelatedTopic messageTopic = getMessage(comment);
        RelatedTopic contactTopic = getContact(comment);
        // Assign comment and assignment to "Comments" workspace
        workspaces.assignToWorkspace(comment, getCommentsWorkspaceId());
        workspaces.assignToWorkspace(messageTopic, getCommentsWorkspaceId());
        workspaces.assignToWorkspace(messageTopic.getRelatingAssociation(), getCommentsWorkspaceId());
        // Optional: Comment Contact
        if (contactTopic != null) {
            workspaces.assignToWorkspace(contactTopic, getCommentsWorkspaceId());
            workspaces.assignToWorkspace(contactTopic.getRelatingAssociation(), getCommentsWorkspaceId());
        }
        workspaces.assignToWorkspace(assignment, getCommentsWorkspaceId());
        log.info("Moved new comment into \"Comments\" workspace");
    }
    
    private Association createCommentToUsername(Topic comment) {
        Topic username = accesscl.getUsernameTopic();
        return dm4.createAssociation(mf.newAssociationModel("dm4.core.association",
                mf.newTopicRoleModel(username.getId(), "dm4.core.parent"),
                mf.newTopicRoleModel(comment.getId(), "dm4.core.child")));
    }

    @Override
    public RelatedTopic getMessage(Topic comment) {
        return comment.getChildTopics().getTopicOrNull(COMMENT_MESSAGE);
    }

    @Override
    public RelatedTopic getContact(Topic comment) {
        return comment.getChildTopics().getTopicOrNull(COMMENT_CONTACT);
    }

    @Override
    public long getCommentsWorkspaceId() {
        if (workspace == null) {
            workspace = dm4.getTopicByUri(COMMENTS_WORKSPACE_URI);
        }
        return workspace.getId();
    }

    @Override
    public boolean isCommentsWorkspaceMember() {
        // ## use hasReadPermission as this is a confidential ws
        return accesscl.isMember(accesscl.getUsername(), getCommentsWorkspaceId());
    }

    @Override
    public boolean isCommentsWorkspaceMember(String username) {
        // ### use hasReadPermission as this is a confidential ws
        return accesscl.isMember(username, getCommentsWorkspaceId());
    }

    /**
     * Private helper to create a comment assignment. Returns assoc for custom workspace assignments.
     * @param topic
     * @param comment
     * @return Association object representing the newly assigned comment.
     */
    private Association createCommentAssignment(Topic topic, Topic comment) {
        return dm4.createAssociation(mf.newAssociationModel(COMMENT_ASSIGNMENT,
                mf.newTopicRoleModel(topic.getId(), "dm4.core.parent"),
                mf.newTopicRoleModel(comment.getId(), "dm4.core.child")));
    }

}
