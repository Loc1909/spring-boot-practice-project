package com.ou.springcode.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPatchRequest(
        @Size(min = 3, max = 50)
        String username,

        @Email(message = "Email không hợp lệ")
        String email,

        @Size(max = 100)
        String fullName
) {
}
