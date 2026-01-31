package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.BracketDTO;
import edu.augustana.csc305.project.model.api.MatchDTO;
import edu.augustana.csc305.project.model.api.MatchUpdateDTO;
import edu.augustana.csc305.project.model.api.RoundDTO;
import edu.augustana.csc305.project.model.domain.*;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.BracketView;
import edu.augustana.csc305.project.userInterface.MatchDetailView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the {@link BracketView}.
 *
 * <p>This class manages interactions with the bracket visualization, including
 * fetching bracket data from the Kronos API, mapping DTOs to domain objects,
 * handling bracket selection, and managing match updates via pop-ups and API synchronization.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class BracketViewController extends ViewController {

    private final BracketView bracketView;
    private final AuthenticationService authService;
    private final KronosApi api;

    /**
     * Constructs a new BracketViewController.
     *
     * @param tournament The currently active {@link Tournament} whose brackets are being viewed.
     * @param appController The main application controller for navigation and state access.
     */
    public BracketViewController(Tournament tournament, AppController appController) {
        super(new BracketView(), tournament, appController);
        this.bracketView = (BracketView) view;
        this.authService = appController.getAuthService();
        this.api = ApiClient.getInstance().getKronosApi();
        initialize();
        attachEvents();
    }

    /**
     * Initializes the view by reloading bracket data and setting up interactivity.
     */
    private void initialize() {
        reloadTournamentData();
        bracketView.updateMatchNodeInteractivity(authService.getCurrentUser(), this::showMatchDetailPopup);
    }

    /**
     * Fetches bracket data from the API, maps the DTOs to {@link Bracket} domain objects,
     * and updates the tournament model.
     * This runs on a background thread.
     */
    private void reloadTournamentData() {
        Task<List<Bracket>> task = new Task<>() {
            @Override
            protected List<Bracket> call() throws Exception {
                Response<List<BracketDTO>> response = api.getBracketsForTournament(tournament.getTournamentId()).execute();
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("Failed to load brackets.");
                }

                List<BracketDTO> bracketDTOs = response.body();
                List<Bracket> domainBrackets = new ArrayList<>();

                for (BracketDTO bDto : bracketDTOs) {
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

            @Override
            protected void succeeded() {
                tournament.getBrackets().setAll(getValue());
                if (!tournament.getBrackets().isEmpty()) {
                    if (bracketView.getBracketSelector().getSelectionModel().isEmpty()) {
                        bracketView.getBracketSelector().getSelectionModel().selectFirst();
                    } else {
                        Bracket selected = bracketView.getBracketSelector().getSelectionModel().getSelectedItem();
                        bracketView.renderBracket(selected);
                    }
                }
            }

            @Override
            protected void failed() {
                System.err.println("Error reloading brackets: " + getException().getMessage());
                getException().printStackTrace();
            }
        };
        new Thread(task).start();
    }

    /**
     * Recursively maps a {@link MatchDTO} to a {@link Match} domain object.
     * Uses a cache to ensure shared references for source matches are maintained,
     * allowing for correct construction of the bracket tree.
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

    /**
     * Attaches event handlers to the UI components in the {@link BracketView}.
     * This includes listeners for bracket selection, navigation buttons, the back button,
     * and changes to the tournament's list of brackets.
     *
     * <p>This implementation received assistance from an AI model
     * (Gemini 2.5 Pro) for specific functionality/debugging.</p>
     */
    @Override
    protected void attachEvents() {
        bracketView.getBracketSelector().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bracketView.renderBracket(newVal);
                bracketView.updateMatchNodeInteractivity(authService.getCurrentUser(), this::showMatchDetailPopup);
            }
        });

        bracketView.getPreviousButton().setOnAction(e -> {
            ComboBox<Bracket> selector = bracketView.getBracketSelector();
            if (selector.getItems().isEmpty()) return;
            int currentIndex = selector.getSelectionModel().getSelectedIndex();
            int newIndex = (currentIndex - 1 + selector.getItems().size()) % selector.getItems().size();
            selector.getSelectionModel().select(newIndex);
        });

        bracketView.getNextButton().setOnAction(e -> {
            ComboBox<Bracket> selector = bracketView.getBracketSelector();
            if (selector.getItems().isEmpty()) return;
            int currentIndex = selector.getSelectionModel().getSelectedIndex();
            int newIndex = (currentIndex + 1) % selector.getItems().size();
            selector.getSelectionModel().select(newIndex);
        });

        bracketView.getBackButton().setOnAction(e -> appController.showHomeView());

        tournament.getBrackets().addListener((ListChangeListener<Bracket>) c -> {
            Bracket currentSelection = bracketView.getBracketSelector().getValue();
            bracketView.getBracketSelector().getItems().setAll(tournament.getBrackets());

            if (currentSelection != null && tournament.getBrackets().contains(currentSelection)) {
                bracketView.getBracketSelector().setValue(currentSelection);
            } else if (!tournament.getBrackets().isEmpty()) {
                bracketView.getBracketSelector().getSelectionModel().selectFirst();
            }
        });

        bracketView.setOnZoomRequested(this::handleZoom);
        bracketView.getResetZoomButton().setOnAction(e -> bracketView.resetZoom());
    }

    /**
     * Handles zoom operations on the bracket visualization.
     * Calculates the new scale and clamps it within the {@link View#MIN_SCALE} and {@link View#MAX_SCALE} bounds.
     *
     * @param zoomFactor The multiplicative factor for the zoom (e.g., 1.1 for zoom in).
     * <p>The Javadoc for this method was generated with the assistance of an AI model (Gemini 2.5).</p>
     */
    private void handleZoom(double zoomFactor) {
        double oldScale = bracketView.scaleProperty().get();
        double newScale = oldScale * zoomFactor;
        newScale = Math.max(View.MIN_SCALE, Math.min(View.MAX_SCALE, newScale));
        bracketView.setScale(newScale);
    }

    /**
     * Creates and displays a modal pop-up window containing the {@link MatchDetailView} for the given match.
     * After the pop-up closes, it triggers synchronization with the API and winner propagation.
     *
     * @param match The match to display details for.
     * <p>The structure and implementation of this method were developed with the assistance of an AI model
     *              (Gemini 2.5 Pro).</p>
     */
    private void showMatchDetailPopup(Match match) {
        MatchDetailView detailView = new MatchDetailView(match, authService.getCurrentUser());

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Match Details");
        popupStage.setScene(new Scene(detailView));

        popupStage.showAndWait();

        syncMatchUpdate(match);

        if (match.getWinner() != null) {
            propagateWinnerToNextMatch(match);
        }

        bracketView.renderBracket(bracketView.getBracketSelector().getValue());
    }

    /**
     * Sends a PATCH request to the API to update the match details (winner, teams, etc.).
     * This operation runs on a background thread.
     *
     * @param match The {@link Match} domain object containing the updated data.
     * <p>This method's implementation, including logic for database synchronization, received assistance from an AI model
     *              (Gemini 2.5 Pro).</p>
     */
    private void syncMatchUpdate(Match match) {
        MatchUpdateDTO updateDTO = new MatchUpdateDTO();
        if (match.getTeam1() != null) updateDTO.setTeam1Id(match.getTeam1().getTeamId());
        if (match.getTeam2() != null) updateDTO.setTeam2Id(match.getTeam2().getTeamId());
        if (match.getWinner() != null) updateDTO.setWinnerId(match.getWinner().getTeamId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.updateMatch(match.getMatchId(), updateDTO).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to sync match update: " + response.message());
                }
                return null;
            }
            @Override
            protected void failed() {
                System.err.println("API Update Error: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Propagates the winner of the current match locally to the subsequent match in the bracket tree,
     * and then triggers an API sync for the affected next match.
     *
     * @param match The match that was just completed and whose winner needs to be propagated.
     */
    private void propagateWinnerToNextMatch(Match match) {
        Bracket currentBracket = bracketView.getBracketSelector().getValue();
        if (currentBracket == null) return;

        for (Round round : currentBracket.getRounds()) {
            for (Match nextMatch : round.getMatches()) {
                boolean wasUpdated = false;

                if (match.equals(nextMatch.getSourceMatch1())) {
                    nextMatch.setTeam1(match.getWinner());
                    wasUpdated = true;
                }
                if (match.equals(nextMatch.getSourceMatch2())) {
                    nextMatch.setTeam2(match.getWinner());
                    wasUpdated = true;
                }

                if (wasUpdated) {
                    syncMatchUpdate(nextMatch);
                }
            }
        }
    }
}