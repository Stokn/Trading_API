package com.vulturi.trading.api.models.dca;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class DCAPk implements Serializable {
    @EqualsAndHashCode.Include
    private String accountId;
    @EqualsAndHashCode.Include
    private String portfolioId;
    @EqualsAndHashCode.Include
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
}
