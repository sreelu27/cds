package com.sree.cds.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.sree.cds.entity.FileMetadata;
import com.sree.cds.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final FileMetadataRepository fileMetadataRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 amazonS3, FileMetadataRepository fileMetadataRepository) {
        this.amazonS3 = amazonS3;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public String uploadFile(MultipartFile file, String uploadedBy) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        // Upload the file
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), new ObjectMetadata()));

        // Save metadata to the database
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(fileName);
        metadata.setS3Url(fileUrl);
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setUploadedBy(uploadedBy);
        fileMetadataRepository.save(metadata);

        return fileUrl;
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));
        return s3Object.getObjectContent().readAllBytes();
    }
}