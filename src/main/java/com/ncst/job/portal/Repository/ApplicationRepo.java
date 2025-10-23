package com.ncst.job.portal.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ncst.job.portal.entities.Application;

public interface ApplicationRepo extends JpaRepository<Application, String>{
	

	 List<Application> findByApplicantId(String applicantId);
	    List<Application> findByJobId(String jobId);
}
