package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Represents a sports league, managing its name, associated tournaments,
 * and team standings.
 * <p>
 * This class uses JavaFX properties and collections for easy integration
 * with a graphical user interface (GUI), allowing for automatic updates
 * when data changes.
 * </p>
 * JavaDoc by gemini 2.5 Flash.
 */
public class League {

    /**
     * The name of the league, encapsulated in a JavaFX StringProperty for binding in a GUI.
     */
    private final StringProperty leagueName;

    /**
     * A list of {@link Tournament} objects associated with this league.
     * It is an ObservableList for GUI integration.
     */
    private final ObservableList<Tournament> tournaments;

    /**
     * A map representing the standings for the league.
     * The key is a {@link Team} and the value is the team's total points or score.
     * It is an ObservableMap for GUI integration.
     */
    private final ObservableMap<Team, Integer> standings;

    /**
     * The unique identifier for this league.
     */
    private int leagueID;

    /**
     * Constructs a new League with the specified name.
     * Initializes all JavaFX properties and observable collections.
     *
     * @param leagueName The display name of the league.
     */
    public League(String leagueName) {
        this.leagueName = new SimpleStringProperty(leagueName);
        this.tournaments = FXCollections.observableArrayList();
        this.standings = FXCollections.observableHashMap();
    }

    /**
     * Retrieves the unique identifier for the league.
     *
     * @return The unique integer ID of the league.
     */
    public int getLeagueID() {
        return leagueID;
    }

    /**
     * Sets the unique identifier for the league.
     * This is typically called by the DAO after the league is saved to the database.
     *
     * @param leagueId The new unique integer ID for the league.
     */
    public void setLeagueID(int leagueId) {
        this.leagueID = leagueId;
    }

    /**
     * Returns the JavaFX StringProperty for the league name, which can
     * be used for binding to GUI elements.
     *
     * @return The {@code StringProperty} of the league name.
     */
    public StringProperty getLeagueNameProperty() {
        return leagueName;
    }

    /**
     * Returns the current name of the league.
     *
     * @return The league's name as a {@code String}.
     */
    public String getLeagueName() {
        return leagueName.get();
    }

    /**
     * Sets the name of the league.
     *
     * @param leagueName The new name for the league.
     */
    public void setLeagueName(String leagueName) {
        this.leagueName.set(leagueName);
    }

    /**
     * Returns the ObservableList of tournaments associated with this league.
     *
     * @return An {@code ObservableList<Tournament>} containing all tournaments in the league.
     */
    public ObservableList<Tournament> getTournaments() {
        return tournaments;
    }

    /**
     * Adds a new tournament to the league's list of tournaments.
     *
     * @param tournament The {@link Tournament} to be added.
     */
    public void addTournament(Tournament tournament) {
        tournaments.add(tournament);
    }

    /**
     * Removes a specified tournament from the league's list of tournaments.
     *
     * @param tournament The {@link Tournament} to be removed.
     */
    public void removeTournament(Tournament tournament) {
        tournaments.remove(tournament);
    }

    /**
     * Returns the ObservableMap representing the current standings of the league.
     * The map keys are {@link Team} objects and the values are their integer scores/points.
     *
     * @return An {@code ObservableMap<Team, Integer>} of the league standings.
     */
    public ObservableMap<Team, Integer> getStandings() {
        return standings;
    }
}