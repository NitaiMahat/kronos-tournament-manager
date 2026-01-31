package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.TeamDTO;
import edu.augustana.csc305.project.model.api.TeamNewDTO;
import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.TeamManagementView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.concurrent.Task;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the {@link TeamManagementView}.
 *
 * <p>Handles adding, deleting, and displaying teams associated with the current {@link Tournament}
 * via the Kronos API. Data operations are performed asynchronously using JavaFX {@link Task}s.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TeamManagementViewController extends ViewController {

    private final TeamManagementView view;
    private final KronosApi api;
    private final List<TeamDTO> currentTeams = new ArrayList<>();

    /**
     * Constructs the controller for the team management screen.
     *
     * @param view The {@link TeamManagementView} instance this controller manages.
     * @param tournament The currently selected {@link Tournament} model.
     * @param appController The main {@link AppController} for delegation of navigation.
     */
    public TeamManagementViewController(TeamManagementView view, Tournament tournament, AppController appController) {
        super(view, tournament, appController);
        this.view = view;
        this.api = ApiClient.getInstance().getKronosApi();
        loadTeams();
        attachEvents();
    }

    /**
     * Attaches event handlers to the Add, Delete, and Back buttons.
     */
    @Override
    protected void attachEvents() {
        view.getSubmitButton().setOnAction(e -> addTeam());
        view.getDeleteButton().setOnAction(e -> deleteTeam());
        view.getBackButton().setOnAction(e -> appController.showHomeView());
    }

    /**
     * Handles the logic for adding a new team to the current tournament.
     * Performs client-side validation and sends the request to the API asynchronously.
     */
    private void addTeam() {
        String teamName = view.getTeamNameField().getText().trim();
        int tournamentId = tournament.getTournamentId();

        if (teamName.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Team name cannot be empty!");
            return;
        }

        boolean exists = currentTeams.stream()
                .anyMatch(t -> t.getName().equalsIgnoreCase(teamName));
        if (exists) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Team with that name already exists!");
            return;
        }

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Adding team...");
        view.getSubmitButton().setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                TeamNewDTO newTeam = new TeamNewDTO(teamName);
                Response<Void> response = api.createTeamInTournament(tournamentId, newTeam).execute();

                if (!response.isSuccessful()) {
                    throw new IOException("API Error: " + response.message());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                view.getSubmitButton().setDisable(false);
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("'" + teamName + "' added successfully!");
                view.getTeamNameField().clear();

                loadTeams();
            }

            @Override
            protected void failed() {
                view.getSubmitButton().setDisable(false);
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Failed to add team: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Handles the deletion of the currently selected team from the tournament.
     * Finds the corresponding team ID from the local cache before sending the delete request.
     */
    private void deleteTeam() {
        String selectedTeamName = view.getTeamList().getSelectionModel().getSelectedItem();

        if (selectedTeamName == null || selectedTeamName.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Please select a team to delete.");
            return;
        }

        Optional<TeamDTO> teamToDelete = currentTeams.stream()
                .filter(t -> t.getName().equals(selectedTeamName))
                .findFirst();

        if (teamToDelete.isEmpty()) {
            view.getFeedbackLabel().setText("Error: Could not find team ID.");
            return;
        }

        int teamId = teamToDelete.get().getTeamId();
        int tournamentId = tournament.getTournamentId();

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Deleting team...");
        view.getDeleteButton().setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.deleteTeamFromTournament(tournamentId, teamId).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("API Error: " + response.message());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                view.getDeleteButton().setDisable(false);
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("'" + selectedTeamName + "' deleted.");

                loadTeams();
            }

            @Override
            protected void failed() {
                view.getDeleteButton().setDisable(false);
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Failed to delete: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Asynchronously loads all teams associated with the current tournament from the API
     * and updates the local cache and the view's list.
     */
    private void loadTeams() {
        Task<List<TeamDTO>> task = new Task<>() {
            @Override
            protected List<TeamDTO> call() throws Exception {
                Response<List<TeamDTO>> response = api.getTeamsForTournament(tournament.getTournamentId()).execute();
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("Failed to load teams");
                }
                return response.body();
            }

            @Override
            protected void succeeded() {
                List<TeamDTO> teams = getValue();
                currentTeams.clear();
                currentTeams.addAll(teams);

                view.getTeamList().getItems().clear();
                for (TeamDTO team : teams) {
                    view.getTeamList().getItems().add(team.getName());
                }
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Error loading teams: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }
}