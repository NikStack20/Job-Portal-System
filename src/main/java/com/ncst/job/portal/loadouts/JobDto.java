package com.ncst.job.portal.loadouts;
import java.util.HashSet;
import com.ncst.job.portal.entities.Application;
import com.ncst.job.portal.entities.Job;
import com.ncst.job.portal.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {
	
	private String jobId;
	
    private String title;

    private String description;
    
    private String company;
	
    private String postedDate;
	
    private User postedBy; // Employer who posted the job

	private boolean isActive; // Job Status Activity
	
    private String location;
  
    private double salary;

    private String applicationDeadLine;
	
    private String jobType; // e.g. Remote, Full-time, Part-Time,  Contract
	
    private String experienceLevel; // e.g. Fresher, Internship, Experienced
	
    private String industry; // e.g. IT, Finance, Healthcare, Education, CS/DA, AI/ML, etc

    private String skills; // e.g. Java, Python, SQL, Shell Scripting, Angular, etc
    
    private HashSet<Application> applicationsByJob = new HashSet<>();
    
    private HashSet<Job> jobsByUser = new HashSet<>();
	

}
