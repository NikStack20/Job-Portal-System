package com.ncst.job.portal.loadouts;

import lombok.AllArgsConstructor; 
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor

public class ApiExceptions {
	private String message;
	private boolean success;
}
