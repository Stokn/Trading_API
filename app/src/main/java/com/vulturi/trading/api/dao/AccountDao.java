package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account,String> {
}
