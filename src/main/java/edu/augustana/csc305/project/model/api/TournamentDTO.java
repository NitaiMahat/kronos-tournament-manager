package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for reading Tournament data from the API.
 * Used for both list summaries and full detail views.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TournamentDTO {
    private int tournamentId;
    private String name;
    private int leagueId;

    public int getTournamentId() {
        return tournamentId;
    }

    public String getName() {
        return name;
    }

    public int getLeagueId() {
        return leagueId;
    }
}