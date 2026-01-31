package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.BracketDTO;
import edu.augustana.csc305.project.model.api.MatchDTO;
import edu.augustana.csc305.project.model.api.RoundDTO;
import edu.augustana.csc305.project.model.api.TeamDTO;
import edu.augustana.csc305.project.model.api.TournamentStandingsPutDTO;
import edu.augustana.csc305.project.model.domain.*;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.BracketServices;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.PointsEntryView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the {@link PointsEntryView}.
 *
 * <p>This controller manages the logic for loading all teams, displaying their current points,
 * allowing manual modification, and calculating points based on a completed single-elimination
 * bracket using {@link BracketServices}.
 * All data synchronization is handled through the
 * {@link KronosApi} asynchronously.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class PointsEntryViewController extends ViewController {

    private final PointsEntryView view;
    private final Tournament currentTournament;
    private final KronosApi api;

    /**
     * A map to maintain a direct reference between a Team object and the
     * TextField used to edit its points.
     */
    private final Map<Team, TextField> teamPointsFields = new HashMap<>();

    /**
     * Constructs a new PointsEntryViewController.
     *
     * @param view              The {@code PointsEntryView} instance this controller will manage.
     * @param currentTournament The currently active tournament.
     * @param appController     The main application controller.
     */
    public PointsEntryViewController(PointsEntryView view, Tournament currentTournament, AppController appController) {
        super(view, null, appController);
        this.view = view;
        this.currentTournament = currentTournament;
        this.api = ApiClient.getInstance().getKronosApi();

        initialize();
        attachEvents();
    }

    /**
     * Initializes the view by clearing existing data fields and triggering the asynchronous data load.
     */
    private void initialize() {
        view.getTeamsList().getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        teamPointsFields.clear();

        loadData();
    }

    /**
     * Fetches Brackets (for generation dropdown), Teams, and Standings from the API.
     */
    private void loadData() {
        api.getBracketsForTournament(currentTournament.getTournamentId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<BracketDTO>> call, @NotNull Response<List<BracketDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bracket> domainBrackets = mapBracketDTOs(response.body());
                    List<Bracket> eliminationBrackets = domainBrackets.stream()
                            .filter(b -> b.getBracketType() == BracketType.SINGLE_ELIMINATION ||
                                    b.getBracketType() == BracketType.SINGLE_ELIMINATION_SEEDED)
                            .collect(Collectors.toList());

                    Platform.runLater(() -> {
                        view.getBracketComboBox().getItems().setAll(eliminationBrackets);
                        if (!eliminationBrackets.isEmpty()) {
                            view.getBracketComboBox().getSelectionModel().selectFirst();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<BracketDTO>> call, @NotNull Throwable t) {
                Platform.runLater(() -> System.err.println("Failed to load brackets: " + t.getMessage()));
            }
        });

        api.getTeamsForTournament(currentTournament.getTournamentId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<TeamDTO>> call, @NotNull Response<List<TeamDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TeamDTO> teamDTOs = response.body();
                    List<Team> domainTeams = new ArrayList<>();

                    for (TeamDTO dto : teamDTOs) {
                        Team t = new Team(dto.getName());
                        t.setTeamId(dto.getTeamId());
                        domainTeams.add(t);
                    }

                    loadStandings(domainTeams);
                } else {
                    Platform.runLater(() -> view.getMessageLabel().setText("Error loading teams."));
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<TeamDTO>> call, @NotNull Throwable t) {
                Platform.runLater(() -> view.getMessageLabel().setText("Network error loading teams."));
            }
        });
    }

    /**
     * Asynchronously loads the current tournament standings (points) and populates the view
     * with the combined team and point data.
     *
     * @param loadedTeams The list of {@link Team} domain objects whose points need to be displayed.
     */
    private void loadStandings(List<Team> loadedTeams) {
        api.getTournamentStandings(currentTournament.getTournamentId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Map<Integer, Integer>> call, @NotNull Response<Map<Integer, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<Integer, Integer> standings = response.body();

                    Platform.runLater(() -> {
                        for (Team team : loadedTeams) {
                            Integer points = standings.getOrDefault(team.getTeamId(), 0);
                            TextField pointsField = view.addTeam(team, points);
                            teamPointsFields.put(team, pointsField);
                        }
                    });
                } else {
                    Platform.runLater(() -> view.getMessageLabel().setText("Error loading standings."));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Map<Integer, Integer>> call, @NotNull Throwable t) {
                Platform.runLater(() -> view.getMessageLabel().setText("Network error loading standings."));
            }
        });
    }

    /**
     * Attaches event handlers to the Save, Back, and Generate Points buttons.
     */
    @Override
    protected void attachEvents() {
        view.getSaveButton().setOnAction(e -> savePoints());
        view.getBackButton().setOnAction(e -> appController.showHomeView());
        view.getGeneratePointsButton().setOnAction(e -> generatePoints());
    }

    /**
     * Helper method to map final rank in a single-elimination bracket to a point value.
     * Uses explicit percentages relative to the total number of teams.
     *
     * @param rank The final rank (e.g., 1st, 2nd, 3rd...).
     * @param totalTeams The total number of teams involved in the tournament.
     * @return The point value corresponding to the rank tier.
     */
    private int getPointsForRank(int rank, int totalTeams) {
        if (totalTeams == 0) return 0;

        if (rank > totalTeams) return 0;

        int multiplier = 10;

        return (totalTeams - rank + 1) * multiplier;
    }

    /**
     * Calculates the final standings (points) based on the currently selected completed bracket
     * and updates the text fields in the view, but does not save them to the API.
     */
    private void generatePoints() {
        view.getMessageLabel().setText("");
        Bracket selectedBracket = view.getBracketComboBox().getSelectionModel().getSelectedItem();

        if (selectedBracket == null) {
            view.getMessageLabel().setText("ERROR: Please select a bracket from the dropdown to generate points.");
            return;
        }

        try {
            Map<Integer, Integer> standingsById = BracketServices.generatePointsStandings(selectedBracket);

            int teamsInBracket = standingsById.size();
            if (teamsInBracket == 0) teamsInBracket = 1;

            final String baseStyle = View.TEXT_INPUT_STYLE;
            final String successBorder = "-fx-border-color: " + View.ACCENT_COLOR + "; -fx-border-width: 2;";

            int finalTeamsInBracket = teamsInBracket;

            Platform.runLater(() -> {
                for (Map.Entry<Team, TextField> entry : teamPointsFields.entrySet()) {
                    Team team = entry.getKey();
                    TextField field = entry.getValue();

                    int rank = standingsById.getOrDefault(team.getTeamId(), finalTeamsInBracket + 1);

                    int points = getPointsForRank(rank, finalTeamsInBracket);

                    field.setText(String.valueOf(points));

                    if (standingsById.containsKey(team.getTeamId())) {
                        field.setStyle(baseStyle + successBorder);
                    } else {
                        field.setStyle(baseStyle);
                    }
                }

                view.getMessageLabel().setText("Points have been generated successfully! Click 'Save Points' to commit changes.");
                view.getMessageLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + "; -fx-font-weight: bold;");
            });

        } catch (Exception e) {
            System.err.println("Error generating points: " + e.getMessage());
            e.printStackTrace();
            view.getMessageLabel().setText("ERROR: Failed to generate points. " + e.getMessage());
            view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");
        }
    }

    /**
     * Validates user input and sends a request to the API to replace the entire set of tournament standings.
     */
    private void savePoints() {
        view.getMessageLabel().setText("Saving...");
        view.getSaveButton().setDisable(true);

        Map<Integer, Integer> newStandings = new HashMap<>();
        boolean formatError = false;

        for (Map.Entry<Team, TextField> entry : teamPointsFields.entrySet()) {
            Team team = entry.getKey();
            TextField field = entry.getValue();

            try {
                int points = Integer.parseInt(field.getText().trim());
                if (points < 0) points = 0;
                newStandings.put(team.getTeamId(), points);
                View.styleTextField(field);
            } catch (NumberFormatException e) {
                formatError = true;
                field.setStyle(View.TEXT_INPUT_STYLE + "-fx-border-color: red; -fx-border-width: 2;");
            }
        }

        if (formatError) {
            view.getMessageLabel().setText("ERROR: Invalid input. Please check highlighted fields.");
            view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");
            view.getSaveButton().setDisable(false);
            return;
        }

        TournamentStandingsPutDTO request = new TournamentStandingsPutDTO(newStandings);
        api.replaceTournamentStandings(currentTournament.getTournamentId(), request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                Platform.runLater(() -> {
                    view.getSaveButton().setDisable(false);
                    if (response.isSuccessful()) {
                        view.getMessageLabel().setText("SUCCESS: All points have been saved!");
                        view.getMessageLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        view.getMessageLabel().setText("ERROR: Server returned " + response.code());
                        view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                Platform.runLater(() -> {
                    view.getSaveButton().setDisable(false);
                    view.getMessageLabel().setText("Network Error: " + t.getMessage());
                    view.getMessageLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + "; -fx-font-weight: bold;");
                });
            }
        });
    }

    /**
     * Maps a list of {@link BracketDTO}s from the API to a list of {@link Bracket} domain objects.
     *
     * @param dtos The list of DTOs to map.
     * @return The list of domain objects.
     */
    private List<Bracket> mapBracketDTOs(List<BracketDTO> dtos) {
        List<Bracket> domainBrackets = new ArrayList<>();
        for (BracketDTO bDto : dtos) {
            Bracket bracket = new Bracket(bDto.getName(), bDto.getType());
            bracket.setBracketId(bDto.getBracketId());

            Map<Integer, Match> matchCache = new HashMap<>();
            List<RoundDTO> sortedRounds = new ArrayList<>(bDto.getRounds());
            sortedRounds.sort(Comparator.comparingInt(RoundDTO::getRoundId));

            for (RoundDTO rDto : sortedRounds) {
                Round round = new Round();
                round.setRoundId(rDto.getRoundId());
                for (MatchDTO mDto : rDto.getMatches()) {
                    Match match = mapMatchDTO(mDto, matchCache);
                    round.addMatch(match);
                }
                bracket.addRound(round);
            }
            domainBrackets.add(bracket);
        }
        return domainBrackets;
    }

    /**
     * Recursively maps a single {@link MatchDTO} to a {@link Match} domain object.
     * Uses a cache to ensure shared references for source matches are maintained.
     *
     * @param dto The Match DTO to map.
     * @param cache A map used to store and retrieve already created Match objects by ID.
     * @return The resulting {@link Match} domain object.
     */
    private Match mapMatchDTO(MatchDTO dto, Map<Integer, Match> cache) {
        if (dto == null) return null;

        Match match;
        if (cache.containsKey(dto.getMatchId())) {
            match = cache.get(dto.getMatchId());
        } else {
            match = new Match();
            match.setMatchId(dto.getMatchId());
            cache.put(dto.getMatchId(), match);
        }

        match.setComplete(dto.isComplete());

        if (dto.getTeam1() != null) {
            Team t = new Team(dto.getTeam1().getName());
            t.setTeamId(dto.getTeam1().getTeamId());
            match.setTeam1(t);
        }
        if (dto.getTeam2() != null) {
            Team t = new Team(dto.getTeam2().getName());
            t.setTeamId(dto.getTeam2().getTeamId());
            match.setTeam2(t);
        }
        if (dto.getWinner() != null) {
            Team t = new Team(dto.getWinner().getName());
            t.setTeamId(dto.getWinner().getTeamId());
            match.setWinner(t);
        }

        if (dto.getSourceMatch1() != null) {
            match.setSourceMatch1(mapMatchDTO(dto.getSourceMatch1(), cache));
        }
        if (dto.getSourceMatch2() != null) {
            match.setSourceMatch2(mapMatchDTO(dto.getSourceMatch2(), cache));
        }

        return match;
    }
}