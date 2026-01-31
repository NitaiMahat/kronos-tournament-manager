package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.User;
import edu.augustana.csc305.project.model.domain.UserRole;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A view restricted to Administrators for creating new users and managing existing user roles.
 * Allows creating users with roles: USER, REFEREE, or TOURNAMENT_ORGANIZER.
 * Displays a table of all users where roles can be updated.
 */
public class AdminUserManagementView extends View {

    private final TextField usernameField;
    private final PasswordField passwordField;
    private final ComboBox<UserRole> roleComboBox;
    private final Button createUserButton;
    private final Button backButton;
    private final Label messageLabel;

    private final TableView<User> userTable;
    private final TableColumn<User, String> usernameColumn;
    private final TableColumn<User, UserRole> roleColumn;
    private final TableColumn<User, Void> actionColumn;

    public AdminUserManagementView() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("Admin User Management", ACCENT_COLOR, 24, true);

        VBox createSection = View.createSectionBox("Create New User");
        createSection.setMaxWidth(600);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        View.styleTextField(usernameField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        View.stylePasswordField(passwordField);

        roleComboBox = new ComboBox<>();
        roleComboBox.setItems(FXCollections.observableArrayList(
                UserRole.USER,
                UserRole.REFEREE,
                UserRole.TOURNAMENT_ORGANIZER,
                UserRole.ADMIN
        ));
        roleComboBox.getSelectionModel().select(UserRole.USER);
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        View.styleComboBox(roleComboBox);

        roleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserRole item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: transparent;");
                }
            }
        });

        roleComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(UserRole item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                }
            }
        });

        grid.add(View.createStyledLabel("Username:", 14, true), 0, 0);
        grid.add(usernameField, 1, 0);

        grid.add(View.createStyledLabel("Password:", 14, true), 0, 1);
        grid.add(passwordField, 1, 1);

        grid.add(View.createStyledLabel("Role:", 14, true), 0, 2);
        grid.add(roleComboBox, 1, 2);

        createUserButton = new Button("Create User");
        View.styleButton(createUserButton);
        createUserButton.setMaxWidth(Double.MAX_VALUE);

        VBox formBox = new VBox(15, grid, createUserButton);
        formBox.setAlignment(Pos.CENTER);
        createSection.getChildren().add(formBox);

        VBox tableSection = View.createSectionBox("Manage Existing Users");
        tableSection.setMaxWidth(600);
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        userTable = new TableView<>();
        View.styleTableView(userTable);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: black;");

        roleColumn = new TableColumn<>("Current Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setStyle("-fx-alignment: CENTER; -fx-text-fill: black;");

        actionColumn = new TableColumn<>("Update Role");
        actionColumn.setStyle("-fx-alignment: CENTER;");

        userTable.getColumns().addAll(usernameColumn, roleColumn, actionColumn);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        tableSection.getChildren().add(userTable);

        backButton = new Button("Back to Home");
        View.styleButton(backButton);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);

        VBox mainLayout = new VBox(20, titleLabel, createSection, tableSection, messageLabel, backButton);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(650);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        this.getChildren().add(mainLayout);
    }

    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public ComboBox<UserRole> getRoleComboBox() { return roleComboBox; }
    public Button getCreateUserButton() { return createUserButton; }
    public Button getBackButton() { return backButton; }
    public Label getMessageLabel() { return messageLabel; }

    public TableView<User> getUserTable() { return userTable; }
    public TableColumn<User, Void> getActionColumn() { return actionColumn; }

    @Override
    public void refreshView() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().select(UserRole.USER);
        messageLabel.setText("");
        userTable.getItems().clear();
    }
}