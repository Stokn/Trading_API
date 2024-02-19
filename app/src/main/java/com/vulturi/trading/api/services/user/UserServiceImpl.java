package com.vulturi.trading.api.services.user;

import com.amazonaws.services.cognitoidp.model.*;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.UserDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.balance.PortfolioBalance;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.user.*;
import com.vulturi.trading.api.services.account.AccountService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private Map<String, User> cache = Maps.newConcurrentMap();

    @Autowired
    private CognitoUserService cognitoUserService;

    @Autowired
    private AccountService accountService;

    @PostConstruct
    void init() {
        for (User user : userDao.findAll()) {
            cache.put(user.getUserId(), user);
        }
        log.info("{} user(s) have been stored in cache", cache.size());
    }

    @Override
    public User updateKycStatus(String sub, UserKycStatus userKycStatus) throws ApiException {
        User existingUser = cache.get(sub);
        if (existingUser == null) {
            throw new ApiException(ApiError.USER_DOES_NOT_EXIST);
        }
        existingUser.setKycStatus(userKycStatus);
        return save(existingUser);
    }

    @Override
    public AuthResponse<?> authenticate(UserLogin userLogin) throws ApiException {

        AdminInitiateAuthResult result = cognitoUserService.initiateAuth(userLogin.getUsername(), userLogin.getPassword())
                .orElseThrow(() -> new UserNotFoundException(String.format("Username %s  not found.", userLogin.getUsername())));

        if (result.getAuthenticationResult() == null) {
            return AuthResponse.build(AuthChallenge.builder()
                    .challengeType(result.getChallengeName())
                    .sessionId(result.getSession())
                    .username(userLogin.getUsername())
                    .build());
        }

        return AuthResponse.build(TokenInfo.builder()
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userLogin.getUsername())
                .build());
    }


    public AuthResponse<?> refreshToken(GetRefreshToken getRefreshToken) throws ApiException {
        AuthenticationResultType authenticationResultType = cognitoUserService.refreshToken(getRefreshToken);
        return AuthResponse.build(
                TokenInfo.builder()
                        .idToken(authenticationResultType.getIdToken())
                        .refreshToken(authenticationResultType.getRefreshToken())
                        .accessToken(authenticationResultType.getAccessToken())
                        .build()
        );
    }

    @Override
    public AuthResponse<?> updateUserPassword(UserPasswordUpdate userPasswordUpdate) throws ApiException {

        AdminRespondToAuthChallengeResult result =
                cognitoUserService.respondToAuthChallenge(userPasswordUpdate.getUsername(), userPasswordUpdate.getPassword(), userPasswordUpdate.getSessionId()).get();

        return AuthResponse.build(TokenInfo.builder()
                .issuedAt(LocalDateTime.now(Clock.systemUTC()))
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userPasswordUpdate.getUsername())
                .build());
    }

    @Override
    public void updateUserAttributes(Jwt jwt, UserAttributesUpdate userAttributesUpdate) throws ApiException {
        GetUserResult me = userInfo(jwt);
        cognitoUserService.userAttributesUpdate(me.getUsername(), userAttributesUpdate);
    }


    public AuthResponse<?> responseToAuthChallenge(AuthChallengeOTP authChallengeOTP) throws ApiException {
        Optional<AdminRespondToAuthChallengeResult> adminRespondToAuthChallengeResult = cognitoUserService.respondToAuthChallenge(authChallengeOTP);
        boolean present = adminRespondToAuthChallengeResult.isPresent();
        if (present) {
            AuthenticationResultType authenticationResult = adminRespondToAuthChallengeResult.get().getAuthenticationResult();
            GetUserRequest getUserRequest = new GetUserRequest();
            getUserRequest.setAccessToken(authenticationResult.getAccessToken());
            GetUserResult me = cognitoUserService.me(getUserRequest);
            return AuthResponse.build(
                    TokenInfo.builder().accessToken(authenticationResult.getAccessToken())
                            .username(me.getUsername())
                            .phoneNumber(cache.get(me.getUsername()).getPhoneNumber())
                            .issuedAt(LocalDateTime.now(Clock.systemUTC()))
                            .idToken(authenticationResult.getIdToken())
                            .refreshToken(authenticationResult.getRefreshToken())
                            .expiresIn(authenticationResult.getExpiresIn())
                            .build()
            );
        }


        return null;
    }

    @Override
    public AuthResponse<?> updateUserAdditionalInfo(Object userInfoAdditional) throws ApiException {
        return null;
    }

    @Override
    public void logout(@NotNull String accessToken) throws ApiException {
        cognitoUserService.signOut(accessToken);
    }

    @Override
    public ForgotPasswordResult userForgotPassword(String username) throws ApiException {
        return cognitoUserService.forgotPassword(username);
    }


    @Override
    public User createUser(UserSignUp userSignUp) throws ApiException {
        UserType userType = cognitoUserService.signUp(userSignUp);
        log.info("Prepare to save in db userId {}", userType.getUsername());
        User user = toUser(userType);
        user.setRole(userSignUp.getRoles().stream().toList());
        accountService.create(user.getUserId(), user.getEmail());
        user.setAccountIds(List.of(user.getUserId()));
        return save(user);
    }


    public User save(User user) {
        User userSaved = userDao.save(user);
        cache.put(userSaved.getUserId(), userSaved);
        return userSaved;
    }

    @Override
    public User get(String userId) {
        return cache.get(userId);
    }

    @Override
    public User getByAccountId(String accountId) {
        return cache.values().stream().filter(user -> user.getAccountIds().contains(accountId)).findAny().orElse(null);
    }


    @Override
    public ListUsersResult getAllUsers(ListUsersRequest listUsersRequest) {
        return cognitoUserService.getAllUsers(listUsersRequest);
    }

    public User me(Jwt jwt) throws ApiException {
        GetUserRequest getUserRequest = new GetUserRequest();
        getUserRequest.setAccessToken(jwt.getTokenValue());
        GetUserResult me = cognitoUserService.me(getUserRequest);
        //DescribeUserPoolClientResult describe = cognitoUserService.describe(jwt);
        User byId = userDao.findById(me.getUsername()).orElse(null);
        if (byId == null) {
            throw new ApiException(ApiError.USER_DOES_NOT_EXIST);
        }
        return byId;
    }

    @Override
    public Collection<PortfolioBalance> myPortfolioBalance(Jwt jwt) throws ApiException {
        User me = me(jwt);
        Collection<PortfolioBalance> portfolioBalances = new ArrayList<>();
        for (String accountId : me.getAccountIds()) {
            GlobalBalance globalBalance = accountService.getGlobalBalance(accountId);
            PortfolioBalance existingPortfolioBalance = globalBalance.getPortfolioBalances().stream().filter(portfolioBalance -> portfolioBalance.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElse(null);
            if (existingPortfolioBalance != null) {
                portfolioBalances.add(existingPortfolioBalance);
            }
        }
        return portfolioBalances;
    }

    public GetUserResult userInfo(Jwt jwt) {
        GetUserRequest getUserRequest = new GetUserRequest();
        getUserRequest.setAccessToken(jwt.getTokenValue());
        return cognitoUserService.me(getUserRequest);
    }

    @Override
    public AuthResponse<?> refresh(GetRefreshToken getRefreshToken) throws ApiException {
        AuthenticationResultType authenticationResultType = cognitoUserService.refreshToken(getRefreshToken);
        return AuthResponse.build(TokenInfo.builder()
                .username(getRefreshToken.getUsername())
                .issuedAt(LocalDateTime.now(Clock.systemUTC()))
                .accessToken(authenticationResultType.getAccessToken())
                .idToken(authenticationResultType.getIdToken())
                .refreshToken(authenticationResultType.getRefreshToken())
                .expiresIn(authenticationResultType.getExpiresIn())
                .build());
    }

    private User toUser(UserType userType) {
        User user = new User();
        user.setUserId(userType.getUsername());
        user.setBankingTransferRefId(user.getUserId().replace("-", "").toUpperCase().substring(0, 15));
        AttributeType phoneNumber = userType.getAttributes().stream().filter(attributeType -> attributeType.getName().equals("phone_number")).findFirst().orElse(null);
        AttributeType email = userType.getAttributes().stream().filter(attributeType -> attributeType.getName().equals("email")).findFirst().orElse(null);
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.getValue());
        }
        if (email != null) {
            user.setEmail(email.getValue());
        }
        return user;
    }

    public AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) throws ApiException {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken);
    }
}
