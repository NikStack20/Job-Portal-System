package com.ncst.job.portal.service;
import java.util.List;  
import org.springframework.data.domain.Page;
import com.ncst.job.portal.loadouts.JobDto;

public interface JobService {

    JobDto createJob(JobDto jobDto, String employerId);

    JobDto updateJob(JobDto jobDto, String jobId);

    void deleteJob(String jobId);

    Page<JobDto> getAllJobs(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    JobDto getJobById(String jobId);

    List<JobDto> getJobsByEmployer(String employerId);

    List<JobDto> searchJobs(String keyword);

    // NEW: change active status (activate / deactivate)
    JobDto changeJobStatus(String jobId, boolean active);
}

