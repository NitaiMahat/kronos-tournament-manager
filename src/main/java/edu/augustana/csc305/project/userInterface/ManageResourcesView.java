package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.Court;
import edu.augustana.csc305.project.model.domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A JavaFX view that allows users to manage tournament resources,
 * including {@link Court} and {@link User} objects (for referees).
 *
 * <p>This view is divided into two main sections: one for managing Courts and one for managing Referees.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class ManageResourcesView extends View {

    private final ListView<Court> courtsListView = new ListView<>();
    private final TextField newCourtField = new TextField();
    private final Button addCourtButton = new Button("Add Court");
    private final Button removeCourtButton = new Button("Remove Selected Court");

    private final ListView<User> refereesListView = new ListView<>();
    private final TextField newRefereeField = new TextField();
    private final Button addRefereeButton = new Button("Add Referee");
    private final Button removeRefereeButton = new Button("Remove Selected Referee");

    private final Button backButton = new Button("Back to Home");
    private final Label feedbackLabel = new Label();

    /**
     * Constructs the {@code ManageResourcesView}, initializes all UI components,
     * and sets up the layout and styling.
     */
    public ManageResourcesView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("Manage Tournament Resources", ACCENT_COLOR, 24, true);

        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(createCourtsSection(), createRefereesSection());

        feedbackLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_HOVER + ";");

        VBox layout = new VBox(20, titleLabel, mainContent, feedbackLabel, backButton);
        layout.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(layout);

        View.styleButton(addCourtButton, false);
        View.styleButton(removeCourtButton, true);
        View.styleButton(addRefereeButton, false);
        View.styleButton(removeRefereeButton, true);
        View.styleButton(backButton, false);

        View.styleTextField(newCourtField);
        View.styleTextField(newRefereeField);
        View.styleListView(courtsListView);
        View.styleListView(refereesListView);

        courtsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Court item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getCourtName());
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });

        refereesListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getUsername());
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
     * Creates the UI section for managing courts.
     *
     * @return A {@link VBox} containing the courts list and management buttons.
     */
    private VBox createCourtsSection() {
        newCourtField.setPromptText("New Court Name");
        HBox addCourtBox = new HBox(10, newCourtField, addCourtButton);
        addCourtBox.setAlignment(Pos.CENTER);

        VBox courtsSection = View.createSectionBox("Courts");
        courtsSection.getChildren().addAll(courtsListView, addCourtBox, removeCourtButton);
        return courtsSection;
    }

    /**
     * Creates the UI section for managing referees.
     *
     * @return A {@link VBox} containing the referees list and management buttons.
     */
    private VBox createRefereesSection() {
        newRefereeField.setPromptText("Existing Referee Username");

        VBox inputFields = new VBox(5, newRefereeField);
        inputFields.setAlignment(Pos.CENTER);
        inputFields.setMaxWidth(200);

        HBox addRefereeBox = new HBox(10, inputFields, addRefereeButton);
        addRefereeBox.setAlignment(Pos.CENTER);

        VBox refereesSection = View.createSectionBox("Referees");
        refereesSection.getChildren().addAll(refereesListView, addRefereeBox, removeRefereeButton);
        return refereesSection;
    }

    /**
     * Gets the {@link ListView} displaying courts.
     *
     * @return The courts list view.
     */
    public ListView<Court> getCourtsListView() { return courtsListView; }

    /**
     * Gets the {@link TextField} for entering a new court name.
     *
     * @return The new court name field.
     */
    public TextField getNewCourtField() { return newCourtField; }

    /**
     * Gets the button to add a court.
     *
     * @return The add court button.
     */
    public Button getAddCourtButton() { return addCourtButton; }

    /**
     * Gets the button to remove a selected court.
     *
     * @return The remove court button.
     */
    public Button getRemoveCourtButton() { return removeCourtButton; }

    /**
     * Gets the {@link ListView} displaying referees.
     *
     * @return The referees list view.
     */
    public ListView<User> getRefereesListView() { return refereesListView; }

    /**
     * Gets the {@link TextField} for entering an existing referee username.
     *
     * @return The new referee username field.
     */
    public TextField getNewRefereeField() { return newRefereeField; }

    /**
     * Gets the button to add a referee.
     *
     * @return The add referee button.
     */
    public Button getAddRefereeButton() { return addRefereeButton; }

    /**
     * Gets the button to remove a selected referee.
     *
     * @return The remove referee button.
     */
    public Button getRemoveRefereeButton() { return removeRefereeButton; }

    /**
     * Gets the button to navigate back to the home view.
     *
     * @return The back button.
     */
    public Button getBackButton() { return backButton; }

    /**
     * Gets the label used for displaying user feedback and errors.
     *
     * @return The feedback label.
     */
    public Label getFeedbackLabel() { return feedbackLabel; }

    /**
     * Clears input fields and feedback messages when the view is refreshed.
     */
    @Override
    public void refreshView() {
        feedbackLabel.setText("");
        newCourtField.clear();
        newRefereeField.clear();
    }
}