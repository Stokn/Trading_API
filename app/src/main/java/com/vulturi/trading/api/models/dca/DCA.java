package com.vulturi.trading.api.models.dca;

import com.vulturi.trading.api.dao.DCADao;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "T_DCA")
@Getter
@Setter@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class DCA {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private DCAPk pk = new DCAPk();
    private String id = UUID.randomUUID().toString();
    private DCAStatus status;
    private BigDecimal fromQuantity;
    private String fromCoin;
    private String toCoin;
    private DCAFrequency frequency;
}
