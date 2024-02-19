package com.vulturi.trading.api;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.*;
import com.vulturi.trading.api.services.user.UserService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private static String testPwd = "Password123!";

    private static String  mail = "hugo@stokn.io";

    @Test
    public void createUser() throws ApiException {
        String a = UUID.randomUUID().toString().replace("-","").toUpperCase();

        UserSignUp userSignUp = new UserSignUp();

        userSignUp.setPassword(testPwd);
        userSignUp.setEmail(mail);
        userSignUp.setPhoneNumber("+33669357520");;
        userSignUp.setRoles(new HashSet<>(Collections.singleton("USER")));
        //userService.createUser(userSignUp);
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(userSignUp.getPhoneNumber());
        userLogin.setPassword(testPwd);
        AuthResponse<AuthChallenge> authenticate = (AuthResponse<AuthChallenge>) userService.authenticate(userLogin);


        AuthChallengeOTP authChallengeOTP = new AuthChallengeOTP();
        authChallengeOTP.setChallengeType(authenticate.getData().getChallengeType());
        authChallengeOTP.setUsername(authenticate.getData().getUsername());
        authChallengeOTP.setSessionId(authenticate.getData().getSessionId());
        userService.responseToAuthChallenge(authChallengeOTP);
    }

    @Test
    public void refreshToken() throws ApiException {
        GetRefreshToken getRefreshToken = new GetRefreshToken();
        getRefreshToken.setUsername("9149b0ae-6091-700c-333d-9522011be6e2");
        getRefreshToken.setRefreshToken("eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.r-xtAsSCNIoHL0b5s4od2h6pVJtmgJ8I7r8lAUFGUYpcGOczCYrrXTFNrGOsxY7edVc7E75me5J9KVlBaqn36ubucD_LziaVKx2aypCvsG-H-VliUN01kWXW_ZL52ikyTsLvPn2s995maluyk0T0E3J9tDVq-EBc6o6mel9dCNuOjFqphsiyvRgl2lvQhtYkXAuvOFbE8JvryIg7V7Rn_UiZEDf6FKgjZzu7H-ffRlZCUPsGSi2Cq7HWc-k3AMnrGy6emSENmOHZBWG-rwk8E1AbA5bHslL7TOboohzSov5Bi89FWHFLdGy8VZ_CcG3lhPoRs1LWTxbmeQo0U9FE2A.U8eVRQbIPnY0xR_E.e9T9SffLdwIOiNHs6R9KL1TjhvfNM1xJVlhluc0LhbAn_AzE6oCc4SjM-fhCYFSkEMY4_LqEadqogDcs2IkceDPsZPKeZmGi77GqUxz9hQKjUUY2Y94NeRdpbGplv3QDInNE4p4iWWKDIk6SvYwS3b-dNcRBR1V8vkGAewpUI1tN6UQjVaIGO3JA6KFKwmI6PgOGEVKr6cQWIDtR9nQB-Zx14CC1h3XESXVqxnOAi2vG-hF7b2WUlhBZW18fFyKa-Y4X-d6zJBC8M4MXdNyDhG0an4mMQ8LQam61-83RMiu1J3Uo58fAbrZb1mWXQtBtmsR4jtAp3f0_kJ1N388L4V9TGrQmRFihIZGqhlbnOb70DiojG_the66ojHkqzhGHn_exaY1NMe7C_Ue0OM9BfjX6GZkpVOuLv8gJJ6XeMByVbBljx9EJewRitF9yEN-Fzv_gGluvjbq3-PscfxYixA2yAoAjdOlobjK-TqdbNJAb5FEw1HpWHJ29hgF85bsSdcS9loytn1YlzSUmNi4bpR0Y-wJtXikmrUu0-yxEJcCctUoytYbU527ErZMZUoyEKMx-7XhZnMlqhCRcY51Ym2XAUjgl8tYKblqL8T0yvNtNS2PDEwclJJSYV2spuCJIejDU16oC9IBowQi7E4HXoDXkhvK9FtTra4ff5rhzHZAdBWxsMggksiy0CERW5zgAn-TUi4j4QnbU0KO98fqN79CHn8sXLZXbUzh_Yod7cgiMjIyajrlSGr1iZqT8k2qG67r2qckriXVCLQqY3OVPKGegq_2W6ouqfOzraMjYUAfRV4CoZT8N_S7_d47c-fBI0C-tL6x4d5y5i4_AoeybrlIcbntkK1QbKyyG2IQaV4wKa3zd57UZAak3OkjSjdva7tRz3Ahddr6F-_LH8AGlavd_Q3Q08C0V6mRzO9xWzCoQnpd5EShn3qon-WP9VQc2UMYICYcJO_Yk_l9XjYi6KCoUQRp-atQFBMG8y0MHhx6spNDSjWDLdfzuBj63DcIz1zI_L4fx5JJt2vdmV2IO68pB1p_9PMCFpFlN7xG8bwNwrsohJ2JuMqIu4FZ6cQHvZXW-0-R7Sdy2ryGrVHHnUDXuP2_KU1sohXMcped1ba5Z8VWrIxAiofVryL3r_ew2hQFR-5O6EuaCdEdTgoX_OZok8M3tqtyvDbrY_aqp9zQI2ofZGxkDp1NUuYnZZ_BB8qoeBqhv6LdSNNjgzmg4ir1xIwWwOYr65YyEyI-aOvrtDS0_SbFHF4L656v9wLYrq7uJJ_kpRcN2oQ4e34ydBoeE2PY08dYaZRXe5FS04_ifd3frL13TMY0PSg.tSBQaW3sgx1SbuJQpkxXdg");
        userService.refresh(getRefreshToken);
    }

}
