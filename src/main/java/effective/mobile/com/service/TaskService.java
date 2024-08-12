package effective.mobile.com.service;

import effective.mobile.com.exception.ForbiddenAccessException;
import effective.mobile.com.exception.NotFoundException;
import effective.mobile.com.mapper.TaskMapper;
import effective.mobile.com.model.task.Task;
import effective.mobile.com.model.task.TaskStatus;
import effective.mobile.com.model.task.dto.TaskCreateRequest;
import effective.mobile.com.model.task.dto.TaskInfo;
import effective.mobile.com.model.task.dto.TaskSearchParams;
import effective.mobile.com.model.task.dto.TaskShortInfo;
import effective.mobile.com.model.task.dto.TaskUpdateRequest;
import effective.mobile.com.model.user.User;
import effective.mobile.com.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final EntityManager em;

    public Task getTask(UUID id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Task.class, id));
    }

    public TaskInfo getTaskInfo(UUID id) {
        Task task = getTask(id);
        return taskMapper.toTaskInfo(task);
    }

    public List<TaskShortInfo> searchTasksInfo(TaskSearchParams params, Pageable pageable) {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Task> q = qb.createQuery(Task.class);
        Root<Task> root = q.from(Task.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if (params.getPriority() != null) {
            predicates.add(qb.equal(root.get("priority"), params.getPriority()));
        }
        if (params.getStatus() != null) {
            predicates.add(qb.equal(root.get("status"), params.getStatus()));
        }
        if (params.getText() != null) {
            String searched = "%" + params.getText() + "%";
            predicates.add(qb.or(
                qb.like(root.get("name"), searched),
                qb.like(root.get("description"), searched)));
        }
        if (params.getOrder() != null && !params.getOrder().isEmpty()) {
            for(TaskSearchParams.OrderField field : params.getOrder()) {
                orders.add(qb.asc(root.get(field.getField())));
            }
        }

        q.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orders);
        List<Task> tasks = em.createQuery(q)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        return taskMapper.toTasksShortInfo(tasks);
    }

    @Transactional
    public TaskInfo addTask(TaskCreateRequest request, User requester) {
        Task task = taskMapper.toTask(request);
        task.setCreator(requester);
        updatePerformer(request.getPerformer(), task);
        task.setStatus(TaskStatus.WAITING);
        task.setCreated(Timestamp.from(Instant.now()));
        taskRepository.save(task);
        return taskMapper.toTaskInfo(task);
    }

    @Transactional
    public TaskInfo updateTask(UUID taskId, TaskUpdateRequest request, User requester) {
        Task task = getTask(taskId);
        validateTaskCreator(task, requester);
        task = taskMapper.updateTask(task, request);
        updatePerformer(request.getPerformer(), task);
        updateStatus(task, request.getStatus(), requester);
        taskRepository.save(task);
        return taskMapper.toTaskInfo(task);
    }

    @Transactional
    public TaskInfo removeTask(UUID taskId, User requester) {
        Task task = getTask(taskId);
        validateTaskCreator(task, requester);
        taskRepository.deleteById(task.getId());
        return taskMapper.toTaskInfo(task);
    }

    private void validateTaskCreator(Task task, User user) {
        if (!task.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenAccessException(user.getId(), Task.class, task.getId());
        }
    }

    private void updateStatus(Task task, TaskStatus status, User updater) {
        if (!updater.getId().equals(task.getPerformer().getId())
            && !updater.getId().equals(task.getCreator().getId())) {
            throw new ForbiddenAccessException(updater.getId(), Task.class, task.getId());
        }

        task.setStatus(status);
    }

    private void updatePerformer(UUID performerId, Task task) {
        if (performerId != null) {
            User performer = userService.getUser(performerId);
            task.setPerformer(performer);
        }
    }

}
