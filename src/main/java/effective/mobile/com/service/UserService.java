package effective.mobile.com.service;

import effective.mobile.com.controller.UserController;
import effective.mobile.com.exception.NotFoundException;
import effective.mobile.com.mapper.UserMapper;
import effective.mobile.com.model.user.User;
import effective.mobile.com.model.user.dto.UserCreateRequest;
import effective.mobile.com.model.user.dto.UserInfo;
import effective.mobile.com.model.user.dto.UserSearchOrder;
import effective.mobile.com.model.user.dto.UserShortInfo;
import effective.mobile.com.model.user.dto.UserUpdateRequest;
import effective.mobile.com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUser(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    public UserInfo getUserInfo(UUID id) {
        User user = getUser(id);
        return userMapper.toUserInfo(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(User.class, email));
    }

    public List<UserShortInfo> searchUsersInfo(String text, UserSearchOrder order, Pageable pageable) {
        List<User> users;
        switch (order) {
            case NAME -> users = userRepository.findByAllTextOrderByName(text, pageable).getContent();
            case EMAIL -> users = userRepository.findByAllTextOrderByEmail(text, pageable).getContent();
            default -> throw new IllegalArgumentException("Illegal value of order: " + order);
        }
        return userMapper.toUserShortInfo(users);
    }

    @Transactional
    public UserInfo addUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        userRepository.save(user);
        return userMapper.toUserInfo(user);
    }

    @Transactional
    public UserInfo updateUser(UserUpdateRequest request, UUID id) {
        User user = getUser(id);
        user = userMapper.updateUser(user, request);
        userRepository.save(user);
        return userMapper.toUserInfo(user);
    }


}
