package com.vulturi.trading.api.models.user;

import com.vulturi.trading.api.converters.ListOfStringConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.Collection;

@Getter
@Setter
@ToString
@Entity
@Table(name = "T_ACCOUNT_MASTER")
public class AccountMaster {
    @Id
    private String accountId;// cognitoSub
    private String email;
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> accountManagerIds; // id of account manager monitored
}
