package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.domain.User;
import edu.augustana.csc305.project.userInterface.LoginView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.concurrent.Task;

/**
 * Controller for the {@link LoginView}.
 *
 * <p>Handles user interaction for login by coordinating with the asynchronous
 * {@link AuthenticationService} via JavaFX {@link Task}s to ensure the UI remains responsive.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LoginViewController extends ViewController {

    private final LoginView loginView;
    private final AuthenticationService authService;

    /**
     * Constructs a {@code LoginViewController}.
     *
     * @param view The {@link LoginView} instance this controller manages.
     * @param appController The main {@link AppController} for delegation of navigation.
     * @param authService The application's authentication service.
     */
    public LoginViewController(LoginView view, AppController appController, AuthenticationService authService) {
        super(view, null, appController);
        this.loginView = view;
        this.authService = authService;
        attachEvents();
    }

    /**
     * Attaches event handlers to the Login button.
     */
    @Override
    protected void attachEvents() {
        loginView.getLoginButton().setOnAction(e -> handleLogin());
    }

    /**
     * Attempts to log in the user using the provided credentials.
     * The network request is executed on a background {@link Task}.
     */
    private void handleLogin() {
        String username = loginView.getLoginUsernameField().getText().trim();
        String password = loginView.getLoginPasswordField().getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            loginView.getMessageLabel().setText("Login failed: Username and password cannot be empty.");
            return;
        }

        setButtonsDisabled(true);
        loginView.getMessageLabel().setText("Attempting login...");

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return authService.login(username, password);
            }

            @Override
            protected void succeeded() {
                setButtonsDisabled(false);
                User result = getValue();
                if (result != null) {
                    loginView.getMessageLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                    loginView.getMessageLabel().setText("Login successful for " + result.getUsername() + "!");
                    appController.showSelectionView();
                } else {
                    loginView.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                    loginView.getMessageLabel().setText("Login failed: Invalid username/password, or network error.");
                }
            }

            @Override
            protected void failed() {
                setButtonsDisabled(false);
                loginView.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                loginView.getMessageLabel().setText("Login failed due to an unexpected error: " + getException().getMessage());
            }
        };

        new Thread(loginTask).start();
    }

    /**
     * Utility method to disable/enable all interactive buttons in the view while a task is running.
     *
     * @param disabled {@code true} to disable buttons, {@code false} to enable them.
     */
    private void setButtonsDisabled(boolean disabled) {
        loginView.getLoginButton().setDisable(disabled);
    }
}