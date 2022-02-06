package com.interjoin.teach.services;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.interjoin.teach.config.AWSCredentialsConfig;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.jwt.AwsCognitoIdTokenProcessor;
import com.interjoin.teach.utils.IdentityProviderFactory;
import com.interjoin.teach.utils.SecretHashUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    private AwsCognitoIdTokenProcessor cognitoIdTokenProcessor;

    private final AWSCredentialsConfig cognitoCreds;

    AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();

    private BasicAWSCredentials basicAwsCredentials;

    private AWSCognitoIdentityProvider basicAuthCognitoIdentityProvider;
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    private UserService userService;

    public AuthService(AwsCognitoIdTokenProcessor cognitoIdTokenProcessor,
                       AWSCredentialsConfig cognitoCreds,
                       UserService userService) {
        this.cognitoCreds = cognitoCreds;

        basicAwsCredentials = new BasicAWSCredentials(this.cognitoCreds.getAwsAccessKey(), this.cognitoCreds.getAwsSecretKey());

        basicAuthCognitoIdentityProvider = IdentityProviderFactory.getIdentityProvider(basicAwsCredentials);

        cognitoIdentityProvider = IdentityProviderFactory.getIdentityProvider(this.awsCreds);

        this.cognitoIdTokenProcessor = cognitoIdTokenProcessor;

        this.userService = userService;
    }

    public void signUpUser(UserSignupRequest requestForm, String groupName) {

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

            addUserToGroup(requestForm.getEmail(), groupName);

//            User user = User.builder()
//                    .username(signUpResult.getUserSub())
//                    .email(requestForm.getEmail())
//                    .firstName(requestForm.getFirstName())
//                    .lastName(requestForm.getLastName())
//                    .createdDate(LocalDateTime.now())
//
//                    .build();

            this.userService.createUser(requestForm, groupName);

        } catch (AWSCognitoIdentityProviderException ex) {
            throw ex;
        }

    }

    public void addUserToGroup(String username, String groupName) {

        AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest();

        addUserToGroupRequest.setUserPoolId(this.cognitoCreds.getPoolId());
        addUserToGroupRequest.setUsername(username);
        addUserToGroupRequest.setGroupName(groupName);

        basicAuthCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
    }
}
