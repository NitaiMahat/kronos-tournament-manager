package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Represents a tournament containing teams, referees, courts, and brackets.
 * <p>
 * This class uses JavaFX properties for data binding with UI components,
 * allowing automatic updates when data changes.
 * JavaDoc by Gemini 2.5 Pro.
 */
public class Tournament {

    private final StringProperty tournamentName;
    private final ObservableList<Bracket> brackets;
    private final ObservableList<User> referees;
    private final ObservableList<Court> courts;
    private final ObservableList<Team> teams;
    private int tournamentId;
    private int leagueId;

    /**
     * Constructs a new {@code Tournament} with the specified name.
     * Initializes JavaFX properties and observable lists for related data.
     *
     * @param tournamentName the display name of the tournament
     */
    public Tournament(String tournamentName) {
        this.tournamentName = new SimpleStringProperty(tournamentName);
        this.brackets = FXCollections.observableArrayList();
        this.referees = FXCollections.observableArrayList();
        this.courts = FXCollections.observableArrayList();
        this.teams = FXCollections.observableArrayList();
    }

    /**
     * Gets the unique ID of the tournament.
     *
     * @return The integer tournament ID.
     */
    public int getTournamentId() {
        return tournamentId;
    }

    /**
     * Sets the unique ID of the tournament, typically after it has been persisted.
     *
     * @param tournamentId The database ID for the tournament.
     */
    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    /**
     * Gets the display name of the tournament.
     *
     * @return The name of the tournament.
     */
    public String getTournamentName() {
        return tournamentName.get();
    }

    /**
     * Sets the display name of the tournament.
     *
     * @param name The new name for the tournament.
     */
    public void setTournamentName(String name) {
        this.tournamentName.set(name);
    }

    /**
     * Returns the property representing the tournament's name.
     *
     * @return The {@code StringProperty} of the tournament name.
     */
    public StringProperty tournamentNameProperty() {
        return tournamentName;
    }

    /**
     * Returns the observable list of all teams in the tournament.
     *
     * @return An {@code ObservableList} of {@code Team} objects.
     */
    public ObservableList<Team> getTeams() {
        return teams;
    }

    /**
     * Attempts to add a team to the tournament.
     *
     * @param team the {@code Team} object to add
     * @return {@code true} if the team was successfully added
     */
    public boolean addTeam(Team team) {
        teams.add(team);
        return true;
    }

    /**
     * Removes a team from the tournament.
     *
     * @param team The team to be removed.
     */
    public void removeTeam(Team team) {
        teams.remove(team);
    }

    /**
     * Returns the observable list of all brackets in the tournament.
     *
     * @return An {@code ObservableList} of {@code Bracket} objects.
     */
    public ObservableList<Bracket> getBrackets() {
        return brackets;
    }

    /**
     * Adds a bracket to the tournament.
     *
     * @param bracket The {@code Bracket} object to add.
     */
    public void addBracket(Bracket bracket) {
        if (bracket != null) {
            brackets.add(bracket);
        }
    }

    /**
     * Returns the observable list of all referees in the tournament.
     *
     * @return An {@code ObservableList} of {@code User} objects.
     */
    public ObservableList<User> getReferees() {
        return referees;
    }

    /**
     * Adds a referee to the tournament.
     *
     * @param referee The {@code User} object to add (must have {@code REFEREE} role).
     */
    public void addReferee(User referee) {
        if (referee != null) {
            referees.add(referee);
        }
    }

    /**
     * Returns the observable list of all courts in the tournament.
     *
     * @return An {@code ObservableList} of {@code Court} objects.
     */
    public ObservableList<Court> getCourts() {
        return courts;
    }

    /**
     * Adds a court to the tournament.
     *
     * @param court The {@code Court} object to add.
     */
    public void addCourt(Court court) {
        if (court != null) {
            courts.add(court);
        }
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }
}