package com.ncst.job.portal.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ncst.job.portal.entities.Application;

public interface ApplicationRepo extends JpaRepository<Application, String>{
	

}
