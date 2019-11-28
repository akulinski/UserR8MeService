package com.akulinski.userr8meservice.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO implements Serializable {

  private String username;

  private String password;

  private String email;

}
