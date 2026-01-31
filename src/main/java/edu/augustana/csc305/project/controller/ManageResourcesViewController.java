package edu.augustana.csc305.project.controller;

import edu.augustana.csc305.project.model.api.*;
import edu.augustana.csc305.project.model.domain.*;
import edu.augustana.csc305.project.service.ApiClient;
import edu.augustana.csc305.project.service.KronosApi;
import edu.augustana.csc305.project.userInterface.ManageResourcesView;
import edu.augustana.csc305.project.userInterface.View;
import javafx.concurrent.Task;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the {@link ManageResourcesView}.
 *
 * <p>Handles the application logic for managing the **Courts** and **Referees** assigned to the current {@link Tournament}
 * via the Kronos API.
 * All data fetching and modification operations are performed asynchronously using JavaFX {@link Task}s.</p>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class ManageResourcesViewController extends ViewController {

    private final ManageResourcesView view;
    private final KronosApi api;

    /**
     * Constructs a {@code ManageResourcesViewController}.
     *
     * @param view The {@link ManageResourcesView} instance this controller manages.
     * @param tournament The currently selected {@link Tournament} model.
     * @param appController The main {@link AppController} for delegation of navigation.
     */
    public ManageResourcesViewController(ManageResourcesView view, Tournament tournament, AppController appController) {
        super(view, tournament, appController);
        this.view = view;
        this.api = ApiClient.getInstance().getKronosApi();
        initialize();
        attachEvents();
    }

    /**
     * Initializes the view by loading the current data from the API.
     */
    private void initialize() {
        loadData();
    }

    /**
     * Triggers the asynchronous loading of both courts and referees for the current tournament.
     */
    private void loadData() {
        loadCourts();
        loadReferees();
    }

    /**
     * Asynchronously loads the list of {@link Court}s associated with the current tournament from the API.
     */
    private void loadCourts() {
        Task<List<CourtDTO>> task = new Task<>() {
            @Override
            protected List<CourtDTO> call() throws Exception {
                Response<List<CourtDTO>> response = api.getCourtsForTournament(tournament.getTournamentId()).execute();
                if (!response.isSuccessful() || response.body() == null) throw new IOException("Failed to load courts");
                return response.body();
            }

            @Override
            protected void succeeded() {
                List<CourtDTO> dtos = getValue();
                tournament.getCourts().clear();
                for (CourtDTO dto : dtos) {
                    Court c = new Court(dto.getName(), true);
                    c.setCourtId(dto.getCourtId());
                    tournament.addCourt(c);
                }
                view.getCourtsListView().setItems(tournament.getCourts());
            }

            @Override
            protected void failed() {
                System.err.println("Error loading courts: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Asynchronously loads the list of {@link User}s (referees) associated with the current tournament from the API.
     */
    private void loadReferees() {
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                Response<List<User>> response = api.getRefereesForTournament(tournament.getTournamentId()).execute();
                if (!response.isSuccessful() || response.body() == null) throw new IOException("Failed to load referees");
                return response.body();
            }

            @Override
            protected void succeeded() {
                tournament.getReferees().setAll(getValue());
                view.getRefereesListView().setItems(tournament.getReferees());
            }

            @Override
            protected void failed() {
                System.err.println("Error loading referees: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Attaches event handlers to all interactive components in the view.
     */
    @Override
    protected void attachEvents() {
        view.getAddCourtButton().setOnAction(e -> handleAddCourt());
        view.getRemoveCourtButton().setOnAction(e -> handleRemoveCourt());
        view.getAddRefereeButton().setOnAction(e -> handleAddReferee());
        view.getRemoveRefereeButton().setOnAction(e -> handleRemoveReferee());
        view.getBackButton().setOnAction(e -> appController.showHomeView());
    }

    /**
     * Handles the logic for adding a new court to the current tournament via the API.
     */
    private void handleAddCourt() {
        String courtName = view.getNewCourtField().getText().trim();
        if (courtName.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Court name cannot be empty.");
            return;
        }

        view.getFeedbackLabel().setText("Adding court...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                CourtNewDTO dto = new CourtNewDTO(courtName, tournament.getTournamentId());
                Response<Void> response = api.createCourtInTournament(tournament.getTournamentId(), dto).execute();
                if (!response.isSuccessful()) throw new IOException("Error: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getNewCourtField().clear();
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Court '" + courtName + "' added.");
                loadCourts();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Failed to add court: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Handles the logic for removing a selected court from the current tournament via the API.
     */
    private void handleRemoveCourt() {
        Court selected = view.getCourtsListView().getSelectionModel().getSelectedItem();
        if (selected == null) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Please select a court to remove.");
            return;
        }

        view.getFeedbackLabel().setText("Removing court...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.deleteCourtFromTournament(tournament.getTournamentId(), selected.getCourtId()).execute();
                if (!response.isSuccessful()) throw new IOException("Error: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Court removed.");
                loadCourts();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Failed to remove court: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Handles the logic for assigning an existing user (who must be a REFEREE) to the current tournament via the API.
     */
    private void handleAddReferee() {
        String username = view.getNewRefereeField().getText().trim();

        if (username.isEmpty()) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Referee username is required.");
            return;
        }

        view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.TEXT_COLOR + ";");
        view.getFeedbackLabel().setText("Searching for referee...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<List<User>> usersRes = api.getAllUsers(null).execute();

                if (!usersRes.isSuccessful() || usersRes.body() == null) {
                    throw new IOException("Failed to search users.");
                }

                Optional<User> foundUser = usersRes.body().stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(username))
                        .findFirst();

                if (foundUser.isEmpty()) {
                    throw new IOException("User '" + username + "' not found.");
                }

                User user = foundUser.get();
                if (user.getRole() != UserRole.REFEREE) {
                    throw new IOException("User '" + username + "' exists but is not a REFEREE.");
                }

                RefereeNewDTO refDto = new RefereeNewDTO(user.getUserId());
                Response<Void> linkRes = api.addRefereeToTournament(tournament.getTournamentId(), refDto).execute();

                if (!linkRes.isSuccessful()) {
                    if (linkRes.code() == 409) {
                        throw new IOException("Referee is already assigned to this tournament.");
                    }
                    throw new IOException("Failed to add referee: " + linkRes.message());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                view.getNewRefereeField().clear();
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Referee added successfully.");
                loadReferees();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText(getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    /**
     * Handles the logic for removing a selected referee from the current tournament via the API.
     */
    private void handleRemoveReferee() {
        User selected = view.getRefereesListView().getSelectionModel().getSelectedItem();
        if (selected == null) {
            view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
            view.getFeedbackLabel().setText("Please select a referee to remove.");
            return;
        }

        view.getFeedbackLabel().setText("Removing referee...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response<Void> response = api.removeRefereeFromTournament(tournament.getTournamentId(), selected.getUserId()).execute();
                if (!response.isSuccessful()) throw new IOException("Error: " + response.message());
                return null;
            }

            @Override
            protected void succeeded() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.ACCENT_COLOR + ";");
                view.getFeedbackLabel().setText("Referee removed.");
                loadReferees();
            }

            @Override
            protected void failed() {
                view.getFeedbackLabel().setStyle("-fx-text-fill: " + View.WARNING_COLOR_HOVER + ";");
                view.getFeedbackLabel().setText("Failed to remove referee: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }
}