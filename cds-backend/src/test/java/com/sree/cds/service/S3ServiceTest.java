package com.sree.cds.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sree.cds.entity.FileMetadata;
import com.sree.cds.repository.FileMetadataRepository;
import com.sree.cds.service.impl.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;  // Your service class
    @Mock
    private AmazonS3 amazonS3;  // Mock the Amazon S3 client
    @Mock
    private FileMetadataRepository fileMetadataRepository;  // Mock the repository
    @Mock
    private S3ObjectInputStream s3ObjectInputStream;  // Mock S3ObjectInputStream
    @Mock
    private S3Object s3Object;  // Mock S3Object

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks

        // Set the bucketName field via reflection if necessary
        ReflectionTestUtils.setField(s3Service, "bucketName", "sree");

        // Mock the behavior of the S3 client
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        // Mock readAllBytes to return a dummy byte array
        byte[] fileContent = "dummy content".getBytes();
        try {
            when(s3ObjectInputStream.readAllBytes()).thenReturn(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testUploadFile() throws IOException {

        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test-file.txt");

        String uploadedBy = "user";
        // Act
        String result = s3Service.uploadFile(file, uploadedBy);

        // Assert
        String expectedBaseUrl = "https://sree.s3.amazonaws.com/";
        String expectedFileName = "test-file.txt";

        assertNotNull(result);
        assertTrue(result.startsWith(expectedBaseUrl));
        assertTrue(result.endsWith(expectedFileName));

        // Verify interactions
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));  // Verify S3 upload
        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));  // Verify metadata save
    }

    @Test
    void testDownloadFile() throws IOException {
        // Arrange
        String fileName = "test-file.txt";

        // Act
        byte[] result = s3Service.downloadFile(fileName);

        // Assert
        assertNotNull(result);
        assertArrayEquals("dummy content".getBytes(), result);
    }

}
