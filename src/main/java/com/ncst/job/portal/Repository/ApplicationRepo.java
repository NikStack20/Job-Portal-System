package com.ncst.job.portal.repository;
import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncst.job.portal.entities.Application;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, String>{
	

	 List<Application> findByApplicantId(String applicantId);
	    List<Application> findByJobJobId(String jobId);
	
}
