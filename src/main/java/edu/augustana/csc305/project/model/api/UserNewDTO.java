package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.UserRole;

/**
 * Data Transfer Object for creating a new user account via the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class UserNewDTO {

    private final String username;
    private final String password;
    private final UserRole role;

    /**
     * Constructs a DTO for creating a new user.
     *
     * @param username The desired unique username.
     * @param password The password for the new account.
     * @param role The {@link UserRole} of the new user.
     */
    public UserNewDTO(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}