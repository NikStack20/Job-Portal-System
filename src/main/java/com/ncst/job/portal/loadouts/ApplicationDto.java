package com.ncst.job.portal.loadouts;
import com.ncst.job.portal.entities.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
	
	
 private String applicationId;
 private String jobId;         // <-- store job id, not full Job entity
 private String applicantId;   // <-- storing applicant user id
 private String applicationDate;
 private String resumeLink;
 private ApplicationStatus applicationStatus;
}

