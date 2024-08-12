package effective.mobile.com.model.comment.dto;

import effective.mobile.com.model.task.dto.TaskShortInfo;
import effective.mobile.com.model.user.dto.UserShortInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentInfo {
    private UUID id;
    private String text;
    private UserShortInfo author;
    private LocalDateTime created;
    private LocalDateTime lastUpdated;
    private TaskShortInfo task;
}
