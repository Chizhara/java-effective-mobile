package effective.mobile.com.model.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CommentCreateRequest {
    private UUID taskId;
    private String text;
}
