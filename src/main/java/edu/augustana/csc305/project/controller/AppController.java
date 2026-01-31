package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.domain.Bracket;
import edu.augustana.csc305.project.model.domain.League;
import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.model.domain.UserRole;
import edu.augustana.csc305.project.userInterface.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main application controller responsible for managing application state,
 * handling navigation between different views (screens), and coordinating the
 * application flow. It holds references to the primary Stage, the
 * AuthenticationService, and the currently active {@link Tournament} and {@link League}
 * using JavaFX properties for reactive UI updates.
 * The persistence layer now relies on API calls, managed by individual view controllers.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class AppController {

    private final Stage primaryStage;
    private final AuthenticationService authService;
    private final ObjectProperty<Tournament> currentTournament = new SimpleObjectProperty<>();
    private final ObjectProperty<League> currentLeague = new SimpleObjectProperty<>();

    // Standardized window dimensions for different views
    private static final double LARGE_WIDTH = 1200;
    private static final double LARGE_HEIGHT = 800;
    private static final double MEDIUM_WIDE_WIDTH = 900;
    private static final double MEDIUM_HEIGHT = 600;
    private static final double MEDIUM_WIDTH = 800;
    private static final double SMALL_WIDTH = 500;
    private static final double SMALL_HEIGHT = 550;

    /**
     * Constructs the main application controller.
     * Initializes the AuthenticationService.
     *
     * @param primaryStage The main window stage of the JavaFX application.
     */
    public AppController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.authService = new AuthenticationService();
    }

    /**
     * Constructs the application controller and sets an initial active tournament.
     *
     * @param primaryStage The main window stage of the JavaFX application.
     * @param tournament The {@link Tournament} to set as the initial current tournament.
     */
    public AppController(Stage primaryStage, Tournament tournament) {
        this(primaryStage);
        setCurrentTournament(tournament);
    }

    /**
     * Gets the currently active tournament.
     * @return The current {@link Tournament} object.
     */
    public final Tournament getCurrentTournament() { return currentTournament.get(); }

    /**
     * Sets the currently active tournament and updates the property.
     * @param tournament The new {@link Tournament} object.
     */
    public final void setCurrentTournament(Tournament tournament) { this.currentTournament.set(tournament); }

    /**
     * Gets the currently active league.
     * @return The current {@link League} object.
     */
    public final League getCurrentLeague() { return this.currentLeague.get(); }

    /**
     * Sets the currently active league and updates the property.
     * @param newLeague The new {@link League} object.
     */
    public final void setCurrentLeague(League newLeague) { this.currentLeague.set(newLeague); }

    /**
     * Returns the property object for the current league, allowing listeners to track changes.
     * @return The {@link ObjectProperty} of {@link League}.
     */
    public final ObjectProperty<League> currentLeagueProperty() { return currentLeague; }

    /**
     * Returns the property object for the current tournament, allowing listeners to track changes.
     * @return The {@link ObjectProperty} of {@link Tournament}.
     */
    public final ObjectProperty<Tournament> currentTournamentProperty() { return currentTournament; }

    /**
     * Starts the application by displaying the initial login view.
     */
    public void startApp() {
        showLoginView();
        primaryStage.show();
    }

    /**
     * Updates the scene on the primary stage.
     * If the stage already has a scene (meaning the window is open), it reuses the current
     * dimensions (width/height) to prevent the window from resizing abruptly.
     * If it is the first time (no scene), it uses the provided default dimensions.
     *
     * @param view          The new view (Root node) to display.
     * @param title         The window title.
     * @param defaultWidth  The default width to use if no window is currently open.
     * @param defaultHeight The default height to use if no window is currently open.
     */
    private void updateScene(Parent view, String title, double defaultWidth, double defaultHeight) {
        double width = defaultWidth;
        double height = defaultHeight;

        if (primaryStage.getScene() != null) {
            width = primaryStage.getScene().getWidth();
            height = primaryStage.getScene().getHeight();
        }

        Scene scene = new Scene(view, width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
    }

    /**
     * Navigates to the Login View.
     */
    public void showLoginView() {
        LoginView view = new LoginView();
        new LoginViewController(view, this, authService);
        updateScene(view, "Login - Tournament Manager", LARGE_WIDTH, LARGE_HEIGHT);
    }

    /**
     * Navigates to the Selection View for choosing or creating a tournament.
     */
    public void showSelectionView() {
        SelectionView view = new SelectionView();
        new SelectionViewController(view, this);
        updateScene(view, "Select or Create Tournament", MEDIUM_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Logs out the current user and navigates back to the Login View.
     */
    public void logout() {
        authService.logout();
        showLoginView();
    }

    /**
     * Navigates to the Home View for the currently active tournament.
     * If no tournament is active, it redirects to the Selection View.
     */
    public void showHomeView() {
        if (getCurrentTournament() == null) {
            showSelectionView();
            return;
        }
        HomeView view = new HomeView();
        new HomeViewController(view, getCurrentTournament(), this);
        updateScene(view, "Home - " + getCurrentTournament().getTournamentName(), MEDIUM_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Navigates to the Bracket View, showing the default or main bracket.
     */
    public void showBracketView() {
        showBracketView(null);
    }

    /**
     * Navigates to the Bracket View, optionally highlighting a specific bracket.
     *
     * @param bracketToShow The specific {@link Bracket} to display or focus on (can be null).
     */
    public void showBracketView(Bracket bracketToShow) {
        if (getCurrentTournament() == null) return;
        BracketViewController controller = new BracketViewController(getCurrentTournament(), this);
        View bracketView = controller.getView();
        updateScene(bracketView, "Bracket - " + getCurrentTournament().getTournamentName(), LARGE_WIDTH, LARGE_HEIGHT);
    }

    /**
     * Navigates to the Team Management View for the current tournament.
     */
    public void showTeamManagementView() {
        if (getCurrentTournament() == null) return;
        TeamManagementView view = new TeamManagementView();
        new TeamManagementViewController(view, getCurrentTournament(), this);
        updateScene(view, "Manage Teams", MEDIUM_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Navigates to the Manage Resources View for the current tournament.
     */
    public void showManageResourcesView() {
        if (getCurrentTournament() == null) return;
        ManageResourcesView view = new ManageResourcesView();
        new ManageResourcesViewController(view, getCurrentTournament(), this);
        updateScene(view, "Manage Resources", MEDIUM_WIDE_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Navigates to the Generate Bracket View for the current tournament.
     */
    public void showGenerateBracketView() {
        if (getCurrentTournament() == null) return;
        GenerateBracketView view = new GenerateBracketView();
        new GenerateBracketViewController(view, getCurrentTournament(), this);
        updateScene(view, "Generate Bracket", SMALL_WIDTH, SMALL_HEIGHT);
    }

    /**
     * Navigates to the Points Entry View for the current tournament.
     */
    public void showPointsEntryView() {
        if (getCurrentTournament() == null) return;
        PointsEntryView view = new PointsEntryView();
        new PointsEntryViewController(view, getCurrentTournament(), this);
        updateScene(view, "Edit Points", SMALL_WIDTH, SMALL_HEIGHT);
    }

    /**
     * Navigates to the League Standings View for the currently active league.
     * If no league is active, it redirects to the Selection View.
     */
    public void showLeagueStandingsView() {
        if (getCurrentLeague() == null) {
            showSelectionView();
            return;
        }
        LeagueStandingsView view = new LeagueStandingsView();
        new LeagueStandingsViewController(view, this);
        updateScene(view, "League Standings - " + getCurrentLeague().getLeagueName(), MEDIUM_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Navigates to the Admin User Management View.
     * Only allows access if the current user is an ADMIN.
     */
    public void showAdminUserManagementView() {
        if (authService.getCurrentUser().getRole() != UserRole.ADMIN) {
            System.out.println("Access Denied: Admin role required.");
            return;
        }

        AdminUserManagementView view = new AdminUserManagementView();
        new AdminUserManagementViewController(view, this);
        updateScene(view, "Admin - User Management", LARGE_WIDTH, LARGE_HEIGHT);
    }

    /**
     * Returns the authentication service instance.
     * @return The {@link AuthenticationService} used by the application.
     */
    public AuthenticationService getAuthService() { return authService; }

    /**
     * Returns the primary stage of the application.
     * @return The main {@link Stage}.
     */
    public Stage getPrimaryStage() { return primaryStage; }
}