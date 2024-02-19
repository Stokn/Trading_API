package com.vulturi.trading.api.services.dca;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAStatus;
import com.vulturi.trading.api.models.user.Account;

import java.util.List;

public interface DCAService {

    DCA save(DCA dca);

    DCA update(Account account, String id, DCAStatus status) throws ApiException;

    List<DCA> getActiveDCA(Account account);

    List<DCA> getDCA(Account account);


}
