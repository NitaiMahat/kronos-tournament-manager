package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.UserUpdateDTO;
import edu.augustana.csc305.project.model.domain.User;
import edu.augustana.csc305.project.model.domain.UserRole;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.AdminUserManagementView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Controller for the AdminUserManagementView.
 * Handles creating new users and updating roles for existing users via a table.
 */
public class AdminUserManagementViewController extends ViewController {

    private final AdminUserManagementView view;
    private final AuthenticationService authService;
    private final KronosApi api;

    public AdminUserManagementViewController(AdminUserManagementView view, AppController appController) {
        super(view, null, appController);
        this.view = view;
        this.authService = appController.getAuthService();
        this.api = ApiClient.getInstance().getKronosApi();

        initializeTable();
        attachEvents();
        loadUsers();
    }

    private void initializeTable() {
        view.getActionColumn().setCellFactory(param -> new TableCell<>() {
            private final ComboBox<UserRole> roleCombo = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));
            private final Button updateButton = new Button("Update");
            private final HBox pane = new HBox(5, roleCombo, updateButton);

            {
                View.styleComboBox(roleCombo);
                roleCombo.setStyle(roleCombo.getStyle() + "-fx-font-size: 10px; -fx-pref-width: 100px;");

                roleCombo.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(UserRole item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.name());
                            setStyle("-fx-text-fill: " + View.TEXT_COLOR + "; -fx-background-color: transparent;");
                        }
                    }
                });

                roleCombo.setCellFactory(listView -> new ListCell<>() {
                    @Override
                    protected void updateItem(UserRole item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.name());
                            setStyle("-fx-text-fill: " + View.TEXT_COLOR + "; -fx-background-color: " + View.PANE_BG_COLOR + ";");
                        }
                    }
                });

                final String CUSTOM_STYLES = "-fx-font-size: 10px; -fx-padding: 3 8;";
                final String BASE_STYLE = View.BUTTON_STYLE + CUSTOM_STYLES;
                final String HOVER_STYLE = View.BUTTON_HOVER_STYLE + CUSTOM_STYLES;

                updateButton.setStyle(BASE_STYLE);
                updateButton.setOnMouseEntered(e -> updateButton.setStyle(HOVER_STYLE));
                updateButton.setOnMouseExited(e -> updateButton.setStyle(BASE_STYLE));

                updateButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    UserRole newRole = roleCombo.getValue();
                    if (newRole != null && newRole != user.getRole()) {
                        updateUserRole(user, newRole);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    roleCombo.setValue(user.getRole());
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadUsers() {
        api.getAllUsers(null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Platform.runLater(() -> view.getUserTable().setItems(FXCollections.observableArrayList(response.body())));
                } else {
                    Platform.runLater(() -> view.getMessageLabel().setText("Failed to load users."));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Platform.runLater(() -> view.getMessageLabel().setText("Network error loading users."));
            }
        });
    }

    private void updateUserRole(User user, UserRole newRole) {
        UserUpdateDTO updateDTO = new UserUpdateDTO(null, null, newRole);

        view.getMessageLabel().setText("Updating role...");

        api.updateUser(user.getUserId(), updateDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        view.getMessageLabel().setText("Updated " + user.getUsername() + " to " + newRole);
                        view.getMessageLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                        loadUsers();
                    } else {
                        view.getMessageLabel().setText("Failed to update role: " + response.code());
                        view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                    }
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Platform.runLater(() -> view.getMessageLabel().setText("Network error: " + t.getMessage()));
            }
        });
    }

    @Override
    protected void attachEvents() {
        view.getCreateUserButton().setOnAction(e -> createUser());
        view.getBackButton().setOnAction(e -> appController.showHomeView());
    }

    private void createUser() {
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();
        UserRole role = view.getRoleComboBox().getValue();

        if (username.isEmpty() || password.isEmpty()) {
            view.getMessageLabel().setText("Error: Username and Password cannot be empty.");
            view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            return;
        }

        view.getCreateUserButton().setDisable(true);
        view.getMessageLabel().setText("Creating user...");
        view.getMessageLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");

        Task<User> creationTask = new Task<>() {
            @Override
            protected User call() {
                return authService.createAccount(username, password, role);
            }

            @Override
            protected void succeeded() {
                view.getCreateUserButton().setDisable(false);
                User newUser = getValue();
                if (newUser != null) {
                    view.getMessageLabel().setText("Success! Created user '" + newUser.getUsername() + "'.");
                    view.getMessageLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                    view.getUsernameField().clear();
                    view.getPasswordField().clear();
                    loadUsers();
                } else {
                    view.getMessageLabel().setText("Error: Failed to create user. Username may be taken.");
                    view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                }
            }

            @Override
            protected void failed() {
                view.getCreateUserButton().setDisable(false);
                view.getMessageLabel().setText("Error: Network or server issue.");
                view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            }
        };

        new Thread(creationTask).start();
    }
}