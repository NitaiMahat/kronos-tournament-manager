package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.*;
import edu.augustana.csc305.project.model.domain.*;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.GenerateBracketView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the {@link GenerateBracketView}.
 *
 * <p>This class handles the logic for requesting the API to generate a single-elimination
 * or round-robin bracket based on user selections and manages client-side validation
 * and data synchronization after generation.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class GenerateBracketViewController extends ViewController {

    private final GenerateBracketView view;
    private final KronosApi api;

    private static final String ERROR_COLOR_STYLE = "-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";";
    private static final String SUCCESS_COLOR_STYLE = "-fx-text-fill: " + View.ACCENT_COLOR + ";";

    /**
     * Constructs a {@code GenerateBracketViewController}.
     *
     * @param view The UI view associated with this controller.
     * @param tournament The currently active {@link Tournament}.
     * @param appController The main application controller for navigation.
     */
    public GenerateBracketViewController(GenerateBracketView view, Tournament tournament, AppController appController) {
        super(view, tournament, appController);
        this.view = view;
        this.api = ApiClient.getInstance().getKronosApi();
        initialize();
        attachEvents();
    }

    /**
     * Initializes the view components and ensures the local tournament data (teams, resources) is current.
     */
    private void initialize() {
        view.getSourceBracketComboBox().setItems(
                FXCollections.observableArrayList(tournament.getBrackets())
        );
        view.getSeededCheckBox().setVisible(true);
        view.getSourceBracketLabel().setVisible(false);
        view.getSourceBracketComboBox().setVisible(false);

        refreshTournamentData();
    }

    /**
     * Fetches the latest teams, courts, and referees from the API to ensure validation passes before generation.
     */
    private void refreshTournamentData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<List<TeamDTO>> teamsRes = api.getTeamsForTournament(tournament.getTournamentId()).execute();
                if (teamsRes.isSuccessful() && teamsRes.body() != null) {
                    List<Team> teams = new ArrayList<>();
                    for (TeamDTO dto : teamsRes.body()) {
                        Team t = new Team(dto.getName());
                        t.setTeamId(dto.getTeamId());
                        teams.add(t);
                    }
                    Platform.runLater(() -> tournament.getTeams().setAll(teams));
                }

                Response<List<CourtDTO>> courtsRes = api.getCourtsForTournament(tournament.getTournamentId()).execute();
                if (courtsRes.isSuccessful() && courtsRes.body() != null) {
                    List<Court> courts = new ArrayList<>();
                    for (CourtDTO dto : courtsRes.body()) {
                        Court c = new Court(dto.getName(), true);
                        c.setCourtId(dto.getCourtId());
                        courts.add(c);
                    }
                    Platform.runLater(() -> tournament.getCourts().setAll(courts));
                }

                Response<List<User>> refsRes = api.getRefereesForTournament(tournament.getTournamentId()).execute();
                if (refsRes.isSuccessful() && refsRes.body() != null) {
                    Platform.runLater(() -> tournament.getReferees().setAll(refsRes.body()));
                }

                return null;
            }
            @Override
            protected void failed() {
                System.err.println("Failed to refresh tournament data: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Attaches event handlers to UI components, including button actions and combo box listeners
     * to manage view visibility based on bracket format selection.
     */
    @Override
    protected void attachEvents() {
        view.getGenerateButton().setOnAction(e -> handleGenerateBracket());
        view.getBackButton().setOnAction(e -> appController.showHomeView());

        view.getFormatComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSingleElim = "Single Elimination".equals(newVal);
            view.getSeededCheckBox().setVisible(isSingleElim);
            if (!isSingleElim) {
                view.getSeededCheckBox().setSelected(false);
            }
        });

        view.getSeededCheckBox().selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            view.getSourceBracketLabel().setVisible(isSelected);
            view.getSourceBracketComboBox().setVisible(isSelected);
        });
    }

    /**
     * Executes the bracket generation process: performs client-side validation,
     * sends the request to the API, and handles success/failure feedback and navigation.
     */
    private void handleGenerateBracket() {
        String bracketName = view.getBracketNameField().getText().trim();

        if (bracketName.isEmpty()) {
            view.getFeedbackLabel().setStyle(ERROR_COLOR_STYLE);
            view.getFeedbackLabel().setText("Bracket name cannot be empty.");
            return;
        }

        if (tournament.getTeams().size() < 2) {
            view.getFeedbackLabel().setStyle(ERROR_COLOR_STYLE);
            view.getFeedbackLabel().setText("Need at least 2 teams to generate a bracket. (Current: " + tournament.getTeams().size() + ")");
            return;
        }

        String format = view.getFormatComboBox().getValue();
        BracketType type;
        Integer sourceBracketId = null;

        if ("Single Elimination".equals(format)) {
            type = BracketType.SINGLE_ELIMINATION;

            if (view.getSeededCheckBox().isSelected()) {
                Bracket source = view.getSourceBracketComboBox().getValue();
                if (source == null) {
                    view.getFeedbackLabel().setStyle(ERROR_COLOR_STYLE);
                    view.getFeedbackLabel().setText("Please select a source bracket for seeding.");
                    return;
                }
                sourceBracketId = source.getBracketId();

                type = BracketType.SINGLE_ELIMINATION_SEEDED;
            }
        } else if ("Round Robin".equals(format)) {
            type = BracketType.ROUND_ROBIN;
        } else {
            view.getFeedbackLabel().setStyle(ERROR_COLOR_STYLE);
            view.getFeedbackLabel().setText("Invalid format selected.");
            return;
        }

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Generating bracket...");
        view.getGenerateButton().setDisable(true);

        Task<Bracket> task = getBracketTask(bracketName, type, sourceBracketId);
        new Thread(task).start();
    }

    @NotNull
    private Task<Bracket> getBracketTask(String bracketName, BracketType type, Integer sourceBracketId) {
        BracketNewDTO requestDto = new BracketNewDTO(bracketName, type, tournament.getTournamentId(), sourceBracketId);

        return new Task<>() {
            @Override
            protected Bracket call() throws Exception {
                Response<BracketDTO> createRes = api.createBracket(requestDto).execute();
                if (!createRes.isSuccessful()) {
                    String errorMsg = createRes.errorBody() != null ? createRes.errorBody().string() : createRes.message();
                    throw new IOException("API Error (" + createRes.code() + "): " + errorMsg);
                }

                Response<List<BracketDTO>> listRes = api.getBracketsForTournament(tournament.getTournamentId()).execute();
                if (!listRes.isSuccessful() || listRes.body() == null) {
                    throw new IOException("Failed to reload brackets after generation.");
                }

                List<Bracket> updatedBrackets = new ArrayList<>();
                Bracket newlyCreatedBracket = null;

                for (BracketDTO bDto : listRes.body()) {
                    Bracket b = mapBracketDTO(bDto);
                    updatedBrackets.add(b);

                    if (createRes.body() != null && bDto.getBracketId() == createRes.body().getBracketId()) {
                        newlyCreatedBracket = b;
                    }
                }

                final List<Bracket> finalBrackets = updatedBrackets;
                Platform.runLater(() -> tournament.getBrackets().setAll(finalBrackets));

                return newlyCreatedBracket;
            }

            @Override
            protected void succeeded() {
                view.getGenerateButton().setDisable(false);
                view.getFeedbackLabel().setStyle(SUCCESS_COLOR_STYLE);
                view.getFeedbackLabel().setText("Bracket generated successfully!");

                Bracket newBracket = getValue();
                if (newBracket != null) {
                    appController.showBracketView(newBracket);
                } else {
                    appController.showBracketView();
                }
            }

            @Override
            protected void failed() {
                view.getGenerateButton().setDisable(false);
                view.getFeedbackLabel().setStyle(ERROR_COLOR_STYLE);
                view.getFeedbackLabel().setText("Generation failed: " + getException().getMessage());
                getException().printStackTrace();
            }
        };
    }

    /**
     * Maps a {@link BracketDTO} fetched from the API to a {@link Bracket} domain object.
     *
     * @param bDto The Bracket DTO to map.
     * @return The resulting {@link Bracket} domain object.
     */
    private Bracket mapBracketDTO(BracketDTO bDto) {
        Bracket bracket = new Bracket(bDto.getName(), bDto.getType());
        bracket.setBracketId(bDto.getBracketId());

        Map<Integer, Match> matchCache = new HashMap<>();

        for (RoundDTO rDto : bDto.getRounds()) {
            Round round = new Round();
            round.setRoundId(rDto.getRoundId());

            for (MatchDTO mDto : rDto.getMatches()) {
                Match match = mapMatchDTO(mDto, matchCache);
                round.addMatch(match);
            }
            bracket.addRound(round);
        }
        return bracket;
    }

    /**
     * Recursively maps a {@link MatchDTO} to a {@link Match} domain object.
     * Uses a cache to ensure shared references for source matches are maintained.
     *
     * @param dto The Match DTO to map.
     * @param cache A map used to store and retrieve already created Match objects by ID.
     * @return The resulting {@link Match} domain object.
     */
    private Match mapMatchDTO(MatchDTO dto, Map<Integer, Match> cache) {
        if (dto == null) return null;
        if (cache.containsKey(dto.getMatchId())) return cache.get(dto.getMatchId());

        Match match = new Match();
        match.setMatchId(dto.getMatchId());
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

        cache.put(dto.getMatchId(), match);
        return match;
    }
}