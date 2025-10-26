package com.ncst.job.portal.service; 
import com.ncst.job.portal.entities.User; 
import com.ncst.job.portal.repository.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) { this.userRepo = userRepo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // you store email as username — so find by email
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> auth = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName().name())) // r.getName() is RoleName enum
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // username
                user.getPassword(),
                true, true, true, true,
                auth
        );
    }
}




