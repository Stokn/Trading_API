package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.deposit.BankInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankInfoDao extends JpaRepository<BankInfo,String> {
}
