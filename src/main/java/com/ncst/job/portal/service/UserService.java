package com.ncst.job.portal.service;

import java.security.Principal; 
import java.util.List;

import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.loadouts.UserDto;

public interface UserService {

    // Return entity (used by internal logic / ownership checks)
    User getUserById(String id);

    User getUserByEmail(String email);

    // Create / update using DTOs for API usage
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, String userId);

    void deleteUser(String userId);

    List<UserDto> getAllUsers();

    // helper to resolve principal -> user
    default User getUserFromPrincipal(Principal principal) {
        if (principal == null) return null;
        return getUserByEmail(principal.getName());
    }

	UserDto changeUserRoles(String userId, List<String> roleNames);
}

