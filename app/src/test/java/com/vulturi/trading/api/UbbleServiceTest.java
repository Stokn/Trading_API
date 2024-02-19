package com.vulturi.trading.api;

import com.vulturi.trading.api.backend.ubble.UbbleIdentificationResponse;
import com.vulturi.trading.api.backend.ubble.UbbleService;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserStartKyc;
import com.vulturi.trading.api.services.kyc.KycService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
public class UbbleServiceTest {
    @Autowired
    private KycService kycService;

    @Autowired
    private UbbleService ubbleService;




    @Test
    public void get() throws ApiException, IOException {
        User user = new User();
        user.setUserId("testUser");
        user.setPhoneNumber("+33669357520");
        UbbleIdentificationResponse identificationResponse = ubbleService.getIdentification(user,"fd3ab88d-f0c3-4898-b842-7fddc578b5c2");

        UserStartKyc userStartKyc = kycService.initiate(user);
    }

}
