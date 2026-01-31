package edu.augustana.csc305.project.model.domain;

/**
 * The concrete base class representing a User in the tournament system.
 * This class directly mirrors the 'User' schema returned by the Kronos API,
 * including the username, userId, and UserRole. It is no longer abstract.
 */
public class User {

    private String username;
    private UserRole role;
    private int userId;

    private boolean isLoggedIn;

    /**
     * Default constructor for Retrofit deserialization.
     */
    public User() {
        this.isLoggedIn = false;
        this.role = UserRole.ANYONE;
    }

    /**
     * Constructor used for creating new User objects on the client side (e.g., Guest).
     * @param username The name for the user.
     * @param role The user's role.
     */
    public User(String username, UserRole role) {
        this.username = username;
        this.role = role;
        this.isLoggedIn = false;
    }

    /**
     * Sets the user's status to logged in. Called by the AuthenticationService on success.
     */
    public void login() {
        this.isLoggedIn = true;
        System.out.println(username + " (" + role + ") has logged in.");
    }

    /**
     * Sets the user's status to logged out. Called by the AuthenticationService on logout.
     */
    public void logout() {
        this.isLoggedIn = false;
        System.out.println(username + " (" + role + ") has logged out.");
    }

    /**
     * Gets the username of this user.
     *
     * @return The username as a {@code String}.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the role of the user, which is now a field populated by the API.
     *
     * @return The {@code UserRole} of this user.
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Gets the unique database identifier for this user.
     *
     * @return The user's ID as an {@code int}.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the unique database identifier for this user.
     *
     * @param userId The unique ID to set for the user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Checks if the user is currently considered logged in by the client.
     *
     * @return True if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return username;
    }
}