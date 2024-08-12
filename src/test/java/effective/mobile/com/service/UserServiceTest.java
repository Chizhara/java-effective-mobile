package effective.mobile.com.service;

import effective.mobile.com.model.user.User;
import effective.mobile.com.model.user.dto.UserCreateRequest;
import effective.mobile.com.model.user.dto.UserInfo;
import effective.mobile.com.model.user.dto.UserSearchOrder;
import effective.mobile.com.model.user.dto.UserShortInfo;
import effective.mobile.com.model.user.dto.UserUpdateRequest;
import effective.mobile.com.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> database =
        new PostgreSQLContainer<>("postgres:16-alpine");
    private static int userIndex = 0;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public static User initUser() {
        userIndex++;
        User user = User.builder()
            .email("email_" + userIndex + "@gmail.com")
            .firstName("firstName_" + userIndex)
            .lastName("lastName_" + userIndex)
            .patronymic("patronymic_" + userIndex)
            .password("password_" + userIndex)
            .build();
        return user;
    }

    @Test
    public void testAdd_shouldReturnUserInfo_whenCorrect() {
        User user = initUser();
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .patronymic(user.getPatronymic())
            .password(user.getPassword())
            .build();

        UserInfo res = userService.addUser(userCreateRequest);

        assertNotNull(res);
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getFirstName(), res.getFirstName());
        assertEquals(user.getLastName(), res.getLastName());
        assertEquals(user.getPatronymic(), res.getPatronymic());
    }

    @Test
    public void testGet_shouldReturnUserInfo_whenExists() {
        User user = initUser();
        userRepository.save(user);

        UserInfo res = userService.getUserInfo(user.getId());

        assertEquals(res.getId(), user.getId());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getFirstName(), res.getFirstName());
        assertEquals(user.getLastName(), res.getLastName());
        assertEquals(user.getPatronymic(), res.getPatronymic());
    }

    @Test
    public void testGetByEmil_shouldReturnUser_whenExists() {
        User user = initUser();
        userRepository.save(user);

        userRepository.save(user);
        User res = userService.getUserByEmail(user.getEmail());

        assertEquals(res.getId(), user.getId());
    }

    @Test
    public void testUpdateUser_shouldReturnUserInfo_whenExists() {
        User user = initUser();
        userRepository.save(user);

        User user2 = initUser();
        UserUpdateRequest request = UserUpdateRequest.builder()
            .email(user2.getEmail())
            .firstName(user2.getFirstName())
            .lastName(user2.getLastName())
            .patronymic(user2.getPatronymic())
            .password(user2.getPassword())
            .build();


        UserInfo res = userService.updateUser(request, user.getId());

        assertEquals(res.getId(), user.getId());
        assertEquals(res.getEmail(), user2.getEmail());
        assertEquals(res.getFirstName(), user2.getFirstName());
        assertEquals(res.getLastName(), user2.getLastName());
        assertEquals(res.getPatronymic(), user2.getPatronymic());
    }

    @Test
    public void testSearchUser_shouldReturnUserInfo_whenExists() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = initUser();
            user.setEmail(i + "_" + user.getEmail() + "_searching");
            users.add(user);
        }

        for (int i = 9; i >= 0; i--) {
            userRepository.save(users.get(i));
        }

        UserSearchOrder order = UserSearchOrder.EMAIL;

        List<UserShortInfo> res = userService.searchUsersInfo("searching", order, Pageable.ofSize(5));

        res.forEach(System.out::println);
        assertArrayEquals(users.subList(0, 5).stream().map(User::getId).toArray(),
            res.stream().map(UserShortInfo::getId).toArray());
    }


}
