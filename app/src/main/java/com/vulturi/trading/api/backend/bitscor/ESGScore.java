package com.vulturi.trading.api.backend.bitscor;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ESGScore {
    private String symbol;
    private LocalDateTime ts;
    private String e;
    private String s;
    private String g;
    private String score;

    @Override
    public String toString() {
        return "ESGScore{" +
                "symbol='" + symbol + '\'' +
                ", ts=" + ts +
                ", e='" + e + '\'' +
                ", s='" + s + '\'' +
                ", g='" + g + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
