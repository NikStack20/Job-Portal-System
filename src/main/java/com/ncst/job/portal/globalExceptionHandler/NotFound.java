package com.ncst.job.portal.globalExceptionHandler;

import lombok.Data; 
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

public class NotFound extends RuntimeException {

	String resourceName;
	String fieldName;
	String fieldValue;

	public NotFound(String resourceName, String fieldName, String fieldValue) {
		super("%s not found with %s :%s".formatted(resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
}
