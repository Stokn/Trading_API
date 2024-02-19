package com.vulturi.trading.api.services.user;


import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;

import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.CognitoAttributesEnum;
import com.vulturi.trading.api.models.user.AuthChallengeOTP;
import com.vulturi.trading.api.models.user.GetRefreshToken;
import com.vulturi.trading.api.models.user.UserAttributesUpdate;
import com.vulturi.trading.api.models.user.UserSignUp;
import com.vulturi.trading.api.util.AwsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@Slf4j
@Service
public class CognitoUserServiceImpl implements CognitoUserService {
    @Autowired
    private AWSCognitoIdentityProvider awsCognitoIdentityProvider;
    @Autowired
    private AwsConfig awsConfig;


    @Override
    public UserType signUp(UserSignUp userSignUp) throws ApiException {
        log.info("Initiating {}",userSignUp);
        try {
            final AdminCreateUserRequest signUpRequest = new AdminCreateUserRequest()
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    // The user's temporary password.
                    // Specify "EMAIL" if email will be used to send the welcome message
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withUsername(userSignUp.getPhoneNumber())
                    .withMessageAction(MessageActionType.SUPPRESS)
                    .withUserAttributes(
                            new AttributeType().withName("email").withValue(userSignUp.getEmail()),
                            new AttributeType().withName("email_verified").withValue("true"),
                            new AttributeType().withName("phone_number").withValue(userSignUp.getPhoneNumber()),
                            new AttributeType().withName("phone_number_verified").withValue("true"));

            // create user
            AdminCreateUserResult createUserResult;
            try {
                createUserResult = awsCognitoIdentityProvider.adminCreateUser(signUpRequest);
            } catch (Exception e) {
                throw new ApiException(ApiError.CANNOT_FIND_ANY_WALLET_TRANSACTION, e.getMessage());
            }
            log.info("Created User id: {}", createUserResult.getUser().getUsername());

            // assign the roles
            if(userSignUp.getRoles()!=null){
                for (String r : userSignUp.getRoles()) {
                    addUserToGroup(userSignUp.getPhoneNumber(), r);
                }
            }

            // set permanent password
            setUserPassword(userSignUp.getPhoneNumber(), userSignUp.getPassword());

            return createUserResult.getUser();

        } catch (UsernameExistsException e) {
            throw new UsernameExistsException("User name that already exists");
        } catch (InvalidPasswordException e) {
            throw new ApiException(ApiError.INVALID_PASSWORD, e.getErrorMessage());
        }

    }

