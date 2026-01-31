package edu.augustana.csc305.project.userInterface;

import edu.augustana.csc305.project.model.api.MatchDTO;
import edu.augustana.csc305.project.model.api.MatchUpdateDTO;
import edu.augustana.csc305.project.model.domain.Match;
import edu.augustana.csc305.project.model.domain.Team;
import edu.augustana.csc305.project.model.domain.User;
import edu.augustana.csc305.project.model.domain.UserRole;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A temporary, pop-up view that displays detailed information about a single match.
 *
 * <p>It dynamically shows controls for updating the match winner based on the
 * current user's role (Admin, Organizer, or Referee).
 * This view is typically
 * displayed in a separate pop-up stage and closes upon successfully saving the winner.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 * <p>The initial structure, Javadoc, and key data binding implementations received assistance from an AI model
 * (Gemini 2.5 Pro).</p>
 */
public class MatchDetailView extends View {

    /**
     * The match object whose details are being displayed and possibly updated.
     */
    private final Match match;
    /** The currently logged-in user, used to check permissions for updating the winner. */
    private final User currentUser;
    private final KronosApi api;

    private Label courtLabel;
    private Label refereeLabel;

    /**
     * Constructs the Match Detail pop-up window with an improved, modern layout.
     * The view's layout is dynamically assembled, including a winner selection
     * section only if the current user has the necessary permissions.
     *
     * @param match The {@link Match} to display information for.
     * @param currentUser The currently logged-in {@link User}, to determine access level.
     */
    public MatchDetailView(Match match, User currentUser) {
        this.match = match;
        this.currentUser = currentUser;
        this.api = ApiClient.getInstance().getKronosApi();

        setPadding(new Insets(25));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + PANE_BG_COLOR + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1;");
        setSpacing(15);
        setMaxWidth(400);

        Label titleLabel = View.createStyledLabel("Match Details", ACCENT_COLOR, 18, true);

        VBox teamSection = createTeamDisplay();
        GridPane detailsGrid = createInfoGrid();

        getChildren().addAll(titleLabel, new Separator(), teamSection, detailsGrid);

        if (canUpdateWinner()) {
            getChildren().addAll(new Separator(), createWinnerSelection());
        }

        fetchMatchDetails();
    }

