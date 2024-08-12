package effective.mobile.com.model.task.dto;

import effective.mobile.com.model.task.TaskPriority;
import effective.mobile.com.model.task.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class TaskSearchParams {

    @Schema(description = "Значение приоритета")
    private TaskPriority priority;
    @Schema(description = "Значение статуса")
    private TaskStatus status;
    @Schema(description = "Искомый текст")
    private String text;
    @Schema(description = "Идентификатор создателя")
    private UUID creatorId;
    @Schema(description = "Идентификтор исполнителя")
    private UUID performerId;
    @Schema(description = "Перечисление полей для фильтрации")
    private Set<OrderField> order;

    @Getter
    public enum OrderField {
        NAME ("name"),
        DESCRIPTION ("description"),
        PRIORITY ("priority"),
        STATUS ("status"),
        CREATOR ("creator_id"),
        PERFORMER ("performer_id"),;

        private final String field;

        OrderField(String field) {
            this.field = field;
        }
    }
}
