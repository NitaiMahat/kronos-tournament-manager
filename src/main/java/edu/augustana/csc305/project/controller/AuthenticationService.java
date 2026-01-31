package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.LoginRequestDTO;
import edu.augustana.csc305.project.model.api.LoginResponseDTO;
import edu.augustana.csc305.project.model.api.UserNewDTO;
import edu.augustana.csc305.project.model.domain.*;
import edu.augustana.csc305.project.service.ApiClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import retrofit2.Response;

/**
 * Manages the current user session for the application using the Kronos API.
 *
 * <p>It handles authentication (login, account creation, and logout) by interacting
 * with the {@link ApiClient}. It manages the JWT token received upon successful
 * login and maintains the state of the currently logged-in {@link User}.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class AuthenticationService {

    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();
    private final ApiClient apiClient;

    /**
     * Constructs the {@code AuthenticationService}.
     * Initializes the {@link ApiClient} and defaults the session state to a guest user.
     */
    public AuthenticationService() {
        this.apiClient = ApiClient.getInstance();
        logout();
    }

    /**
     * Attempts to log in a user by verifying credentials against the Kronos API.
     *
     * <p>Note: This method performs a blocking network call and must be executed
     * on a background thread by the caller.</p>
     *
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return The logged-in {@link User} object on success, or {@code null} on failure (e.g., bad credentials or network error).
     */
    public User login(String username, String password) {
        try {
            LoginRequestDTO request = new LoginRequestDTO(username, password);
            Response<LoginResponseDTO> response = apiClient.getKronosApi().login(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                LoginResponseDTO body = response.body();

                apiClient.setToken(body.getToken());

                User loggedInUser = new User();
                loggedInUser.setUsername(username);
                loggedInUser.setUserId(body.getUserId());
                loggedInUser.setRole(body.getRole());
                loggedInUser.login();

                currentUser.set(loggedInUser);
                System.out.println("Login successful for: " + loggedInUser.getUsername());
                return loggedInUser;
            } else {
                System.err.println("Login failed with API error: " + response.code() + " " + response.message());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Network/Serialization error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new user account via the Kronos API.
     *
     * <p>Note: This method performs a blocking network call and must be executed
     * on a background thread by the caller.</p>
     *
     * @param username The unique username for the new account.
     * @param password The password for the new account.
     * @param role The {@link UserRole} of the new user.
     * @return The newly created {@link User} object with its assigned ID, or {@code null} on failure.
     */
    public User createAccount(String username, String password, UserRole role) {
        try {
            UserNewDTO request = new UserNewDTO(username, password, role);
            Response<User> response = apiClient.getKronosApi().createUser(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                User newUser = response.body();
                newUser.login();
                System.out.println("Account created successfully for: " + newUser.getUsername());
                return newUser;
            } else {
                System.err.println("Account creation failed with API error: " + response.code() + " " + response.message());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Network/Serialization error during account creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Logs out the current user by clearing the JWT token in the API client
     * and setting the {@code currentUser} to a default guest user with {@link UserRole#ANYONE}.
     */
    public void logout() {
        apiClient.clearToken();
        System.out.println("Logging out. Reverting to GUEST access.");
        currentUser.set(new User("Guest", UserRole.ANYONE));
    }

    /**
     * Gets the currently logged-in user object.
     *
     * @return The current {@link User} object, which is guaranteed to be non-null (at least a guest user).
     */
    public User getCurrentUser() {
        return currentUser.get();
    }

    /**
     * Provides the observable property representing the current user,
     * allowing UI elements to react to authentication state changes.
     *
     * @return The {@link ObjectProperty} wrapping the current {@link User}.
     */
    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }
}