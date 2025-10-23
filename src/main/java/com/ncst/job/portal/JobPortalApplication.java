package com.ncst.job.portal;

import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ncst.job.portal.Repository.RoleRepo;
import com.ncst.job.portal.Repository.UserRepo;
import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.User;

@EnableJpaRepositories(basePackages = "com.ncst.job.portal.Repository")
@SpringBootApplication
public class JobPortalApplication {
	
	@Bean
	public CommandLineRunner seedData(RoleRepo roleRepo, UserRepo userRepo, PasswordEncoder encoder) {
	    return args -> {
	        if(roleRepo.count()==0) {
	            Role r1 = roleRepo.save(new Role(null,"ROLE_ADMIN"));
	            Role r2 = roleRepo.save(new Role(null,"ROLE_EMPLOYER"));
	            Role r3 = roleRepo.save(new Role(null,"ROLE_APPLICANT"));
	        }
	        if(!userRepo.findByEmail("admin@job.com").isPresent()) {
	            User admin = new User();
	            admin.setName("Admin");
	            admin.setEmail("admin@job.com");
	            admin.setPassword(encoder.encode("Admin@123"));
	            admin.setRoles(new HashSet<>(roleRepo.findAll()));
	            userRepo.save(admin);
	            System.out.println("Created admin: admin@job.com / Admin@123");
	        }
	    };
	}

	

	public static void main(String[] args) {
		SpringApplication.run(JobPortalApplication.class, args);
		
		
		
		
	}
	

}
