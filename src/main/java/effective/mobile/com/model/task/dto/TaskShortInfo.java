package effective.mobile.com.model.task.dto;

import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.TaskStatus;
import effective.mobile.com.model.user.dto.UserShortInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskShortInfo {
    private UUID id;
    private String name;
    private TaskStatus status;
    private TaskPriority priority;
    private UserShortInfo creator;
    private UserShortInfo performer;
    private LocalDateTime created;
}
