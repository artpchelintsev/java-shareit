package ru.practicum.shareit.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id <> :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);
}