package com.propsightai.Repository;

import com.propsightai.Model.User;
import com.propsightai.Role.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Query("UPDATE User u SET u.isEmailVerified = true, u.isActive = true, u.status = :status WHERE u.id = :id")
    void verifyUserByAdmin(@Param("id") Integer id, @Param("status") UserStatus status);
}
