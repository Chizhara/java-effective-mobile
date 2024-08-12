package effective.mobile.com.model.task.dto;

import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.TaskStatus;
import effective.mobile.com.model.user.dto.UserShortInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskInfo {
    private UUID id;
    private String name;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UserShortInfo creator;
    private UserShortInfo performer;
    private LocalDateTime created;
}
