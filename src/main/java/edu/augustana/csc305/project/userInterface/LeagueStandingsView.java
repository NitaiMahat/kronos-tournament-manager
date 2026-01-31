package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.domain.League;
import edu.augustana.csc305.project.model.domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A view for displaying league standings in a table format.
 *
 * <p>This view displays team standings (Team Name and Points) for a selected league
 * in a {@link TableView}.
 * The standings are intended to be sorted by points
 * in descending order by the controller.
 * It includes a Back button for navigation.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LeagueStandingsView extends View {

    /**
     * The table view for displaying team standings with Team Name and Points columns.
     */
    private final TableView<Map.Entry<Team, Integer>> standingsTable = new TableView<>();

    /**
     * The label used to display the selected league name.
     */
    private final Label leagueNameLabel = View.createStyledLabel("", 18, true);

    /**
     * The button to navigate back to the league selection screen.
     */
    private final Button backButton = new Button("Back");

    /**
     * Constructs the League Standings View and initializes its UI components and layout.
     * Sets up the {@link TableView} with two columns (Team Name and Points), styling, and navigation.
     */
    public LeagueStandingsView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label titleLabel = View.createStyledLabel("League Standings", ACCENT_COLOR, 28, true);

        standingsTable.setMaxWidth(600);
        standingsTable.setMaxHeight(400);
        View.styleTableView(standingsTable);

        TableColumn<Map.Entry<Team, Integer>, String> teamNameColumn = getEntryStringTableColumn();

        TableColumn<Map.Entry<Team, Integer>, Integer> pointsColumn = getEntryIntegerTableColumn();

        standingsTable.getColumns().add(teamNameColumn);
        standingsTable.getColumns().add(pointsColumn);
        standingsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        standingsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Map.Entry<Team, Integer> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + ";");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });


        VBox standingsBox = View.createSectionBox(null);
        standingsBox.getChildren().addAll(leagueNameLabel, standingsTable);
        standingsBox.setMaxWidth(650);
        standingsBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10, backButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(titleLabel, standingsBox, buttonBox);

        View.styleButton(backButton);
    }

    @NotNull
    private TableColumn<Map.Entry<Team, Integer>, Integer> getEntryIntegerTableColumn() {
        TableColumn<Map.Entry<Team, Integer>, Integer> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(param ->
                new javafx.beans.property.SimpleIntegerProperty(param.getValue().getValue()).asObject());
        pointsColumn.setPrefWidth(200);
        pointsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-alignment: CENTER; -fx-background-color: transparent;");
                }
            }
        });
        return pointsColumn;
    }

    @NotNull
    private TableColumn<Map.Entry<Team, Integer>, String> getEntryStringTableColumn() {
        TableColumn<Map.Entry<Team, Integer>, String> teamNameColumn = new TableColumn<>("Team Name");
        teamNameColumn.setCellValueFactory(param ->
                new javafx.beans.property.SimpleStringProperty(param.getValue().getKey().getTeamName()));
        teamNameColumn.setPrefWidth(398);
        teamNameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: transparent;");
                }
            }
        });
        return teamNameColumn;
    }

    /**
     * Gets the standings table view.
     *
     * @return The standings table view.
     */
    public TableView<Map.Entry<Team, Integer>> getStandingsTable() {
        return standingsTable;
    }

    /**
     * Gets the league name label.
     *
     * @return The league name label.
     */
    public Label getLeagueNameLabel() {
        return leagueNameLabel;
    }

    /**
     * Gets the "Back" button.
     *
     * @return The back button.
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Updates the standings table with the provided standing's data.
     *
     * @param standings A map of {@link Team} objects to their points.
     */
    public void updateStandings(Map<Team, Integer> standings) {
        standingsTable.getItems().clear();
        if (standings != null && !standings.isEmpty()) {
            standingsTable.getItems().addAll(standings.entrySet());
        }
    }

    /**
     * Updates the league name label to display the selected league.
     *
     * @param league The {@link League} for which standings are being displayed.
     */
    public void updateLeagueName(League league) {
        if (league != null) {
            leagueNameLabel.setText("League: " + league.getLeagueName());
        } else {
            leagueNameLabel.setText("No league selected");
        }
    }

    /**
     * Clears the standings table and resets the league name label.
     */
    @Override
    public void refreshView() {
        standingsTable.getItems().clear();
        leagueNameLabel.setText("No league selected");
    }
}