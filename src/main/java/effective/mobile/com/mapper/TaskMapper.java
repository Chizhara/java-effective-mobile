package effective.mobile.com.mapper;

import effective.mobile.com.model.task.Task;
import effective.mobile.com.model.task.dto.TaskCreateRequest;
import effective.mobile.com.model.task.dto.TaskInfo;
import effective.mobile.com.model.task.dto.TaskShortInfo;
import effective.mobile.com.model.task.dto.TaskUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = UserMapper.class)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priority", defaultValue = "MEDIUM")
    @Mapping(target = "performer", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task toTask(TaskCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "performer", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task updateTask(@MappingTarget Task task, TaskUpdateRequest request);

    TaskInfo toTaskInfo(Task task);

    TaskShortInfo toTaskShortInfo(Task task);

    List<TaskShortInfo> toTasksShortInfo(List<Task> task);
}
