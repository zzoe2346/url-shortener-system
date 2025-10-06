package com.jeongseonghun.urlshortener.user.repository;

import com.jeongseonghun.urlshortener.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
