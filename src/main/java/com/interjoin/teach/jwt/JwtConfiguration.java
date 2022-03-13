package com.interjoin.teach.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "com.interjoin.teach.jwt.aws"
)
@Data
public class JwtConfiguration {
    @Value("${aws.poolId}")
    private String userPoolId;
    private String identityPoolId;
    private String jwkUrl;
    private String region = "eu-west-2";
    private String userNameField = "username";
    private String groupsField = "cognito:groups";
    private int connectionTimeout = 2000;
    private int readTimeout = 2000;
    private String httpHeader = "Authorization";

    public JwtConfiguration() {
    }

    public String getJwkUrl() {
        return this.jwkUrl != null && !this.jwkUrl.isEmpty() ? this.jwkUrl : String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", this.region, this.userPoolId);
    }

    public String getCognitoIdentityPoolUrl() {
        return String.format("https://cognito-idp.%s.amazonaws.com/%s", this.region, this.userPoolId);
    }
}