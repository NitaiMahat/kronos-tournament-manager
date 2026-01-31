package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.League;
import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.model.domain.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A view for selecting, loading, or creating a new tournament, utilizing a master-detail
 * pattern where Leagues and Tournaments are presented side-by-side.
 *
 * <p>This view provides the GUI for selection, creation, and deletion, with certain
 * controls restricted based on the {@link UserRole}.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial Javadoc and styling received assistance from an AI model (Gemini 2.5 Pro).</p>
 */
public class SelectionView extends View {

    /**
     * The list view component displaying available leagues.
     */
    private final ListView<League> leagueListView = new ListView<>();
    /**
     * The button to create a new league.
     */
    private final Button createLeagueButton = new Button("Create New League");
    /**
     * The text field for entering the name of a new league.
     */
    private final TextField newLeagueField = new TextField();
    /**
     * The VBox container for all elements related to creating a new league.
     */
    private VBox createLeagueSection;

    /**
     * The list view component displaying available tournaments for the selected league.
     */
    private final ListView<Tournament> tournamentListView = new ListView<>();
    /**
     * The button to initiate the creation of a new tournament.
     */
    private final Button createTournamentButton = new Button("Create New Tournament");
    /**
     * The text field for entering the name of a new tournament.
     */
    private final TextField newTournamentField = new TextField();
    /**
     * The VBox container for all elements related to creating a new tournament.
     */
    private VBox createTournamentSection;

    /**
     * The button to select the currently highlighted tournament from the list.
     */
    private final Button selectButton = new Button("Select Tournament");

    /**
     * The button to delete the selected tournament (visible only to organizers and admins).
     */
    private final Button deleteTournamentButton = new Button("Delete Tournament");

    /**
     * The button to view standings for the selected league.
     */
    private final Button viewStandingsButton = new Button("View Standings");
    /**
     * The button to delete the selected league (visible only to organizers and admins).
     */
    private final Button deleteLeagueButton = new Button("Delete League");
    /**
     * The button to log out of the application.
     */
    private final Button logoutButton = new Button("Logout");
    /**
     * The label used to display feedback messages (e.g., error messages) to the user.
     */
    private final Label feedbackLabel = new Label();

    /**
     * Constructs the Tournament Selection View and initializes its UI components and layout.
     */
    public SelectionView() {
        configureView();

        Label titleLabel = View.createStyledLabel("Select or Create League/Tournament", ACCENT_COLOR, 24, true);

        VBox tournamentBox = createTournamentPane();
        VBox leagueBox = createLeaguePane();

        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(tournamentBox, leagueBox);

        HBox bottomControls = createBottomControls();

        this.getChildren().addAll(titleLabel, mainContent, feedbackLabel, bottomControls);

        applyInitialStylingAndState();
    }

