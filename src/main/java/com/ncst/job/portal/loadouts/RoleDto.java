package com.ncst.job.portal.loadouts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
	
	private Long roleId;
	
	private String name;
	
	public String getRole() {
		return this.name.toString();
	}

}
