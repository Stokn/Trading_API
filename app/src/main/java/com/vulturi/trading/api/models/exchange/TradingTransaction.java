package com.vulturi.trading.api.models.exchange;

import com.vulturi.trading.api.converters.ListOfStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter@Setter@ToString@Entity(name = "T_TRADING_TRANSACTION")
public class TradingTransaction {
    @EmbeddedId
    private TradingTransactionPk pk = new TradingTransactionPk();
    private String orderId;
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> transactionIds=new ArrayList<>();
    private String fromCoin;
    private String toCoin;
    private String id = UUID.randomUUID().toString();
    private boolean success = false;
}
