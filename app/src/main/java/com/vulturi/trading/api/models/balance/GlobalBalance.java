package com.vulturi.trading.api.models.balance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.Collection;
@Getter@Setter@ToString
@Entity
@Table(name = "T_GLOBAL_BALANCE")
public class GlobalBalance {
    @EmbeddedId
    private GlobalBalancePk pk = new GlobalBalancePk();

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "accountId", referencedColumnName = "accountId"),
            @JoinColumn(name = "ts", referencedColumnName = "ts")
    })
    private Collection<PortfolioBalance> portfolioBalances;

}
