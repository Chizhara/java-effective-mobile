package effective.mobile.com.mapper;

import effective.mobile.com.model.comment.Comment;
import effective.mobile.com.model.comment.dto.CommentCreateRequest;
import effective.mobile.com.model.comment.dto.CommentInfo;
import effective.mobile.com.model.comment.dto.CommentUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = {UserMapper.class, TaskMapper.class})
public interface CommentMapper {

    Comment toComment(CommentCreateRequest request);

    Comment updateComment(@MappingTarget Comment comment, CommentUpdateRequest request);

    @Mapping(target = "created", expression = "java(comment.getCreated().toLocalDateTime())")
    @Mapping(target = "lastUpdated",
        expression = "java(comment.getLastUpdated() != null ? comment.getLastUpdated().toLocalDateTime() : null )")
    CommentInfo toCommentInfo(Comment comment);

    List<CommentInfo> toCommentsInfo(List<Comment> comment);
}
