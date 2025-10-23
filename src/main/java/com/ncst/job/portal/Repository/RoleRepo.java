package com.ncst.job.portal.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.RoleName;

	
	@Repository
	public interface RoleRepo extends JpaRepository<Role, Long> {
	      
		    Optional<Role> findByName(RoleName name);
}
