package com.vulturi.trading.api.models.deposit;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "T_BANK_INFO")
public class BankInfo {
    @Id
    private String iban;
    private BankName bankName;
    private BankCountry bankCountry;
    private String holderName;
    private String address;
    private String bic;
    private String swift;
}
