package com.ncst.job.portal.entities;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

public class User implements UserDetails {
	
	@Id
	@Column(name="user_id")
	private String id;
	
	@Column(name="user_name", nullable = false)
    private String username;
	
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
     
     @Override
 	public Collection<? extends GrantedAuthority> getAuthorities() { // Since, Spring Security understands authorities
 																		// not roles
 		List<SimpleGrantedAuthority> authorities = this.roles.stream()
 				.map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
 		return authorities;
 	}

 	@Override
 	public String getUsername() {

 		return this.email;
 	}

 	@Override
 	public String getPassword() {

 		return this.password;
 	}

 	@Override
 	public boolean isAccountNonExpired() {

 		return true;
 	}

 	@Override
 	public boolean isAccountNonLocked() {

 		return true;
 	}

 	@Override
 	public boolean isCredentialsNonExpired() {

 		return true;
 	}

 	@Override
 	public boolean isEnabled() {

 		return true;
 	}

 }

	


