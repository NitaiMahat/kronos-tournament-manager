package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for reading Team data from the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TeamDTO {
    private int teamId;
    private String name;

    public int getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }
}