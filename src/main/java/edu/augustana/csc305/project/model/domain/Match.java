package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a single match in the tournament management system.
 * A {@code Match} contains two competing teams, a court where it is played,
 * and a referee (now represented by a {@link User} with the REFEREE role) responsible
 * for overseeing the game and reporting the result.
 * <p>
 * This class is now fully reactive. When a winner is set on a source match,
 * this match will automatically update its corresponding team property.
 * </p>
 * JavaDoc made with Gemini 2.5 Flash and some parts helped by Gemini 2.5 Pro.
 */
public class Match {

    private final ObjectProperty<Team> team1;
    private final ObjectProperty<Team> team2;
    private final ObjectProperty<Team> winner;
    private final ObjectProperty<Court> court;
    private final ObjectProperty<User> referee;
    private final ObjectProperty<Match> sourceMatch1;
    private final ObjectProperty<Match> sourceMatch2;
    private final BooleanProperty isComplete;
    private int matchId;

    /**
     * The primary private constructor to initialize all match properties.
     * It now includes listeners to make the bracket reactive.
     * Listens in the class was recommended by Gemini 2.5 Flash to handle updating automatically.
     */
    private Match(Team team1, Team team2, Match sourceMatch1, Match sourceMatch2, Court court, User referee) {
        this.team1 = new SimpleObjectProperty<>(team1);
        this.team2 = new SimpleObjectProperty<>(team2);
        this.winner = new SimpleObjectProperty<>(null);
        this.court = new SimpleObjectProperty<>(court);
        this.referee = new SimpleObjectProperty<>(referee);
        this.isComplete = new SimpleBooleanProperty(false);
        this.sourceMatch1 = new SimpleObjectProperty<>(sourceMatch1);
        this.sourceMatch2 = new SimpleObjectProperty<>(sourceMatch2);

        if (sourceMatch1 != null) {
            sourceMatch1.winnerProperty().addListener((obs, oldWinner, newWinner) -> this.setTeam1(newWinner));
            if (sourceMatch1.getWinner() != null) this.setTeam1(sourceMatch1.getWinner());
        }
        if (sourceMatch2 != null) {
            sourceMatch2.winnerProperty().addListener((obs, oldWinner, newWinner) -> this.setTeam2(newWinner));
            if (sourceMatch2.getWinner() != null) this.setTeam2(sourceMatch2.getWinner());
        }
    }

    /**
     * Default no-argument constructor.
     * Initializes all properties to null/false. Useful for DTO mapping.
     */
    public Match() {
        this(null, null, null, null, null, null);
    }

    /**
     * Constructs a new FIRST-ROUND Match where the teams are known beforehand.
     *
     * @param team1   The first team competing.
     * @param team2   The second team competing.
     * @param court   The court for the match.
     * @param referee The referee for the match (must be a {@code User} with {@code REFEREE} role).
     */
    public Match(Team team1, Team team2, Court court, User referee) {
        this(team1, team2, null, null, court, referee);
    }

    /**
     * Constructs a new Match where one team is the winner of a source match
     * and the other team is predetermined (e.g., received a bye).
     *
     * @param sourceMatch The match providing the first team.
     * @param directTeam  The second team, which is already known.
     * @param court       The court for the match.
     * @param referee The referee for the match (must be a {@code User} with {@code REFEREE} role).
     */
    public Match(Match sourceMatch, Team directTeam, Court court, User referee) {
        this(null, directTeam, sourceMatch, null, court, referee);
    }

    /**
     * Constructs a new SUBSEQUENT-ROUND Match where the teams are determined
     * by the winners of two previous source matches.
     *
     * @param sourceMatch1 The match that will provide the first team.
     * @param sourceMatch2 The match that will provide the second team.
     * @param court        The court for the match.
     * @param referee The referee for the match (must be a {@code User} with {@code REFEREE} role).
     */
    public Match(Match sourceMatch1, Match sourceMatch2, Court court, User referee) {
        this(null, null, sourceMatch1, sourceMatch2, court, referee);
    }

    /**
     * Gets the unique ID of the match.
     *
     * @return The integer match ID.
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * Sets the unique ID of the match, typically after it has been persisted.
     *
     * @param matchId The database ID for the match.
     */
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * Gets the first team competing in the match.
     *
     * @return The {@code Team} object for team one.
     */
    public Team getTeam1() {
        return team1.get();
    }

