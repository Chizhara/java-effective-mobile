package effective.mobile.com.model.task.dto;

import effective.mobile.com.model.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TaskCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private TaskPriority priority;
    private UUID performer;
}
