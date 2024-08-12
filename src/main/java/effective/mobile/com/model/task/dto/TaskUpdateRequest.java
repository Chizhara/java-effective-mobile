package effective.mobile.com.model.task.dto;

import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TaskUpdateRequest {
    private String name;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID performer;
}
