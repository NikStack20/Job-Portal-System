package com.ncst.job.portal.service.impl;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ncst.job.portal.entities.Job;
import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.globalExceptionHandler.ResourceNotFoundException;
import com.ncst.job.portal.loadouts.JobDto;
import com.ncst.job.portal.Repository.ApplicationRepo;
import com.ncst.job.portal.Repository.JobRepo;
import com.ncst.job.portal.Repository.UserRepo;
import com.ncst.job.portal.service.JobService;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ApplicationRepo applicationRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JobDto createJob(JobDto jobDto, String employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerId));

        Job job = modelMapper.map(jobDto, Job.class);
        // generate id if not provided
        String id = (job.getJobId() == null || job.getJobId().isBlank()) ? UUID.randomUUID().toString()
                : job.getJobId();
        job.setJobId(id);
        job.setPostedBy(employer);
        job.setCreatedAt(java.time.LocalDateTime.now());
        Job saved = jobRepository.save(job);
        return modelMapper.map(saved, JobDto.class);
    }

    @Override
    public JobDto updateJob(JobDto jobDto, String jobId) {
        Job existing = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        // Approach: map DTO to a Job instance then set fields explicitly to avoid overwriting postedBy/createdAt unintentionally.
        Job temp = modelMapper.map(jobDto, Job.class);

        existing.setTitle(temp.getTitle());
        existing.setDescription(temp.getDescription());
        existing.setCompany(temp.getCompany());
        existing.setLocation(temp.getLocation());
        existing.setSalary(temp.getSalary());
        existing.setApplicationDeadLine(temp.getApplicationDeadLine());
        existing.setJobType(temp.getJobType());
        existing.setExperienceLevel(temp.getExperienceLevel());
        existing.setIndustry(temp.getIndustry());
        existing.setSkills(temp.getSkills());
        existing.setActive(temp.isActive());

        Job updated = jobRepository.save(existing);
        return modelMapper.map(updated, JobDto.class);
    }

    @Override
    public void deleteJob(String jobId) {
        Job existing = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        jobRepository.delete(existing);
    }

    @Override
    public Page<JobDto> getAllJobs(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Job> page = jobRepository.findAll(pageable);
        List<JobDto> dtoList = page.getContent().stream().map(job -> modelMapper.map(job, JobDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Override
    public JobDto getJobById(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return modelMapper.map(job, JobDto.class);
    }

    @Override
    public List<JobDto> getJobsByEmployer(String employerId) {
        List<Job> jobs = jobRepository.findByPostedById(employerId);
        return jobs.stream().map(job -> modelMapper.map(job, JobDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<JobDto> searchJobs(String keyword) {
        Pageable p = PageRequest.of(0, 100);
        Page<Job> page = jobRepository
                .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword,
                        keyword, keyword, p);
        return page.getContent().stream().map(job -> modelMapper.map(job, JobDto.class)).collect(Collectors.toList());
    }

    // ---------------- NEW: change active status ----------------
    @Override
    public JobDto changeJobStatus(String jobId, boolean active) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        job.setActive(active);
        Job saved = jobRepository.save(job);
        return modelMapper.map(saved, JobDto.class);
    }
}

