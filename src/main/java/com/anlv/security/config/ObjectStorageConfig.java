package com.anlv.security.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectStorageConfig {
    @Value("${object-storage.endpoint}")
    private String url;

    @Value("${object-storage.accessKey}")
    private String accessKey;

    @Value("${object-storage.secretKey}")
    private String accessSecret;



    @Bean
    public AmazonS3 s3Client() {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
        AmazonS3 client = new AmazonS3Client(credentials);
        client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
        client.setEndpoint(url);

        return client;
    }
}