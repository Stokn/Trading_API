package com.vulturi.trading.api.backend.binance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.Collection;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class BinanceIpRestrictionSwitchRequest {

    private String subAccountId;
    private String subAccountApiKey;
    @JsonIgnore
    private Boolean ipRestrict;
    public String getStatus() {
        return ipRestrict == Boolean.TRUE ? "2" : "1";
    }
    @JsonIgnore
    private Collection<String> ips = Lists.newArrayList();
    public String getIpAddress() {
        return ips != null && !ips.isEmpty() ? String.join(",", ips) : null;
    }
}
