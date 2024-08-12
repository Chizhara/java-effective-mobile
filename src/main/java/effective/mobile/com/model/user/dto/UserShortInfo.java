package effective.mobile.com.model.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserShortInfo {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
