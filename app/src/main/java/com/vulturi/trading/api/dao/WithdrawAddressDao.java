package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.withdraw.WithdrawAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawAddressDao extends JpaRepository<WithdrawAddress,String> {
}
