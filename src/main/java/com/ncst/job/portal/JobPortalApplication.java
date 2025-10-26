package com.ncst.job.portal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.RoleName;
import com.ncst.job.portal.repository.RoleRepo;

@SpringBootApplication(
    exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    }
)
public class JobPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobPortalApplication.class, args);
        
    }
    @Bean
    CommandLineRunner roleSeeder(RoleRepo roleRepo) {
        return args -> {
            for (RoleName rn : RoleName.values()) {
                if (roleRepo.findByName(rn).isEmpty()) {
                    roleRepo.save(new Role(rn));
                    System.out.println("Inserted role -> " + rn);
                }
            }
            System.out.println("RoleDataSeeder completed. Role count = " + roleRepo.count());
        };
    }
}


