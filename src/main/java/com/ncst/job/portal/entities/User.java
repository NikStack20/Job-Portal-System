package com.ncst.job.portal.entities;
import java.time.LocalDateTime; 
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="users")

public class User {
	
	@Id
	@Column(name="user_id")
	private String id;
	
	@Column(name="username", nullable = false)
    private String name;
	
	@Column(name = "user_email", nullable = false, unique = true)
	private String email;

	@Column(name="user_passwords", nullable = false)
	private String password;
	
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
     private Set<Role> roles = new HashSet<>();
	

	 @Column(name="user_contact_numbers", nullable = false)
     private String contactNumber;
	
	 @Column(name = "created_at")
     private LocalDateTime createdAt = LocalDateTime.now();
	 
     @OneToMany(mappedBy = "applicant", fetch = FetchType.EAGER)
     private Set<Application> applicationsByUser = new HashSet<>();
     
     @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER)
     private Set<Job> jobsByUser = new HashSet<>();
	

}