    /**
     * Sets the first team competing in the match.
     *
     * @param team The {@code Team} object to set as team one.
     */
    public void setTeam1(Team team) {
        this.team1.set(team);
    }

    /**
     * Returns the object property for team one.
     *
     * @return The {@code ObjectProperty<Team>} for team one.
     */
    public ObjectProperty<Team> team1Property() {
        return team1;
    }

    /**
     * Gets the second team competing in the match.
     *
     * @return The {@code Team} object for team two.
     */
    public Team getTeam2() {
        return team2.get();
    }

    /**
     * Sets the second team competing in the match.
     *
     * @param team The {@code Team} object to set as team two.
     */
    public void setTeam2(Team team) {
        this.team2.set(team);
    }

    /**
     * Returns the object property for team two.
     *
     * @return The {@code ObjectProperty<Team>} for team two.
     */
    public ObjectProperty<Team> team2Property() {
        return team2;
    }

    /**
     * Gets the winning team of the match.
     *
     * @return The winning {@code Team}, or {@code null} if the match is not yet decided.
     */
    public Team getWinner() {
        return winner.get();
    }

    /**
     * Sets the winner of the match.
     *
     * @param team The {@code Team} that won the match.
     */
    public void setWinner(Team team) {
        this.winner.set(team);
    }

    /**
     * Returns the object property for the winner.
     *
     * @return The {@code ObjectProperty<Team>} for the winner.
     */
    public ObjectProperty<Team> winnerProperty() {
        return winner;
    }

    /**
     * Gets the court where the match is scheduled to be played.
     *
     * @return The {@code Court} object assigned to this match.
     */
    public Court getCourt() {
        return court.get();
    }

    /**
     * Sets or changes the court where the match will be played.
     *
     * @param court The {@code Court} to assign to this match.
     */
    public void setCourt(Court court) {
        this.court.set(court);
    }

    /**
     * Returns the object property for the court.
     *
     * @return The {@code ObjectProperty<Court>} for the court.
     */
    public ObjectProperty<Court> courtProperty() {
        return court;
    }

    /**
     * Gets the referee assigned to officiate the match.
     *
     * @return The {@code User} object for this match (must have {@code REFEREE} role).
     */
    public User getReferee() {
        return referee.get();
    }

    /**
     * Sets or changes the referee assigned to the match.
     *
     * @param referee The {@code User} to assign to this match.
     */
    public void setReferee(User referee) {
        this.referee.set(referee);
    }

    /**
     * Returns the object property for the referee.
     *
     * @return The {@code ObjectProperty<User>} for the referee.
     */
    public ObjectProperty<User> refereeProperty() {
        return referee;
    }

    /**
     * Checks if the match has been completed.
     *
     * @return {@code true} if the match is complete, {@code false} otherwise.
     */
    public boolean isComplete() {
        return isComplete.get();
    }

    /**
     * Sets the completion status of the match.
     *
     * @param complete The new completion status.
     */
    public void setComplete(boolean complete) {
        this.isComplete.set(complete);
    }

    /**
     * Returns the boolean property for the match's completion status.
     *
     * @return The {@code BooleanProperty} for the completion status.
     */
    public BooleanProperty isCompleteProperty() {
        return isComplete;
    }

    /**
     * Gets the first source match that provides a team for this match.
     *
     * @return The first source {@code Match}, or {@code null}.
     */
    public Match getSourceMatch1() {
        return sourceMatch1.get();
    }

    /**
     * Sets the first source match.
     *
     * @param match The source match.
     */
    public void setSourceMatch1(Match match) {
        this.sourceMatch1.set(match);
    }

    /**
     * Returns the object property for the first source match.
     *
     * @return The {@code ObjectProperty<Match>} for the first source match.
     */
    public ObjectProperty<Match> sourceMatch1Property() {
        return sourceMatch1;
    }

    /**
     * Gets the second source match that provides a team for this match.
     *
     * @return The second source {@code Match}, or {@code null}.
     */
    public Match getSourceMatch2() {
        return sourceMatch2.get();
    }

    /**
     * Sets the second source match.
     *
     * @param match The source match.
     */
    public void setSourceMatch2(Match match) {
        this.sourceMatch2.set(match);
    }

    /**
     * Returns the object property for the second source match.
     *
     * @return The {@code ObjectProperty<Match>} for the second source match.
     */
    public ObjectProperty<Match> sourceMatch2Property() {
        return sourceMatch2;
    }
}