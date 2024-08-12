package effective.mobile.com.service;

import effective.mobile.com.exception.AuthenticationException;
import effective.mobile.com.model.jwt.JwtRequest;
import effective.mobile.com.model.jwt.JwtResponse;
import effective.mobile.com.model.user.User;
import effective.mobile.com.security.JwtProvider;
import effective.mobile.com.security.model.AuthenticationImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider provider;
    @Qualifier("refreshTokenCache")
    private final Map<String, String> refreshStorage;

    public JwtResponse login(JwtRequest authRequest) {
        final User user = userService.getUserByEmail(authRequest.getEmail());
        if (user.getPassword().equals(authRequest.getPassword())) {
            final String accessToken = provider.generateAccessToken(user);
            final String refreshToken = provider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } else {
            throw new AuthenticationException("Password is invalid");
        }
    }

    public JwtResponse refresh(String refreshToken) {
        if (provider.validateRefreshToken(refreshToken)) {
            final Claims claims = provider.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getUserByEmail(email);
                final String accessToken = provider.generateAccessToken(user);
                final String newRefreshToken = provider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), newRefreshToken);
                return JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            }
        }
        throw new AuthenticationException("Invalid JWT");
    }

}
