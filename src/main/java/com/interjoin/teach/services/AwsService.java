package com.interjoin.teach.services;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.interjoin.teach.config.AWSCredentialsConfig;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.jwt.AwsCognitoIdTokenProcessor;
import com.interjoin.teach.utils.IdentityProviderFactory;
import com.interjoin.teach.utils.SecretHashUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AwsService {

    private AwsCognitoIdTokenProcessor cognitoIdTokenProcessor;

    private final AWSCredentialsConfig cognitoCreds;

    AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();

    private BasicAWSCredentials basicAwsCredentials;

    private AWSCognitoIdentityProvider basicAuthCognitoIdentityProvider;
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

//    private UserService userService;

    public AwsService(AwsCognitoIdTokenProcessor cognitoIdTokenProcessor,
                       AWSCredentialsConfig cognitoCreds) {
        this.cognitoCreds = cognitoCreds;

        basicAwsCredentials = new BasicAWSCredentials(this.cognitoCreds.getAwsAccessKey(), this.cognitoCreds.getAwsSecretKey());

        basicAuthCognitoIdentityProvider = IdentityProviderFactory.getIdentityProvider(basicAwsCredentials);

        cognitoIdentityProvider = IdentityProviderFactory.getIdentityProvider(this.awsCreds);

        this.cognitoIdTokenProcessor = cognitoIdTokenProcessor;

    }


    public String signUpUser(UserSignupRequest requestForm, String groupName) {

        String userCreatedUsername = null;

        List<AttributeType> attributeTypes = new ArrayList<>();
        attributeTypes.addAll(Arrays.asList(new AttributeType().withName("email").withValue(requestForm.getEmail()),
                new AttributeType().withName("custom:role").withValue(groupName.toLowerCase())
               ));

//        ResponseEntity<AuthenticationResponseDTO> authResponse;
        try {
            String secretVal = SecretHashUtils.calculateSecretHash(this.cognitoCreds.getClientId(), this.cognitoCreds.getClientSecret(), requestForm.getEmail());
            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setUsername(requestForm.getEmail());
            signUpRequest.setUserAttributes(attributeTypes);

            signUpRequest.setClientId(this.cognitoCreds.getClientId());
            signUpRequest.setPassword(requestForm.getPassword());
            signUpRequest.setSecretHash(secretVal);

            SignUpResult signUpResult = basicAuthCognitoIdentityProvider.signUp(signUpRequest);
            userCreatedUsername = signUpResult.getUserSub();

            addUserToGroup(requestForm.getEmail(), groupName);


        } catch (AWSCognitoIdentityProviderException ex) {
            throw ex;
        }
        return userCreatedUsername;
    }

    public AuthResponse signInUser(UserSignInRequest request) {
        InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest();

        initiateAuthRequest.setAuthFlow(AuthFlowType.USER_PASSWORD_AUTH);
        initiateAuthRequest.setClientId(this.cognitoCreds.getClientId());
        initiateAuthRequest.addAuthParametersEntry("USERNAME", request.getEmail());
        initiateAuthRequest.addAuthParametersEntry("PASSWORD", request.getPassword());

        //Only to be used if the pool contains the secret key.
        if (this.cognitoCreds.getClientSecret() != null && !this.cognitoCreds.getClientSecret().isEmpty()) {
            initiateAuthRequest.addAuthParametersEntry("SECRET_HASH", SecretHashUtils.calculateSecretHash(this.cognitoCreds.getClientId(), this.cognitoCreds.getClientSecret(), request.getEmail()));
        }

        InitiateAuthResult initiateAuthResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest);

        final String ACCESS_TOKEN = initiateAuthResult.getAuthenticationResult().getAccessToken();
        final String REFRESH_TOKEN = initiateAuthResult.getAuthenticationResult().getRefreshToken();
        return AuthResponse.builder()
                .token(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    public void addUserToGroup(String username, String groupName) {

        AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest();

        addUserToGroupRequest.setUserPoolId(this.cognitoCreds.getPoolId());
        addUserToGroupRequest.setUsername(username);
        addUserToGroupRequest.setGroupName(groupName);

        basicAuthCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
    }
}
