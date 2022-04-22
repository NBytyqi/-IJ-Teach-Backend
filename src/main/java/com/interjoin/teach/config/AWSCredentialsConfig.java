package com.interjoin.teach.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
@NoArgsConstructor
@Data
public class AWSCredentialsConfig {
    private String clientId;
    private String clientSecret;
    private String poolId;
    private String awsAccessKey;
    private String awsSecretKey;
    private String defaultGroupName;
    private String cvBucketName;


}