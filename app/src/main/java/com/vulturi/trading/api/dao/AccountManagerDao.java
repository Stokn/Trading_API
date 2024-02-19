package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.user.AccountManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountManagerDao extends JpaRepository<AccountManager, String> {
}
