package com.ncst.job.portal.loadouts;
import lombok.*;  
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userId;

    @NotEmpty
    private String name;
    
    @NotEmpty 
    @Email
    private String email;
    
    @NotEmpty
    private String password;

    // accept role names as strings like "ROLE_CANDIDATE"
    private Set<String> roleNames = new HashSet<>();

    @NotEmpty
    private String contactNumber;
}
