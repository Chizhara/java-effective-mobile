package effective.mobile.com.service;

import effective.mobile.com.model.task.Task;
import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.TaskStatus;
import effective.mobile.com.model.task.dto.TaskCreateRequest;
import effective.mobile.com.model.task.dto.TaskInfo;
import effective.mobile.com.model.task.dto.TaskSearchParams;
import effective.mobile.com.model.task.dto.TaskShortInfo;
import effective.mobile.com.model.task.dto.TaskUpdateRequest;
import effective.mobile.com.model.user.User;
import effective.mobile.com.repository.TaskRepository;
import effective.mobile.com.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskServiceTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> database =
        new PostgreSQLContainer<>("postgres:16-alpine");
    private static int taskIndex = 0;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public static Task initTask(User creator, User performer, TaskPriority taskPriority) {
        taskIndex++;
        return Task.builder()
            .name("task_" + taskIndex)
            .description("task description num " + taskIndex)
            .creator(creator)
            .performer(performer)
            .priority(taskPriority)
            .build();
    }

    private User generateUser() {
        User user = UserServiceTest.initUser();
        return userRepository.save(user);
    }

    private TaskInfo generateTask(User creator, User performer, TaskPriority taskPriority) {
        Task task = initTask(creator, performer, taskPriority);
        TaskCreateRequest request = TaskCreateRequest.builder()
            .name(task.getName())
            .description(task.getDescription())
            .priority(task.getPriority())
            .performer(performer.getId())
            .build();
        return taskService.addTask(request, creator);
    }

    @Test
    public void testAdd_shouldReturnTaskInfo_whenCorrect() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = initTask(creator, performer, TaskPriority.LOW);

        TaskCreateRequest request = TaskCreateRequest.builder()
            .name(task.getName())
            .description(task.getDescription())
            .priority(task.getPriority())
            .performer(performer.getId())
            .build();

        TaskInfo res = taskService.addTask(request, creator);

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(res.getCreator().getId(), creator.getId());
        assertEquals(res.getPerformer().getId(), performer.getId());
        assertEquals(res.getPriority(), task.getPriority());
        assertEquals(res.getName(), task.getName());
        assertEquals(res.getDescription(), task.getDescription());
        assertTrue(res.getCreated().isAfter(LocalDateTime.now().minusMinutes(5))
            && res.getCreated().isBefore(LocalDateTime.now().plusMinutes(5)));
    }

    @Test
    public void testGet_shouldReturnTaskInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        TaskInfo task = generateTask(creator, performer, TaskPriority.HIGH);

        TaskInfo res = taskService.getTaskInfo(task.getId());

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(res.getCreator().getId(), creator.getId());
        assertEquals(res.getPerformer().getId(), performer.getId());
        assertEquals(res.getPriority(), task.getPriority());
        assertEquals(res.getName(), task.getName());
        assertEquals(res.getDescription(), task.getDescription());
        assertEquals(res.getCreated(), task.getCreated());
    }

    @Test
    public void testRemove_shouldReturnTaskInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        TaskInfo task = generateTask(creator, performer, TaskPriority.HIGH);

        TaskInfo res = taskService.removeTask(task.getId(), creator);

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(res.getCreator().getId(), creator.getId());
        assertEquals(res.getPerformer().getId(), performer.getId());
        assertEquals(res.getPriority(), task.getPriority());
        assertEquals(res.getName(), task.getName());
        assertEquals(res.getDescription(), task.getDescription());
        assertTrue(res.getCreated().equals(task.getCreated()));

        assertTrue(taskRepository.findById(task.getId()).isEmpty());
    }

    @Test
    public void testUpdate_shouldReturnTaskInfo_whenCorrect() {
        User creator = generateUser();
        User performer = generateUser();
        TaskInfo task = generateTask(creator, performer, TaskPriority.HIGH);

        User newPerformer = generateUser();
        task.setName(task.getName() + "_updated");
        task.setDescription(task.getDescription() + "_updated");
        task.setStatus(TaskStatus.IN_PROCESS);
        task.setPriority(TaskPriority.LOW);

        TaskUpdateRequest request = TaskUpdateRequest.builder()
            .name(task.getName())
            .description(task.getDescription())
            .status(task.getStatus())
            .priority(task.getPriority())
            .performer(newPerformer.getId())
            .build();

        TaskInfo res = taskService.updateTask(task.getId(), request, creator);

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(res.getCreator().getId(), creator.getId());
        assertEquals(res.getPerformer().getId(), newPerformer.getId());
        assertEquals(res.getPriority(), task.getPriority());
        assertEquals(res.getName(), task.getName());
        assertEquals(res.getDescription(), task.getDescription());
        assertEquals(res.getCreated(), task.getCreated());
    }

    @Test
    public void testSearch_shouldReturnTasksInfo_whenExist() {
        User creator = generateUser();
        User performer = generateUser();

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Task task = initTask(creator, performer, TaskPriority.HIGH);
            task.setName(task.getName() + "_searched");
            task.setDescription(task.getDescription() + "_searched");
            tasks.add(task);
        }

        for (int i = 4; i >= 0; i--) {
            TaskCreateRequest request = TaskCreateRequest.builder()
                .name(tasks.get(i).getName())
                .description(tasks.get(i).getDescription())
                .priority(tasks.get(i).getPriority())
                .performer(performer.getId())
                .build();
            TaskInfo res = taskService.addTask(request, creator);
            tasks.get(i).setId(res.getId());
        }

        TaskSearchParams request = TaskSearchParams.builder()
            .status(TaskStatus.WAITING)
            .text("searched")
            .priority(TaskPriority.HIGH)
            .creatorId(creator.getId())
            .performerId(performer.getId())
            .order(Set.of(TaskSearchParams.OrderField.NAME))
            .build();

        List<TaskShortInfo> res = taskService.searchTasksInfo(request, Pageable.ofSize(3));

        assertNotNull(res);
        assertEquals(res.size(), 3);
        assertArrayEquals(tasks.subList(0, 3).stream().map(Task::getId).toArray(),
            res.stream().map(TaskShortInfo::getId).toArray());
    }
}
