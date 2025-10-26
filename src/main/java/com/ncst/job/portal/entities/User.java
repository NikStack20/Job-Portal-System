package com.ncst.job.portal.entities;
import jakarta.persistence.*; 
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    private String id;

    @Column(name = "user_name", nullable = false)
    private String username; // display name

    @Column(name = "user_email", nullable = false, unique = true)
    private String email; // we will use email as principal (getUsername())

    @Column(name = "user_passwords", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "user_contact_numbers", nullable = false)
    private String contactNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "applicant", fetch = FetchType.EAGER)
    private Set<Application> applicationsByUser = new HashSet<>();

    @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER)
    private Set<Job> jobsByUser = new HashSet<>();

    // ------------ UserDetails implementation --------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                // IMPORTANT: use enum.name() (string) as authority
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    /**
     * For Spring Security principal, use email (as your code expects).
     * If you want username (display name) to be the principal instead,
     * change this to return this.username.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // keep all account flags true for now (implement as needed)
    @Override
    public boolean isAccountNonExpired() {
    	return true; 
    	}
    
    @Override
    public boolean isAccountNonLocked() { 
    	return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired()  {
    	return true;
    	}
    
    @Override
    public boolean isEnabled() { 
    	return true; 
    	}
}


	


