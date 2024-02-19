package com.vulturi.trading.api.services.user;

import com.amazonaws.services.cognitoidp.model.*;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.PortfolioBalance;
import com.vulturi.trading.api.models.user.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.oauth2.jwt.Jwt;



import java.util.Collection;


public interface UserService {

    User updateKycStatus(String sub, UserKycStatus userKycStatus) throws ApiException;

    AuthResponse<?> authenticate(UserLogin userLogin) throws ApiException;

    AuthResponse<?>  updateUserPassword(UserPasswordUpdate userPasswordUpdate) throws ApiException;

    void updateUserAttributes(Jwt jwt, UserAttributesUpdate userAttributesUpdate) throws ApiException;

    AuthResponse<?> responseToAuthChallenge(AuthChallengeOTP authChallengeOTP) throws ApiException;

    AuthResponse<?>  updateUserAdditionalInfo(Object userInfoAdditional) throws ApiException;

    void logout(@NotNull String accessToken) throws ApiException;

    ForgotPasswordResult userForgotPassword(String username) throws ApiException;
    User createUser(UserSignUp userSignUp) throws ApiException;

    User save(User user);

    User get(String userId);

    User getByAccountId(String accountId);



    ListUsersResult getAllUsers(ListUsersRequest listUsersRequest);

    User me(Jwt jwt) throws ApiException;

    Collection<PortfolioBalance> myPortfolioBalance(Jwt jwt) throws ApiException;

    GetUserResult userInfo(Jwt jwt);

    AuthResponse<?> refresh(GetRefreshToken getRefreshToken) throws ApiException;
    AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) throws ApiException;
}