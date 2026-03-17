package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import org.example.backend.entity.Account;

@Data
@Builder
public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String department;
    private String position;
    private String university;
    private String major;
    private String status;
    private Account.Role role;
}
