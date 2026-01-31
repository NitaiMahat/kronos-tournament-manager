package edu.augustana.csc305.project.model.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Represents a single round within a tournament bracket.
 * <p>
 * A round contains a list of matches. This class uses JavaFX properties to
 * enable seamless data binding with the user interface, allowing UI components
 * to automatically update when round data changes.
 * JavaDoc fot this class was made with Gemini 2.5 Flash.
 */
public class Round {

    private final ObservableList<Match> matches;
    private int roundId;

    /**
     * Constructs a new {@code Round}.
     * The round is initialized with an empty list of matches.
     */
    public Round() {
        this.matches = FXCollections.observableArrayList();
    }

    /**
     * Constructs a new {@code Round} with an initial list of matches.
     *
     * @param matches The initial list of matches for this round.
     */
    public Round(List<Match> matches) {
        this.matches = FXCollections.observableArrayList(matches);
    }

    /**
     * Gets the unique ID of the round.
     *
     * @return The integer round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Sets the unique ID of the round, typically after it has been persisted.
     *
     * @param roundId The database ID for the round.
     */
    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    /**
     * Returns the observable list of all matches in the round.
     *
     * @return An {@code ObservableList} of {@code Match} objects.
     */
    public ObservableList<Match> getMatches() {
        return matches;
    }

    /**
     * Adds a new match to the round.
     *
     * @param match The {@code Match} to add. Does nothing if the match is null.
     */
    public void addMatch(Match match) {
        if (match != null) {
            this.matches.add(match);
        }
    }
}