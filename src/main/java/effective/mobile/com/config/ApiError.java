package effective.mobile.com.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private String errors;
    private LocalDateTime timestamp;
}
