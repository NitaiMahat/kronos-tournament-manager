package edu.augustana.csc305.project.model.api;

import edu.augustana.csc305.project.model.domain.UserRole;

/**
 * Data Transfer Object for receiving authentication details from the API upon successful login.
 * Contains the JWT token, user ID, and user role.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LoginResponseDTO {
    private String token;
    private int userId;
    private UserRole role;

    public LoginResponseDTO() {}

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
}