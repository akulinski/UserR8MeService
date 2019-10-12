package com.akulinski.keepmeawake.core.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangePasswordDTO implements Serializable {
    private String oldPassword;
    private String newPassword;
}
