package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class IdentificationCreate {
    private IdentificationData data = new IdentificationData();

    @Getter
    @Setter
    public static class IdentificationData {
        @JsonProperty("type")
        private String type = "identifications";

        @JsonProperty("attributes")
        private Attributes attributes = new Attributes();
    }

    @Getter
    @Setter
    public static class Attributes {
        @JsonProperty("identification-form")
        private IdentificationForm identificationForm = new IdentificationForm();
        @JsonIgnore
        private ReferenceData referenceData = new ReferenceData();

        @JsonProperty("webhook")
        private String webhook;

        @JsonProperty("redirect_url")
        private String redirectUrl;

        @JsonProperty("face_required")
        private boolean faceRequired = true;
    }

    @Getter
    @Setter
    public static class IdentificationForm {
        @JsonProperty("external-user-id")
        private String externalUserId;

        @JsonProperty("phone-number")
        private String phoneNumber;

    }

    @Getter
    @Setter
    public static class ReferenceData {
        @JsonProperty("birth-date")
        private String birthDate;

        @JsonProperty("first-name")
        private String firstName;

        @JsonProperty("last-name")
        private String lastName;

    }
}
