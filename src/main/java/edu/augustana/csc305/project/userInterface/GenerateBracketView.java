package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.Bracket;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * A view for generating new tournament brackets with various configuration options,
 * such as name, format, and seeding based on a previous bracket.
 *
 * <p>This view provides the user interface for all necessary bracket creation inputs.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial styling and documentation for JavaFX components were assisted by an AI model (Gemini 2.5 Pro).</p>
 */
public class GenerateBracketView extends View {

    /**
     * The text field for entering the name of the new bracket.
     */
    private final TextField bracketNameField = new TextField();
    /** The combo box for selecting the tournament format (e.g., Single Elimination, Round Robin). */
    private final ComboBox<String> formatComboBox = new ComboBox<>();
    /** The checkbox to enable or disable seeding functionality. */
    private final CheckBox seededCheckBox = new CheckBox("Seeded");
    /** The combo box for selecting an existing bracket to use as the source for seeding. */
    private final ComboBox<Bracket> sourceBracketComboBox = new ComboBox<>();
    /** The label for the source bracket selection combo box. */
    private final Label sourceBracketLabel = View.createStyledLabel("Seed from Bracket:", 12, false);
    /** The button to trigger the bracket generation process. */
    private final Button generateButton = new Button("Generate Bracket");
    /** The button to navigate back to the home or previous screen. */
    private final Button backButton = new Button("Back to Home");
    /** The label used to display feedback (e.g., validation errors, success messages). */
    private final Label feedbackLabel = new Label();

    /**
     * Constructs the GenerateBracketView, initializing the UI layout, styling,
     * input fields, and controls.
     */
    public GenerateBracketView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("Generate New Bracket", ACCENT_COLOR, 24, true);

        VBox formBox = createFormBox();

        HBox buttonBox = new HBox(20, generateButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        feedbackLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + WARNING_COLOR_BASE + ";");

        View.styleCheckBox(seededCheckBox);

        sourceBracketLabel.setVisible(false);
        sourceBracketComboBox.setVisible(false);

        this.getChildren().addAll(titleLabel, formBox, buttonBox, feedbackLabel);

        View.styleButton(generateButton);
        View.styleButton(backButton);
    }

    /**
     * Creates a styled VBox container that holds the main input form using a GridPane.
     *
     * @return A styled VBox containing the input form controls.
     * <p>The use of GridPane nested within a VBox for clean form layout was advised by an AI model (Gemini 2.5 Pro).</p>
     */
    private VBox createFormBox() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + PANE_BG_COLOR + "; -fx-background-radius: 8;");
        box.setMaxWidth(400);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        bracketNameField.setPromptText("Enter Bracket Name");
        formatComboBox.getItems().addAll("Single Elimination", "Round Robin");
        formatComboBox.setValue("Single Elimination");

        sourceBracketComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Bracket bracket) {
                return bracket != null ? bracket.getBracketName() : "None";
            }

            @Override
            public Bracket fromString(String string) {
                return null;
            }
        });

        formatComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-text-fill: " + LINE_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                }
            }
        });

        formatComboBox.setCellFactory(param -> new ListCell<>() {
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

        sourceBracketComboBox.setButtonCell(new ListCell<>() {
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

        sourceBracketComboBox.setCellFactory(param -> new ListCell<>() {
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

        grid.add(View.createStyledLabel("Bracket Name:", 12, false), 0, 0);
        grid.add(bracketNameField, 1, 0);
        grid.add(View.createStyledLabel("Format:", 12, false), 0, 1);
        grid.add(formatComboBox, 1, 1);
        grid.add(seededCheckBox, 1, 2);

        grid.add(sourceBracketLabel, 0, 3);
        grid.add(sourceBracketComboBox, 1, 3);

        View.styleTextField(bracketNameField);
        View.styleComboBox(formatComboBox);
        View.styleComboBox(sourceBracketComboBox);

        box.getChildren().add(grid);
        return box;
    }

    /**
     * Gets the text field for the bracket name input.
     *
     * @return The {@code TextField} for the bracket name.
     */
    public TextField getBracketNameField() {
        return bracketNameField;
    }

    /**
     * Gets the combo box for selecting the bracket format.
     *
     * @return The {@code ComboBox} of format strings.
     */
    public ComboBox<String> getFormatComboBox() {
        return formatComboBox;
    }

    /**
     * Gets the checkbox for enabling/disabling seeding.
     *
     * @return The "Seeded" {@code CheckBox}.
     */
    public CheckBox getSeededCheckBox() {
        return seededCheckBox;
    }

    /**
     * Gets the combo box for selecting the source bracket for seeding.
     *
     * @return The {@code ComboBox} of {@code Bracket} objects.
     */
    public ComboBox<Bracket> getSourceBracketComboBox() {
        return sourceBracketComboBox;
    }

    /**
     * Gets the label associated with the source bracket combo box.
     *
     * @return The "Seed from Bracket:" {@code Label}.
     */
    public Label getSourceBracketLabel() {
        return sourceBracketLabel;
    }

    /**
     * Gets the button that triggers the bracket generation logic.
     *
     * @return The "Generate Bracket" {@code Button}.
     */
    public Button getGenerateButton() {
        return generateButton;
    }

    /**
     * Gets the button for navigating back.
     *
     * @return The "Back to Home" {@code Button}.
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Gets the label used for displaying user feedback.
     *
     * @return The feedback {@code Label}.
     */
    public Label getFeedbackLabel() {
        return feedbackLabel;
    }

    /**
     * Resets the view to its initial state by clearing input fields, resetting
     * control values, and clearing the feedback message.
     */
    @Override
    public void refreshView() {
        bracketNameField.clear();
        feedbackLabel.setText("");
        seededCheckBox.setSelected(false);
        formatComboBox.setValue("Single Elimination");
        feedbackLabel.setStyle("-fx-text-fill: " + WARNING_COLOR_BASE + "; -fx-font-weight: bold;");
    }
}