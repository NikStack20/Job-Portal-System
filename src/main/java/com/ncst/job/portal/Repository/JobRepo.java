package com.ncst.job.portal.Repository;
import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncst.job.portal.entities.Job;

@Repository
public interface JobRepo extends JpaRepository<Job, String> {
    
	    Page<Job> findAll(Pageable pageable);

	    List<Job> findByPostedById(String employerId);

	    Page<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
	            String title, String company, String description, org.springframework.data.domain.Pageable p);

	}

