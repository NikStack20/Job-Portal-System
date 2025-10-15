package com.ncst.job.portal.loadouts;
import java.util.HashSet;
import java.util.Set;
import com.ncst.job.portal.entities.Application;
import com.ncst.job.portal.entities.Job;
import com.ncst.job.portal.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	
	private String userId;

	@NotEmpty(message = "username must not be empty !!")
    private String name;
	
	@NotEmpty(message = "email must not be empty !!")
	@Email(message = "enter a valid email address !!")
	private String email;

	@NotEmpty(message = "password must not be empty !!")
	private String password;

    private Set<Role> roles = new HashSet<>();

    @NotEmpty(message = "contact must not be empty !!")
	private String contactNumber;
	 
    private Set<Application> applicationsByUser = new HashSet<>();
    
    private Set<Job> jobsByUser = new HashSet<>();
	

}
