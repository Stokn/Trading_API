package com.vulturi.trading.api.services.kyc;

import com.vulturi.trading.api.backend.ubble.Document;
import com.vulturi.trading.api.backend.ubble.IdentificationCreate;
import com.vulturi.trading.api.backend.ubble.UbbleIdentificationResponse;
import com.vulturi.trading.api.backend.ubble.UbbleService;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserKycStatus;
import com.vulturi.trading.api.models.user.UserStartKyc;
import com.vulturi.trading.api.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KycServiceImpl implements KycService {
    @Autowired
    private UbbleService ubbleService;

    @Autowired
    private UserService userService;

    @Autowired
    UserDocumentService userDocumentService;


    public Collection<Document> getDocuments(User user) throws IOException {
        Collection<String> identificationIds = user.getKycIds();
        Collection<Document> documents = new ArrayList<>();
        for (String identificationId : identificationIds) {
            try {
                UbbleIdentificationResponse identification = ubbleService.getIdentification(user, identificationId);
                documents.add(identification.getDocument());
            } catch (Exception e) {
                log.error("Error requesting documents for userId: {}", user.getUserId());
                log.error(e.getMessage());
            }
        }
        return documents;
    }

    @Override
    public UserStartKyc initiate(User user) throws IOException {
        IdentificationCreate identificationCreate = new IdentificationCreate();
        identificationCreate.getData().getAttributes().getIdentificationForm().setExternalUserId(user.getUserId());
        identificationCreate.getData().getAttributes().getIdentificationForm().setPhoneNumber(user.getPhoneNumber());
        UserStartKyc userStartKyc = toUserStartKyc(user, ubbleService.getKycStart(identificationCreate));
        if (user.getKycIds() == null) {
            user.setKycIds(Collections.singletonList(userStartKyc.getIdentificationId()));
        } else {
            log.info("Adding to user the KYC Id : {}", userStartKyc.getIdentificationId());
            Collection<String> kycIds = user.getKycIds();
            ArrayList<String> existingIds = new ArrayList<>(kycIds);
            existingIds.add(userStartKyc.getIdentificationId());
            user.setKycIds(existingIds);
        }
        userService.save(user);
        return userStartKyc;
    }


    private UserStartKyc toUserStartKyc(User user, UbbleIdentificationResponse ubbleIdentificationResponse) {
        UserStartKyc userStartKyc = new UserStartKyc();
        userStartKyc.setUserId(user.getUserId());
        userStartKyc.setPhoneNumber(user.getPhoneNumber());
        userStartKyc.setIdentificationId(ubbleIdentificationResponse.getData().getAttributes().getIdentificationId());
        userStartKyc.setIdentificationUrl(ubbleIdentificationResponse.getData().getAttributes().getIdentificationUrl());
        userStartKyc.setNumberOfAttempts(ubbleIdentificationResponse.getData().getAttributes().getNumberOfAttempts());
        return userStartKyc;
    }


    public void updateUserKycStatus(String userId, UserKycStatus userKycStatus) throws ApiException {
        User user = userService.get(userId);
        if (user == null) {
            throw new ApiException(ApiError.USER_DOES_NOT_EXIST);
        }
        user.setKycStatus(userKycStatus);
        userService.save(user);
    }
}
