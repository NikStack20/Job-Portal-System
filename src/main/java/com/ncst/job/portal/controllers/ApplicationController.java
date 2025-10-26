package com.ncst.job.portal.controllers;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ncst.job.portal.loadouts.ApplicationDto;
import com.ncst.job.portal.service.ApplicationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Apply to a job (candidate)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CANDIDATE','ROLE_ADMIN')") // candidate or admin can apply
    public ResponseEntity<ApplicationDto> createApplication(@RequestBody ApplicationDto applicationDto,
                                                            Principal principal) {
        ApplicationDto created = applicationService.applyToJob(applicationDto, principal);
        return ResponseEntity.status(201).body(created);
    }

    // Update application (status etc) - allow recruiter/employer or admin to change status
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECRUITER','ROLE_ADMIN','ROLE_CANDIDATE')")
    public ResponseEntity<ApplicationDto> updateApplication(@RequestBody ApplicationDto applicationDto,
                                                            @PathVariable("id") String id,
                                                            Principal principal) {
        ApplicationDto updated = applicationService.updateApplication(applicationDto, id, principal);
        return ResponseEntity.ok(updated);
    }

    // Delete application
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECRUITER','ROLE_ADMIN','ROLE_CANDIDATE')")
    public ResponseEntity<?> deleteApplication(@PathVariable("id") String id, Principal principal) {
        applicationService.deleteApplication(id, principal);
        return ResponseEntity.noContent().build();
    }

    // Get application by id (all roles allowed to view; you can restrict)
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplication(@PathVariable("id") String id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    // Get all (admin)
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // Get all by applicant
    @GetMapping("/user/{applicantId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECRUITER','ROLE_CANDIDATE')")
    public ResponseEntity<List<ApplicationDto>> getByApplicant(@PathVariable String applicantId, Principal principal) {
        // optionally allow candidate only to request their own list; check principal if needed
        return ResponseEntity.ok(applicationService.getApplicationsByApplicant(applicantId));
    }

    // Get all by job
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECRUITER')")
    public ResponseEntity<List<ApplicationDto>> getByJob(@PathVariable String jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }
}

