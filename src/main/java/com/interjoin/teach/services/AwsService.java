package com.interjoin.teach.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.interjoin.teach.config.AWSCredentialsConfig;
import com.interjoin.teach.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Service
public class AwsService {

    private final AWSCredentialsConfig cognitoCreds;

    AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();

    private BasicAWSCredentials basicAwsCredentials;

    private final AmazonS3 s3Client;

//    private UserService userService;

    public AwsService(AWSCredentialsConfig cognitoCreds) {
        this.cognitoCreds = cognitoCreds;

        basicAwsCredentials = new BasicAWSCredentials(this.cognitoCreds.getAwsAccessKey(), this.cognitoCreds.getAwsSecretKey());

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
                .withRegion(Regions.EU_WEST_2)
                .build();
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
