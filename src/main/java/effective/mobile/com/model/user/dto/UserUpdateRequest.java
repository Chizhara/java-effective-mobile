package effective.mobile.com.model.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String patronymic;
    @Email
    private String email;
    private String password;
}
