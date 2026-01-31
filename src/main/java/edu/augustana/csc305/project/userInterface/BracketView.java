package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.StringConverter;

import java.util.*;
import java.util.function.Consumer;

/**
 * A view for displaying a single-elimination tournament bracket with a modern UI.
 *
 * <p>This view includes styled navigation controls to select which bracket to display,
 * and supports interactive features like zooming (CTRL + Scroll or +/- keys) and panning via a ScrollPane.
 * It is responsible solely for rendering and handling user interaction, delegating all logic to the Controller.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial Javadoc and usage of core JavaFX components in this class were developed with the assistance of an AI model
 * (Gemini 2.5 Pro).</p>
 */
public class BracketView extends View {

    /**
     * The horizontal space between the start of one round and the next.
     */
    private static final double ROUND_GAP = 220;
    /**
     * The vertical space dedicated for each match in the first round (determines bracket height).
     */
    private static final double MATCH_VERTICAL_GAP = 150;
    /**
     * The fixed width for team labels and match boxes.
     */
    private static final double TEAM_LABEL_WIDTH = 140;
    /**
     * The fixed height for a single team label within a match box.
     */
    private static final double TEAM_LABEL_HEIGHT = 45;
    /**
     * The vertical starting offset for the first matches in the bracket view.
     */
    private static final double BRACKET_START_Y = 80;
    /**
     * The ComboBox used to select and switch between available Brackets.
     */
    private final ComboBox<Bracket> bracketSelector;
    /**
     * Button to navigate to the previous Bracket in the list.
     */
    private final Button previousButton;

    /**
     * Button to navigate to the next Bracket in the list.
     */
    private final Button nextButton;
    /**
     * Button to reset the current zoom level to 1.0.
     */
    private final Button resetZoomButton;
    /**
     * The ScrollPane containing the zoom-able bracket content, providing panning functionality.
     */
    private final ScrollPane scrollPane;
    /**
     * The primary Pane where all match boxes, lines, and round labels are drawn.
     */
    private final Pane bracketPane;
    /**
     * The StackPane wrapping the bracketPane, used to apply the visual zoom scale.
     */
    private final StackPane zoomablePane;
    /**
     * A button to navigate back to the home screen.
     */
    private final Button backButton;

    /**
     * Property representing the current visual scale (zoom level) of the bracket.
     * <p>The use of this property was recommended by an AI model (Gemini 2.5 Pro) for JavaFX data binding.</p>
     */
    private final DoubleProperty scale = new SimpleDoubleProperty(1.0);
    /**
     * Callback executed when a match box is clicked, passing the clicked Match object.
     */
    private Consumer<Match> onMatchClicked;
    /**
     * Callback executed when a zoom action (scroll or key press) is requested, passing the zoom factor.
     */
    private Consumer<Double> onZoomRequested;


