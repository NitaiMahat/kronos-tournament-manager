package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


/**
 * Home screen view for the Tournament Manager application.
 *
 * <p>Provides navigation buttons to different sections of the application.
 * The visibility of administrative controls is managed based on the current user's role.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial styling and access control logic received assistance from an AI model (Gemini 2.5 Pro).</p>
 */
public class HomeView extends View {

    /** Button to navigate to the team management section. */
    private final Button manageTeamsButton;
    /** Button to navigate to the resource management section (Courts/Referees). */
    private final Button manageResourcesButton;
    /** Button to navigate to the bracket viewing section. */
    private final Button viewBracketsButton;
    /** Button to initiate the automatic bracket generation process. */
    private final Button generateBracketButton;
    /** Button to enter points for teams in this tournament. */
    private final Button pointsEntryButton;
    /** Button to navigate back to the Tournament Selection view. */
    private final Button selectionViewButton;
    /** Button for Admins to create new users. */
    private final Button manageUsersButton;
    /** Button to log out of the application. */
    private final Button logoutButton;

    /**
     * Constructs the HomeView, setting up the main title, subtitle, and all navigation buttons
     * with consistent styling and alignment.
     */
    public HomeView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(40);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("Augustana Volleyball", ACCENT_COLOR, 48, true);
        Label subtitleLabel = View.createStyledLabel("Tournament Manager", TEXT_COLOR, 24, false);

        VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);

        manageTeamsButton = new Button("Manage Teams");
        manageResourcesButton = new Button("Manage Resources (Courts/Referees)");
        viewBracketsButton = new Button("View Brackets");
        generateBracketButton = new Button("Generate Bracket");
        pointsEntryButton = new Button("Enter Points");
        selectionViewButton = new Button("Change Tournament/League");
        manageUsersButton = new Button("Manage Users");

        logoutButton = new Button("Logout");

        VBox buttonBox = new VBox(20, manageTeamsButton, manageResourcesButton, viewBracketsButton, generateBracketButton, pointsEntryButton, selectionViewButton, manageUsersButton, logoutButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(600);

        for (Button btn : new Button[]{manageTeamsButton, manageResourcesButton, viewBracketsButton, generateBracketButton, pointsEntryButton, selectionViewButton, manageUsersButton, logoutButton}) {
            boolean isWarning = (btn == logoutButton);

            String customBaseStyle = (isWarning ? View.BUTTON_WARNING_STYLE : View.BUTTON_STYLE) + "-fx-font-size: 16px;";
            String customHoverStyle = (isWarning ? View.BUTTON_WARNING_HOVER_STYLE : View.BUTTON_HOVER_STYLE) + "-fx-font-size: 16px;";

            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPadding(new Insets(15));

            btn.setStyle(customBaseStyle);

            btn.setOnMouseEntered(e -> btn.setStyle(customHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(customBaseStyle));
        }

        this.getChildren().addAll(titleBox, buttonBox);
    }

    /**
     * Updates the visibility and managed state of administrative controls based on the user's role.
     * Buttons for managing teams, resources, generating, and manually editing brackets are only visible
     * if the user role is {@code ADMIN} or {@code TOURNAMENT_ORGANIZER}.
     * The Manage Users button is strictly for {@code ADMIN}.
     *
     * @param role The role of the current user.
     */
    public void updateUserAccess(UserRole role) {
        boolean isAuthorized = (role == UserRole.ADMIN || role == UserRole.TOURNAMENT_ORGANIZER);
        boolean isAdmin = (role == UserRole.ADMIN);

        manageTeamsButton.setVisible(isAuthorized);
        manageTeamsButton.setManaged(isAuthorized);

        manageResourcesButton.setVisible(isAuthorized);
        manageResourcesButton.setManaged(isAuthorized);

        generateBracketButton.setVisible(isAuthorized);
        generateBracketButton.setManaged(isAuthorized);

        pointsEntryButton.setVisible(isAuthorized);
        pointsEntryButton.setManaged(isAuthorized);

        manageUsersButton.setVisible(isAdmin);
        manageUsersButton.setManaged(isAdmin);
    }

    public Button getManageTeamsButton() { return manageTeamsButton; }
    public Button getManageResourcesButton() { return manageResourcesButton; }
    public Button getViewBracketsButton() { return viewBracketsButton; }
    public Button getGenerateBracketButton() { return generateBracketButton; }
    public Button getLogoutButton() { return logoutButton; }
    public Button getPointsEntryButton() { return pointsEntryButton; }
    public Button getSelectionViewButton() { return selectionViewButton; }
    public Button getManageUsersButton() { return manageUsersButton; }

    @Override
    public void refreshView() {
    }
}