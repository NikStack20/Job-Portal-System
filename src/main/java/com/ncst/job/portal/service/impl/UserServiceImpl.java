package com.ncst.job.portal.service.impl;
import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.RoleName;
import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.globalExceptionHandler.ResourceNotFoundException;
import com.ncst.job.portal.loadouts.UserDto;
import com.ncst.job.portal.repository.RoleRepo;
import com.ncst.job.portal.repository.UserRepo;
import com.ncst.job.portal.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------- entity-returning helpers ----------------
    @Override
    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    // ---------------- DTO operations ----------------
    @Override
    public UserDto createUser(UserDto userDto) {
        // checking unique email or not
        userRepo.findByEmail(userDto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use: " + userDto.getEmail());
        });

        // mapping DTO -> entity
        User user = modelMapper.map(userDto, User.class);
        
        // set id here, if not present :>
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }

        // encoding password if present
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // map roleNames (strings) to Role entities (enum-aware) DONE
        if (userDto.getRoleNames() != null && !userDto.getRoleNames().isEmpty()) {
            Set<Role> roles = userDto.getRoleNames().stream()
                    .map(this::toRoleEntity)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            // default role (candidate :> ) if not provided
            Role defaultRole = roleRepo.findByName(RoleName.ROLE_CANDIDATE)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_CANDIDATE));
            user.getRoles().add(defaultRole);
        }

        User saved = userRepo.save(user);
        return mapToDto(saved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User existing = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // map allowed fields from DTO
        if (userDto.getName() != null) existing.setUsername(userDto.getName());
        if (userDto.getEmail() != null) existing.setEmail(userDto.getEmail());
        if (userDto.getContactNumber() != null) existing.setContactNumber(userDto.getContactNumber());

        // password update (if provided)
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // ignoring roleNames here (only admin endpoint should change roles)
        // userDto.getRoleNames() is intentionally not applied  <_>

        User updated = userRepo.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void deleteUser(String userId) {
        User existing = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        userRepo.delete(existing);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ---------------- Admin: change roles ----------------

    @Override
	public UserDto changeUserRoles(String userId, List<String> roleNames) {
		  User user = userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

	        Set<Role> newRoles = roleNames.stream()
	                .map(this::toRoleEntity)
	                .collect(Collectors.toSet());

	        user.setRoles(newRoles);
	        User saved = userRepo.save(user);
	        return mapToDto(saved);
	}
    
    // ---------------- helpers ----------------

    
     //Converting incoming role string to Role entity.
     // Accepts values like "ROLE_ADMIN", "admin", "ADMIN" (case-insensitive).
     
    private Role toRoleEntity(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new IllegalArgumentException("Empty role name");
        }
        String normalized = roleStr.trim().toUpperCase();
        // allow both "ADMIN" or "ROLE_ADMIN"
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        RoleName rn;
        try {
            rn = RoleName.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }

        return roleRepo.findByName(rn)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", rn));
    }

    
     // Mapping User entity to UserDto safely (by not exposing the password).
     
    private UserDto mapToDto(User user) {
        UserDto dto = modelMapper.map(user, UserDto.class);

        // map roles -> roleNames (strings)
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<String> roleNames = user.getRoles().stream()
                    .map(r -> r.getName().toString())
                    .collect(Collectors.toSet());
            dto.setRoleNames(roleNames);
        } else {
            dto.setRoleNames(Collections.emptySet());
        }

        //will never return password field
        dto.setPassword(null);

        // keeping applications/jobs as-is or you can map selectively if you want
        return dto;
    }

	
}


