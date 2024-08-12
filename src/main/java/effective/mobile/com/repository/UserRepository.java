package effective.mobile.com.repository;

import effective.mobile.com.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u " +
        "FROM User u " +
        "WHERE u.email ILIKE CONCAT('%',?1,'%') " +
        "OR u.firstName ILIKE CONCAT('%',?1,'%') " +
        "OR u.lastName ILIKE CONCAT('%',?1,'%') " +
        "OR u.patronymic ILIKE CONCAT('%',?1,'%') " +
        "ORDER BY u.email")
    Page<User> findByAllTextOrderByEmail(String text, Pageable pageable);

    @Query("SELECT u " +
        "FROM User u " +
        "WHERE u.email ILIKE CONCAT('%',?1,'%') " +
        "OR u.firstName ILIKE CONCAT('%',?1,'%') " +
        "OR u.lastName ILIKE CONCAT('%',?1,'%') " +
        "OR u.patronymic ILIKE CONCAT('%',?1,'%') " +
        "ORDER BY u.firstName, u.lastName, u.patronymic")
    Page<User> findByAllTextOrderByName(String text, Pageable pageable);
}
