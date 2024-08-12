package effective.mobile.com.controller;

import effective.mobile.com.model.comment.dto.CommentCreateRequest;
import effective.mobile.com.model.comment.dto.CommentInfo;
import effective.mobile.com.model.comment.dto.CommentSearchParams;
import effective.mobile.com.model.comment.dto.CommentUpdateRequest;
import effective.mobile.com.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController extends CommonController {
    private final CommentService commentService;

    public CommentController(UserService userService, CommentService commentService) {
        super(userService);
        this.commentService = commentService;
    }

    @Operation(
        summary = "Добавление нового комментария"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentInfo addComment(@RequestBody CommentCreateRequest request) {
        return commentService.addComment(request, getUser());
    }

    @Operation(
        summary = "Редактирование комментария",
        description = "Обновление комментария с указанным идентификатором"
    )
    @PatchMapping("/{commentId}")
    public CommentInfo updateComment(@RequestBody CommentUpdateRequest request,
                                     @PathVariable UUID commentId) {
        return commentService.updateComment(request, commentId, getUser());
    }

    @Operation(
        summary = "Удаление комментария",
        description = "Удаление сущетсвующего комментария с указанным идентификатором"
    )
    @DeleteMapping("/{commentId}")
    public CommentInfo removeComment(@PathVariable UUID commentId) {
        return commentService.removeComment(commentId, getUser());
    }

    @Operation(
        summary = "Поиск комментария",
        description = "Поиск данных сущетсвующего комментария с указанным идентификатором"
    )
    @GetMapping("/{commentId}")
    public CommentInfo getComment(@PathVariable UUID commentId) {
        return commentService.getCommentInfo(commentId);
    }

    @Operation(
        summary = "Поиск комменатриев",
        description = "Поиск данных о комментариях соответствующих указанным правилам фильтрации и сортировки"
    )
    @GetMapping
    public List<CommentInfo> searchComments(@ParameterObject CommentSearchParams params,
                                            @ParameterObject Pageable pageable) {
        return commentService.searchComments(params, pageable);
    }
}
