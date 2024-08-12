package effective.mobile.com.controller;

import effective.mobile.com.model.user.User;
import effective.mobile.com.security.model.AuthenticationImpl;
import effective.mobile.com.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class CommonController {

    private final UserService userService;

    protected User getUser() {
        AuthenticationImpl authentication = (AuthenticationImpl) SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByEmail(authentication.getEmail());
    }
}
