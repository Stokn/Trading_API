package com.vulturi.trading.api.models.balance;

import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;


@Getter@Setter@ToString
@Entity
@Table(name = "T_PORTFOLIO_BALANCE")
public class PortfolioBalance {
    @EmbeddedId
    private PortfolioBalancePk pk = new PortfolioBalancePk();
    @Column
    private PortfolioAccountType portfolioAccountType;
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "portfolioId", referencedColumnName = "portfolioId"),
            @JoinColumn(name = "accountId", referencedColumnName = "accountId"),
            @JoinColumn(name = "ts", referencedColumnName = "ts")
    })
    private Collection<AssetBalance> assetBalances;
}
