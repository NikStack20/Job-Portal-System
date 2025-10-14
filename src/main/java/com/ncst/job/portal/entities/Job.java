package com.ncst.job.portal.entities;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="jobs")

public class Job {
	   
	@Id
	@Column(name="job_id")
      private String jobId;
	
	@Column(name="job_title")
      private String title;
	
	@Column(name="job_description", length = 1000)
      private String description;
	
	@Column(name="Company")
      private String company;
	
	@Column(name="job_posted_date")
      private String postedDate;
	
	@ManyToOne
	@JoinColumn(name="posted_by_user")
    private User postedBy; // Employer who posted the job

	@Column(name="is_active")
	private boolean isActive; // Job Status Activity
	
	 @Column(name = "created_at")
	    private LocalDateTime createdAt = LocalDateTime.now();
	

    private String location;
    
    
    private double salary;


      private String applicationDeadLine;
	
	
      private String jobType; // e.g. Remote, Full-time, Part-Time,  Contract
	

      private String experienceLevel; // e.g. Fresher, Internship, Experienced
	
	
      private String industry; // e.g. IT, Finance, Healthcare, Education, CS/DA, AI/ML, etc
	

      private String skills; // e.g. Java, Python, SQL, Shell Scripting, Angular, etc
	

}
