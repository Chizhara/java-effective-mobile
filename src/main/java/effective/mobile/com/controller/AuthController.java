package effective.mobile.com.controller;

import effective.mobile.com.model.jwt.JwtRequest;
import effective.mobile.com.model.jwt.JwtResponse;
import effective.mobile.com.model.jwt.RefreshJwtRequest;
import effective.mobile.com.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
        summary = "Авторизация",
        description = "Получить токены доступа и обновления"
    )
    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest request) {
        return authService.login(request);
    }

    @Operation(
        summary = "Обновление токенов",
        description = "Обновить токены авторизации"
    )
    @PostMapping("/refresh")
    public JwtResponse getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        return authService.refresh(request.getRefreshToken());
    }
}
