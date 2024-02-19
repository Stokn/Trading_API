package com.vulturi.trading.api.models.user;

import com.vulturi.trading.api.converters.HashMapConverter;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Getter@Setter@ToString@Entity
@Table(name = "T_ACCOUNT")
public class Account {
    @Id
    private String accountId;// cognitoSub
    private String email;
    private String subAccountId;
    private String subAccountEmail;//binanceSubAccountId
    private String encryptionKey;//toDecryptCredentials
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Collection<Portfolio> portfolios;
    @Convert(converter = HashMapConverter.class)
    private Map<String,String> additionalInfo;
    private boolean active = false;
    private String tradingPortfolioId;

}
