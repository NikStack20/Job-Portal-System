package com.ncst.job.portal.service; 
import com.ncst.job.portal.entities.User;   
import com.ncst.job.portal.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
              .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        List<GrantedAuthority> auth = user.getRoles().stream()
        		.map(r -> new SimpleGrantedAuthority(r.getName().name()))
 // if Role.name is enum RoleName
            .collect(Collectors.toList());
        System.out.println("DB pw: " + user.getPassword()); // debug, remove later
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), auth);
    }
}





