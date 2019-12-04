package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.services.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.Principal;

/**
 * Endpoints related to photos
 */

@RestController
@RequestMapping("/api/v1/photo")
@Slf4j
@RequiredArgsConstructor
public class PhotoResource {

  private final PhotoService photoService;

  /**
   * Allows to upload multipart file
   *
   * @param avatar
   * @param principal
   * @return
   */
  @PostMapping("/avatar")
  public ResponseEntity uploadAvatar(@RequestPart("avatar") MultipartFile avatar, Principal principal) {
    try {
      return ResponseEntity.created(photoService.uploadAvatar(avatar, principal.getName()).toURI()).build();
    } catch (URISyntaxException e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (IOException e) {
      log.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
  }

  /**
   * redirects request to link from user profile
   * @param principal
   * @return
   */
  @GetMapping("/avatar")
  public ResponseEntity getPhotoOfCurrentProfile(Principal principal){
    try {
      return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header("Location",photoService.getPhotoLinkFromUsername(principal.getName()).toString()).build();
    } catch (MalformedURLException e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/avatar/{username}")
  public ResponseEntity getPhotoOfUser(@PathVariable("username") String username){
    try {
      return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header("Location",photoService.getPhotoLinkFromUsername(username).toString()).build();
    } catch (MalformedURLException e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/avatar")
  public ResponseEntity deletePhotoForCurrentUser(Principal principal){
    photoService.deletePhotoForUser(principal.getName());
    return ResponseEntity.ok().build();
  }
}