    public AuthenticationResultType refreshToken(GetRefreshToken getRefreshToken) {
        Map<String, String> authParams = new HashMap<String, String>();
        authParams.put(CognitoAttributesEnum.USERNAME.name(), getRefreshToken.getUsername());
        authParams.put("REFRESH_TOKEN", getRefreshToken.getRefreshToken());
        String hash = calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), getRefreshToken.getUsername());
        authParams.put(CognitoAttributesEnum.SECRET_HASH.name(), hash);
        log.info("secret_hash: {}",hash );
        AdminInitiateAuthRequest initiateAuthRequest = new AdminInitiateAuthRequest().withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withAuthParameters(authParams);
        AdminInitiateAuthResult adminInitiateAuthResult = awsCognitoIdentityProvider.adminInitiateAuth(initiateAuthRequest);
        return adminInitiateAuthResult.getAuthenticationResult();

    }

    @Override
    public void addUserToGroup(String username, String groupName) throws ApiException {
        try {
            // add user to group
            AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest()
                    .withGroupName(groupName)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withUsername(username);
            awsCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
        } catch (InvalidPasswordException e) {
            throw new ApiException(ApiError.COGNITO_INVALID_PARAM, e.getErrorMessage());
        }
    }

    @Override
    public AdminSetUserPasswordResult setUserPassword(String username, String password) throws ApiException {

        try {
            // Sets the specified user's password in a user pool as an administrator. Works on any user.
            AdminSetUserPasswordRequest adminSetUserPasswordRequest = new AdminSetUserPasswordRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withPermanent(true);
            return awsCognitoIdentityProvider.adminSetUserPassword(adminSetUserPasswordRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new ApiException(ApiError.COGNITO_INVALID_PARAM, e.getErrorMessage());
        }
    }


    public AdminUpdateUserAttributesResult userAttributesUpdate(String username, UserAttributesUpdate userAttributesUpdate) throws ApiException {

        try {
            // add attributes to user
            AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = new AdminUpdateUserAttributesRequest()
                    .withUsername(username)
                    .withUserAttributes(new AttributeType().withName("name").withValue(userAttributesUpdate.getName()),
                            new AttributeType().withName("family_name").withValue(userAttributesUpdate.getLastname()),
                            new AttributeType().withName("address").withValue(userAttributesUpdate.getAddress()),
                            new AttributeType().withName("nationality").withValue(userAttributesUpdate.getNationality())
                    )
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId());
            return awsCognitoIdentityProvider.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new ApiException(ApiError.COGNITO_INVALID_PARAM, e.getErrorMessage());
        }
    }


    @Override
    public Optional<AdminInitiateAuthResult> initiateAuth(String username, String password) throws ApiException {

        final Map<String, String> authParams = new HashMap<>();
        authParams.put(CognitoAttributesEnum.USERNAME.name(), username);
        authParams.put(CognitoAttributesEnum.PASSWORD.name(), password);
        authParams.put(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withAuthParameters(authParams);

        return adminInitiateAuthResult(authRequest);
    }


    @Override
    public Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session) throws ApiException {
        AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
        request.withChallengeName(NEW_PASSWORD_REQUIRED)
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withSession(session)
                .addChallengeResponsesEntry("userAttributes.name", "aek")
                .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name(), username)
                .addChallengeResponsesEntry(CognitoAttributesEnum.NEW_PASSWORD.name(), newPassword)
                .addChallengeResponsesEntry(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));

        try {
            return Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(request));
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException("User not found." + e.getErrorMessage());
        } catch (UserNotFoundException e) {
            throw new ApiException(ApiError.USER_DOES_NOT_EXIST, e.getErrorMessage());
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new ApiException(ApiError.INVALID_PASSWORD, e.getErrorMessage());
        }
    }

    @Override
    public Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(AuthChallengeOTP authChallengeOTP) throws ApiException {
        final AdminRespondToAuthChallengeRequest challengeRequest = new AdminRespondToAuthChallengeRequest();
        challengeRequest.withChallengeName(ChallengeNameType.valueOf(authChallengeOTP.getChallengeType()))
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withSession(authChallengeOTP.getSessionId())
                .addChallengeResponsesEntry("SMS_MFA_CODE", authChallengeOTP.getCode())
                .addChallengeResponsesEntry("USERNAME", authChallengeOTP.getUsername())
                .addChallengeResponsesEntry(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), authChallengeOTP.getUsername()));
        return Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(challengeRequest));
    }


    @Override
    public AdminListUserAuthEventsResult getUserAuthEvents(String username, int maxResult, String nextToken) throws ApiException {
        try {

            AdminListUserAuthEventsRequest userAuthEventsRequest = new AdminListUserAuthEventsRequest();
            userAuthEventsRequest.setUsername(username);
            userAuthEventsRequest.setUserPoolId(awsConfig.getCognito().getUserPoolId());
            userAuthEventsRequest.setMaxResults(maxResult);
            if (Strings.isNotBlank(nextToken)) {
                userAuthEventsRequest.setNextToken(nextToken);
            }

            return awsCognitoIdentityProvider.adminListUserAuthEvents(userAuthEventsRequest);
        } catch (InternalErrorException e) {
            throw new InternalErrorException(e.getErrorMessage());
        } catch (InvalidParameterException | UserPoolAddOnNotEnabledException e) {
            throw new ApiException(ApiError.COGNITO_INVALID_PARAM, e.getErrorMessage());
        }
    }


    @Override
    public GlobalSignOutResult signOut(String accessToken) throws ApiException {
        try {
            return awsCognitoIdentityProvider.globalSignOut(new GlobalSignOutRequest().withAccessToken(accessToken));
        } catch (NotAuthorizedException e) {
            throw new ApiException(ApiError.LOG_OUT_FAILED, e.getErrorMessage());
        }
    }


    @Override
    public ForgotPasswordResult forgotPassword(String username) throws ApiException {
        try {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.withClientId(awsConfig.getCognito().getAppClientId())
                    .withUsername(username)
                    .withSecretHash(calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(), username));

            return awsCognitoIdentityProvider.forgotPassword(request);

        } catch (NotAuthorizedException e) {
            throw new ApiException(ApiError.FORGET_PASSWORD_FAILED, e.getErrorMessage());
        }
    }

    private Optional<AdminInitiateAuthResult> adminInitiateAuthResult(AdminInitiateAuthRequest request) throws ApiException {
        try {
            return Optional.of(awsCognitoIdentityProvider.adminInitiateAuth(request));
        } catch (NotAuthorizedException e) {
            throw new ApiException(ApiError.AUTH_FAILED, e.getErrorMessage());
        } catch (UserNotFoundException e) {
            String username = request.getAuthParameters().get(CognitoAttributesEnum.USERNAME.name());
            throw new ApiException(ApiError.USER_DOES_NOT_EXIST, e.getErrorMessage());
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating");
        }
    }


    public ListUsersResult getAllUsers(ListUsersRequest listUsersRequest) {
        return awsCognitoIdentityProvider.listUsers(listUsersRequest);
    }

    public GetUserResult me(GetUserRequest getUserRequest) {
        return awsCognitoIdentityProvider.getUser(getUserRequest);
    }

    public DescribeUserPoolClientResult describe(Jwt jwt){
        DescribeUserPoolClientRequest describeUserPoolClientRequest = new DescribeUserPoolClientRequest();
        describeUserPoolClientRequest.setUserPoolId(awsConfig.getCognito().getUserPoolId());
        describeUserPoolClientRequest.setClientId(jwt.getClaim("client_id"));
        return awsCognitoIdentityProvider.describeUserPoolClient(describeUserPoolClientRequest);
    }

    public AdminGetUserResult adminGetUser(String username){
        AdminGetUserRequest adminGetUserRequest = new AdminGetUserRequest();
        adminGetUserRequest.setUsername(username);
        adminGetUserRequest.setUserPoolId(awsConfig.getCognito().getUserPoolId());
        return awsCognitoIdentityProvider.adminGetUser(adminGetUserRequest);
    }


    private String generateValidPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERRONEOUS_SPECIAL_CHARS";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

}
