package com.anlv.security.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



@RequiredArgsConstructor
@Service
public class ObjectStorageService {

    private final AmazonS3 client;
    public void uploadFile(MultipartFile multipartFile) {
        try {
            boolean found = client.doesBucketExist("anlv");
            if (!found) {
                client.createBucket("anlv");
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            PutObjectRequest request = new PutObjectRequest("anlv", StringUtils.cleanPath(multipartFile.getOriginalFilename()), multipartFile.getInputStream(), metadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(request);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
    }
}
