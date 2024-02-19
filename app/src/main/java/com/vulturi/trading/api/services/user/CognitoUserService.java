package com.vulturi.trading.api.services.user;


import com.amazonaws.services.cognitoidp.model.*;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.AuthChallengeOTP;
import com.vulturi.trading.api.models.user.GetRefreshToken;
import com.vulturi.trading.api.models.user.UserAttributesUpdate;
import com.vulturi.trading.api.models.user.UserSignUp;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public interface CognitoUserService {
    Optional<AdminInitiateAuthResult> initiateAuth(String username, String password) throws ApiException;

    AuthenticationResultType refreshToken(GetRefreshToken getRefreshToken) throws ApiException;
    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session) throws ApiException;

    AdminUpdateUserAttributesResult userAttributesUpdate(String username, UserAttributesUpdate userAttributesUpdate) throws ApiException;

    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(AuthChallengeOTP authChallengeOTP) throws ApiException;

    GlobalSignOutResult signOut(String accessToken) throws ApiException;


    ForgotPasswordResult forgotPassword(String username) throws ApiException;


    void addUserToGroup(String username, String groupName) throws ApiException;

    ListUsersResult getAllUsers(ListUsersRequest listUsersRequest);

    GetUserResult me(GetUserRequest getUserRequest);

    DescribeUserPoolClientResult describe(Jwt jwt);

    AdminSetUserPasswordResult setUserPassword(String username, String password) throws ApiException;

    UserType signUp(UserSignUp userSignUp) throws ApiException;

    AdminGetUserResult adminGetUser(String username);

    AdminListUserAuthEventsResult getUserAuthEvents(String username, int maxResult, String nextToken) throws ApiException;
}
