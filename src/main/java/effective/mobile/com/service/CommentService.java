package effective.mobile.com.service;

import effective.mobile.com.exception.ForbiddenAccessException;
import effective.mobile.com.exception.NotFoundException;
import effective.mobile.com.mapper.CommentMapper;
import effective.mobile.com.model.comment.Comment;
import effective.mobile.com.model.comment.dto.CommentCreateRequest;
import effective.mobile.com.model.comment.dto.CommentInfo;
import effective.mobile.com.model.comment.dto.CommentSearchParams;
import effective.mobile.com.model.comment.dto.CommentUpdateRequest;
import effective.mobile.com.model.task.Task;
import effective.mobile.com.model.user.User;
import effective.mobile.com.repository.CommentRepository;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TaskService taskService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EntityManager em;

    public Comment getComment(UUID id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Comment.class, id));
    }

    public CommentInfo getCommentInfo(UUID id) {
        Comment comment = getComment(id);
        return commentMapper.toCommentInfo(comment);
    }

    @Transactional
    public CommentInfo addComment(CommentCreateRequest request, User requester) {
        Task task = taskService.getTask(request.getTaskId());
        Comment comment = commentMapper.toComment(request);
        comment.setTask(task);
        comment.setAuthor(requester);
        comment.setCreated(Timestamp.from(Instant.now()));
        commentRepository.save(comment);
        return commentMapper.toCommentInfo(comment);
    }

    @Transactional
    public CommentInfo updateComment(CommentUpdateRequest request, UUID commentId, User requester) {
        Comment comment = getComment(commentId);
        validateCommentCreator(comment, requester);
        comment = commentMapper.updateComment(comment, request);
        comment.setLastUpdated(Timestamp.from(Instant.now()));
        commentRepository.save(comment);
        return commentMapper.toCommentInfo(comment);
    }

    @Transactional
    public CommentInfo removeComment(UUID commentId, User requester) {
        Comment comment = getComment(commentId);
        validateCommentCreator(comment, requester);
        commentRepository.deleteById(commentId);
        return commentMapper.toCommentInfo(comment);
    }

    public List<CommentInfo> searchComments(CommentSearchParams request, Pageable pageable) {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Comment> q = qb.createQuery(Comment.class);
        Root<Comment> root = q.from(Comment.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        if (request.getSearchedText() != null && !request.getSearchedText().isEmpty()) {
            String searched = "%" + request.getSearchedText() + "%";
            predicates.add(qb.like(root.get("text"), searched));
        }
        if (request.getCreated() != null) {
            if (request.getCreated().isAfter()) {
                predicates.add(qb.greaterThan(root.get("created"), request.getCreated().getDateTime()));
            } else {
                predicates.add(qb.lessThan(root.get("created"),
                    Timestamp.from(request.getCreated().getDateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        if (request.getAuthor() != null) {
            predicates.add(qb.equal(root.get("author").get("id"), request.getAuthor()));
        }
        if (request.getTask() != null) {
            predicates.add(qb.equal(root.get("task").get("id"), request.getTask()));
        }
        if (request.getOrders() != null) {
            for (CommentSearchParams.OrderField field : request.getOrders()) {
                if(field.isIdentifiable()) {
                    orders.add(qb.asc(root.get(field.getField()).get("id")));
                } else {
                    orders.add(qb.asc(root.get(field.getField())));
                }
            }
        }

        q.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orders);
        List<Comment> comments = em.createQuery(q)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();


        return commentMapper.toCommentsInfo(comments);
    }

    private void validateCommentCreator(Comment comment, User user) {
        if (!comment.getAuthor().equals(user)) {
            throw new ForbiddenAccessException(user.getId(), Comment.class, comment.getId());
        }
    }
}
