package effective.mobile.com.model.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUpdateRequest {
    String text;
}
