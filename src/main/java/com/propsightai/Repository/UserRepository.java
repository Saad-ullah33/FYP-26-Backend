package com.propsightai.Repository;

import com.propsightai.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetToken(String token);

    long countByStatus(com.propsightai.Role.UserStatus status);
    Optional<User> findByRefreshToken(String refreshToken);

    long countByActiveTrue();

    long countByActiveFalse();

    long countByEmailVerifiedTrue();
}
