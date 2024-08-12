package effective.mobile.com.service;

import effective.mobile.com.model.comment.Comment;
import effective.mobile.com.model.comment.dto.CommentCreateRequest;
import effective.mobile.com.model.comment.dto.CommentInfo;
import effective.mobile.com.model.comment.dto.CommentSearchParams;
import effective.mobile.com.model.comment.dto.CommentUpdateRequest;
import effective.mobile.com.model.task.Task;
import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.dto.TaskCreateRequest;
import effective.mobile.com.model.task.dto.TaskInfo;
import effective.mobile.com.model.user.User;
import effective.mobile.com.repository.CommentRepository;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentServiceTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> database =
        new PostgreSQLContainer<>("postgres:16-alpine");
    private static int commentIndex = 0;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Comment initComment(User author, Task task) {
        commentIndex++;
        return Comment.builder()
            .text("text_" + commentIndex)
            .author(author)
            .task(task)
            .build();
    }

    private User generateUser() {
        User user = UserServiceTest.initUser();
        return userRepository.save(user);
    }

    private Task generateTask(User creator, User performer, TaskPriority taskPriority) {
        Task task = TaskServiceTest.initTask(creator, performer, taskPriority);
        TaskCreateRequest request = TaskCreateRequest.builder()
            .name(task.getName())
            .description(task.getDescription())
            .priority(task.getPriority())
            .performer(performer.getId())
            .build();
        TaskInfo taskInfo = taskService.addTask(request, creator);
        task.setId(taskInfo.getId());
        task.setCreated(Timestamp.from(taskInfo.getCreated().toInstant(ZoneOffset.UTC)));
        return task;
    }

    private Comment generateComment(User author, Task task) {
        Comment comment = initComment(author, task);

        CommentCreateRequest request = CommentCreateRequest.builder()
            .taskId(task.getId())
            .text(comment.getText())
            .build();

        CommentInfo commentInfo = commentService.addComment(request, author);
        comment.setId(commentInfo.getId());
        comment.setCreated(Timestamp.from(commentInfo.getCreated().toInstant(ZoneOffset.UTC)));
        return comment;
    }

    @Test
    public void testAdd_shouldReturnCommentInfo_whenCorrect() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = generateTask(creator, performer, TaskPriority.HIGH);
        Comment comment = initComment(performer, task);

        CommentCreateRequest request = CommentCreateRequest.builder()
            .taskId(task.getId())
            .text(comment.getText())
            .build();

        CommentInfo res = commentService.addComment(request, performer);

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(res.getAuthor().getId(), performer.getId());
        assertEquals(res.getTask().getId(), task.getId());
        assertEquals(res.getText(), comment.getText());
        assertNull(res.getLastUpdated());
        assertTrue(res.getCreated().isAfter(LocalDateTime.now().minusMinutes(5))
            && res.getCreated().isBefore(LocalDateTime.now().plusMinutes(5)));
    }

    @Test
    public void testGet_shouldReturnCommentInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = generateTask(creator, performer, TaskPriority.HIGH);
        Comment comment = generateComment(performer, task);

        CommentInfo res = commentService.getCommentInfo(comment.getId());

        assertEquals(comment.getId(), res.getId());
        assertEquals(performer.getId(), res.getAuthor().getId());
        assertEquals(res.getTask().getId(), task.getId());
        assertEquals(res.getText(), comment.getText());
        assertNull(res.getLastUpdated());
        assertTrue(res.getCreated().isAfter(LocalDateTime.now().minusSeconds(30))
            && res.getCreated().isBefore(LocalDateTime.now().plusSeconds(30)));
    }

    @Test
    public void testUpdate_shouldReturnCommentInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = generateTask(creator, performer, TaskPriority.HIGH);
        Comment comment = generateComment(creator, task);

        CommentUpdateRequest request = CommentUpdateRequest.builder()
            .text(comment.getText())
            .build();

        CommentInfo res = commentService.updateComment(request, comment.getId(), creator);

        assertEquals(comment.getId(), res.getId());
        assertEquals(creator.getId(), res.getAuthor().getId());
        assertEquals(res.getTask().getId(), task.getId());
        assertEquals(res.getText(), comment.getText());
        assertTrue(res.getLastUpdated().isAfter(LocalDateTime.now().minusSeconds(30))
            && res.getLastUpdated().isBefore(LocalDateTime.now().plusSeconds(30)));
        assertTrue(res.getCreated().isAfter(LocalDateTime.now().minusSeconds(30))
            && res.getCreated().isBefore(LocalDateTime.now().plusSeconds(30)));
    }

    @Test
    public void testRemove_shouldReturnCommentInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = generateTask(creator, performer, TaskPriority.HIGH);
        Comment comment = generateComment(performer, task);

        CommentInfo res = commentService.removeComment(comment.getId(), performer);

        assertEquals(comment.getId(), res.getId());

        assertTrue(commentRepository.findById(comment.getId()).isEmpty());
    }

    @Test
    public void testSearch_shouldReturnCommentsInfo_whenExists() {
        User creator = generateUser();
        User performer = generateUser();
        Task task = generateTask(creator, performer, TaskPriority.HIGH);
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Comment comment = initComment(performer, task);
            comment.setText(comment.getText() + " searched");

            CommentCreateRequest request = CommentCreateRequest.builder()
                .taskId(task.getId())
                .text(comment.getText())
                .build();

            CommentInfo commentInfo = commentService.addComment(request, performer);
            comment.setId(commentInfo.getId());
            comment.setCreated(Timestamp.from(commentInfo.getCreated().toInstant(ZoneOffset.UTC)));
            comments.add(comment);
        }

        CommentSearchParams params = CommentSearchParams.builder()
            .created(CommentSearchParams.CreatedFilter.builder()
                .dateTime(LocalDateTime.now())
                .after(false)
                .build())
            .searchedText("searched")
            .task(task.getId())
            .author(performer.getId())
            .orders(Set.of(CommentSearchParams.OrderField.CREATED))
            .build();

        List<CommentInfo> res = commentService.searchComments(params, Pageable.ofSize(3));

        assertEquals(3, res.size());
        assertArrayEquals(res.stream().map(CommentInfo::getId).toArray(),
            comments.subList(0, 3).stream().map(Comment::getId).toArray());
    }
}
