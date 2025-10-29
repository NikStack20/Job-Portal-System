package com.ncst.job.portal.controllers;
import java.security.Principal; 
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ncst.job.portal.service.ApplicationService;
import com.ncst.job.portal.service.FileStorageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService storageService;
    private final ApplicationService applicationService; // to update resumeLink in application

    // Endpoint used by frontend to upload resume while applying
    @PostMapping("/upload-resume")
    public ResponseEntity<Map<String,String>> uploadResume(@RequestParam("file") MultipartFile file,
                                                         Principal principal) {
        // get userId from principal (use userService)
        String ownerId = principal == null ? "anon" : principal.getName(); // ideally get user id
        String storedPath = storageService.store(file, ownerId);
        return ResponseEntity.ok(Map.of("path", storedPath));
    }

    // Secure download of stored file (local disk) - check access
    @GetMapping("/resumes/{filename:.+}")
    public ResponseEntity<Resource> serveResume(@PathVariable String filename, Principal principal) {
        Resource res = storageService.loadAsResource(filename);
        // TODO: check that principal is allowed to access (owner/employer/admin)
        String contentDisposition = "attachment; filename=\"" + res.getFilename() + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(res);
    }
}