    /**
     * Configures the root pane's (this) basic layout and styling.
     */
    private void configureView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");
    }

    /**
     * Creates the left-side pane for tournament selection and creation.
     *
     * @return A {@link VBox} containing all tournament-related controls.
     */
    private VBox createTournamentPane() {
        newTournamentField.setPromptText("Enter New Tournament Name");
        HBox tournamentCreateControls = new HBox(10, newTournamentField, createTournamentButton);
        tournamentCreateControls.setAlignment(Pos.CENTER);

        createTournamentSection = View.createSectionBox("Create Tournament");
        createTournamentSection.getChildren().add(tournamentCreateControls);

        HBox actionButtons = new HBox(10, selectButton, deleteTournamentButton);
        actionButtons.setAlignment(Pos.CENTER);

        VBox tournamentBox = View.createSectionBox("Tournaments");
        tournamentBox.getChildren().addAll(tournamentListView, actionButtons, createTournamentSection);
        tournamentBox.setAlignment(Pos.TOP_CENTER);

        tournamentListView.setMaxWidth(300);
        tournamentListView.setMaxHeight(300);

        return tournamentBox;
    }

    /**
     * Creates the right-side pane for league selection and creation.
     *
     * @return A {@link VBox} containing all league-related controls.
     */
    private VBox createLeaguePane() {
        newLeagueField.setPromptText("Enter New League Name");
        HBox leagueCreateControls = new HBox(10, newLeagueField, createLeagueButton);
        leagueCreateControls.setAlignment(Pos.CENTER);

        createLeagueSection = View.createSectionBox("Create League");
        createLeagueSection.getChildren().add(leagueCreateControls);

        HBox actionButtons = new HBox(10, viewStandingsButton, deleteLeagueButton);
        actionButtons.setAlignment(Pos.CENTER);

        VBox leagueBoxContent = View.createSectionBox("Leagues");
        leagueBoxContent.getChildren().addAll(leagueListView, actionButtons, createLeagueSection);
        leagueBoxContent.setAlignment(Pos.TOP_CENTER);

        leagueListView.setMaxWidth(300);
        leagueListView.setMaxHeight(300);

        return leagueBoxContent;
    }

    /**
     * Creates the bottom control bar containing the logout button.
     *
     * @return An {@link HBox} with the logout button.
     */
    private HBox createBottomControls() {
        HBox bottomControls = new HBox(logoutButton);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);
        bottomControls.setPadding(new Insets(20, 0, 0, 0));
        return bottomControls;
    }

    /**
     * Applies final styling to buttons and sets the initial disabled
     * state for controls.
     */
    private void applyInitialStylingAndState() {
        feedbackLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_HOVER + ";");

        View.styleButton(selectButton);
        View.styleButton(deleteTournamentButton, true);
        View.styleButton(viewStandingsButton);
        View.styleButton(deleteLeagueButton, true);
        View.styleButton(createTournamentButton);
        View.styleButton(createLeagueButton);
        View.styleButton(logoutButton, true);

        View.styleTextField(newTournamentField);
        View.styleTextField(newLeagueField);

        View.styleListView(tournamentListView);
        View.styleListView(leagueListView);

        tournamentListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tournament item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getTournamentName());
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });

        leagueListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(League item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getLeagueName());
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });

        selectButton.setDisable(true);
        deleteTournamentButton.setDisable(true);
        viewStandingsButton.setDisable(true);
        deleteLeagueButton.setDisable(true);
        newTournamentField.setDisable(true);
        createTournamentButton.setDisable(true);
    }

    /**
     * Updates the visibility and management of the "Create" sections and delete button based on the user's role.
     * The sections and delete button are visible and managed only if the user role is {@code ADMIN} or
     * {@code TOURNAMENT_ORGANIZER}.
     *
     * @param role The role of the current user.
     */
    public void updateCreateVisibility(UserRole role) {
        boolean isAuthorized = (role == UserRole.ADMIN || role == UserRole.TOURNAMENT_ORGANIZER);

        createTournamentSection.setVisible(isAuthorized);
        createTournamentSection.setManaged(isAuthorized);

        deleteTournamentButton.setVisible(isAuthorized);
        deleteTournamentButton.setManaged(isAuthorized);

        createLeagueSection.setVisible(isAuthorized);
        createLeagueSection.setManaged(isAuthorized);

        deleteLeagueButton.setVisible(isAuthorized);
        deleteLeagueButton.setManaged(isAuthorized);
    }

    /**
     * Gets the {@code ListView} that displays the available leagues (Master list).
     *
     * @return The league list view.
     */
    public ListView<League> getLeagueListView() {
        return leagueListView;
    }

    /**
     * Gets the {@code TextField} used for entering the new league name.
     *
     * @return The new league text field.
     */
    public TextField getNewLeagueField() {
        return newLeagueField;
    }

    /**
     * Gets the "Create New League" button.
     *
     * @return The create league button.
     */
    public Button getCreateLeagueButton() {
        return createLeagueButton;
    }

    /**
     * Gets the {@code ListView} that displays the available tournaments for the selected league (Detail list).
     *
     * @return The tournament list view.
     */
    public ListView<Tournament> getTournamentListView() {
        return tournamentListView;
    }

    /**
     * Gets the "Select Tournament" button.
     *
     * @return The select button.
     */
    public Button getSelectButton() {
        return selectButton;
    }

    /**
     * Gets the "Delete Tournament" button.
     *
     * @return The delete tournament button.
     */
    public Button getDeleteTournamentButton() {
        return deleteTournamentButton;
    }

    /**
     * Gets the "View Standings" button.
     *
     * @return The view standings button.
     */
    public Button getViewStandingsButton() {
        return viewStandingsButton;
    }

    /**
     * Gets the "Delete League" button.
     *
     * @return The delete league button.
     */
    public Button getDeleteLeagueButton() {
        return deleteLeagueButton;
    }

    /**
     * Gets the {@code TextField} used for entering the new tournament name.
     *
     * @return The new tournament text field.
     */
    public TextField getNewTournamentField() {
        return newTournamentField;
    }

    /**
     * Gets the "Create New Tournament" button.
     *
     * @return The create tournament button.
     */
    public Button getCreateTournamentButton() {
        return createTournamentButton;
    }

    /**
     * Gets the "Logout" button.
     *
     * @return The logout button.
     */
    public Button getLogoutButton() {
        return logoutButton;
    }

    /**
     * Gets the {@code Label} used to provide feedback (e.g., error/success messages) to the user.
     *
     * @return The feedback label.
     */
    public Label getFeedbackLabel() {
        return feedbackLabel;
    }

    /**
     * Clears any existing feedback messages and the new tournament/league name input fields.
     */
    @Override
    public void refreshView() {
        feedbackLabel.setText("");
        newTournamentField.clear();
        newLeagueField.clear();
    }
}