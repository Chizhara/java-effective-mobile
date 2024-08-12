package effective.mobile.com.controller;

import effective.mobile.com.model.user.dto.UserCreateRequest;
import effective.mobile.com.model.user.dto.UserInfo;
import effective.mobile.com.model.user.dto.UserSearchOrder;
import effective.mobile.com.model.user.dto.UserShortInfo;
import effective.mobile.com.model.user.dto.UserUpdateRequest;
import effective.mobile.com.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Регистрация нового пользователя"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfo addUser(@Valid @RequestBody UserCreateRequest request) {
         return userService.addUser(request);
    }

    @Operation(
        summary = "Обновление пользователя",
        description = "Обновить значения полей существующего пользователя"
    )
    @PatchMapping("/{userId}")
    public UserInfo updateUser(@Valid @RequestBody UserUpdateRequest request,
                               @PathVariable UUID userId) {
        return userService.updateUser(request, userId);
    }

    @Operation(
        summary = "Получение пользователя",
        description = "Поиск данных о пользователе с подобным идентификтатором"
    )
    @GetMapping("/{userId}")
    public UserInfo getUser(@PathVariable UUID userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(
        summary = "Поиск пользователей",
        description = " Поиск данных о пользователях в соответствии с указанными правилами фильтрации и сортировки"
    )
    @GetMapping
    public List<UserShortInfo> searchUsers(@RequestParam("searchedText") String searchedText,
                                           @RequestParam("order") UserSearchOrder order,
                                           @ParameterObject Pageable pageable) {
        return userService.searchUsersInfo(searchedText, order, pageable);
    }
}
