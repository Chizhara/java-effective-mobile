package effective.mobile.com.model.user.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserInfo {
    private UUID id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
}
