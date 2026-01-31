package edu.augustana.csc305.project.model.api;

/**
 * Data Transfer Object for creating a new Court via the API.
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class CourtNewDTO {
    private final String name;
    private final int tournamentId;

    /**
     * Constructs a DTO for creating a new court.
     *
     * @param name The name of the new court.
     * @param tournamentId The ID of the tournament the court is assigned to.
     */
    public CourtNewDTO(String name, int tournamentId) {
        this.name = name;
        this.tournamentId = tournamentId;
    }
}