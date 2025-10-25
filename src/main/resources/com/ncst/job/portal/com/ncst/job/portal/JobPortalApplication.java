package com.ncst.job.portal.com.ncst.job.portal;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class JobPortalApplication {
    public static void main(String[] args) {
    		  
        System.out.println("Spring version at runtime: " + SpringVersion.getVersion());
        SpringApplication.run(JobPortalApplication.class, args);
      
    }
}

