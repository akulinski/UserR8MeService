package com.akulinski.userr8meservice.core.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangePasswordDTO implements Serializable {
    private String oldPassword;
    private String newPassword;
}