    /**
     * Fetches the latest match details (specifically Court and Referee assignments) from the API.
     */
    private void fetchMatchDetails() {
        api.getMatchById(match.getMatchId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<MatchDTO> call, @NotNull Response<MatchDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MatchDTO dto = response.body();
                    Platform.runLater(() -> {
                        if (dto.getCourt() != null) {
                            courtLabel.setText(dto.getCourt().getName());
                        } else {
                            courtLabel.setText("Unassigned");
                        }

                        if (dto.getReferee() != null) {
                            refereeLabel.setText(dto.getReferee().getUsername());
                        } else {
                            refereeLabel.setText("Unassigned");
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        courtLabel.setText("Error loading");
                        refereeLabel.setText("Error loading");
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<MatchDTO> call, @NotNull Throwable t) {
                Platform.runLater(() -> {
                    courtLabel.setText("Network Error");
                    refereeLabel.setText("Network Error");
                });
            }
        });
    }

    /**
     * Checks if the current user has the authority to update a match winner.
     *
     * @return true if the user's role is {@code ADMIN}, {@code TOURNAMENT_ORGANIZER}, or {@code REFEREE}; false otherwise.
     * <p>This permission check logic received assistance from an AI model (Gemini 2.5 Pro).</p>
     */
    private boolean canUpdateWinner() {
        UserRole role = currentUser.getRole();
        return role == UserRole.ADMIN || role == UserRole.TOURNAMENT_ORGANIZER || role == UserRole.REFEREE;
    }

    /**
     * Creates an HBox to display the two competing teams with a "VS" separator
     * and a separate section to display the current winner, which is bound to the {@code match}'s winner property.
     * <p>The use of {@code Bindings.createStringBinding} for the winner label was a data binding recommendation by an AI model
     * (Gemini 2.5 Pro).</p>
     *
     * @return A styled VBox containing the team names and winner status.
     */
    private VBox createTeamDisplay() {
        String team1Name;
        if (match.getTeam1() != null) {
            team1Name = match.getTeam1().getTeamName();
        } else {
            team1Name = "TBD";
        }
        Label team1Label = View.createStyledLabel(team1Name, 16, false);

        String team2Name;
        if (match.getTeam2() != null) {
            team2Name = match.getTeam2().getTeamName();
        } else {
            team2Name = "TBD";
        }
        Label team2Label = View.createStyledLabel(team2Name, 16, false);

        Label vsLabel = View.createStyledLabel("VS", 14, true);
        vsLabel.setPadding(new Insets(0, 20, 0, 20));

        Label winnerHeaderLabel = View.createStyledLabel("Winner", 14, true);
        winnerHeaderLabel.setPadding(new Insets(10, 0, 5, 0));
        Label winnerValueLabel = View.createStyledLabel("", ACCENT_COLOR, 16, true);

        winnerValueLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (match.getWinner() != null) {
                return match.getWinner().getTeamName();
            } else {
                return "Not Decided";
            }
        }, match.winnerProperty()));


        HBox teamsBox = new HBox(team1Label, vsLabel, team2Label);
        teamsBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(5, teamsBox, winnerHeaderLabel, winnerValueLabel);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    /**
     * Creates the grid of labels that displays match metadata like Court and Referee.
     *
     * @return A {@link GridPane} containing match details.
     */
    private GridPane createInfoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(View.createStyledLabel("Court:", 14, true), 0, 0);
        grid.add(View.createStyledLabel("Referee:", 14, true), 0, 1);

        courtLabel = View.createStyledLabel("Loading...", 14, false);
        refereeLabel = View.createStyledLabel("Loading...", 14, false);

        if (match.getCourt() != null) {
            courtLabel.setText(match.getCourt().getCourtName());
        }
        if (match.getReferee() != null) {
            refereeLabel.setText(match.getReferee().getUsername());
        }

        grid.add(courtLabel, 1, 0);
        grid.add(refereeLabel, 1, 1);

        return grid;
    }

    /**
     * Creates the UI controls for selecting and saving the match winner.
     * This section is only visible to authorized users.
     * <p>The implementation details for the ComboBox converter, cell factories, and window closing logic
     * received assistance from an AI model (Gemini 2.5 Pro).</p>
     *
     * @return A {@link VBox} containing the winner selection ComboBox and Save button.
     */
    private VBox createWinnerSelection() {
        VBox winnerBox = new VBox(10);
        winnerBox.setAlignment(Pos.CENTER);
        winnerBox.setPadding(new Insets(10, 0, 0, 0));

        Label title = View.createStyledLabel("Admin: Set Winner", 14, true);
        ComboBox<Team> winnerSelector = new ComboBox<>();
        Button saveButton = new Button("Save Winner");
        Label statusLabel = View.createStyledLabel("", 12, false);

        if (match.getTeam1() != null) {
            winnerSelector.getItems().add(match.getTeam1());
        }
        if (match.getTeam2() != null) {
            winnerSelector.getItems().add(match.getTeam2());
        }

        winnerSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getTeamName() : "";
            }

            @Override
            public Team fromString(String string) {
                return null;
            }
        });

        if (match.getWinner() != null) {
            winnerSelector.setValue(match.getWinner());
        }

        saveButton.setOnAction(e -> {
            Team selectedWinner = winnerSelector.getSelectionModel().getSelectedItem();
            if (selectedWinner != null) {
                if (selectedWinner.getTeamId() == 0) {
                    statusLabel.setText("Error: Invalid Team ID (0). Cannot save.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                    return;
                }

                saveButton.setDisable(true);
                winnerSelector.setDisable(true);
                statusLabel.setText("Saving...");
                statusLabel.setTextFill(javafx.scene.paint.Color.BLUE);

                MatchUpdateDTO updateDTO = new MatchUpdateDTO();
                updateDTO.setWinnerId(selectedWinner.getTeamId());

                System.out.println("DEBUG: Sending Update for Match " + match.getMatchId() + " -> WinnerID: " + selectedWinner.getTeamId());

                api.updateMatch(match.getMatchId(), updateDTO).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                        Platform.runLater(() -> {
                            if (response.isSuccessful()) {
                                match.setWinner(selectedWinner);
                                match.setComplete(true);
                                ((Stage) getScene().getWindow()).close();
                            } else {
                                statusLabel.setText("Error " + response.code() + ": " + response.message());
                                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                                saveButton.setDisable(false);
                                winnerSelector.setDisable(false);

                                try {
                                    if (response.errorBody() != null) {
                                        System.err.println("API Error Body: " + response.errorBody().string());
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Network Error: " + t.getMessage());
                            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                            saveButton.setDisable(false);
                            winnerSelector.setDisable(false);
                        });
                    }
                });
            }
        });

        View.styleComboBox(winnerSelector);
        View.styleButton(saveButton);

        winnerSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-text-fill: " + LINE_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getTeamName());
                    setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + PANE_BG_COLOR + ";");
                }
            }
        });

        winnerSelector.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + PANE_BG_COLOR + ";");
                } else {
                    setText(item.getTeamName());
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: " + NODE_BG_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        String bgColor = (getIndex() % 2 == 0) ? PANE_BG_COLOR : LIST_CELL_ALT_BG_COLOR;
                        setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-background-color: " + bgColor + ";");
                    }
                }
            }
        });

        winnerBox.getChildren().addAll(title, winnerSelector, saveButton, statusLabel);
        return winnerBox;
    }

    /**
     * This view is designed to be static once displayed in a pop-up and does not require a refresh
     * mechanism, as data is bound or managed by the pop-ups life cycle.
     */
    @Override
    public void refreshView() {
    }
}