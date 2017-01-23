package de.kiezatlas.comments;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import java.util.List;



public interface CommentsService {

    static final String COMMENTS_WORKSPACE_NAME     = "Comments";
    static final String COMMENTS_WORKSPACE_URI      = "de.kiezatlas.ws_comments";

    static final String COMMENT                     = "ka2.comment";
    static final String COMMENT_MESSAGE             = "ka2.comment.message";
    static final String COMMENT_CONTACT             = "ka2.comment.contact";

    static final String COMMENT_ASSIGNMENT          = "ka2.comment.assignment";
    
    // --- Comments API
    
    void createComment(long topicId, String message, String contact);

    List<RelatedTopic> getComments(long topicId);

    RelatedTopic getMessage(Topic comment);

    RelatedTopic getContact(Topic comment);

    // ---
    
    boolean isCommentsWorkspaceMember();

}
