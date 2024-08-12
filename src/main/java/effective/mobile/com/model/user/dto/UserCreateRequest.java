package effective.mobile.com.model.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String patronymic;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
