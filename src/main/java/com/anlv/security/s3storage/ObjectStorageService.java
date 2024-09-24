package com.anlv.security.s3storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.anlv.security.product.ProductImage;
import com.anlv.security.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


@RequiredArgsConstructor
@Service
@Slf4j
public class ObjectStorageService {

    private final AmazonS3 client;
    @Value("${object-storage.bucketName}")
    private String bucketName;
    @Value("${object-storage.endpoint}")
    private String prefix;
    public ProductImage uploadFile(MultipartFile multipartFile) {
        ProductImage productImage = null;
        try {
            productImage = new ProductImage();
            boolean found = client.doesBucketExist(bucketName);
            if (!found) {
                client.createBucket(bucketName);
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());
            String fileName = FileUtil.generateFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), metadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(request);
            productImage.setImageUrl(prefix + "/" + bucketName + "/" + fileName);
            productImage.setImageName(fileName);
            return productImage;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void deleteFile(String  fileName) {
        try {
            DeleteObjectRequest dor = new DeleteObjectRequest(bucketName,fileName);
            client.deleteObject(dor);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
    }



}
