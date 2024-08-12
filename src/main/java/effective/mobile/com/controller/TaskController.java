package effective.mobile.com.controller;

import effective.mobile.com.model.task.dto.TaskCreateRequest;
import effective.mobile.com.model.task.dto.TaskInfo;
import effective.mobile.com.model.task.dto.TaskSearchParams;
import effective.mobile.com.model.task.dto.TaskShortInfo;
import effective.mobile.com.model.task.dto.TaskUpdateRequest;
import effective.mobile.com.service.TaskService;
import effective.mobile.com.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task")
public class TaskController extends CommonController {

    private final TaskService taskService;

    public TaskController(UserService userService, TaskService taskService) {
        super(userService);
        this.taskService = taskService;
    }

    @Operation(
        summary = "Добавление новой задачи"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskInfo addTask(@RequestBody TaskCreateRequest request) {
        return taskService.addTask(request, getUser());
    }

    @Operation(
        summary = "Обновление задачи",
        description = "Обновление значений полей существующей задачи с указанным идентификатором"
    )
    @PatchMapping("/{taskId}")
    public TaskInfo updateTask(@RequestBody TaskUpdateRequest request,
                               @PathVariable UUID taskId) {
        return taskService.updateTask(taskId, request, getUser());
    }

    @Operation(
        summary = "Удаление задачи",
        description = "Удаление сущетсвующей задачи с указанным идентификатором"
    )
    @DeleteMapping("/{taskId}")
    public TaskInfo removeTask(@PathVariable UUID taskId) {
        return taskService.removeTask(taskId, getUser());
    }

    @Operation(
        summary = "Получение задачи",
        description = "Поиск данных сущетсвующей задачи с указанным идентификатором"
    )
    @GetMapping("/{taskId}")
    public TaskInfo getTask(@PathVariable UUID taskId) {
        return taskService.getTaskInfo(taskId);
    }

    @Operation(
        summary = "Получение задачи",
        description = "Поиск данных о задачах в соответствии с указанными правилами фильтрации и сортировки"
    )
    @GetMapping
    public List<TaskShortInfo> searchTasks(@ParameterObject TaskSearchParams params,
                                           @ParameterObject Pageable pageable) {
        return taskService.searchTasksInfo(params, pageable);
    }

}
