package com.ncst.job.portal.loadouts;
import lombok.AllArgsConstructor; 
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

//package com.ncst.job.portal.auth;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "password required")
    private String password;
}

