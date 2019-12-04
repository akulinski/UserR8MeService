package com.akulinski.userr8meservice.core.repository;

import com.akulinski.userr8meservice.core.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface PhotoRepository {
  URL upload(MultipartFile multipartFile, User user) throws IOException;

  URL find(String photoName);

  void delete(String username);
}
