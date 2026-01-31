package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.domain.Tournament;
import edu.augustana.csc305.project.userInterface.HomeView;

/**
 * Controller for the {@link HomeView}.
 *
 * <p>This class manages navigation from the home screen to various parts of the application
 * and handles UI element visibility based on the current user's role using the
 * {@link AuthenticationService}.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class HomeViewController extends ViewController {

    private final HomeView homeView;
    private final AuthenticationService authService;

    /**
     * Constructs a {@code HomeViewController}.
     * Initializes the view, retrieves the authentication service, and sets up the UI state and event handlers.
     *
     * @param view The {@link HomeView} instance this controller manages.
     * @param tournament The currently selected {@link Tournament} model.
     * @param appController The main {@link AppController} for delegation of navigation.
     */
    public HomeViewController(HomeView view, Tournament tournament, AppController appController) {
        super(view, tournament, appController);
        this.homeView = view;
        this.authService = appController.getAuthService();
        initialize();
        attachEvents();
    }

    /**
     * Initializes the view by setting button visibility and access based on the current user's role
     * retrieved from the {@link AuthenticationService}.
     */
    private void initialize() {
        homeView.updateUserAccess(authService.getCurrentUser().getRole());
    }

    /**
     * Attaches event handlers to all interactive components (buttons) in the {@link HomeView}.
     * All actions delegate navigation responsibility to the {@link AppController}.
     */
    @Override
    protected void attachEvents() {
        homeView.getManageTeamsButton().setOnAction(e -> appController.showTeamManagementView());
        homeView.getManageResourcesButton().setOnAction(e -> appController.showManageResourcesView());
        homeView.getViewBracketsButton().setOnAction(e -> appController.showBracketView());
        homeView.getGenerateBracketButton().setOnAction(e -> appController.showGenerateBracketView());
        homeView.getPointsEntryButton().setOnAction(e -> appController.showPointsEntryView());
        homeView.getSelectionViewButton().setOnAction(e -> appController.showSelectionView());
        homeView.getLogoutButton().setOnAction(e -> appController.logout());

        homeView.getManageUsersButton().setOnAction(e -> appController.showAdminUserManagementView());
    }
}