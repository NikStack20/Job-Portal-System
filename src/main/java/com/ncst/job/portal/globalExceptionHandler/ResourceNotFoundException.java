package com.ncst.job.portal.globalExceptionHandler;

import lombok.Data; 
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}


