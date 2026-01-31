package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for reading League data from the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class LeagueDTO {
    private int leagueId;
    private String name;

    public int getLeagueId() {
        return leagueId;
    }

    public String getName() {
        return name;
    }
}