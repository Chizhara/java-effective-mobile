package effective.mobile.com.mapper;

import effective.mobile.com.model.user.User;
import effective.mobile.com.model.user.dto.UserCreateRequest;
import effective.mobile.com.model.user.dto.UserInfo;
import effective.mobile.com.model.user.dto.UserShortInfo;
import effective.mobile.com.model.user.dto.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateRequest request);

    UserInfo toUserInfo(User user);

    UserShortInfo toUserShortInfo(User user);

    List<UserShortInfo> toUserShortInfo(List<User> users);

    @Mapping(target = "id", ignore = true)
    User updateUser(@MappingTarget User user, UserUpdateRequest request);
}
