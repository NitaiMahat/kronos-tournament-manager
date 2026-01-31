package edu.augustana.csc305.project.model.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a single team in the tournament. This class uses JavaFX
 * properties to allow for easy data binding with the user interface.
 * JavaDoc by Gemini 2.5 Pro.
 */
public class Team {

    private final StringProperty teamName;
    private int teamId;

    /**
     * Constructs a new Team with a specified name.
     *
     * @param teamName The name of the team.
     */
    public Team(String teamName) {
        this.teamName = new SimpleStringProperty(teamName);
    }


    /**
     * Gets the unique ID of the team.
     *
     * @return The integer team ID.
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Sets the unique ID of the team, typically after it has been persisted.
     *
     * @param teamId The database ID for the team.
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets the current name of the team.
     *
     * @return the team name as a String.
     */
    public final String getTeamName() {
        return teamName.get();
    }

    /**
     * Sets the name of the team.
     *
     * @param teamName the new name for the team.
     */
    public final void setTeamName(String teamName) {
        this.teamName.set(teamName);
    }

    /**
     * Returns the StringProperty for the team name, essential for data binding.
     *
     * @return the teamName StringProperty.
     */
    public StringProperty teamNameProperty() {
        return teamName;
    }

    /**
     * Returns the name of the team.
     *
     * @return the team's name.
     */
    @Override
    public String toString() {
        return getTeamName();
    }
}
