package effective.mobile.com.model.jwt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
