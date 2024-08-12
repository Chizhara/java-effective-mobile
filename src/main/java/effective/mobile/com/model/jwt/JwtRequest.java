package effective.mobile.com.model.jwt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