    /**
     * Constructs the BracketView, initializing all UI components, setting up
     * the layout, applying styles, and configuring interactive behaviors like zooming.
     */
    public BracketView() {
        bracketSelector = new ComboBox<>();
        previousButton = new Button("◀");
        nextButton = new Button("▶");
        resetZoomButton = new Button("⟲ Reset Zoom");
        backButton = new Button("Back to Home");

        bracketPane = new Pane();
        zoomablePane = new StackPane(bracketPane);

        zoomablePane.scaleXProperty().bind(scale);
        zoomablePane.scaleYProperty().bind(scale);

        Group zoomGroup = new Group(zoomablePane);
        scrollPane = new ScrollPane(zoomGroup);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox navigationControls = new HBox(10, previousButton, bracketSelector, nextButton);
        navigationControls.setAlignment(Pos.CENTER);

        HBox leftControls = new HBox(10, backButton);
        leftControls.setAlignment(Pos.CENTER_LEFT);

        HBox rightControls = new HBox(10, resetZoomButton);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controlsContainer = new HBox(leftControls, spacer, navigationControls, rightControls);
        controlsContainer.setPadding(new Insets(10));
        controlsContainer.setAlignment(Pos.CENTER);

        this.getChildren().addAll(controlsContainer, scrollPane);

        styleView();
        styleControls();

        configureComboBox();
        configureInteractions();

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    /**
     * Applies general styling to the main container and panes, using constants from the {@link View} base class.
     */
    private void styleView() {
        this.setStyle("-fx-background-color: " + View.BG_COLOR + ";");
        bracketPane.setStyle("-fx-background-color: " + View.PANE_BG_COLOR + ";");
        zoomablePane.setStyle("-fx-background-color: " + View.PANE_BG_COLOR + ";");

        scrollPane.setStyle(
                "-fx-background: " + View.PANE_BG_COLOR + ";" +
                        "-fx-background-color: " + View.PANE_BG_COLOR + ";" +
                        "-fx-base: " + View.NODE_BG_COLOR + ";"
        );
    }

    /**
     * Applies consistent styling and hover effects to all control buttons.
     */
    private void styleControls() {
        View.styleButton(previousButton);
        View.styleButton(nextButton);
        View.styleButton(resetZoomButton);
        View.styleButton(backButton);

        View.styleComboBox(bracketSelector);
    }

    /**
     * Configures the ComboBox's display properties, including a custom {@link StringConverter}
     * to show the Bracket name and a custom cell factory to style the displayed list items.
     * <p>The custom {@link StringConverter} and {@link ListCell} styling received assistance from an AI model
     * (Gemini 2.5 Pro).</p>
     */
    private void configureComboBox() {
        bracketSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Bracket bracket) {
                return bracket != null ? bracket.getBracketName() : "No Brackets Available";
            }

            @Override
            public Bracket fromString(String string) {
                return null;
            }
        });
        bracketSelector.setPromptText("Select a bracket to view");

        bracketSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Bracket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(bracketSelector.getPromptText());
                    setStyle("-fx-text-fill: " + View.LINE_COLOR + "; -fx-background-color: " + View.PANE_BG_COLOR + ";");
                } else {
                    setText(item.getBracketName());
                    setStyle("-fx-text-fill: " + View.TEXT_COLOR + "; -fx-background-color: " + View.PANE_BG_COLOR + ";");
                }
            }
        });

        bracketSelector.setCellFactory(param -> new ListCell<>() {
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
    }

    /**
     * Sets up event handlers for zooming (CTRL+Scroll) and CTRL + (+/-) keys, delegating
     * the actual scale update logic to the Controller via the {@code onZoomRequested} callback.
     * <p>The configuration for handling combined mouse scroll and keyboard input for Zoom was assisted by an AI model
     * (Gemini 2.5 Pro).</p>
     */
    private void configureInteractions() {
        scrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.isControlDown()) {
                event.consume();
                double zoomFactor = (event.getDeltaY() > 0) ? 1.1 : 1 / 1.1;
                if (onZoomRequested != null) {
                    onZoomRequested.accept(zoomFactor);
                }
            }
        });

        scrollPane.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.EQUALS) {
                    if (onZoomRequested != null) onZoomRequested.accept(1.1);
                    event.consume();
                } else if (event.getCode() == KeyCode.MINUS) {
                    if (onZoomRequested != null) onZoomRequested.accept(1 / 1.1);
                    event.consume();
                }
            }
        });
    }

    /**
     * Sets the new scale (zoom level) for the bracket content.
     * This method is intended to be called by the Controller in response to a zoom request.
     *
     * @param newScale The new scale value to set.
     */
    public void setScale(double newScale) {
        scale.set(newScale);
    }

    /**
     * Resets the bracket view's zoom to the default 1.0 scale.
     */
    public void resetZoom() {
        scale.set(1.0);
    }

    /**
     * Organizes the matches within the bracket into an order that is suitable for
     * visual, left-to-right rendering in a traditional bracket layout.
     * <p>The core recursive sorting logic for bracket visualization was partly structured and implemented by an AI model
     * (Gemini 2.5 Pro).</p>
     *
     * @param bracket The bracket containing the rounds and matches to order.
     * @return A List of Lists of Matches, where the outer list represents rounds
     * and the inner list represents the visually sorted matches within that round.
     */
    private List<List<Match>> getVisuallyOrderedRounds(Bracket bracket) {
        if (bracket == null || bracket.getRounds().isEmpty()) {
            return Collections.emptyList();
        }

        List<Round> originalRounds = bracket.getRounds();
        int numRounds = originalRounds.size();
        List<List<Match>> sortedRounds = new ArrayList<>();

        for (Round round : originalRounds) {
            sortedRounds.add(new ArrayList<>(round.getMatches()));
        }

        for (int i = numRounds - 2; i >= 0; i--) {
            List<Match> nextRoundSorted = sortedRounds.get(i + 1);
            List<Match> currentRoundToSort = sortedRounds.get(i);
            List<Match> currentRoundSorted = new ArrayList<>();

            for (Match nextMatch : nextRoundSorted) {
                Match source1 = nextMatch.getSourceMatch1();
                if (source1 != null && currentRoundToSort.contains(source1) && !currentRoundSorted.contains(source1)) {
                    currentRoundSorted.add(source1);
                }
                Match source2 = nextMatch.getSourceMatch2();
                if (source2 != null && currentRoundToSort.contains(source2) && !currentRoundSorted.contains(source2)) {
                    currentRoundSorted.add(source2);
                }
            }

            for (Match match : currentRoundToSort) {
                if (!currentRoundSorted.contains(match)) {
                    currentRoundSorted.add(match);
                }
            }

            if (!currentRoundSorted.isEmpty()) {
                sortedRounds.set(i, currentRoundSorted);
            }
        }
        return sortedRounds;
    }

    /**
     * Renders the specified {@code Bracket} onto the {@code bracketPane}.
     * This clears any existing content, determines the layout coordinates, and draws
     * all match boxes, round labels, and connecting lines.
     *
     * @param bracket The Bracket object to be rendered. If null or empty, the view is cleared.
     */
    public void renderBracket(Bracket bracket) {
        bracketPane.getChildren().clear();
        resetZoom();

        if (bracket == null || bracket.getRounds().isEmpty()) {
            bracketPane.setPrefSize(0, 0);
            return;
        }

        List<List<Match>> orderedRounds = getVisuallyOrderedRounds(bracket);
        if (orderedRounds.isEmpty()) {
            return;
        }

        int maxMatches = 0;
        for (List<Match> round : orderedRounds) {
            if (round.size() > maxMatches) {
                maxMatches = round.size();
            }
        }
        double totalHeight = BRACKET_START_Y + (maxMatches * MATCH_VERTICAL_GAP);


        Map<Match, VBox> matchUINodes = new HashMap<>();
        double currentX = 50;
        int roundCounter = 1;
        double finalWidth = 0;

        for (List<Match> roundMatches : orderedRounds) {
            if (roundMatches.isEmpty()) {
                roundCounter++;
                continue;
            }

            Label roundLabel = View.createStyledLabel("Round " + roundCounter, View.ACCENT_COLOR, 16, true);
            roundLabel.setLayoutX(currentX);
            roundLabel.setLayoutY(20);
            roundLabel.setPrefWidth(TEAM_LABEL_WIDTH);
            roundLabel.setAlignment(Pos.CENTER);
            bracketPane.getChildren().add(roundLabel);

            double ySpacingFactor = totalHeight / (roundMatches.size() + 1);

            for (int i = 0; i < roundMatches.size(); i++) {
                Match match = roundMatches.get(i);
                VBox matchBox = createMatchUI(match);
                matchBox.setLayoutX(currentX);

                double currentY = ySpacingFactor * (i + 1);
                matchBox.setLayoutY(currentY - (matchBox.getPrefHeight() / 2));

                bracketPane.getChildren().add(matchBox);
                matchUINodes.put(match, matchBox);
            }

            currentX += ROUND_GAP;
            roundCounter++;
            finalWidth = currentX;
        }

        for (Map.Entry<Match, VBox> entry : matchUINodes.entrySet()) {
            drawConnectorLines(entry.getKey(), entry.getValue(), matchUINodes);
        }

        bracketPane.setPrefSize(finalWidth, totalHeight + BRACKET_START_Y);
    }


    /**
     * Draws the connector lines (L-shaped or straight) from the source matches to the target match box.
     *
     * @param match The target match whose box is being positioned.
     * @param matchBox The VBox UI element representing the target match.
     * @param matchUINodes A map of all rendered matches to their corresponding VBox UI elements.
     */
    private void drawConnectorLines(Match match, VBox matchBox, Map<Match, VBox> matchUINodes) {
        if (match.getSourceMatch1() != null && matchUINodes.containsKey(match.getSourceMatch1())) {
            drawConnectingLine(matchUINodes.get(match.getSourceMatch1()), matchBox);
        }
        if (match.getSourceMatch2() != null && matchUINodes.containsKey(match.getSourceMatch2())) {
            drawConnectingLine(matchUINodes.get(match.getSourceMatch2()), matchBox);
        }
    }

    /**
     * Creates the UI component (VBox) for a single {@code Match}.
     * This includes two team labels and a separator, with data binding and click handlers.
     *
     * @param match The Match object to create the UI for.
     * @return A VBox containing the UI representation of the match.
     */
    private VBox createMatchUI(Match match) {
        Label team1Label = new Label();
        Label team2Label = new Label();

        team1Label.textProperty().bind(Bindings.createStringBinding(() -> {
            if (match.getTeam1() != null) {
                return match.getTeam1().getTeamName();
            } else {
                return "TBD";
            }
        }, match.team1Property()));

        team2Label.textProperty().bind(Bindings.createStringBinding(() -> {
            if (match.getTeam2() != null) {
                return match.getTeam2().getTeamName();
            } else {
                return "TBD";
            }
        }, match.team2Property()));

        javafx.beans.value.ChangeListener<Object> styleListener = (obs, oldVal, newVal) -> updateMatchStyles(match, team1Label, team2Label);

        match.winnerProperty().addListener(styleListener);
        match.team1Property().addListener(styleListener);
        match.team2Property().addListener(styleListener);

        updateMatchStyles(match, team1Label, team2Label);

        styleTeamLabel(team1Label);
        styleTeamLabel(team2Label);

        Line separator = new Line(0, 0, TEAM_LABEL_WIDTH, 0);
        separator.setStroke(Color.web(View.BORDER_COLOR));

        VBox teamsContainer = new VBox(team1Label, separator, team2Label);
        teamsContainer.setAlignment(Pos.CENTER);
        teamsContainer.setStyle(View.DEFAULT_MATCH_STYLE);

        teamsContainer.setOnMouseClicked(event -> {
            if (onMatchClicked != null) {
                onMatchClicked.accept(match);
            }
            event.consume();
        });

        teamsContainer.setOnMouseEntered(e -> {
            teamsContainer.setCursor(Cursor.HAND);
            teamsContainer.setStyle(View.HOVER_MATCH_STYLE);
        });

        teamsContainer.setOnMouseExited(e -> {
            teamsContainer.setCursor(Cursor.DEFAULT);
            teamsContainer.setStyle(View.DEFAULT_MATCH_STYLE);
        });

        VBox matchContainer = new VBox(teamsContainer);
        matchContainer.setPrefHeight(TEAM_LABEL_HEIGHT * 2 + 5);
        matchContainer.setPrefWidth(TEAM_LABEL_WIDTH);
        matchContainer.setMinWidth(TEAM_LABEL_WIDTH);
        matchContainer.setMaxWidth(TEAM_LABEL_WIDTH);

        return matchContainer;
    }

    /**
     * Updates the text styles of the match box labels based on the winner.
     */
    private void updateMatchStyles(Match match, Label team1Label, Label team2Label) {
        Team winner = match.getWinner();
        Team t1 = match.getTeam1();
        Team t2 = match.getTeam2();

        team1Label.setStyle(View.NORMAL_STYLE);
        team2Label.setStyle(View.NORMAL_STYLE);

        if (winner != null) {
            if (t1 != null && (t1.equals(winner) || (t1.getTeamId() != 0 && t1.getTeamId() == winner.getTeamId()))) {
                team1Label.setStyle(View.WINNER_STYLE);
            }
            if (t2 != null && (t2.equals(winner) || (t2.getTeamId() != 0 && t2.getTeamId() == winner.getTeamId()))) {
                team2Label.setStyle(View.WINNER_STYLE);
            }
        }
    }


    /**
     * Applies standard dimensions and alignment styling to a team label.
     *
     * @param label The Label object to style.
     */
    private void styleTeamLabel(Label label) {
        label.setPrefSize(TEAM_LABEL_WIDTH, TEAM_LABEL_HEIGHT);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(5));
    }

    /**
     * Draws the L-shaped lines connecting the center of the source match box
     * to the center of the target match box.
     * <p>The approach for calculating mid-points and drawing connecting lines using JavaFX bindings
     * received assistance from an AI model (Gemini 2.5 Pro).</p>
     *
     * @param sourceBox The VBox of the match that feeds into the next round.
     * @param targetBox The VBox of the match in the next round.
     */
    private void drawConnectingLine(VBox sourceBox, VBox targetBox) {
        Line hLine1 = new Line();
        Line vLine = new Line();
        Line hLine2 = new Line();

        for (Line line : new Line[]{hLine1, vLine, hLine2}) {
            line.setStroke(Color.web(View.LINE_COLOR));
            line.setStrokeWidth(2);
        }

        NumberBinding startY = sourceBox.layoutYProperty().add(sourceBox.heightProperty().divide(2));
        NumberBinding endY = targetBox.layoutYProperty().add(targetBox.heightProperty().divide(2));
        NumberBinding midX = sourceBox.layoutXProperty().add(sourceBox.widthProperty()).add(targetBox.layoutXProperty()).divide(2);

        hLine1.startXProperty().bind(sourceBox.layoutXProperty().add(sourceBox.widthProperty()));
        hLine1.startYProperty().bind(startY);
        hLine1.endXProperty().bind(midX);
        hLine1.endYProperty().bind(startY);

        vLine.startXProperty().bind(midX);
        vLine.startYProperty().bind(startY);
        vLine.endXProperty().bind(midX);
        vLine.endYProperty().bind(endY);

        hLine2.startXProperty().bind(midX);
        hLine2.startYProperty().bind(endY);
        hLine2.endXProperty().bind(targetBox.layoutXProperty());
        hLine2.endYProperty().bind(endY);

        bracketPane.getChildren().addAll(hLine1, vLine, hLine2);
    }

    /**
     * Gets the ComboBox used to select the current Bracket.
     *
     * @return The Bracket selection ComboBox.
     */
    public ComboBox<Bracket> getBracketSelector() {
        return bracketSelector;
    }

    /**
     * Gets the button for navigating to the previous Bracket.
     *
     * @return The previous button.
     */
    public Button getPreviousButton() {
        return previousButton;
    }

    /**
     * Gets the button for navigating to the next Bracket.
     *
     * @return The next button.
     */
    public Button getNextButton() {
        return nextButton;
    }

    /**
     * Gets the button for resetting the zoom level.
     *
     * @return The reset zoom button.
     */
    public Button getResetZoomButton() {
        return resetZoomButton;
    }

    /**
     * Gets the button for navigating back to the home view.
     *
     * @return The back button.
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Gets the scale property for binding or monitoring the current zoom level.
     *
     * @return The DoubleProperty representing the current scale.
     */
    public DoubleProperty scaleProperty() {
        return scale;
    }

    /**
     * Sets the handler to be called when a zoom action is initiated by the user
     * (e.g., CTRL+Scroll or keyboard shortcuts).
     *
     * @param handler A Consumer that accepts the zoom factor (e.g., 1.1 for zoom in).
     */
    public void setOnZoomRequested(Consumer<Double> handler) {
        this.onZoomRequested = handler;
    }

    /**
     * Sets the handler to be called when a match box in the bracket is clicked.
     *
     * @param handler A Consumer that accepts the {@link Match} object that was clicked.
     * <p>The use of the Consumer interface in this method signature received assistance from an AI model
     *                (Gemini 2.5 Pro).</p>
     */
    public void setOnMatchClicked(Consumer<Match> handler) {
        this.onMatchClicked = handler;
    }

    /**
     * Updates the interactivity of all match nodes. This version ensures all nodes
     * are always clickable, as the pop-up view will handle role-based permissions.
     *
     * @param currentUser The current user (ignored in this implementation).
     * @param handler The action to perform when any user clicks a match.
     * <p>The creation of this method was suggested by an AI model (Gemini 2.5 Pro).</p>
     */
    public void updateMatchNodeInteractivity(User currentUser, Consumer<Match> handler) {
        this.setOnMatchClicked(handler);
        final Cursor cursor = Cursor.HAND;
        for (javafx.scene.Node node : bracketPane.getChildren()) {
            if (node instanceof VBox) {
                node.setCursor(cursor);
            }
        }
    }

    /**
     * Placeholder implementation for the refresh method required by the base View class.
     * <p>
     * Note: Bracket content is typically refreshed via {@link #renderBracket(Bracket)}
     * when a new bracket is selected or the underlying model changes.
     * </p>
     */
    @Override
    public void refreshView() {
        // Implementation
    }
}