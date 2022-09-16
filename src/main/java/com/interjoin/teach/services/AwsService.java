package com.interjoin.teach.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.interjoin.teach.config.AWSCredentialsConfig;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.requests.AgencySignupRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.jwt.AwsCognitoIdTokenProcessor;
import com.interjoin.teach.utils.IdentityProviderFactory;
import com.interjoin.teach.utils.SecretHashUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class AwsService {

    private AwsCognitoIdTokenProcessor cognitoIdTokenProcessor;

    private final AWSCredentialsConfig cognitoCreds;

    AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();

    private BasicAWSCredentials basicAwsCredentials;

    private AWSCognitoIdentityProvider basicAuthCognitoIdentityProvider;
    private AWSCognitoIdentityProvider cognitoIdentityProvider;


    private final AmazonS3 s3Client;

//    private UserService userService;

    public AwsService(AWSCredentialsConfig cognitoCreds) {
        this.cognitoCreds = cognitoCreds;

        basicAwsCredentials = new BasicAWSCredentials(this.cognitoCreds.getAwsAccessKey(), this.cognitoCreds.getAwsSecretKey());

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
                .withRegion(Regions.EU_WEST_2)
                .build();


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

    public void addUserToGroup(String username, String groupName) {

        AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest();

        addUserToGroupRequest.setUserPoolId(this.cognitoCreds.getPoolId());
        addUserToGroupRequest.setUsername(username);
        addUserToGroupRequest.setGroupName(groupName);

        basicAuthCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
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

    public String signUpAgency(AgencySignupRequest requestForm) {

        String userCreatedUsername = null;

        List<AttributeType> attributeTypes = new ArrayList<>();
        attributeTypes.addAll(Arrays.asList(new AttributeType().withName("email").withValue(requestForm.getContactEmail()),
                new AttributeType().withName("custom:role").withValue("agency")
        ));

//        ResponseEntity<AuthenticationResponseDTO> authResponse;
        try {
            String secretVal = SecretHashUtils.calculateSecretHash(this.cognitoCreds.getClientId(), this.cognitoCreds.getClientSecret(), requestForm.getContactEmail());
            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setUsername(requestForm.getContactEmail());
            signUpRequest.setUserAttributes(attributeTypes);

            signUpRequest.setClientId(this.cognitoCreds.getClientId());
            signUpRequest.setPassword("DefaultPass1!");
            signUpRequest.setSecretHash(secretVal);

            SignUpResult signUpResult = basicAuthCognitoIdentityProvider.signUp(signUpRequest);
            userCreatedUsername = signUpResult.getUserSub();

            addUserToGroup(requestForm.getContactEmail(), "AGENCY");


        } catch (AWSCognitoIdentityProviderException ex) {
            throw ex;
        }
        return userCreatedUsername;
    }

    public void verifyUser(String username, String code) {

        ConfirmSignUpRequest request = new ConfirmSignUpRequest();
        request.setUsername(username);
        request.setConfirmationCode(code);
        request.setClientId(this.cognitoCreds.getClientId());
        request.setSecretHash(SecretHashUtils.calculateSecretHash(this.cognitoCreds.getClientId(), this.cognitoCreds.getClientSecret(), username));
        basicAuthCognitoIdentityProvider.confirmSignUp(request);

    }

    public void resendVerificationEmail(String cognitoUsername) {
        ResendConfirmationCodeRequest request = new ResendConfirmationCodeRequest();
        request.setClientId(this.cognitoCreds.getClientId());
        request.setSecretHash(SecretHashUtils.calculateSecretHash(this.cognitoCreds.getClientId(), this.cognitoCreds.getClientSecret(), cognitoUsername));
        request.setUsername(cognitoUsername);
        basicAuthCognitoIdentityProvider.resendConfirmationCode(request);
    }

    public void uploadFile(String fileRef, MultipartFile multipartFile) throws IOException {

            try {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                objectMetadata.setContentLength(multipartFile.getSize());
                this.s3Client.putObject(this.cognitoCreds.getDefaultBucketName(), fileRef, multipartFile.getInputStream(), objectMetadata);

            }
            catch (IOException e) {
                throw e;
            }

    }

    public String generatePresignedUrl(String fileRef) {
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis +=  7 * 24 * 60 * 60 * 1000;
        Date expiration = new Date();
        expiration.setTime(expTimeMillis);
        return this.s3Client.generatePresignedUrl(new GeneratePresignedUrlRequest(this.cognitoCreds.getDefaultBucketName(), fileRef)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration)).toString();
    }

}
