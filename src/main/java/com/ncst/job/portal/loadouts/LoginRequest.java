package com.ncst.job.portal.loadouts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// package com.ncst.job.portal.auth;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "password required")
    private String password;
}

