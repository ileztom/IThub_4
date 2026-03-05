package com.library.loan.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private boolean active;
}
