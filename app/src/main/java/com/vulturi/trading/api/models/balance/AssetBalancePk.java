package com.vulturi.trading.api.models.balance;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDateTime;

@Getter@Setter@ToString@Embeddable
@Builder@NoArgsConstructor@AllArgsConstructor
public class AssetBalancePk implements Serializable {
    @Column
    private String portfolioId;
    @Column
    private LocalDateTime ts;
    @Column
    private String asset;
}
