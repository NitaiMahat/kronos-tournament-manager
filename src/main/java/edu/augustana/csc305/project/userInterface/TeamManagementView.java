package edu.augustana.csc305.project.userInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A unified view for managing teams, combining the functionality of team entry and team listing.
 *
 * <p>Users can add new teams and view, or delete existing ones from a single screen.
 * This view provides the UI components for team registration, a list of existing teams,
 * and controls for submission, deletion, and navigation.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TeamManagementView extends View {

    /**
     * The text field for entering the name of a new team.
     */
    private final TextField teamNameField = new TextField();
    /** The button to submit the new team name for registration. */
    private final Button submitButton = new Button("Add Team");
    /** The label used to display feedback messages (e.g., success or error) to the user. */
    private final Label feedbackLabel = new Label();
    /** The list view component that displays all currently registered teams. */
    private final ListView<String> teamList = new ListView<>();
    /** The button to delete the team currently selected in the list view. */
    private final Button deleteButton = new Button("Delete Selected");
    /** The button to navigate back to the main or home view. */
    private final Button backButton = new Button("Back to Home");

    /**
     * Constructs the TeamManagementView.
     * Initializes the view's layout, styling, and all UI components, arranging them
     * into a section for adding teams and a section for viewing/deleting existing teams.
     */
    public TeamManagementView() {
        this.setPadding(new Insets(25));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");
        this.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = View.createStyledLabel("Manage Teams", 24, true);

        VBox addTeamBox = createAddTeamSection();
        VBox existingTeamsBox = createExistingTeamsSection();

        feedbackLabel.setStyle("-fx-font-weight: bold;");

        this.getChildren().addAll(
                titleLabel,
                addTeamBox,
                new Separator(),
                existingTeamsBox,
                backButton
        );

        View.styleButton(submitButton);
        View.styleButton(deleteButton, true);
        View.styleButton(backButton);

        View.styleTextField(teamNameField);
        View.styleListView(teamList);

        teamList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item);
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });
    }

    /**
     * Creates the UI section for adding a new team.
     *
     * @return A {@link VBox} containing the input field, button, and feedback label.
     * <p>The use of {@code Priority.ALWAYS} and {@code MaxWidth} property for responsive layout design received assistance from an AI model
     * (Gemini 2.5 Pro).</p>
     */
    private VBox createAddTeamSection() {
        Label addTeamLabel = View.createStyledLabel("Register a New Team", ACCENT_COLOR, 16, true);

        teamNameField.setPromptText("Enter team name");
        teamNameField.setMaxWidth(Double.MAX_VALUE);

        HBox submissionBox = new HBox(10, teamNameField, submitButton);
        HBox.setHgrow(teamNameField, Priority.ALWAYS);
        submissionBox.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, addTeamLabel, submissionBox, feedbackLabel);
        container.setPadding(new Insets(0, 0, 20, 0));
        return container;
    }

    /**
     * Creates the UI section for displaying and managing existing teams.
     *
     * @return A {@link VBox} containing the list view and delete button.
     * <p>The use of {@code Priority.ALWAYS} and {@code MaxWidth} property for responsive layout design received assistance from an AI model
     * (Gemini 2.5 Pro).</p>
     */
    private VBox createExistingTeamsSection() {
        Label listLabel = View.createStyledLabel("Registered Teams", ACCENT_COLOR, 16, true);

        teamList.setPrefHeight(250);

        HBox deleteBox = new HBox(deleteButton);
        deleteBox.setAlignment(Pos.CENTER_RIGHT);
        deleteBox.setPadding(new Insets(10, 0, 0, 0));

        VBox container = new VBox(10, listLabel, teamList, deleteBox);
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    /**
     * Gets the text field used for entering the new team name.
     *
     * @return The {@code TextField} for the team name.
     */
    public TextField getTeamNameField() {
        return teamNameField;
    }

    /**
     * Gets the button for submitting a new team.
     *
     * @return The "Add Team" button.
     */
    public Button getSubmitButton() {
        return submitButton;
    }

    /**
     * Gets the label for displaying feedback messages.
     *
     * @return The feedback {@code Label}.
     */
    public Label getFeedbackLabel() {
        return feedbackLabel;
    }

    /**
     * Gets the list view containing the names of registered teams.
     *
     * @return The {@code ListView} of teams.
     */
    public ListView<String> getTeamList() {
        return teamList;
    }

    /**
     * Gets the button for deleting a selected team from the list.
     *
     * @return The "Delete Selected" button.
     */
    public Button getDeleteButton() {
        return deleteButton;
    }

    /**
     * Gets the button for navigating back to the home screen.
     *
     * @return The "Back to Home" button.
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Clears the input field for the new team name and resets the feedback label.
     */
    @Override
    public void refreshView() {
        teamNameField.clear();
        feedbackLabel.setText("");
        feedbackLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");
    }
}