package com.vulturi.trading.api.models.user;

import com.vulturi.trading.api.converters.HashMapConverter;
import com.vulturi.trading.api.converters.ListOfStringConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "T_USERS")
public class User {
    @Id
    private String userId; // sub cognito
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> documentIds = new ArrayList<>();
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> accountIds = new ArrayList<>();
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String bankingTransferRefId;
    @Convert(converter = HashMapConverter.class) @Column(columnDefinition = "TEXT")
    private Map<String,String> address = new HashMap<>();
    @Convert(converter = HashMapConverter.class) @Column(columnDefinition = "TEXT")
    private Map<String,String> info = new HashMap<>();
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> role = new ArrayList<>();
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private Collection<String> kycIds = new ArrayList<>();
    private UserKycStatus kycStatus = UserKycStatus.UNINITIATED;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
}
