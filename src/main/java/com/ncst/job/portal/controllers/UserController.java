package com.ncst.job.portal.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.loadouts.UserDto;
import com.ncst.job.portal.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register User (public)
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        UserDto created = userService.createUser(userDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    // Update user (Admin or  same user)
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<UserDto> updateUser(@PathVariable String userId,
                                              @Valid @RequestBody UserDto userDto,
                                              Principal principal) {
        User current = userService.getUserFromPrincipal(principal);
        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin && !current.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to update this user");
        }

        // Normal users cannot change roles here
        userDto.setRoleNames(null);

        UserDto updated = userService.updateUser(userDto, userId);
        return ResponseEntity.ok(updated);
    }
    
    // Get current logged-in user (any logged user)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        User current = userService.getUserFromPrincipal(principal);
        if (current == null) throw new AccessDeniedException("User not authenticated");

        UserDto dto = new UserDto();
        dto.setUserId(current.getId());
        dto.setName(current.getUsername());
        dto.setEmail(current.getEmail());
        dto.setContactNumber(current.getContactNumber());
        dto.setPassword(null);

        if (current.getRoles() != null) {
            Set<String> roleNames = current.getRoles().stream()
                    .map(r -> r.getName().toString())
                    .collect(Collectors.toSet());
            dto.setRoleNames(roleNames);
        }

        return ResponseEntity.ok(dto);
    }



    // Get user by ID (Admin or same user)
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId, Principal principal) {
        User current = userService.getUserFromPrincipal(principal);
        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin && !current.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to view this user");
        }

        User target = userService.getUserById(userId);

        UserDto dto = new UserDto();
        dto.setUserId(target.getId());
        dto.setName(target.getUsername());
        dto.setEmail(target.getEmail());
        dto.setContactNumber(target.getContactNumber());
        dto.setPassword(null);
        if (target.getRoles() != null) {
            Set<String> roleNames = target.getRoles().stream()
                    .map(r -> r.getName().toString())
                    .collect(Collectors.toSet());
            dto.setRoleNames(roleNames);
        }

        return ResponseEntity.ok(dto);
    }
    
    //  Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }



    // Change roles (Admin only)
    @PatchMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> changeUserRoles(@PathVariable String userId,
                                                   @RequestBody List<String> roleNames) {
        UserDto updated = userService.changeUserRoles(userId, roleNames);
        return ResponseEntity.ok(updated);
    }

    //  Delete user (Admin or same user)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYER','ROLE_CANDIDATE')")
    public ResponseEntity<String> deleteUser(@PathVariable String userId, Principal principal) {
        User current = userService.getUserFromPrincipal(principal);
        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin && !current.getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to delete this user");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }
}


