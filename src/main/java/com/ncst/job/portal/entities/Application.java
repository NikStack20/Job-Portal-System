package com.ncst.job.portal.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="applications")
public class Application {
	
	@Id
	private String id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="job_id")
	private Job job;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="applicant_id")
	private User applicant;
	
	private String applicationDate;
	
	private String resumeLink;
	
	@Enumerated(EnumType.STRING)
	private ApplicationStatus applicationStatus = ApplicationStatus.ACCEPTED;
	
	
}
