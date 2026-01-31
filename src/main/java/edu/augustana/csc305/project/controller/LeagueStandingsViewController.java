package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.TeamDTO;
import edu.augustana.csc305.project.model.domain.League;
import edu.augustana.csc305.project.model.domain.Team;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.LeagueStandingsView;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the {@link LeagueStandingsView}.
 *
 * <p>This controller is responsible for fetching and displaying the current standings
 * for the selected {@link League} by making asynchronous calls to the Kronos API.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LeagueStandingsViewController extends ViewController {

    private final LeagueStandingsView view;
    private final KronosApi api;
    private final League selectedLeague;

    /**
     * Constructs a LeagueStandingsViewController.
     *
     * <p>Initializes API services, retrieves the currently active league from the {@link AppController},
     * and triggers the asynchronous loading of the league standings.</p>
     *
     * @param view The {@link LeagueStandingsView} instance to control.
     * @param appController The global {@link AppController} instance.
     */
    public LeagueStandingsViewController(LeagueStandingsView view, AppController appController) {
        super(view, null, appController);
        this.view = view;
        this.api = ApiClient.getInstance().getKronosApi();
        this.selectedLeague = appController.getCurrentLeague();

        loadStandings();
        attachEvents();
    }

    /**
     * Sets up event handlers for the view's components.
     */
    @Override
    protected void attachEvents() {
        view.getBackButton().setOnAction(e -> appController.showSelectionView());
    }

    /**
     * Loads standings data for the selected league asynchronously via the API.
     * This involves two steps: fetching team names, then fetching points, and combining them.
     */
    private void loadStandings() {
        if (selectedLeague == null) {
            view.updateLeagueName(null);
            view.updateStandings(null);
            return;
        }

        view.updateLeagueName(selectedLeague);

        api.getTeams(selectedLeague.getLeagueID()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<TeamDTO>> call, @NotNull Response<List<TeamDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TeamDTO> teamDTOs = response.body();
                    fetchStandingsAndPopulate(teamDTOs);
                } else {
                    handleError("Failed to load teams: " + response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<TeamDTO>> call, @NotNull Throwable t) {
                handleError("Network error loading teams: " + t.getMessage());
            }
        });
    }

    /**
     * Fetches the points map for the league and combines it with the provided team DTOs to create
     * the final standings data structure for the view.
     *
     * @param teamDTOs A list of {@link TeamDTO} objects containing team ID and name.
     */
    private void fetchStandingsAndPopulate(List<TeamDTO> teamDTOs) {
        api.getLeagueStandings(selectedLeague.getLeagueID()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Map<Integer, Integer>> call, @NotNull Response<Map<Integer, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<Integer, Integer> pointsMap = response.body();
                    Map<Team, Integer> finalStandings = new HashMap<>();

                    for (TeamDTO dto : teamDTOs) {
                        Team team = new Team(dto.getName());
                        team.setTeamId(dto.getTeamId());

                        Integer points = pointsMap.getOrDefault(dto.getTeamId(), 0);
                        finalStandings.put(team, points);
                    }

                    Platform.runLater(() -> view.updateStandings(finalStandings));
                } else {
                    handleError("Failed to load standings: " + response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Map<Integer, Integer>> call, @NotNull Throwable t) {
                handleError("Network error loading standings: " + t.getMessage());
            }
        });
    }

    /**
     * Handles errors during API calls by logging the message and updating the view
     * on the JavaFX application thread.
     *
     * @param message The error message to log and display.
     */
    private void handleError(String message) {
        System.err.println(message);
        Platform.runLater(() -> view.updateStandings(null));
    }
}