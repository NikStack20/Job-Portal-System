package com.ncst.job.portal.controllers;
import java.security.Principal; 
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ncst.job.portal.loadouts.JobDto;
import com.ncst.job.portal.service.JobService;
import com.ncst.job.portal.service.UserService;
import jakarta.validation.Valid;
import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.RoleName;
import com.ncst.job.portal.entities.User;

@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService; // used to resolve Principal -> User entity

    // ---------------- Create ----------------

    // Create using authenticated user (recommended)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYER','ROLE_ADMIN')")
    public ResponseEntity<JobDto> createJob(@Valid @RequestBody JobDto jobDto, Principal principal) {
        User current = userService.getUserByEmail(principal.getName());
        JobDto created = jobService.createJob(jobDto, current.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    // (Optional) create by userId (testing)
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYER','ROLE_ADMIN')")
    public ResponseEntity<JobDto> createJobByUserId(@PathVariable String userId,
                                                    @Valid @RequestBody JobDto jobDto) {
        JobDto created = jobService.createJob(jobDto, userId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ---------------- Update ----------------

    @PutMapping("/{jobId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYER','ROLE_ADMIN')")
    public ResponseEntity<JobDto> updateJob(@Valid @RequestBody JobDto jobDto,
                                            @PathVariable String jobId,
                                            Principal principal) {
        User current = userService.getUserByEmail(principal.getName());
        JobDto job = jobService.getJobById(jobId);

        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equalsIgnoreCase("ROLE_ADMIN"));
        boolean isOwner = job.getPostedBy().getId().equals(current.getId());

        if (!isAdmin && !isOwner)
            throw new AccessDeniedException("Not allowed to update this job");

        JobDto updated = jobService.updateJob(jobDto, jobId);
        return ResponseEntity.ok(updated);
    }


    // ---------------- Delete ----------------

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYER','ROLE_ADMIN')")
    public ResponseEntity<?> deleteJob(@PathVariable String jobId, Principal principal) {
        User current = userService.getUserByEmail(principal.getName());
        JobDto job = jobService.getJobById(jobId);

        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equalsIgnoreCase("ROLE_ADMIN"));
        boolean isOwner = job.getPostedBy().getId().equals(current.getId());

        if (!isAdmin && !isOwner)
            throw new AccessDeniedException("Not allowed to delete this job");

        jobService.deleteJob(jobId);
        return ResponseEntity.ok("Job deleted successfully");
    }


    // ---------------- Get / List ----------------

    @GetMapping("/{jobId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<JobDto> getJobById(@PathVariable String jobId) {
        JobDto job = jobService.getJobById(jobId);
        return ResponseEntity.ok(job);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<Page<JobDto>> getAllJobs(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        Page<JobDto> page = jobService.getAllJobs(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobDto>> getJobsByEmployer(@PathVariable String userId) {
        List<JobDto> jobs = jobService.getJobsByEmployer(userId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search/{keyword}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<List<JobDto>> searchJobs(@PathVariable String keyword) {
        List<JobDto> results = jobService.searchJobs(keyword);
        return ResponseEntity.ok(results);
    }


    // ---------------- Patch: change active status ----------------

    /**
     * Set job active/inactive.
     * PATCH /api/jobs/{jobId}/status?active=true
     */
    @PatchMapping("/{jobId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYER','ROLE_ADMIN')")
    public ResponseEntity<JobDto> changeJobStatus(@PathVariable String jobId,
                                                  @RequestParam("active") boolean active,
                                                  Principal principal) {
        User current = userService.getUserByEmail(principal.getName());
        if (!isOwnerOrAdmin(current, jobId)) {
            throw new AccessDeniedException("You are not allowed to change status of this job");
        }
        JobDto updated = jobService.changeJobStatus(jobId, active);
        return ResponseEntity.ok(updated);
    }

    // ---------------- helper ----------------
    /**
     * Returns true if current user is admin OR is the owner (poster) of the job.
     * Uses jobService.getJobById(...) which throws ResourceNotFoundException for invalid jobId.
     */
    private boolean isOwnerOrAdmin(User currentUser, String jobId) {
        if (currentUser == null) return false;

        // admin check — compare enum, null-safe
        boolean isAdmin = currentUser.getRoles() != null &&
            currentUser.getRoles().stream()
                .map(Role::getName)             // RoleName (enum) — NOT a String
                .filter(Objects::nonNull)
                .anyMatch(rn -> rn == RoleName.ROLE_ADMIN);

        if (isAdmin) return true;

        // owner check: fetch job and compare postedBy.id safely
        JobDto job = jobService.getJobById(jobId); // <-- implement/adjust to return Job entity
        if (job == null || job.getPostedBy() == null) return false;
        return Objects.equals(job.getPostedBy().getId(), currentUser.getId());
    }
}

