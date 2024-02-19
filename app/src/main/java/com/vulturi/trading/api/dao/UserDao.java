package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,String> {
}
