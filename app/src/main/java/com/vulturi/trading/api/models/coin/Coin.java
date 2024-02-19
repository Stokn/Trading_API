package com.vulturi.trading.api.models.coin;

import com.vulturi.trading.api.converters.ListOfStringConverter;
import jakarta.persistence.*;
import lombok.*;


import java.lang.reflect.Type;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_COIN")
public class Coin {
    @Id
    private String slug;
    private String name;
    private String logoUrl;
    private TypeDeposit typeDeposit;
    @Convert(converter = ListOfStringConverter.class)
    @Column(columnDefinition = "TEXT")
    private Collection<String> availableNetworks;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
}
