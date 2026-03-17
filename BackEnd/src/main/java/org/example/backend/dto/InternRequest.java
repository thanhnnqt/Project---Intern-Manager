package org.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InternRequest {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String university;
    private String major;
    private Long mentorId;
    private String status;
    private String avatar;
    private String password;
}
