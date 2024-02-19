package com.vulturi.trading.api.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vulturi.trading.api.converters.ListOfStringConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "T_USER_DOCUMENTS")
public class UserDocument {
    @Id
    private String uuid = UUID.randomUUID().toString();
    private String userId;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
    @Convert(converter = ListOfStringConverter.class) @Column(columnDefinition = "TEXT")
    private List<String> allFirstNames;
    private LocalDate birthDate;
    private String documentNumber;
    private String documentType;
    private LocalDate expiryDate;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate issueDate;
    private String marriedName;
    private String nationality;
}
