package effective.mobile.com.model.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CommentSearchParams {
    @Schema(description = "Искомый текст")
    private String searchedText;
    @Schema(description = "Идентификатор автора")
    private UUID author;
    @Schema(description = "Идентификатор задачи")
    private UUID task;
    @Schema(description = "Фильтр создания")
    private CreatedFilter created;
    @Schema(description = "Перечисление полей для фильтрации")
    private Set<OrderField> orders;

    @Getter
    public enum OrderField {
        CREATED("created", false),
        AUTHOR("author", true),
        TASK("task", true);

        private final String field;
        private final boolean identifiable;

        OrderField(String field, boolean identifiable) {
            this.field = field;
            this.identifiable = identifiable;
        }
    }

    @Data
    @Builder
    public static class CreatedFilter {
        @Schema(description = "Значение даты и времени для фильтрации")
        private LocalDateTime dateTime;
        @Schema(description = "Флаг, отображающий искать после или до")
        private boolean after;
    }
}
