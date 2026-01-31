package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.UserRole;

/**
 * Data Transfer Object (DTO) used for updating an existing user's details.
 * <p>
 * This class is designed for PATCH operations where fields can be optional.
 * A null value indicates that the field should not be updated.
 * </p>
 */
public class UserUpdateDTO {
    private String username;
    private String password;
    private UserRole role;

    /**
     * Default constructor for serialization/deserialization.
     */
    public UserUpdateDTO() {
    }

    /**
     * Constructs a new UserUpdateDTO with the specified fields.
     *
     * @param username The new username (or null to keep existing).
     * @param password The new password (or null to keep existing).
     * @param role     The new role (or null to keep existing).
     */
    public UserUpdateDTO(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}