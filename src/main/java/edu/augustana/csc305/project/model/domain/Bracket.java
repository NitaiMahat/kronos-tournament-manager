package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a single tournament bracket in the system.
 * <p>
 * Each bracket contains metadata such as its name, completion status,
 * and a list of associated rounds. The class uses JavaFX properties to allow
 * seamless data binding with the user interface.
 * This Classes JavaDoc was made with Gemini 2.5 Pro.
 */
public class Bracket {

    private final ObservableList<Round> rounds;
    private final StringProperty bracketName;
    private final BooleanProperty isComplete;
    private final BracketType bracketType;
    private int bracketId;

    /**
     * Constructs a new {@code Bracket} with the specified name and type.
     * The bracket is initialized as incomplete and without any rounds.
     *
     * @param bracketName the name of the bracket
     * @param bracketType the format type of the bracket
     */
    public Bracket(String bracketName, BracketType bracketType) {
        this.bracketName = new SimpleStringProperty(bracketName);
        this.isComplete = new SimpleBooleanProperty(false);
        this.rounds = FXCollections.observableArrayList();
        this.bracketType = bracketType;
    }

    /**
     * Constructs a new {@code Bracket} with the specified name.
     * The bracket type defaults to {@link BracketType#SINGLE_ELIMINATION}.
     *
     * @param bracketName the name of the bracket
     */
    public Bracket(String bracketName) {
        this(bracketName, BracketType.SINGLE_ELIMINATION);
    }

    /**
     * Gets the unique ID of the bracket.
     *
     * @return The integer bracket ID.
     */
    public int getBracketId() {
        return bracketId;
    }

    /**
     * Sets the unique ID of the bracket, typically after it has been persisted.
     *
     * @param bracketId The database ID for the bracket.
     */
    public void setBracketId(int bracketId) {
        this.bracketId = bracketId;
    }

    /**
     * Gets the display name of the bracket.
     *
     * @return The name of the bracket.
     */
    public String getBracketName() {
        return bracketName.get();
    }

    /**
     * Sets the display name of the bracket.
     *
     * @param name The new name for the bracket.
     */
    public void setBracketName(String name) {
        this.bracketName.set(name);
    }

    /**
     * Returns the property representing the bracket's name.
     *
     * @return The {@code StringProperty} for the name.
     */
    public StringProperty bracketNameProperty() {
        return bracketName;
    }

    /**
     * Checks if the bracket is complete.
     *
     * @return {@code true} if complete, {@code false} otherwise.
     */
    public boolean isComplete() {
        return isComplete.get();
    }

    /**
     * Sets the completion status of the bracket.
     *
     * @param complete The new completion status.
     */
    public void setComplete(boolean complete) {
        this.isComplete.set(complete);
    }

    /**
     * Returns the property representing the bracket's completion status.
     *
     * @return The {@code BooleanProperty} for the completion status.
     */
    public BooleanProperty isCompleteProperty() {
        return isComplete;
    }

    /**
     * Returns the observable list of all rounds in the bracket.
     *
     * @return An {@code ObservableList} of {@code Round} objects.
     */
    public ObservableList<Round> getRounds() {
        return rounds;
    }

    /**
     * Gets the format type of the bracket.
     *
     * @return The {@code BracketType} (e.g., SINGLE_ELIMINATION, ROUND_ROBIN).
     */
    public BracketType getBracketType() {
        return bracketType;
    }

    /**
     * Adds a new round to the bracket.
     *
     * @param round The {@code Round} to add.
     */
    public void addRound(Round round) {
        if (round != null) {
            this.rounds.add(round);
        }
    }
}