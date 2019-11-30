package com.akulinski.userr8meservice.core.services;

import com.akulinski.userr8meservice.core.repository.PhotoRepository;
import com.akulinski.userr8meservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {

  private final UserRepository userRepository;

  private final PhotoRepository photoRepository;

  public URL uploadAvatar(MultipartFile multipartFile, String username) throws IOException {

    final var user = userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier(username));

    final var link = photoRepository.upload(multipartFile, user);
    user.setLink(link.toString());

    userRepository.save(user);
    return link;
  }

  public URL getPhotoLinkFromUsername(String username) throws MalformedURLException {
    final var user = userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier(username));
    return new URL(user.getLink());
  }

  public void deletePhotoForUser(String username){
    final var user = userRepository.findByUsername(username).orElseThrow(getIllegalArgumentExceptionSupplier(username));

    photoRepository.delete(username);

    user.setLink("");
    userRepository.save(user);
  }

  private Supplier<IllegalArgumentException> getIllegalArgumentExceptionSupplier(String username) {
    return () -> new IllegalArgumentException(String.format("No User found with username: %s", username));
  }
}
