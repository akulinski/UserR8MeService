package com.akulinski.userr8meservice.core.repository;

import com.akulinski.userr8meservice.core.domain.User;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Repository
public class PhotoRepositoryImpl implements PhotoRepository {

  private final String photoBucket;

  private final AmazonS3 amazonS3;

  public PhotoRepositoryImpl(@Value("${s3.buckets.photo}") String photoBucket, AmazonS3 amazonS3) {
    this.photoBucket = photoBucket;
    this.amazonS3 = amazonS3;
  }

  @Override
  public URL upload(MultipartFile multipartFile, User user) throws IOException {
    final var objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());

    final String key = getKey(multipartFile, user);

    final var putObjectRequest = new PutObjectRequest(photoBucket, key, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

    amazonS3.putObject(putObjectRequest);

    return amazonS3.getUrl(photoBucket, key);
  }

  private String getKey(MultipartFile multipartFile, User user) {
    return user.getUsername() + "/" + multipartFile.getOriginalFilename();
  }

  @Override
  public URL find(String photoName) {
    return  amazonS3.getUrl(photoBucket, photoName);
  }

  @Override
  public void delete(String username) {
    amazonS3.deleteObject(photoBucket, username);
  }
}
