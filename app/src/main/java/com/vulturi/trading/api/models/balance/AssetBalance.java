package com.vulturi.trading.api.models.balance;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


import java.math.BigDecimal;

@Getter@Setter@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_ASSET_BALANCE")
public class AssetBalance {
    @EmbeddedId
    private AssetBalancePk pk = new AssetBalancePk();
    @Column
    private BigDecimal quantity;
    @Column
    private BigDecimal eurValue;
    @Column
    private BigDecimal usdValue;
}
