package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET " +
            "u.fullName = :#{#user.fullName}, " +
            "u.address = :#{#user.address}, " +
            "u.isActive = :#{#user.isActive}, " +
            "u.role= :#{#user.role} " +
            "WHERE u.id = :#{#user.id}")
    void updateUser(@Param("user") User user);
}