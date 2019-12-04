package com.akulinski.userr8meservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  private final String photoBucket;

  public S3Config(@Value("${s3.buckets.photo}") String photoBucket) {
    this.photoBucket = photoBucket;
  }

  @Bean
  public AmazonS3 amazonS3(AWSCredentials awsCredentials) {
    return AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
      .withRegion(Regions.US_EAST_2)
      .build();
  }

  @Bean
  @Qualifier("photo-bucket")
  public Bucket createPhotoBucket(AmazonS3 amazonS3) {

    if(amazonS3.doesBucketExistV2(photoBucket)){
      final var buckets = amazonS3.listBuckets();
      return buckets.stream().filter(bc->photoBucket.equals(bc.getName())).findFirst().get();
    }

    return amazonS3.createBucket(photoBucket);
  }
}
