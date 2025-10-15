package com.ncst.job.portal.loadouts;

import java.util.HashSet; 

import com.ncst.job.portal.entities.Application;
import com.ncst.job.portal.entities.ApplicationStatus;
import com.ncst.job.portal.entities.Job;
import com.ncst.job.portal.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
	
	private String applicationId;
	
	private Job job;
	
	private User applicant;
	
	private String applicationDate;
	
	private String resumeLink;
	
	private ApplicationStatus applicationStatus = ApplicationStatus.ACCEPTED;
	
    private HashSet<Application> applicationsByJob = new HashSet<>();
	  
    private HashSet<Application> applicationsByUser = new HashSet<>();
	
	
}
