package com.example.thanksdiary.dao.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

}
