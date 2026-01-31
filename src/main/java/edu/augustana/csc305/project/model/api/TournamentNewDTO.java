package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for creating a new Tournament via the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class TournamentNewDTO {
    private final String name;
    private final int leagueId;

    /**
     * Constructs a DTO for creating a new tournament.
     *
     * @param name The name of the new tournament.
     * @param leagueId The ID of the league this tournament belongs to.
     */
    public TournamentNewDTO(String name, int leagueId) {
        this.name = name;
        this.leagueId = leagueId;
    }
}