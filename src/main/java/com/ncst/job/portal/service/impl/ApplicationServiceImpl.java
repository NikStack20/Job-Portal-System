package com.ncst.job.portal.service.impl;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ncst.job.portal.entities.Application;
import com.ncst.job.portal.entities.Job;
import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.loadouts.ApplicationDto;
import com.ncst.job.portal.repository.ApplicationRepo;
import com.ncst.job.portal.repository.JobRepo;
import com.ncst.job.portal.repository.UserRepo;
import com.ncst.job.portal.service.ApplicationService;
import com.ncst.job.portal.service.UserService;
import com.ncst.job.portal.globalExceptionHandler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final JobRepo jobRepo;
    private final UserRepo userRepo;
    private final UserService userService; // has getUserFromPrincipal helper

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public ApplicationDto applyToJob(ApplicationDto applicationDto, Principal principal) {
        // job must exist
        Job job = jobRepo.findById(applicationDto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", applicationDto.getJobId()));

        // get applicant: prefer principal if provided, else applicantId in DTO
        User applicant = null;
        if (principal != null) {
            applicant = userService.getUserFromPrincipal(principal);
        }
        if (applicant == null) {
            applicant = userRepo.findById(applicationDto.getApplicantId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicationDto.getApplicantId()));
        }

        // optional: check job is active
        if (!job.isActive()) {
            throw new IllegalStateException("Cannot apply to an inactive job.");
        }

        // Prevent duplicate application (optional)
        List<Application> existing = applicationRepo.findByApplicantId(applicant.getId()).stream()
                .filter(a -> a.getJob() != null && job.getJobId().equals(a.getJob().getJobId()))
                .collect(Collectors.toList());
        if (!existing.isEmpty()) {
            throw new IllegalStateException("You have already applied to this job.");
        }

        Application app = new Application();
        app.setId(UUID.randomUUID().toString());
        app.setJob(job);
        app.setApplicant(applicant);
        app.setApplicationDate(LocalDateTime.now().format(ISO));
        app.setResumeLink(applicationDto.getResumeLink());
        // applicationStatus uses entity default if not set; you can override:
        if (applicationDto.getApplicationStatus() != null) {
            app.setApplicationStatus(applicationDto.getApplicationStatus());
        }

        Application saved = applicationRepo.save(app);
        return toDto(saved);
    }

    @Override
    public ApplicationDto updateApplication(ApplicationDto applicationDto, String applicationId, Principal principal) {
        Application existing = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        // Who can update? Admin or the poster of the job (employer) or applicant (depending on rule).
        // Here: allow admin OR job.postedBy OR applicant to update certain fields.
        User current = userService.getUserFromPrincipal(principal);
        boolean isOwnerJob = existing.getJob() != null && current != null &&
                current.getId().equals(existing.getJob().getPostedBy().getId());
        boolean isApplicant = current != null && current.getId().equals(existing.getApplicant().getId());
        // allow only owner job or admin or applicant to update
        // (if you have role check, use PreAuthorize in controller)
        if (!(isOwnerJob || isApplicant)) {
            // optionally allow admins via role check (you can inspect current.getRoles())
            throw new AccessDeniedException("Not authorized to update this application");
        }

        if (applicationDto.getResumeLink() != null) {
            existing.setResumeLink(applicationDto.getResumeLink());
        }
        if (applicationDto.getApplicationDate() != null) {
            existing.setApplicationDate(applicationDto.getApplicationDate());
        }
        if (applicationDto.getApplicationStatus() != null) {
            existing.setApplicationStatus(applicationDto.getApplicationStatus());
        }

        Application updated = applicationRepo.save(existing);
        return toDto(updated);
    }

    @Override
    public void deleteApplication(String applicationId, Principal principal) {
        Application existing = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User current = userService.getUserFromPrincipal(principal);
        boolean isApplicant = current != null && current.getId().equals(existing.getApplicant().getId());
        boolean isJobOwner = current != null && existing.getJob() != null && current.getId().equals(existing.getJob().getPostedBy().getId());
        if (!(isApplicant || isJobOwner)) {
            throw new AccessDeniedException("Not authorized to delete this application");
        }

        applicationRepo.delete(existing);
    }

    @Override
    public ApplicationDto getApplicationById(String applicationId) {
        return toDto(applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId)));
    }

    @Override
    public List<ApplicationDto> getAllApplications() {
        return applicationRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getApplicationsByApplicant(String applicantId) {
        return applicationRepo.findByApplicantId(applicantId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getApplicationsByJob(String jobId) {
        return applicationRepo.findByJobJobId(jobId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Application getApplicationEntityById(String id) {
        return applicationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }

    // helper mapping
    private ApplicationDto toDto(Application app) {
        ApplicationDto d = new ApplicationDto();
        d.setApplicationId(app.getId());
        if (app.getJob() != null) d.setJobId(app.getJob().getJobId());
        if (app.getApplicant() != null) d.setApplicantId(app.getApplicant().getId());
        d.setApplicationDate(app.getApplicationDate());
        d.setResumeLink(app.getResumeLink());
        d.setApplicationStatus(app.getApplicationStatus());
        return d;
    }
}
