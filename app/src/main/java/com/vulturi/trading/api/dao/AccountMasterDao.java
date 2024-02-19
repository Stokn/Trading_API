package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.user.AccountMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMasterDao extends JpaRepository<AccountMaster,String> {
}
