package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for sending login credentials to the API.
 * Maps to the LoginRequestDTO schema in the Kronos API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LoginRequestDTO {

    private final String username;
    private final String password;

    /**
     * Constructor used by the client to build the request payload.
     *
     * @param username The username credential.
     * @param password The password credential.
     */
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}