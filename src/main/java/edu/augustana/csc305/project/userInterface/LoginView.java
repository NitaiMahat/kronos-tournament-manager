package edu.augustana.csc305.project.userInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A JavaFX view for handling user authentication.
 *
 * <p>This view allows existing users to log in using their username and password.
 * The layout has been optimized for clarity and responsiveness.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LoginView extends View {

    /**
     * Field for entering the existing user's username.
     */
    private final TextField loginUsernameField = new TextField();
    /**
     * Field for entering the existing user's password.
     */
    private final PasswordField loginPasswordField = new PasswordField();
    /**
     * Button to attempt user login.
     */
    private final Button loginButton = new Button("Login");

    /**
     * Label for displaying error or status messages to the user.
     */
    private final Label messageLabel = new Label();

    /**
     * Constructs a {@code LoginView} with the login section.
     */
    public LoginView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("Login", ACCENT_COLOR, 32, true);
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        VBox loginBox = View.createSectionBox("Existing User");
        loginBox.setMaxWidth(Double.MAX_VALUE);

        GridPane loginGrid = createGrid();
        loginUsernameField.setPromptText("Username");
        loginPasswordField.setPromptText("Password");

        loginUsernameField.setPrefHeight(40);
        loginPasswordField.setPrefHeight(40);

        loginGrid.add(View.createStyledLabel("Username:", 14, false), 0, 0);
        loginGrid.add(loginUsernameField, 1, 0);
        loginGrid.add(View.createStyledLabel("Password:", 14, false), 0, 1);
        loginGrid.add(loginPasswordField, 1, 1);

        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(40);
        HBox.setHgrow(loginButton, Priority.ALWAYS);

        HBox loginButtons = new HBox(loginButton);
        loginButtons.setAlignment(Pos.CENTER);
        loginButtons.setPadding(new Insets(25, 0, 0, 0));

        loginBox.getChildren().addAll(loginGrid, loginButtons);

        messageLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_HOVER + "; -fx-font-weight: bold; -fx-font-size: 14px;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        this.getChildren().addAll(titleLabel, loginBox, messageLabel);

        View.styleButton(loginButton);
        View.styleTextField(loginUsernameField);
        View.stylePasswordField(loginPasswordField);
    }

    /**
     * Creates a grid layout used for form-style input sections.
     * Configured with column constraints to ensure input fields expand.
     *
     * @return a styled {@link GridPane} for use in form layouts.
     */
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(Control.USE_PREF_SIZE);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    /**
     * Gets the text field used for login usernames.
     *
     * @return the {@link TextField} for login usernames.
     */
    public TextField getLoginUsernameField() {
        return loginUsernameField;
    }

    /**
     * Gets the password field used for login passwords.
     *
     * @return the {@link PasswordField} for login passwords.
     */
    public PasswordField getLoginPasswordField() {
        return loginPasswordField;
    }

    /**
     * Gets the button that attempts to log in the user.
     *
     * @return the {@link Button} for user login.
     */
    public Button getLoginButton() {
        return loginButton;
    }

    /**
     * Gets the label used for displaying error or status messages.
     *
     * @return the {@link Label} for displaying messages.
     */
    public Label getMessageLabel() {
        return messageLabel;
    }

    /**
     * Resets all input fields and clears any feedback messages.
     */
    @Override
    public void refreshView() {
        loginUsernameField.clear();
        loginPasswordField.clear();
        messageLabel.setText("");
    }
}