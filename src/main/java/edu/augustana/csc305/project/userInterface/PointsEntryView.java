package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.Bracket;
import edu.augustana.csc305.project.model.domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Provides the user interface for entering or editing points for teams in a
 * specific tournament.
 *
 * <p>This view displays a list of teams, a text field for each team's points,
 * and controls for saving, generating points, or navigating back. It also includes
 * a drop-down menu to select a bracket within the tournament for point generation.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial Javadoc structure and component implementation were assisted by an AI model (Gemini 2.5 Pro).</p>
 */
public class PointsEntryView extends View {

    /**
     * The grid layout that displays team names and their corresponding
     * points-entry text fields.
     */
    private final GridPane teamsList;

    /**
     * Button to trigger logic for automatically generating points based on a selected bracket.
     */
    private final Button generatePointsButton;

    /**
     * Button to save the points entered in the text fields to the database.
     */
    private final Button saveButton;

    /**
     * Button to navigate back to the previous view (typically the Home view).
     */
    private final Button backButton;

    /**
     * Dropdown menu for selecting a specific bracket within the tournament.
     */
    private final ComboBox<Bracket> bracketComboBox;

    /** Label for displaying error or status messages to the user. */
    private final Label messageLabel = new Label();

    /**
     * Constructs and initializes the PointsEntryView.
     * It sets up the layout, styling, and all UI components.
     */
    public PointsEntryView() {
        this.setPadding(new Insets(25));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");
        VBox.setVgrow(this, Priority.ALWAYS);

        Label titleLabel = View.createStyledLabel("Points Management", ACCENT_COLOR, 24, true);

        bracketComboBox = new ComboBox<>();
        bracketComboBox.setPromptText("Select Bracket");
        View.styleComboBox(bracketComboBox);

        bracketComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Bracket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-text-fill: " + LINE_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getBracketName());
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                }
            }
        });

        bracketComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Bracket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getBracketName());
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });

        VBox bracketSelectionBox = new VBox(5,
                View.createStyledLabel("Select Bracket for Generation:", 14, true),
                bracketComboBox);
        bracketSelectionBox.setPadding(new Insets(0, 0, 10, 0));


        Label teamsLabel = View.createStyledLabel("Team Name", 14, true);
        Label pointsLabel = View.createStyledLabel("Points", 14, true);

        teamsList = new GridPane();
        teamsList.setHgap(30);
        teamsList.setVgap(10);
        teamsList.setPadding(new Insets(10));
        teamsList.setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
        teamsList.setAlignment(Pos.TOP_CENTER); // Changed to TOP_CENTER so it fills from top down
        teamsList.addColumn(0, teamsLabel);
        teamsList.addColumn(1, pointsLabel);

        ScrollPane scrollPane = new ScrollPane(teamsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background: " + PANE_BG_COLOR + ";" +
                "-fx-background-color: transparent;" +
                "-fx-control-inner-background: " + PANE_BG_COLOR + ";");

        VBox teamListContainer = new VBox(scrollPane);
        VBox.setVgrow(teamListContainer, Priority.ALWAYS);
        teamListContainer.setPrefHeight(200);
        teamListContainer.setStyle("-fx-background-color: " + PANE_BG_COLOR + "; -fx-background-radius: 8; -fx-padding: 5;");

        generatePointsButton = new Button("Generate Points");
        saveButton = new Button("Save Points");
        backButton = new Button("Back to Tournament");

        View.styleButton(generatePointsButton);
        View.styleButton(saveButton);
        View.styleButton(backButton);

        HBox buttons = new HBox(20, generatePointsButton, saveButton, backButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        messageLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");


        VBox mainContent = new VBox(20, bracketSelectionBox, teamListContainer, messageLabel, buttons);

        mainContent.setAlignment(Pos.CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        mainContent.setMaxWidth(500);

        this.getChildren().addAll(titleLabel, mainContent);
    }

    /**
     * Adds a team row to the grid.
     * This method is called by the controller to populate the view.
     *
     * @param team The team to add.
     * @param points The team's current points (or 0 if new).
     * @return The {@link TextField} created for this team's points, allowing the
     * controller to store a reference.
     */
    public TextField addTeam(Team team, Integer points) {
        TextField enterPoints = new TextField();
        if (points == null) {
            enterPoints.setText("0");
        } else {
            enterPoints.setText(String.valueOf(points));
        }
        enterPoints.setPromptText("Enter Points");

        View.styleTextField(enterPoints);
        enterPoints.setMaxWidth(100);

        Label teamNameLabel = View.createStyledLabel(team.getTeamName(), 12, false);
        teamsList.addRow(teamsList.getRowCount(), teamNameLabel, enterPoints);

        return enterPoints;
    }

    /**
     * Gets the ComboBox used for selecting a bracket.
     *
     * @return The {@link ComboBox} of {@link Bracket}s.
     */
    public ComboBox<Bracket> getBracketComboBox() {
        return bracketComboBox;
    }

    /**
     * Gets the label used for displaying error or status messages.
     *
     * @return The message {@code Label}.
     */
    public Label getMessageLabel() {
        return messageLabel;
    }


    /**
     * Resets the view by clearing input fields and messages.
     */
    @Override
    public void refreshView() {
        messageLabel.setText("");
    }

    /**
     * Gets the main grid for the team and points display.
     *
     * @return The {@link GridPane} containing the team list.
     */
    public GridPane getTeamsList() {
        return teamsList;
    }

    /**
     * Gets the 'Generate' button.
     *
     * @return The 'Generate' {@link Button}.
     */
    public Button getGeneratePointsButton() {
        return generatePointsButton;
    }

    /**
     * Gets the 'Save' button.
     *
     * @return The 'Save' {@link Button}.
     */
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * Gets the 'Back' button.
     *
     * @return The 'Back' {@link Button}.
     */
    public Button getBackButton() {
        return backButton;
    }
}