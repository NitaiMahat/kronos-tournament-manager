package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.LeagueDTO;
import edu.augustana.csc305.project.model.api.LeagueNewDTO;
import edu.augustana.csc305.project.model.api.TournamentDTO;
import edu.augustana.csc305.project.model.api.TournamentNewDTO;
import edu.augustana.csc305.project.model.domain.League;
import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.model.domain.User;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.SelectionView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the {@link SelectionView}.
 *
 * <p>Handles loading, selecting, creating, and deleting leagues and tournaments
 * by coordinating with the Kronos API and managing application state via the {@link AppController}.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class SelectionViewController extends ViewController {

    private final SelectionView view;
    private final AuthenticationService authService;
    private final KronosApi api;
    private final ObservableList<League> allLeagues = FXCollections.observableArrayList();
    private final ObservableList<Tournament> leagueTournaments = FXCollections.observableArrayList();

    private final ChangeListener<League> leagueSelectionListener;
    private final ChangeListener<League> leagueUpdateListener;

    /**
     * Constructs a {@code SelectionViewController}.
     *
     * @param view The {@link SelectionView} instance this controller manages.
     * @param appController The main {@link AppController} for delegation of navigation and state management.
     */
    public SelectionViewController(SelectionView view, AppController appController) {
        super(view, null, appController);
        this.view = view;
        this.authService = appController.getAuthService();
        this.api = ApiClient.getInstance().getKronosApi();

        leagueSelectionListener = (observable, oldLeague, newLeague) ->
                appController.setCurrentLeague(newLeague);

        leagueUpdateListener = (observable, oldLeague, newLeague) -> {
            leagueTournaments.clear();
            boolean isLeagueSelected = newLeague != null;
            view.getNewTournamentField().setDisable(!isLeagueSelected);
            view.getCreateTournamentButton().setDisable(!isLeagueSelected);

            if (isLeagueSelected) {
                loadTournaments(newLeague);
            }
        };

        initialize();
        attachEvents();
    }

    /**
     * Initializes the view by setting list views, loading leagues, and setting initial UI visibility
     * based on the current user's role.
     */
    private void initialize() {
        view.getLeagueListView().setItems(allLeagues);
        view.getTournamentListView().setItems(leagueTournaments);

        loadLeagues();
        User currentUser = authService.getCurrentUser();
        view.updateCreateVisibility(currentUser.getRole());
    }

    /**
     * Attaches event handlers and property listeners to the UI components.
     */
    @Override
    protected void attachEvents() {
        view.getLeagueListView().getSelectionModel().selectedItemProperty().addListener(leagueSelectionListener);
        appController.currentLeagueProperty().addListener(leagueUpdateListener);

        view.getSelectButton().setOnAction(e -> selectTournament());
        view.getViewStandingsButton().setOnAction(e -> viewStandings());
        view.getDeleteLeagueButton().setOnAction(e -> deleteLeague());
        view.getDeleteTournamentButton().setOnAction(e -> deleteTournament());

        view.getCreateTournamentButton().setOnAction(e -> createTournament());
        view.getCreateLeagueButton().setOnAction(e -> createLeague());
        view.getLogoutButton().setOnAction(e -> appController.logout());

        view.getTournamentListView().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) selectTournament();
        });

        view.getSelectButton().disableProperty().bind(view.getTournamentListView().getSelectionModel().selectedItemProperty().isNull());
        view.getDeleteTournamentButton().disableProperty().bind(view.getTournamentListView().getSelectionModel().selectedItemProperty().isNull());
        view.getViewStandingsButton().disableProperty().bind(view.getLeagueListView().getSelectionModel().selectedItemProperty().isNull());
        view.getDeleteLeagueButton().disableProperty().bind(view.getLeagueListView().getSelectionModel().selectedItemProperty().isNull());

        authService.currentUserProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) view.updateCreateVisibility(newUser.getRole());
        });
    }

    /**
     * Asynchronously loads all leagues from the Kronos API and updates the League ListView.
     */
    private void loadLeagues() {
        Task<List<League>> task = new Task<>() {
            @Override
            protected List<League> call() throws Exception {
                Response<List<LeagueDTO>> response = api.getAllLeagues().execute();
                if (!response.isSuccessful() || response.body() == null) throw new IOException("Failed to load leagues");

                return response.body().stream().map(dto -> {
                    League l = new League(dto.getName());
                    l.setLeagueID(dto.getLeagueId());
                    return l;
                }).collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                allLeagues.setAll(getValue());
                if (!allLeagues.isEmpty() && view.getLeagueListView().getSelectionModel().getSelectedItem() == null) {
                    view.getLeagueListView().getSelectionModel().selectFirst();
                } else if (allLeagues.isEmpty()) {
                    leagueTournaments.clear();
                }
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Error loading leagues: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Asynchronously loads all tournaments associated with the given league from the Kronos API.
     *
     * @param league The {@link League} whose tournaments should be loaded.
     */
    private void loadTournaments(League league) {
        Task<List<Tournament>> task = new Task<>() {
            @Override
            protected List<Tournament> call() throws Exception {
                Response<List<TournamentDTO>> response = api.getTournamentsByLeague(league.getLeagueID()).execute();
                if (!response.isSuccessful() || response.body() == null) throw new IOException("Failed to load tournaments");

                return response.body().stream().map(dto -> {
                    Tournament t = new Tournament(dto.getName());
                    t.setTournamentId(dto.getTournamentId());
                    t.setLeagueId(dto.getLeagueId());
                    return t;
                }).collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                leagueTournaments.setAll(getValue());
                if (!leagueTournaments.isEmpty()) {
                    view.getTournamentListView().getSelectionModel().selectFirst();
                }
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Error loading tournaments: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Attempts to create a new league using the name entered by the user.
     */
    private void createLeague() {
        String name = view.getNewLeagueField().getText().trim();
        if (name.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("League name cannot be empty.");
            return;
        }

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Creating league...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.createLeague(new LeagueNewDTO(name)).execute();
                if (!response.isSuccessful()) throw new IOException("Error: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getNewLeagueField().clear();
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("League created.");
                loadLeagues();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Creation failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Attempts to create a new tournament within the currently selected league.
     */
    private void createTournament() {
        League currentLeague = appController.getCurrentLeague();
        String name = view.getNewTournamentField().getText().trim();
        if (name.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Tournament name cannot be empty.");
            return;
        }

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Creating tournament...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.createTournament(new TournamentNewDTO(name, currentLeague.getLeagueID())).execute();
                if (!response.isSuccessful()) throw new IOException("Error: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getNewTournamentField().clear();
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Tournament created.");
                loadTournaments(currentLeague);
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Creation failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Deletes the currently selected league from the API and reloads the league list.
     */
    private void deleteLeague() {
        League selected = view.getLeagueListView().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Deleting league...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.deleteLeague(selected.getLeagueID()).execute();
                if (!response.isSuccessful()) throw new IOException("Delete failed: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("League deleted.");
                if (appController.getCurrentLeague() == selected) appController.setCurrentLeague(null);
                loadLeagues();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Delete failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Deletes the currently selected tournament from the API and reloads the tournament list for the current league.
     */
    private void deleteTournament() {
        Tournament selected = view.getTournamentListView().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Deleting tournament...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.deleteTournament(selected.getTournamentId()).execute();
                if (!response.isSuccessful()) throw new IOException("Delete failed: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Tournament deleted.");
                loadTournaments(appController.getCurrentLeague());
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Delete failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Selects the currently chosen tournament, loads its full details, sets it as the active tournament
     * in the {@link AppController}, and navigates to the Home View.
     */
    private void selectTournament() {
        Tournament selected = view.getTournamentListView().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Loading tournament details...");

        Task<Tournament> task = new Task<>() {
            @Override
            protected Tournament call() throws Exception {
                Response<TournamentDTO> response = api.getTournamentById(selected.getTournamentId()).execute();
                if (!response.isSuccessful() || response.body() == null) throw new IOException("Could not load full details.");

                TournamentDTO dto = response.body();

                Tournament t = new Tournament(dto.getName());
                t.setTournamentId(dto.getTournamentId());
                t.setLeagueId(dto.getLeagueId());

                return t;
            }

            @Override
            protected void succeeded() {
                Tournament full = getValue();
                appController.setCurrentTournament(full);
                appController.showHomeView();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Error opening tournament: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Navigates to the League Standings View for the currently selected league.
     */
    private void viewStandings() {
        League selectedLeague = view.getLeagueListView().getSelectionModel().getSelectedItem();
        if (selectedLeague != null) {
            appController.setCurrentLeague(selectedLeague);
            appController.showLeagueStandingsView();
        }
    }
}